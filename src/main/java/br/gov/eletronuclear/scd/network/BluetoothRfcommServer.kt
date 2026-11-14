package br.gov.eletronuclear.scd.network

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Executors

class BluetoothRfcommServer(
    private val adapter: BluetoothAdapter,
    private val listener: Listener
) {
    interface Listener {
        fun onConnected(deviceName: String?)
        fun onMessage(message: String)
        fun onDisconnected()
        fun onError(error: String)
    }

    companion object {
        private const val SERVICE_NAME = "AndroidRFCOMM"

        val SPP_UUID: UUID = UUID.fromString(
            "00001101-0000-1000-8000-00805F9B34FB"
        )
    }

    private val serverExecutor = Executors.newSingleThreadExecutor()
    private val sendExecutor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile
    private var running = false

    @Volatile
    private var serverSocket: BluetoothServerSocket? = null

    @Volatile
    private var bluetoothSocket: BluetoothSocket? = null

    private val writeLock = Any()

    /**
     * Inicia o servidor Bluetooth RFCOMM.
     */
    fun start() {
        if (running) return

        running = true

        serverExecutor.execute {
            serverLoop()
        }
    }

    private fun serverLoop() {
        try {
            serverSocket = adapter.listenUsingRfcommWithServiceRecord(
                SERVICE_NAME,
                SPP_UUID
            )

            while (running) {
                try {
                    // Aguarda uma conexão do Windows
                    val socket = serverSocket?.accept() ?: break

                    bluetoothSocket = socket

                    notifyConnected(socket.remoteDevice.name)

                    // Processa as mensagens recebidas
                    receiveMessages(socket)

                    closeConnection()

                } catch (e: IOException) {
                    if (running) {
                        notifyError("Erro RFCOMM: ${e.message}")
                    }
                }
            }

        } catch (e: SecurityException) {
            notifyError("Permissão Bluetooth não concedida.")

        } catch (e: IOException) {
            if (running) {
                notifyError("Falha ao iniciar servidor: ${e.message}")
            }

        } finally {
            closeConnection()

            try {
                serverSocket?.close()
            } catch (_: IOException) {
            }

            serverSocket = null
        }
    }

    /**
     * Recebe mensagens do Windows.
     */
    private fun receiveMessages(socket: BluetoothSocket) {
        val input = socket.inputStream
        val buffer = ByteArray(1024)
        val messageBuffer = StringBuilder()

        try {
            while (running && socket.isConnected) {

                val bytesRead = input.read(buffer)

                if (bytesRead == -1) {
                    break
                }

                val received = String(
                    buffer,
                    0,
                    bytesRead,
                    Charsets.UTF_8
                )

                messageBuffer.append(received)

                // Cada mensagem termina com '\n'
                while (true) {
                    val index = messageBuffer.indexOf("\n")

                    if (index < 0) break

                    val message = messageBuffer
                        .substring(0, index)
                        .trimEnd('\r')

                    messageBuffer.delete(0, index + 1)

                    notifyMessage(message)
                }
            }

        } catch (e: IOException) {
            if (running) {
                notifyError("Erro ao receber dados: ${e.message}")
            }
        }
    }

    /**
     * Envia mensagem para o Windows.
     */
    fun sendMessage(message: String) {
        sendExecutor.execute {
            try {
                val socket = bluetoothSocket

                if (socket == null || !socket.isConnected) {
                    notifyError("Nenhum dispositivo conectado.")
                    return@execute
                }

                synchronized(writeLock) {
                    socket.outputStream.write(
                        (message + "\n").toByteArray(Charsets.UTF_8)
                    )

                    socket.outputStream.flush()
                }

            } catch (e: IOException) {
                notifyError("Falha no envio: ${e.message}")
            }
        }
    }

    /**
     * Encerra o servidor e a conexão.
     */
    fun stop() {
        running = false

        try {
            serverSocket?.close()
        } catch (_: IOException) {
        }

        closeConnection()

        serverExecutor.shutdownNow()
        sendExecutor.shutdownNow()
    }

    private fun closeConnection() {
        try {
            bluetoothSocket?.close()
        } catch (_: IOException) {
        }

        bluetoothSocket = null

        mainHandler.post {
            listener.onDisconnected()
        }
    }

    private fun notifyConnected(name: String?) {
        mainHandler.post {
            listener.onConnected(name)
        }
    }

    private fun notifyMessage(message: String) {
        mainHandler.post {
            listener.onMessage(message)
        }
    }

    private fun notifyError(error: String) {
        mainHandler.post {
            listener.onError(error)
        }
    }
}