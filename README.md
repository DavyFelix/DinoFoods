# 🦖 DinoFoods - Delivery App
O **DinoFoods** é um aplicativo de delivery de comida desenvolvido em Android Nativo (Kotlin). O projeto foca em uma interface dinâmica com listagem de produtos, categorias e um sistema de carrinho de compras integrado com backend.

# 📑 Table of Contents

- [📱 Sobre o Projeto](#-sobre-o-projeto)
- [🚀 Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [📂 Estrutura do Projeto](#-estrutura-do-projeto)
- [🛠️ Funcionalidades](#️-funcionalidades)
- [🎨 Layouts](#-layouts)
- [⚙️ Instalação](#️-instalação)
- [▶️ Como Executar](#️-como-executar)
- [🔧 Configuração](#-configuração)
- [📸 Exemplos](#-exemplos)
- [🧩 Dependências](#-dependências)
- [🛠️ Troubleshooting](#️-troubleshooting)
- [🚧 Próximos Passos](#-próximos-passos)
- [👨‍💻 Contribuidores](#-contribuidores)
- [📄 Licença](#-licença)

---

# 📱 Sobre o Projeto

O **DinoFoods** simula um aplicativo moderno de delivery, permitindo que usuários:

- Visualizem produtos em diferentes categorias
- Adicionem itens ao carrinho
- Gerenciem pedidos antes do checkout
- Realizem autenticação de usuário

A aplicação utiliza o Appwrite como backend principal, sendo responsável pela persistência de dados por meio de Collections (Database) e pelo armazenamento de arquivos e imagens via Storage.

O Firebase Authentication é utilizado para gerenciar todo o fluxo de autenticação, incluindo cadastro, login e controle de sessão dos usuários.

Essa arquitetura híbrida permite uma separação clara de responsabilidades, utilizando o Firebase para autenticação segura e o Appwrite para gerenciamento estruturado dos dados, garantindo escalabilidade, organização e maior controle sobre os recursos da aplicação.

# 🚀 Tecnologias Utilizadas

### Linguagem
- Kotlin

### Arquitetura
- Activities + Fragments

### Backend
- Appwrite (Autenticação e Banco de Dados)

### Banco Secundário
- Firebase (configurado via `google-services.json`)

### Bibliotecas e UI
- Material Design
- RecyclerView
- ConstraintLayout
- Glide (carregamento de imagens)

---


📂 Estrutura do Projeto
O projeto segue uma organização por pacotes para facilitar a manutenção:
```bash
com.dinofoods
│
├── activities
│ ├── HomeActivity
│ ├── LoginActivity
│ ├── RegisterActivity
│ ├── MainActivity
│ ├── ProfileActivity
│ ├── AdressActivy
│ ├── ForgotPasswordActivity
│ ├── EditProfileActivity
│ ├── OrdersActivity
│ └── AdminActivity
│
│
├── fragments
│ ├── FoodsFragments
│ ├── CartFragments
│ ├── DetailsFoodsFragments
│ └── DetailsOrdersFragments
│
├── adapter
│ ├── ProdutoAdapter
│ ├── OrdersAdapter
│ └── CarrinhoAdapter
│
├── data
│ └── Cart.kt
│ └── Orders.kt
│ └── Produto.kt
│
└── services
├── Appwrite.kt
└── FirebaseConfiguration.kt
```

# 🛠️ Funcionalidades

## 🛍️ 1. Vitrine de Produtos

- Listagem dinâmica de produtos consumidos via **API do Appwrite**
- Exibição usando **RecyclerView**
- Layout flexível com:
  - `GridLayout`
  - `LinearLayout`
- Separação por categorias:
  - 🍔 Comidas
  - 🥤 Bebidas

---

## 🛒 2. Sistema de Carrinho

### ➕ Adição Dinâmica
Os produtos são adicionados a uma **lista global**, mantendo o estado durante a navegação entre telas.

### 📦 Visualização do Carrinho
Tela dedicada (`CartFragments`) onde o usuário pode visualizar os produtos selecionados.

### ❌ Remoção de Itens
Remoção feita diretamente no `CarrinhoAdapter`, com:

- Atualização do carrinho
- Recalculo do preço em tempo real

---

## 🔐 3. Autenticação e Segurança

Fluxo completo de autenticação:

- Login
- Cadastro de usuário


Configuração de ambiente seguro via local.properties.

# 🎨 Layouts

Principais layouts do projeto:

### `fragment_cart_fragments.xml`
Interface principal do carrinho contendo:

- Lista de itens
- Resumo de valores
- Total do pedido

### `item_carrinho.xml`

Layout customizado para cada produto no carrinho contendo:

- Nome
- Preço
- Botão de remoção

### `item_produto.xml`

Card de produto utilizado na vitrine contendo:

- Imagem
- Nome
- Preço
- Botão de adicionar ao carrinho

---

# ⚙️ Instalação

Clone o repositório:

```bash
git clone https://github.com/seu-usuario/dinofoods.git
```

Certifique-se de ter o arquivo google-services.json na pasta /app.

Configure suas chaves do Appwrite no arquivo Appwrite.kt.

Sincronize o Gradle e execute em um emulador ou dispositivo físico (Min SDK 24).

Próximos Passos
[ ] Implementar persistência local (Room ou SharedPreferences) para o carrinho.


[ ] Finalização de checkout com integração de pagamento simulado.

Desenvolvido por Davy Felix 🦖
