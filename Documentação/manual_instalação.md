# 📘 Manual de Instalação – TechnoCode

## 📌 Requisitos do Sistema
Antes de instalar o TechnoCode, é necessário ter:

- **Java 21** ou superior instalado  
- **MySQL Server 8.0** instalado e em execução  
- **Sistema Operacional:** Windows 10 ou superior

---

# 🛠️ 1. Instalando o MySQL Server

### 🔹 Passo 1 — Baixe o MySQL
Acesse o site oficial:

- MySQL Installer: https://dev.mysql.com/downloads/installer/

Baixe o arquivo **MySQL Installer (versão completa)**.

---

### 🔹 Passo 2 — Instale o MySQL Server

Durante a instalação, selecione:

✔ MySQL Server 8.0  
✔ MySQL Workbench (opcional, porém recomendado)

Continue clicando em **Next** até chegar à etapa *Accounts and Roles*.

---

### 🔹 Passo 3 — Configure o usuário root

- **Username:** `root`  
- **Password:** `fatec`  

⚠️ Caso use outra senha, será necessário alterar a configuração da aplicação.

Finalize a instalação.

---

# 🔧 2. Verificando banco e conexão

Após a instalação:

1. Abra o **MySQL Workbench**
2. Conecte usando:

- Host: `localhost`
- Porta: `3306`
- Usuário: `root`
- Senha: `fatec`

Se conectar, tudo está funcionando corretamente.

---

# 📦 3. Rodando o TechnoCode

### 🔹 Passo 1 — Baixe o arquivo JAR
Baixe o arquivo:

```
TechnoCode-1.0-SNAPSHOT.jar
```

---

### 🔹 Passo 2 — Abra o terminal na pasta do arquivo

- Windows: `Shift + botão direito → Abrir PowerShell aqui`
- Ou via comando:

```
cd "C:/caminho/da/pasta"
```

---

### 🔹 Passo 3 — Execute o programa

```
java -jar TechnoCode-1.0-SNAPSHOT.jar
```

---

### 🔹 O que acontece na primeira execução?

A aplicação irá:

- Verificar se o banco `technotg` existe  
- Criar o banco caso não exista  
- Criar automaticamente as tabelas  
- Validar o usuário MySQL  
- Abrir o sistema

---

# ✔️ 4. Problemas Comuns e Soluções

### ❗ “Nenhuma conexão possível”
Possíveis causas:

- MySQL não está rodando  
- Senha do root diferente de `fatec`  
- Porta 3306 ocupada  

---

### ❗ “Unsupported JavaFX configuration”
Java instalado **sem JavaFX**.

➡️ Instalar **Temurin JDK 21**, não JRE.

---

### ❗ “Banco não inicializa”
Verifique:

- Permissão para criar schema  
- MySQL ativo  
- Usuário root configurado  

---

