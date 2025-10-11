# API 2º BD – 2025-2  
## TechnoTG  

### 📌 Descrição do Desafio
A confecção e orientação de Trabalhos de Graduação (TG) da modalidade Portfólio representam um desafio tanto para os alunos quanto para os professores.  
Um TG Portfólio é composto por uma seção de apresentação do aluno e várias seções correspondentes aos APIs, uma para cada semestre (com um total de 6).  

O processo atual de correção é lento e pouco eficiente, pois envolve plataformas como e-mail institucional e MS Teams, que muitas vezes falham em notificações ou dispersam mensagens importantes. Isso gera atrasos, perda de histórico de correções e dificuldades para consolidar o documento final em formato Markdown.  

Nosso projeto busca propor uma solução em **Java Desktop**, com persistência em **Banco de Dados Relacional**, para melhorar a comunicação, manter o histórico de interações e facilitar a geração do TG final.  

---

### 📶 Backlog do Produto
| Funcionaliade | User Storye | Prioridade | Status |
|-------------|-------------|-------------| ----------- |
| Visualização e correção de secções | Como orientador, quero visualizar as seções dos alunos e corrigi-las, para auxiliar os TG´s dos alunos. | Alta | Planejado |
| Controle dos Alunos | Como orientador, quero ter um controle com todos os alunos que estou orientando, para ver rapidamente quem está em atraso, em revisão ou finalizou seções. | Alta | Planejado |
| Envio de TGs | Como aluno, quero enviar os TG’s em seções e receber os feedbacks, para arrumar os TG´s conforme o orientador recomendou  | Alta | Planejado |
| Análise de Feedbacks | Como aluno, quero analisar os feedbacks recebidos e corrigi-los, para alinhar com a opinião do orientador | Alta | Planejado |
| Visualização de Orientadores | Como cliente, quero visualizar os orientadores e seus alunos atribuídos, para noção de como está sendo as orientações | Média | Planejado |
| Feedback do Orientador | Como orientador, quero adicionar feedbacks em cada seção e manter os feedbacks visíveis, para ter controle dos feedbacks que já foram feitos | Média | Planejado |
| Aprovação de Seções | Como orientador, quero aprovar as seções para controlar o progresso, assim evidenciando ao aluno quando seu TG estiver pronto para continuar | Média | Planejado |
| Visualização de Portfólios | Como orientador, quero visualizar os portfólios completos e suas seções separadamente, para ter facilidade em corrigi-los | Média | Planejado |
| Manual de Usuário | Como orientador, quero ter um manual de usuário dentro do projeto, explicando como usar o sistema. | Baixa | Planejado |
| Manual de Instalação | Como orientador, quero um manual de instalação para o GitHub, para futura entrega do projeto. | Baixa | Planejado |


---

### 🟢 Definition of Ready (DoR) — Definição de Pronto
A finalidade do DoR, é estabelecer um conjunto de critérios que um item de trabalho deve atender antes de poder ser iniciado pela equipe, garantindo que tarefas e histórias de usuário estejam claras, detalhadas e prontas para o desenvolvimento, evitando que a equipe comece um trabalho incompleto e comprometa o cronograma. 

---

### Critérios para uma User Story ser iniciada

### 1️⃣ Clareza e Compreensão
A história de usuário deve estar escrita de forma clara, seguindo o formato:

> **Como** [tipo de usuário], **quero** [ação ou funcionalidade], **para** [objetivo ou benefício].

Assim, o time entende completamente **o que precisa ser feito** e **por que** aquilo é importante.

### 2️⃣ Critérios de Aceitação Definidos
Devem existir **critérios objetivos** que indiquem quando a história será considerada concluída.

📌 *Exemplo:*  
> “O aluno consegue enviar um TG e recebe uma mensagem de sucesso.”

Esses critérios servem como base para testes e validação da entrega.

---

### 3️⃣ Valor de Negócio Identificado
É necessário deixar claro **por que essa funcionalidade é importante** e **o que ela agrega** ao sistema.  
Exemplos de valor de negócio:
- Melhora o controle do fluxo de TCCs;  
- Simplifica a comunicação entre orientador e aluno;  
- Garante rastreabilidade e organização do processo.

