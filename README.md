# 📱 Coletor de Dados Android

Aplicação Android desenvolvida em **Kotlin**, utilizando o **Android Studio**, layouts em **XML** e persistência de dados local com **Room Database**.

O objetivo do projeto é disponibilizar uma solução prática e eficiente para coleta, armazenamento e gerenciamento de dados diretamente em dispositivos Android.

## 📋 Sobre o projeto

O Coletor de Dados Android foi desenvolvido para facilitar a coleta de informações em campo, permitindo que os dados sejam registrados e armazenados localmente no dispositivo e transmitidos para o servidor.

A aplicação utiliza tecnologias nativas do Android, priorizando desempenho, organização do código, facilidade de manutenção e funcionamento independente de conexão com a internet.

## ✨ Funcionalidades

- [x] Interface gráfica desenvolvida com XML.
- [x] Aplicação desenvolvida em Kotlin.
- [ ] Armazenamento local utilizando Room Database.
- [ ] Cadastro de informações coletadas.
- [ ] Consulta das útimas 03 leituras armazenadas.
- [ ] Exclusão dos registros após a confirmação de recebimento dos dados pelo berço.
- [ ] Logout do usuário após a sincronização e confirmação do recebimento pelo berço.
- [ ] Persistência de dados após o encerramento da aplicação.
- [ ] Exportação dos dados para TXT através do berço coletor.
- [ ] Integração com leitura de código de barras.
- [ ] Troca de usuários durante a leitura no período.
- [ ] Reabertura de leituras ecnerradas, desde que dentro do período permitido.

> As funcionalidades marcadas como concluídas devem ser ajustadas conforme o estágio atual do desenvolvimento.

## 🛠️ Tecnologias utilizadas

| Tecnologia | Descrição |
|---|---|
| Kotlin | Linguagem principal de desenvolvimento |
| Android Studio | IDE de desenvolvimento |
| Android SDK | Plataforma de desenvolvimento Android |
| XML | Construção das interfaces gráficas |
| Room Database | Persistência de dados local |
| SQLite | Banco de dados utilizado pelo Room |
| Gradle | Gerenciamento de dependências e compilação |
| Android Jetpack | Componentes modernos para desenvolvimento Android |

## 🏗️ Arquitetura do projeto

A aplicação utiliza uma organização baseada na separação de responsabilidades:

```text
ColetorDeDados/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── exemplo/
│   │       │           └── coletordedados/
│   │       │               ├── MainActivity.kt
│   │       │               ├── data/
│   │       │               │   ├── AppDatabase.kt
│   │       │               │   ├── DataDao.kt
│   │       │               │   └── DataEntity.kt
│   │       │               └── repository/
│   │       │                   └── DataRepository.kt
│   │       │
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   ├── drawable/
│   │       │   ├── values/
│   │       │   └── mipmap/
│   │       │
│   │       └── AndroidManifest.xml
│   │
│   └── build.gradle.kts
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
└── README.md
```

A estrutura apresentada é uma sugestão e pode ser adaptada conforme a organização real do projeto.

## 🗄️ Banco de dados Room

O Room é utilizado como camada de abstração sobre o SQLite, facilitando a manipulação e persistência dos dados.

A implementação é organizada em três componentes principais:

- **Entity:** representa as tabelas do banco de dados.
- **DAO (Data Access Object):** define as operações de acesso aos dados.
- **Database:** configura e disponibiliza a instância do banco.

### Exemplo de entidade

```kotlin
@Entity(tableName = "dados_coletados")
data class DataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val descricao: String,

    val valor: String,

    val dataColeta: Long = System.currentTimeMillis()
)
```

### Exemplo de DAO

```kotlin
@Dao
interface DataDao {

    @Insert
    suspend fun inserir(dado: DataEntity)

    @Query("SELECT * FROM dados_coletados")
    suspend fun listarTodos(): List<DataEntity>

    @Delete
    suspend fun excluir(dado: DataEntity)
}
```

Os exemplos ilustram a estrutura básica de persistência e podem ser adaptados às necessidades da aplicação.

## 🚀 Como executar o projeto

### Pré-requisitos

Antes de executar o projeto, é necessário possuir:

- Android Studio instalado.
- Android SDK configurado.
- JDK compatível com a versão do Android Gradle Plugin.
- Dispositivo Android físico ou emulador.

### Instalação

1. Clone o repositório:

```bash
git clone https://github.com/Augustoklippel/SCD.git
```

2. Acesse o diretório:

```bash
cd ColetorDeDados
```

3. Abra o projeto no Android Studio.

4. Aguarde a sincronização do Gradle.

5. Execute a aplicação utilizando um dispositivo Android ou emulador.

## 📦 Compilação

Para compilar o projeto pelo terminal:

```bash
./gradlew assembleDebug
```

No Windows:

```bash
gradlew.bat assembleDebug
```

O APK de depuração será gerado no diretório:

```text
app/build/outputs/apk/debug/
```

## 🔒 Armazenamento de dados

Os dados coletados são armazenados localmente no dispositivo Android através do Room Database.

Essa abordagem permite:

- Persistência dos dados entre execuções.
- Consulta rápida às informações registradas.
- Funcionamento sem conexão com a internet.
- Organização e manutenção dos registros.

## 🎯 Objetivos do projeto

- Desenvolver uma aplicação Android nativa utilizando Kotlin.
- Aplicar boas práticas de desenvolvimento Android.
- Utilizar XML para construção de interfaces.
- Implementar persistência local com Room.
- Facilitar a coleta e organização de informações.
- Criar uma base para futuras funcionalidades de integração e sincronização.

## 🔮 Melhorias futuras

- Criação do recruso de agrupamento por sistemas.
- Implementação de leitura por NFC.
- Cadastro de Usu´raios Administradores.
- Backup e restauração de registros.

## 👨‍💻 Desenvolvimento

Projeto desenvolvido utilizando Kotlin e tecnologias nativas do Android.

Contribuições, sugestões e melhorias são bem-vindas.

## 📄 Licença

Este projeto poderá ser distribuído sob a licença MIT.

Consulte o arquivo `LICENSE` para obter mais informações.

---

**Desenvolvido com Kotlin, Android Studio, XML e Room Database.**
