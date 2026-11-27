package br.gov.eletronuclear.scd.network

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Executors

class BluetoothRfcommClient(
    private val adapter: BluetoothAdapter,
    private val listener: Listener
) {
    interface Listener {
        fun onConnected()
        fun onMessage(message: String)
        fun onDisconnected()
        fun onError(error: String)
    }

    companion object {
        val SPP_UUID: UUID = UUID.fromString(
            "00001101-0000-1000-8000-00805F9B34FB"
        )
    }

    private val connectExecutor = Executors.newSingleThreadExecutor()
    private val receiveExecutor = Executors.newSingleThreadExecutor()
    private val sendExecutor = Executors.newSingleThreadExecutor()

    @Volatile
    private var socket: BluetoothSocket? = null

    @Volatile
    private var running = false

    private val writeLock = Any()

    /**
     * Tenta conectar aos servidores Windows em ordem.
     * Interrompe a busca no primeiro que aceitar a conexão.
     */
    //@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun connectToFirstAvailable(macAddresses: MutableList<String>) {

        connectExecutor.execute {

            for (mac in macAddresses) {

                var candidateSocket: BluetoothSocket? = null

                try {
                    // adapter.cancelDiscovery()
                    listener.onError("Tentando conectar ao Windows: $mac")
                    Log.d("MensagemBL","Tentando conectar ao Windows: $mac")

                    // Obtém o dispositivo pelo MAC
                    val remoteDevice = adapter.getRemoteDevice(mac)

                    // Cria o socket RFCOMM
                    candidateSocket =
                        remoteDevice.createRfcommSocketToServiceRecord(
                            SPP_UUID
                        )

                    // Tenta estabelecer a conexão
                    candidateSocket.connect()

                    // Conexão estabelecida
                    socket = candidateSocket
                    running = true

                    listener.onConnected()

                    // Inicia a recepção de mensagens
                    receiveExecutor.execute {
                        receiveMessages()
                    }

                    // Interrompe a busca
                    return@execute

                } catch (e: IOException) {

                    try {
                        candidateSocket?.close()
                    } catch (_: IOException) {
                    }

                    listener.onError(
                        "Windows $mac indisponível: ${e.message}"
                    )
                }
            }

            listener.onError(
                "Nenhum servidor Windows disponível."
            )
        }
    }

    /**
     * Recebe mensagens do Windows.
     */
    private fun receiveMessages() {

        val buffer = ByteArray(128)
        val messageBuffer = StringBuilder()

        try {

            val input = socket?.inputStream
                ?: throw IOException("Socket não disponível")

            while (running) {

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

                while (true) {

                    val index = messageBuffer.indexOf("\n")

                    if (index < 0) break

                    val message = messageBuffer
                        .substring(0, index)
                        .trimEnd('\r')

                    messageBuffer.delete(0, index + 1)

                    listener.onMessage(message)
                }
            }

        } catch (e: IOException) {

            if (running) {
                listener.onError(
                    "Erro na recepção: ${e.message}"
                )
            }

        } finally {
            disconnect()
        }
    }

    /**
     * Envia mensagem para o Windows.
     */
    fun sendMessage(message: String) {

        sendExecutor.execute {

            try {

                val bluetoothSocket = socket

                if (bluetoothSocket == null ||
                    !bluetoothSocket.isConnected
                ) {
                    listener.onError("Não conectado.")
                    return@execute
                }

                synchronized(writeLock) {

                    bluetoothSocket.outputStream.write(
                        (message + "\n").toByteArray(Charsets.UTF_8)
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        bluetoothSocket.outputStream.flush()
                    }, 500L)

                }

            } catch (e: IOException) {

                listener.onError(
                    "Erro no envio: ${e.message}"
                )
            }
        }
    }

    /**
     * Encerra a conexão Bluetooth.
     */
    fun disconnect() {

        running = false

        try {
            socket?.close()
        } catch (_: IOException) {
        }

        socket = null

        listener.onDisconnected()
    }

    fun shutdown() {

        disconnect()

        connectExecutor.shutdownNow()
        receiveExecutor.shutdownNow()
        sendExecutor.shutdownNow()
    }
}