---

### 4️⃣ Esforço Estimado
Assim que a tarefa for definida, o time deve **avaliar sua complexidade e esforço**, utilizando métodos como:

- **Planning Poker**  
- **Estimativas em horas ou story points**

Isso permite um melhor entendimento da dificuldade e viabilidade de entrega no sprint.

---

### 5️⃣ Modelagem e Dependências Resolvidas
Antes do desenvolvimento começar:
- As **entidades envolvidas** (classes, tabelas, relações) devem estar **mapeadas e revisadas**;  
- **Dependências técnicas** (como conexões com banco de dados, autenticação, bibliotecas externas etc.) devem estar resolvidas ou documentadas.

---

### DoR Individual por User Story
### 1. Visualização e correção de seções
Critérios:
- Orientador pode visualizar lista de seções por aluno
- Pode adicionar comentários/correções em cada seção
- Status da seção é atualizado após correção
  
UI/UX: Rascunho da tela de visualização e correção

Regras:
- Apenas orientadores acessam seções de seus alunos
- Histórico de alterações deve ser mantido

Dados Necessários: ID do aluno, seções do TG, comentários

---
### 2. Controle dos Alunos
Critérios:
- Lista de alunos com status (atraso, revisão, finalizado)
- Filtros por status
- Indicador visual de status (ex: cores)
  
UI/UX: Wireframe da dashboard do orientador

Regras:
- Status é atualizado com base em prazos
  
Dados: Alunos, prazos, status de seções

---
### 3. Envio de TGs
Critérios:
- Aluno pode fazer upload de arquivos por seção
- Sistema valida formato/tamanho do arquivo
- Aluno recebe confirmação de envio
  
UI/UX: Tela de upload com drag-and-drop ou seletor de arquivos

Regras:
- Apenas seções não finalizadas podem receber envios

Dados: Arquivo, data de envio, ID da seção

---
### 4. Análise de Feedbacks
Critérios:
- Aluno visualiza feedbacks por seção
- Pode marcar feedback como “corrigido”
- Notificação para o orientador quando correção é feita
  
UI/UX: Tela de feedbacks com lista e ações

Regras:
- Feedback permanece visível até a seção ser aprovada

Dados: Comentários, status de correção

---
### 5. Visualização de Orientadores
Critérios:
- Cliente visualiza lista de orientadores
- Pode expandir para ver alunos de cada orientador
  
UI/UX: Layout de lista/detalhes

Regras:
- Apenas o dono do projeto tem acesso

Dados: Orientadores, alunos vinculados

---
### 6. Feedback do Orientador
Critérios:
- Orientador adiciona/comenta em cada seção
- Feedbacks ficam visíveis para o aluno
- Possibilidade de editar feedback recente

UI/UX: Componente de comentários na tela de correção

Regras:
- Feedback não pode ser excluído, apenas editado em tempo limite
  
Dados: Texto do feedback, timestamp, ID do orientador

---
### 7. Aprovação de Seções
Critérios:
- Botão “Aprovar Seção” na tela de correção
- Status da seção muda para “Aprovado”
- Aluno é notificado
  
UI/UX: Botão com confirmação

Regras:
- Apenas orientador responsável pode aprovar
  
Dados: Status da seção, data de aprovação

---
### 8. Visualização de Portfólios
Critérios:
- Orientador acessa visão geral do portfólio
- Pode navegar entre seções
- Visualização separada por seção ou consolidada
  
UI/UX: Página de portfólio com abas/seções

Regras:
- Portfólio só é visível se pelo menos uma seção foi enviada

Dados: Seções, conteúdos, status

---
### 9. Manual de Usuário
Critérios:
- Acesso via menu do sistema
- Conteúdo cobrindo funcionalidades principais
- Interface responsiva e de fácil leitura

UI/UX: Página estática ou com navegação interna

Regras:
- Conteúdo deve ser mantido atualizado com as versões

Dados: Textos, imagens (se necessário)

---

### 10. Manual de Instalação
Critérios:
- Documento disponível no repositório (ex: README.md)
- Passos claros para setup local e deploy
- Lista de dependências e configurações

