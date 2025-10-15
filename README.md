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
| Feedback do Orientador | Como orientador, quero adicionar feedbacks de forma separada em cada seção e manter os feedbacks visíveis, para ter controle dos feedbacks que já foram feitos | Média | Planejado |
| Aprovação de Seções | Como orientador, quero aprovar as seções para controlar o progresso, assim evidenciando ao aluno quando seu TG estiver pronto para continuar | Média | Planejado |
| Visualização de Portfólios | Como orientador, quero visualizar os portfólios completos e suas seções separadamente, para ter facilidade em corrigi-los | Média | Planejado |



---
# 🗓️ Sprint Backlog – Sprint 2

**Objetivo da Sprint:**  
Entregar as principais funcionalidades que viabilizam a comunicação entre aluno e orientador, incluindo envio de TGs, análise de feedbacks e controle de alunos.

**Duração da Sprint:** 2 a 3 semanas   

---

## 🧩 Funcionalidades da Sprint

| ID | Funcionalidade | User Story | Critérios de Aceitação | Prioridade | Status |
|:--:|:--|:--|:--|:--:|:--:|
| **F01** | **Visualização e correção de seções** | Como orientador, quero visualizar as seções dos alunos e corrigi-las, para auxiliar os TGs dos alunos. | O orientador consegue abrir as seções, editar e salvar correções visíveis ao aluno. | 🔺 Alta | 🚧 Em desenvolvimento |
| **F02** | **Controle dos alunos** | Como orientador, quero ter um controle com todos os alunos que estou orientando, para ver rapidamente quem está em atraso, em revisão ou finalizou seções. | Lista de alunos exibe status atualizado corretamente. | 🔺 Alta | ⏳ Planejado |
| **F03** | **Envio de TGs** | Como aluno, quero enviar os TGs em seções e receber os feedbacks, para arrumar os TGs conforme o orientador recomendou. | O aluno consegue enviar TGs e visualizar feedbacks pendentes. | 🔺 Alta | ⏳ Planejado |
| **F04** | **Análise de feedbacks** | Como aluno, quero analisar os feedbacks recebidos e corrigi-los, para alinhar com a opinião do orientador. | Feedbacks aparecem por seção e refletem correções feitas. | 🔺 Alta | ⏳ Planejado |
| **F05** | **Visualização de orientadores** | Como cliente, quero visualizar os orientadores e seus alunos atribuídos, para ter noção de como estão sendo as orientações. | Cliente visualiza orientadores e respectivas orientações sem erros. | 🔸 Média | ⏳ Planejado |


---
## Diagrama de Entidade-Relacionamento (DER)
<img width="1956" height="1159" alt="imagem (4)" src="https://github.com/user-attachments/assets/c9857d51-5c0a-44e9-8abf-5ad490e06596" />

---

### 🟢 Definition of Ready (DoR) — Definição para Iniciar
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

### 🔵 Definition of Done (DoD) — Definição de Feito
A finalidade do DoD, é definir de forma formal e compartilhada os critérios de uma tarefa, user story ou incremento que deve ser atendido para ser considerada finalizada.

---

### Critérios para uma User Story ser finalizada

### 🧩 1. Implementação completa

- A funcionalidade foi codificada integralmente, sem trechos faltando ou “gambiarras temporárias”.
- O código está integrado ao repositório principal (ex.: main ou develop) e compila/executa sem erros.


---
### 🧪 2. Testes
   
- Foram realizados testes unitários, de integração e/ou funcionais, conforme aplicável.
- Todos os testes passam com sucesso.
- Cobertura mínima de testes atendida (por exemplo, 80% de cobertura).
- O recurso foi testado manualmente e validado conforme os critérios de aceitação.

---
### 📋 3. Critérios de aceitação cumpridos
- Todos os critérios de aceitação da história foram atendidos.
- O Product Owner ou responsável confirmou que o comportamento está conforme o esperado.
---
### DoD Individual por User Story

### 1️⃣ Visualização e correção de seções

DoD:
- O orientador consegue visualizar todas as seções dos TGs dos alunos atribuídos.
- É possível editar/corrigir as seções e salvar as alterações.
- Alterações ficam registradas e visíveis para o aluno.
- Testes unitários e funcionais validados.
- Interface validada conforme protótipo.
- Critérios de aceitação e feedback do PO revisados e aprovados.
---
### 2️⃣ Controle dos Alunos

DoD:
- Tela/listagem de alunos implementada e exibindo status (em atraso, revisão, finalizado).
- Informações carregadas corretamente do banco de dados.
- Ordenação e filtros funcionam corretamente.
- Testes de integração realizados.
- Documentação do módulo atualizada.
- Nenhum erro no carregamento de dados.

---
### 3️⃣ Envio de TGs

DoD:
- O aluno consegue fazer upload das seções do TG (com validação de formato e tamanho).
- O sistema registra o envio e vincula ao aluno e orientador corretos.
- Mensagem de confirmação ou erro exibida ao usuário.
- Logs e erros tratados.
- Testes de envio realizados com sucesso.
- Feedback positivo do orientador visível após o envio.

---
### 4️⃣ Análise de Feedbacks

DoD:
- O aluno visualiza todos os feedbacks deixados pelo orientador, por seção.
- Feedbacks são atualizados em tempo real após correção.
- Interface clara e validada com usuários.
- Testes unitários e de interface realizados.
- Código revisado e documentado.

---
### 5️⃣ Visualização de Orientadores

DoD:

- Listagem de orientadores implementada e vinculada aos respectivos alunos.
- Dados consistentes com o banco de dados.
- Permissões de acesso validadas (apenas clientes/autorizados visualizam).
- Testes de listagem e performance executados.
- Nenhum erro de exibição ou acesso indevido.

---
### 6️⃣ Feedback do Orientador

DoD:
- Orientador consegue adicionar e editar feedbacks em cada seção.
- Feedbacks ficam visíveis para o aluno imediatamente.
- Histórico de feedbacks mantido.
- Testes de inserção, edição e visualização validados.
- Interface validada conforme protótipo.
- Código revisado e sem erros de lógica.

---
### 7️⃣ Aprovação de Seções

DoD:
- Orientador pode aprovar ou reprovar seções específicas do TG.
- O sistema registra a aprovação e notifica o aluno.
- Status do TG atualizado corretamente.
- Testes de fluxo completo realizados (aprovar → atualizar status → notificar).
- Sem bugs ou inconsistências.

---
### 8️⃣ Visualização de Portfólios

DoD:
- Orientador pode visualizar o portfólio completo do aluno e suas seções.
- Filtros e pesquisa por aluno/seção funcionando.
- Dados carregam rapidamente e sem erros.
- Interface revisada e aprovada.
- Testes de usabilidade e performance realizados.

---
### 9️⃣ Manual de Usuário

DoD:

- Documento criado dentro do projeto (PDF ou página HTML).
- Explica claramente as funcionalidades principais.
- Testado por alguém fora da equipe (usuário-teste).
- Atualizado conforme versão atual do sistema.
- Link acessível dentro do sistema ou README.

---
### 🔟 Manual de Instalação

DoD:

- Manual disponível no repositório (GitHub ou pasta /docs).
- Passos de instalação, configuração e execução testados em ambiente limpo.
- Inclui dependências, versões e comandos.
- Seguido com sucesso por outro membro do time.
- Documentação validada e atualizada com versão final do sistema.

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
