# 📢 Voz Cidadã

**Sua voz pode transformar a cidade!**

O **Voz Cidadã** é um aplicativo mobile desenvolvido para dar visibilidade a problemas urbanos, de saúde, segurança e infraestrutura, permitindo que cidadãos compartilhem situações do cotidiano, realizem denúncias e apresentem sugestões de melhorias para a comunidade.

Inspirado no conceito das redes sociais, o aplicativo oferece um ambiente de participação cidadã, no qual os usuários podem criar publicações, visualizar problemas relatados por outras pessoas e interagir por meio de curtidas e comentários.

O projeto busca incentivar a colaboração entre cidadãos e contribuir para a construção de cidades mais conscientes, participativas e conectadas.

## 📱 Download do Aplicativo

A primeira versão de testes do **Voz Cidadã** já está disponível para download!

**Versão:** `v1.0.0`  
**Plataforma:** Android  
**Formato:** APK de depuração (Debug)  
**Data de lançamento:** 23/11/2026

📥 **[Baixar APK — Voz Cidadã v1.0.0](https://github.com/1KarolMoraes/Voz-Cidada/releases/download/v1.0.0/app-debug.apk)**

📦 **[Consultar todas as versões (Releases)](https://github.com/1KarolMoraes/Voz-Cidada/releases)**

### Como instalar

1. Acesse o link de download pelo celular Android.
2. Baixe o arquivo `app-debug.apk`.
3. Abra o arquivo após concluir o download.
4. Autorize a instalação de aplicativos dessa origem, caso solicitado pelo Android.
5. Conclua a instalação e abra o **Voz Cidadã**.

> **Observação:** esta é uma versão acadêmica destinada a testes e avaliação. Algumas funcionalidades ainda poderão receber melhorias e correções.

## 🎯 Objetivo do Projeto

Desenvolver uma plataforma digital que facilite a comunicação e a participação cidadã, oferecendo um espaço para:

- Compartilhar problemas urbanos e situações que necessitam de atenção.
- Registrar denúncias e sugestões de melhorias.
- Divulgar situações relacionadas à infraestrutura, saúde, segurança e meio ambiente.
- Incentivar a interação e a colaboração entre usuários.
- Dar maior visibilidade às demandas da comunidade.

## ✨ Funcionalidades

O aplicativo conta com as seguintes funcionalidades:

### 🔐 Cadastro e autenticação

- Cadastro de usuários utilizando e-mail e senha.
- Login com Firebase Authentication.
- Gerenciamento de sessão do usuário.
- Logout e validação de autenticação.

### 📰 Feed de publicações

- Exibição das publicações compartilhadas pela comunidade.
- Integração com Firebase Cloud Firestore.
- Visualização das informações das publicações.
- Identificação visual das categorias por meio de ícones ilustrativos.

### 📢 Denúncias e sugestões

Espaço destinado ao compartilhamento de problemas e sugestões relacionados às seguintes categorias:

- Infraestrutura
- Meio Ambiente
- Iluminação
- Saneamento
- Trânsito
- Saúde
- Segurança
- Outro

Cada categoria possui um ícone ilustrativo próprio, exibido automaticamente conforme a classificação da publicação.

### ❤️ Curtidas e comentários

- Interação dos usuários com as publicações.
- Funcionalidades de curtidas e comentários.
- Interface com ícones personalizados.

### 🔥 Em Alta

- Visualização das publicações em destaque.
- Integração com os dados das publicações.
- Interface padronizada com o Feed.

### 👤 Perfil do usuário

- Visualização das informações do usuário.
- Exibição das publicações realizadas pelo próprio usuário.
- Integração com os dados armazenados no Firebase.

### 🧭 Navegação

- Menu inferior compartilhado entre as principais telas.
- Acesso às seções Feed, Em Alta, Denunciar e Perfil.
- Interface padronizada para facilitar a utilização do aplicativo.

> As funcionalidades continuam em evolução e poderão receber novas melhorias durante o desenvolvimento.

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Aplicação |
|---|---|
| Java | Desenvolvimento das funcionalidades do aplicativo |
| Android Studio | Ambiente de desenvolvimento |
| XML | Construção das interfaces |
| Firebase Authentication | Cadastro, login e autenticação |
| Firebase Cloud Firestore | Armazenamento e consulta de dados |
| Gradle | Gerenciamento de dependências e compilação |
| Git | Controle de versão |
| GitHub | Colaboração, Pull Requests e distribuição do APK |

## 📱 Plataforma

- Aplicativo nativo para Android.
- Interface desenvolvida com XML.
- Lógica de programação implementada em Java.
- Integração com serviços Firebase.
- Ícones vetoriais utilizados na identidade visual e nas categorias.

## 🚀 Como executar o projeto

### Pré-requisitos

Para executar o projeto no ambiente de desenvolvimento, é necessário ter:

- [Android Studio](https://developer.android.com/studio)
- JDK compatível com a configuração do projeto.
- Git.
- Acesso a um projeto Firebase configurado.

### 1. Clone o repositório

```bash
git clone https://github.com/1KarolMoraes/Voz-Cidada.git
```

### 2. Abra o projeto

Abra o Android Studio, selecione **Open** e escolha a pasta do projeto.

### 3. Configure o Firebase

1. Acesse o [Firebase Console](https://console.firebase.google.com/).
2. Crie ou utilize um projeto Firebase.
3. Registre um aplicativo Android com o identificador `com.example.voz_cidada`.
4. Baixe o arquivo `google-services.json`.
5. Coloque o arquivo na pasta `app/`.
6. Habilite o provedor de autenticação por e-mail e senha no Firebase Authentication.
7. Configure o Cloud Firestore e suas regras de segurança conforme as necessidades do projeto.

### 4. Execute o aplicativo

Após a sincronização do Gradle:

1. Selecione um emulador Android ou conecte um dispositivo físico.
2. Aguarde a compilação.
3. Clique em **Run** para executar o aplicativo.

### 5. Gere um APK de testes

Para gerar o APK utilizando o terminal do Android Studio:

```bash
./gradlew assembleDebug
```

No Windows, utilizando PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

O APK gerado estará disponível em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 📂 Organização do Projeto

A estrutura principal segue a organização de um projeto Android:

```text
Voz-Cidada/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/voz_cidada/
│   │       │
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   ├── drawable/
│   │       │   ├── mipmap-*/
│   │       │   └── values/
│   │       │
│   │       └── AndroidManifest.xml
│   │
│   ├── build.gradle.kts
│   └── google-services.json
│
├── docs/
│   └── encoeng/
│       ├── fotos/
│       └── Banner_VozCidada.pdf
│
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

*Observação: a estrutura poderá ser ampliada conforme o desenvolvimento do aplicativo.*

## 🎓 Participação no 13º ENCOENG

O projeto **Voz Cidadã** foi apresentado no **13º Encontro Regional de Engenharia (ENCOENG)**, realizado na FUCAPI.

A participação no evento teve como objetivo apresentar a proposta do aplicativo, demonstrar sua relevância social e compartilhar a ideia de uma plataforma digital voltada à participação cidadã.

Os materiais utilizados na apresentação estão disponíveis no repositório:

- **[Banner do Voz Cidadã](docs/encoeng/Banner_VozCidada.pdf)**
- **[Fotografias da apresentação](docs/encoeng/fotos/)**

## 🔄 Controle de Versão e Colaboração

O desenvolvimento é realizado em equipe, utilizando Git e GitHub.

As contribuições são organizadas por meio de branches específicas, permitindo que diferentes funcionalidades sejam desenvolvidas e integradas ao projeto.

O processo inclui:

- Desenvolvimento em branches.
- Registro de alterações por meio de commits.
- Revisão e integração utilizando Pull Requests.
- Resolução de conflitos de integração.
- Compilação e testes antes da disponibilização de novas versões.
- Publicação de APKs por meio das Releases do GitHub.

## 📦 Versões Disponíveis

| Versão | Data | Descrição |
|---|---|---|
| [v1.0.0](https://github.com/1KarolMoraes/Voz-Cidada/releases/tag/v1.0.0) | 23/11/2026 | Primeira versão de testes, com integração das funcionalidades e melhorias visuais |

As próximas versões poderão incluir correções, aprimoramentos de interface e novas funcionalidades.

## 👥 Desenvolvimento

Projeto acadêmico desenvolvido em equipe como parte das atividades do curso de **Engenharia de Software da FUCAPI**.

O desenvolvimento envolve conhecimentos de programação mobile, integração com banco de dados, autenticação de usuários, construção de interfaces e colaboração utilizando ferramentas de controle de versão.

## 🌱 Nossa Proposta

Acreditamos que pequenas atitudes podem gerar grandes transformações.

O **Voz Cidadã** nasce da ideia de que todo cidadão deve ter espaço para mostrar o que acontece ao seu redor, compartilhar suas preocupações e contribuir com soluções.

**Porque uma cidade melhor começa com pessoas que fazem sua voz ser ouvida.**

---

**Voz Cidadã — Sua voz, sua cidade, sua transformação.**
