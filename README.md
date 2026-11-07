# 📢 Voz Cidadã

**Sua voz pode transformar a cidade!**

O **Voz Cidadã** é um aplicativo mobile desenvolvido com o objetivo de dar visibilidade a problemas urbanos, de saúde e segurança, permitindo que cidadãos compartilhem situações do cotidiano, realizem denúncias e apresentem sugestões para melhorias na comunidade.

Inspirado no conceito das redes sociais, o aplicativo busca aproximar as pessoas por meio do compartilhamento de publicações com imagens, incentivando a participação cidadã e a construção de uma sociedade mais consciente e colaborativa.

## 🎯 Objetivo do Projeto

Desenvolver uma plataforma digital que facilite a comunicação entre cidadãos, oferecendo um espaço para compartilhar problemas, divulgar situações que precisam de atenção e propor soluções para melhorar a qualidade de vida nas cidades.

## ✨ Funcionalidades

O projeto prevê as seguintes funcionalidades:

- **Cadastro e Login:** criação de contas e autenticação de usuários.
- **Feed de publicações:** visualização das publicações compartilhadas pela comunidade.
- **Denúncias e sugestões:** publicação de problemas urbanos, situações de saúde e segurança, além de ideias para melhorias.
- **Curtidas e comentários:** interação entre os usuários nas publicações.
- **Em Alta:** destaque para publicações que estão recebendo maior engajamento.
- **Perfil do usuário:** espaço para visualizar informações e publicações do próprio usuário.

> As funcionalidades estão sujeitas à evolução durante o desenvolvimento do projeto.

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Aplicação |
|---|---|
| Java | Linguagem de programação |
| Android Studio | Ambiente de desenvolvimento |
| XML | Construção das interfaces |
| Firebase Authentication | Autenticação de usuários |
| Firebase | Serviços de backend e integração |

## 📱 Plataforma

- Aplicativo mobile para Android.
- Interface desenvolvida com XML.
- Desenvolvimento utilizando Java.

## 🚀 Como executar o projeto

### Pré-requisitos

Antes de começar, você precisará ter instalado:

- [Android Studio](https://developer.android.com/studio)
- JDK compatível com a configuração do projeto.
- Git.

### 1. Clone o repositório

```bash
git clone https://github.com/1KarolMoraes/Voz-Cidada.git
```

### 2. Abra o projeto

Abra o Android Studio, selecione **Open** e escolha a pasta do projeto.

### 3. Configure o Firebase

Para executar o projeto com os serviços Firebase:

1. Crie um projeto no [Firebase Console](https://console.firebase.google.com/).
2. Registre um aplicativo Android utilizando o mesmo identificador de pacote do projeto.
3. Baixe o arquivo `google-services.json`.
4. Coloque o arquivo dentro da pasta `app/`.
5. Verifique se o Firebase Authentication está habilitado no console.

### 4. Execute o aplicativo

Após a sincronização do Gradle:

1. Selecione um emulador Android ou conecte um dispositivo físico.
2. Aguarde a compilação.
3. Clique em **Run** para executar o aplicativo.

## 📂 Organização do Projeto

A estrutura segue a organização padrão de um projeto Android:

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
│   │       │   └── values/
│   │       │
│   │       └── AndroidManifest.xml
│   │
│   ├── build.gradle.kts
│   └── google-services.json
│
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

*Observação: a estrutura poderá ser ampliada conforme a implementação das funcionalidades.*

## 👥 Desenvolvimento

Projeto acadêmico desenvolvido em equipe como parte das atividades do curso de **Engenharia de Software** da FUCAPI.

O desenvolvimento utiliza Git e GitHub para controle de versão, organização das tarefas e integração das contribuições dos integrantes por meio de branches e Pull Requests.

## 🌱 Nossa proposta

Acreditamos que pequenas atitudes podem gerar grandes transformações.

O Voz Cidadã nasce da ideia de que todo cidadão deve ter espaço para mostrar o que acontece ao seu redor, compartilhar suas preocupações e contribuir com soluções.

**Porque uma cidade melhor começa com pessoas que fazem sua voz ser ouvida.**

---

**Voz Cidadã — Sua voz, sua cidade, sua transformação.**