UI/UX: — (não se aplica)

Regras:
- Deve ser compatível com a versão atual do código

Dados: Comandos, configurações, ambientes

---

🔵 Definition of Done (DoD) — Definição de Feito
A finalidade do DoD, é definir de forma formal e compartilhada os critérios de uma tarefa, user story ou incremento que deve ser atendido para ser considerada finalizada.

---

### Critérios para uma User Story ser iniciada



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
| Função        | Nome                              | LinkedIn & GitHub                                                                                                                                                                                                                                                                                                                      | Foto                                                                                                                                                                                                                                       |
|:-------------:|:---------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| Scrum Master  | João Pedro Barni                  | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/joao-pedro-barni-lima/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/SaturnSeraphin)                | <img src="https://media.licdn.com/dms/image/v2/D4D03AQHov5WKOHnVTA/profile-displayphoto-scale_200_200/B4DZjt1Qo3G8AY-/0/1756336820726?e=1759363200&v=beta&t=ubUptDpr3FIlsUzoCqFMkH7ICOoq8sjYhhtrhGUlzp4" width="100">                      |
| Product Owner | Gabriel Nunes                     | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/gabriel-de-barcelos-nunes-a7a69832a/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/gabrielnunes926) | <img src="https://avatars.githubusercontent.com/u/178607805?v=4" width="100">                                                                                                                                                              |
| Dev. Team     | Gabriel Henrique Rocha Borges     | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/gabriel-rocha-wk27/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/GabrielRocha-27)                  | <img src="https://media.licdn.com/dms/image/v2/D4E03AQFybDDdsk0Z9g/profile-displayphoto-shrink_200_200/profile-displayphoto-shrink_200_200/0/1722628568006?e=1759363200&v=beta&t=DMjb5PgFVnvTKsuv9GT4Npoa0JYoLtSKCF8mQTQeNZE" width="100"> |
| Dev. Team     | Ryan Reis Poltronieri             | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/iryanreiszs/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/iryanreiszs)                             | <img src="https://media.licdn.com/dms/image/v2/D4D03AQFj9WwRApsixw/profile-displayphoto-crop_800_800/B4DZkByEVtG8AI-/0/1756671527235?e=1759363200&v=beta&t=58mR4NvM04FFcwUED1OV8zvGeOo7MFV1jYXtVPBKMJA" width="100"> |
| Dev. Team     | Leandro Henrique de Campos Silva  | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/lincoln-borsoi-35b13b23b/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/LeandroHCampos)             | <img src="https://media.licdn.com/dms/image/v2/D5603AQGkk0A176_DQQ/profile-displayphoto-shrink_400_400/B56ZV_xMzjGUAg-/0/1741605382705?e=1759363200&v=beta&t=sw-GrzUVKbcs98lqB0qkmU2wRr9pJCX7RuJchZiNgfk" width="100">                     |
| Dev. Team     | Pedro Soares                      | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/pedro-soares-276206292/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/pdrsoares)                    | <img src="https://media.licdn.com/dms/image/v2/D4D03AQHqvtlQK3Tylg/profile-displayphoto-shrink_200_200/B4DZYC1N3kHsAc-/0/1743804229023?e=1759363200&v=beta&t=kvznv11Cie1-VtQZbxx3Egn2RCfb21Ymr_B-G9ChZ7Q" width="100">                     |
| Dev. Team     | Guilherme Gomes                   | [![Linkedin Badge](https://img.shields.io/badge/Linkedin-blue?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/guilherme-gomes-crisostomo/) <br> [![GitHub Badge](https://img.shields.io/badge/GitHub-111217?style=flat-square&logo=github&logoColor=white)](https://github.com/guilhermegcris)           | <img src="https://media.licdn.com/dms/image/v2/D4D03AQEvcTc_ydKXOQ/profile-displayphoto-scale_200_200/B4DZjt2eeqG8Ag-/0/1756337140235?e=1759363200&v=beta&t=icAh6Ex9p_x298J_e99cRyEtjlVnuyrV9WVP7cJFz7Y" width="100">                      |
