# API 2º BD – 2025-2 

## TechnoTG  

|Navegação Interna||||
|:-|:-|:-|:-|
| [1- Descrição do Desafio](#-descrição-do-desafio) | [2- Backlog do Produto](#-backlog-do-produto) | [3- Cronograma das Sprints](#️-cronograma-das-sprints) | [4- Tecnologias Utilizadas](#️-tecnologias-utilizadas) |
|[5- Requisitos não funcionais](#-requisitos-não-funcionais) | [6- Estratégia de Branchs](#-estratégia-de-branch) | [7- Padrão de Commits](#-padrão-de-commits) | [8- Representantes da Equipe](#-representantes-da-equipe)|

---

## 📌 Descrição do Desafio
A confecção e orientação de Trabalhos de Graduação (TG) da modalidade Portfólio representam um desafio tanto para os alunos quanto para os professores.  
Um TG Portfólio é composto por uma seção de apresentação do aluno e várias seções correspondentes aos APIs, uma para cada semestre (com um total de 6).  

O processo atual de correção é lento e pouco eficiente, pois envolve plataformas como e-mail institucional e MS Teams, que muitas vezes falham em notificações ou dispersam mensagens importantes. Isso gera atrasos, perda de histórico de correções e dificuldades para consolidar o documento final em formato Markdown.  

Nosso projeto busca propor uma solução em **Java Desktop**, com persistência em **Banco de Dados Relacional**, para melhorar a comunicação, manter o histórico de interações e facilitar a geração do TG final.  

---

#### 📶 Backlog do Produto
| Funcionaliade | User Story | Prioridade | Status |
|:-------------|:-------------|:-------------:| :-----------: |
| Visualização e correção de secções | Como orientador, quero visualizar as seções dos alunos e corrigi-las, para auxiliar os TG´s dos alunos. | Alta | Planejado |
| Controle dos Alunos | Como orientador, quero ter um controle com todos os alunos que estou orientando, para ver rapidamente quem está em atraso, em revisão ou finalizou seções. | Alta | Planejado |
| Envio de TGs | Como aluno, quero enviar os TG’s em seções e receber os feedbacks, para arrumar os TG´s conforme o orientador recomendou  | Alta | Planejado |
| Análise de Feedbacks | Como aluno, quero analisar os feedbacks recebidos e corrigi-los, para alinhar com a opinião do orientador | Alta | Planejado |
| Visualização de Orientadores | Como professor de TG, quero visualizar os orientadores e seus alunos atribuídos, para manter controle do andamento dos trabalhos | Média | Planejado |
| Feedback do Orientador | Como orientador, quero adicionar feedbacks de forma separada em cada seção e manter os feedbacks visíveis, para ter controle dos feedbacks que já foram feitos | Média | Planejado |
| Histórico do Orientador | Como orientador, quero acessar o histórico de versões das seções do TG de cada aluno, para acompanhar a evolução e manter controle das correções | Média | Planejado
| Aprovação de Seções | Como orientador, quero aprovar as seções para controlar o progresso, assim evidenciando ao aluno quando seu TG estiver pronto para continuar | Média | Planejado |
| Visualização de Portfólios | Como orientador, quero visualizar os portfólios completos e suas seções separadamente, para ter facilidade em corrigi-los | Média | Planejado |
| Formato MD | Como aluno, quero exportar meu TG em formato MD (MarkDown), quando estiver finalizado, conforme foi pedido pela instituição | Baixa | Planejado |
| Agendamento de TG | Como professor de TG, quero agendar um dia e uma sala para encontrar o aluno e fazer e defesa do TG, para que o aluno comprove a sua aptidão e domínio sobre o tema estudado | Baixa | Planejado |

---

## ⏱️ Cronograma das Sprints

| Sprint | Início | Fim | Documentação | Status|
|:------:|:------:|:---:|:------------:|:-----:|
|🔖 Sprint 1|08/09|28/09|[Sprint 1](./Documentação/Sprint1/README.md)|✅|
|🔖 Sprint 2|06/10|26/10|[Sprint 2](./Documentação/Sprint2/README.md)|⌛|
|🔖 Sprint 3|03/11|23/11|[Sprint 3](./Documentação/Sprint3/README.md)|⬜|

---

### 🛠️ Tecnologias Utilizadas
- **Linguagem:** Java Desktop (JavaFX)  
- **Banco de Dados:** Relacional (MySQL)  
- **Conexão BD:** JDBC  
- **Controle de Versão:** Git e GitHub  

---

### 📑 Requisitos Não Funcionais
- ✅ Manual de Instalação (obrigatório, no Git)  
- ✅ Manual do Usuário (obrigatório)  
- ✅ Modelo Entidade-Relacionamento (MER) do Banco de Dados  

---

### 🌱 Estratégia de Branch
Utilizaremos o **GitHub Flow**:  
- `main` → versão estável do sistema.  
- `feature/...` → novas funcionalidades.  
- `bugfix/...` → correções de bugs.  
- `docs/...` → documentação.  
- Pull Requests obrigatórios antes do merge.  

---

### 📝 Padrão de Commits
Seguindo o **Conventional Commits** simplificado:  
- `feat: <descrição>` → nova funcionalidade  
- `fix: <descrição>` → correção de bug  
- `docs: <descrição>` → mudanças na documentação  
- `style: <descrição>` → alterações de estilo/identação  
- `refactor: <descrição>` → refatoração de código  
- `test: <descrição>` → adição/alteração de testes  
- `chore: <descrição>` → manutenção geral  

**Exemplo:**  
```bash
feat (TG-01): implementação do CRUD de alunos
docs: inclusão do manual de instalação
```

---

### 👥 Representantes da Equipe
| Função        | Nome                              | LinkedIn & GitHub                                                                                                                                                                                                                                                                                                                      | Foto                                           |
|:-------------:|:---------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------:|
| Scrum Master  | João Pedro Barni                  | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/joao-pedro-barni-lima/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/SaturnSeraphin)                | <img src="./assets/Barni.jpeg" width="100">    |
| Product Owner | Gabriel Nunes                     | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/gabriel-de-barcelos-nunes-a7a69832a/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/gabrielnunes926) | <img src="./assets/Nunes.jpg" width="100">     |
| Dev. Team     | Gabriel Henrique Rocha Borges     | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/gabriel-rocha-wk27/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/GabrielRocha-27)                  | <img src="./assets/Rocha.jpg" width="100">     |
| Dev. Team     | Ryan Reis Poltronieri             | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/iryanreiszs/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/iryanreiszs)                             | <img src="./assets/Ryan.jpg" width="100">      |
| Dev. Team     | Leandro Henrique de Campos Silva  | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/leandrohcampos/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/LeandroHCampos)             | <img src="./assets/Leandro.jpg" width="100">   |
| Dev. Team     | Pedro Soares                      | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/pedro-soares-276206292/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/pdrsoares)                    | <img src="./assets/Pedro.jpg" width="100">     |
| Dev. Team     | Guilherme Gomes                   | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/guilherme-gomes-crisostomo/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/guilhermegcris)           | <img src="./assets/Guilherme.png" width="100"> |
