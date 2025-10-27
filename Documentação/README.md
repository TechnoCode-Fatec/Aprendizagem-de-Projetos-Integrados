# Repositório Referente à Documentação da Equipe

# 🟢 Definition of Ready (DoR) — Definição para Iniciar

A finalidade do DoR, é estabelecer um conjunto de critérios que um item de trabalho deve atender antes de poder ser iniciado pela equipe, garantindo que tarefas e histórias de usuário estejam claras, detalhadas e prontas para o desenvolvimento, evitando que a equipe comece um trabalho incompleto e comprometa o cronograma. 

# Critérios para uma User Story ser iniciada

## 1️⃣ Clareza e Compreensão
A história de usuário deve estar escrita de forma clara, seguindo o formato:

> **Como** [tipo de usuário], **quero** [ação ou funcionalidade], **para** [objetivo ou benefício].

Assim, o time entende completamente **o que precisa ser feito** e **por que** aquilo é importante.

## 2️⃣ Critérios de Aceitação Definidos
Devem existir **critérios objetivos** que indiquem quando a história será considerada concluída.

📌 *Exemplo:*  
> “O aluno consegue enviar um TG e recebe uma mensagem de sucesso.”

Esses critérios servem como base para testes e validação da entrega.

---

## 3️⃣ Valor de Negócio Identificado
É necessário deixar claro **por que essa funcionalidade é importante** e **o que ela agrega** ao sistema.  
Exemplos de valor de negócio:
- Melhora o controle do fluxo de TCCs;  
- Simplifica a comunicação entre orientador e aluno;  
- Garante rastreabilidade e organização do processo.

---

## 4️⃣ Esforço Estimado
Assim que a tarefa for definida, o time deve **avaliar sua complexidade e esforço**, utilizando métodos como:

- **Planning Poker**  
- **Estimativas em horas ou story points**

Isso permite um melhor entendimento da dificuldade e viabilidade de entrega no sprint.

---

## 5️⃣ Modelagem e Dependências Resolvidas
Antes do desenvolvimento começar:
- As **entidades envolvidas** (classes, tabelas, relações) devem estar **mapeadas e revisadas**;  
- **Dependências técnicas** (como conexões com banco de dados, autenticação, bibliotecas externas etc.) devem estar resolvidas ou documentadas.


# DoR Individual por User Story
## 1. Visualização e correção de seções
### Critérios:
- Orientador pode visualizar lista de seções por aluno
- Pode adicionar comentários/correções em cada seção
- Status da seção é atualizado após correção
  
### UI/UX: 
- Rascunho da tela de visualização e correção

### Regras:
- Apenas orientadores acessam seções de seus alunos
- Histórico de alterações deve ser mantido

Dados Necessários: ID do aluno, seções do TG, comentários

---
## 2. Controle dos Alunos
### Critérios:
- Lista de alunos com status (atraso, revisão, finalizado)
- Filtros por status
- Indicador visual de status (ex: cores)
  
### UI/UX: 
- Wireframe da dashboard do orientador

### Regras:
- Status é atualizado com base em prazos
  
Dados: Alunos, prazos, status de seções

---
## 3. Envio de TGs
### Critérios:
- Aluno pode fazer upload de arquivos por seção
- Sistema valida formato/tamanho do arquivo
- Aluno recebe confirmação de envio
  
### UI/UX: 
- Tela de upload com drag-and-drop ou seletor de arquivos

### Regras:
- Apenas seções não finalizadas podem receber envios

### Dados: 
- Arquivo, data de envio, ID da seção

---
## 4. Análise de Feedbacks
### Critérios:
- Aluno visualiza feedbacks por seção
- Pode marcar feedback como “corrigido”
- Notificação para o orientador quando correção é feita
  
### UI/UX: 
- Tela de feedbacks com lista e ações

### Regras:
- Feedback permanece visível até a seção ser aprovada

### Dados: 
- Comentários, status de correção

---
## 5. Visualização de Orientadores
### Critérios:
- Cliente visualiza lista de orientadores
- Pode expandir para ver alunos de cada orientador
  
### UI/UX: 
- Layout de lista/detalhes

### Regras:
- Apenas o dono do projeto tem acesso

### Dados: 
- Orientadores, alunos vinculados

---
## 6. Feedback do Orientador
### Critérios:
- Orientador adiciona/comenta em cada seção
- Feedbacks ficam visíveis para o aluno
- Possibilidade de editar feedback recente

### UI/UX: 
- Componente de comentários na tela de correção

### Regras:
- Feedback não pode ser excluído, apenas editado em tempo limite
  
### Dados: 
- Texto do feedback, timestamp, ID do orientador

---
## 7. Aprovação de Seções
### Critérios:
- Botão “Aprovar Seção” na tela de correção
- Status da seção muda para “Aprovado”
- Aluno é notificado
  
### UI/UX: 
- Botão com confirmação

### Regras:
- Apenas orientador responsável pode aprovar
  
### Dados: 
- Status da seção, data de aprovação

---
## 8. Visualização de Portfólios
### Critérios:
- Orientador acessa visão geral do portfólio
- Pode navegar entre seções
- Visualização separada por seção ou consolidada
  
### UI/UX: 
- Página de portfólio com abas/seções

### Regras:
- Portfólio só é visível se pelo menos uma seção foi enviada

### Dados: 
- Seções, conteúdos, status

---
## 9. Manual de Usuário
### Critérios:
- Acesso via menu do sistema
- Conteúdo cobrindo funcionalidades principais
- Interface responsiva e de fácil leitura

### UI/UX: 
- Página estática ou com navegação interna

### Regras:
- Conteúdo deve ser mantido atualizado com as versões

### Dados: 
- Textos, imagens (se necessário)

---

## 10. Manual de Instalação
### Critérios:
- Documento disponível no repositório (ex: README.md)
- Passos claros para setup local e deploy
- Lista de dependências e configurações

### UI/UX: 
— (não se aplica)

### Regras:
- Deve ser compatível com a versão atual do código

### Dados: 
- Comandos, configurações, ambientes

---

# 🔵 Definition of Done (DoD) — Definição de Feito
A finalidade do DoD, é definir de forma formal e compartilhada os critérios de uma tarefa, user story ou incremento que deve ser atendido para ser considerada finalizada.

---

## Critérios para uma User Story ser finalizada

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

# DoD Individual por User Story

### 1️⃣ Visualização e correção de seções

#### DoD:
- O orientador consegue visualizar todas as seções dos TGs dos alunos atribuídos.
- É possível editar/corrigir as seções e salvar as alterações.
- Alterações ficam registradas e visíveis para o aluno.
- Testes unitários e funcionais validados.
- Interface validada conforme protótipo.
- Critérios de aceitação e feedback do PO revisados e aprovados.
---
### 2️⃣ Controle dos Alunos

#### DoD:
- Tela/listagem de alunos implementada e exibindo status (em atraso, revisão, finalizado).
- Informações carregadas corretamente do banco de dados.
- Ordenação e filtros funcionam corretamente.
- Testes de integração realizados.
- Documentação do módulo atualizada.
- Nenhum erro no carregamento de dados.

---
### 3️⃣ Envio de TGs

#### DoD:
- O aluno consegue fazer upload das seções do TG (com validação de formato e tamanho).
- O sistema registra o envio e vincula ao aluno e orientador corretos.
- Mensagem de confirmação ou erro exibida ao usuário.
- Logs e erros tratados.
- Testes de envio realizados com sucesso.
- Feedback positivo do orientador visível após o envio.

---
### 4️⃣ Análise de Feedbacks

#### DoD:
- O aluno visualiza todos os feedbacks deixados pelo orientador, por seção.
- Feedbacks são atualizados em tempo real após correção.
- Interface clara e validada com usuários.
- Testes unitários e de interface realizados.
- Código revisado e documentado.

---
### 5️⃣ Visualização de Orientadores

#### DoD:

- Listagem de orientadores implementada e vinculada aos respectivos alunos.
- Dados consistentes com o banco de dados.
- Permissões de acesso validadas (apenas clientes/autorizados visualizam).
- Testes de listagem e performance executados.
- Nenhum erro de exibição ou acesso indevido.

---
### 6️⃣ Feedback do Orientador

#### DoD:
- Orientador consegue adicionar e editar feedbacks em cada seção.
- Feedbacks ficam visíveis para o aluno imediatamente.
- Histórico de feedbacks mantido.
- Testes de inserção, edição e visualização validados.
- Interface validada conforme protótipo.
- Código revisado e sem erros de lógica.

---
### 7️⃣ Aprovação de Seções

#### DoD:
- Orientador pode aprovar ou reprovar seções específicas do TG.
- O sistema registra a aprovação e notifica o aluno.
- Status do TG atualizado corretamente.
- Testes de fluxo completo realizados (aprovar → atualizar status → notificar).
- Sem bugs ou inconsistências.

---
### 8️⃣ Visualização de Portfólios

#### DoD:
- Orientador pode visualizar o portfólio completo do aluno e suas seções.
- Filtros e pesquisa por aluno/seção funcionando.
- Dados carregam rapidamente e sem erros.
- Interface revisada e aprovada.
- Testes de usabilidade e performance realizados.

---
### 9️⃣ Manual de Usuário

#### DoD:

- Documento criado dentro do projeto (PDF ou página HTML).
- Explica claramente as funcionalidades principais.
- Testado por alguém fora da equipe (usuário-teste).
- Atualizado conforme versão atual do sistema.
- Link acessível dentro do sistema ou README.

---
### 🔟 Manual de Instalação

#### DoD:

- Manual disponível no repositório (GitHub ou pasta /docs).
- Passos de instalação, configuração e execução testados em ambiente limpo.
- Inclui dependências, versões e comandos.
- Seguido com sucesso por outro membro do time.
- Documentação validada e atualizada com versão final do sistema.

--- 

# Diagrama de Entidade-Relacionamento (DER)

<img width="75%" alt="imagem (4)" src="https://github.com/user-attachments/assets/c9857d51-5c0a-44e9-8abf-5ad490e06596"/>


---


# Critérios de Permanência da Equipe:
## Assiduidade

  - Cada membro deve comparecer a pelo menos ***80% das reuniões presenciais ou virtuais mensais.***
    
  - Ausências devem ser comunicadas com no mínimo ***12h de antecedência.***
    
  - O membro ausente deve se atualizar com o time em até ***48h após a reunião.***

  - ***Mais de 3 faltas injustificadas consecutivas ou 5 faltas no semestre resultam em avaliação da permanência.***
  
## Cordialidade

  - Zero tolerância para atitudes ***discriminatórias, ofensivas ou desrespeitosas.***
    
  - Conflitos devem ser resolvidos em conversa mediada pela equipe antes de qualquer medida maior.
    
  - ***Registro: se houver 2 ocorrências formais de desrespeito (relatadas pela equipe), será necessária a avaliação coletiva.***

## Cooperatividade

  - Espera-se que cada membro contribua em pelo menos ***70%*** das discussões de grupo com ideias, revisões ou apoio.

  - Cada integrante deve prestar auxílio em no mínimo ***2 tarefas de colegas por ciclo de entrega (mesmo que seja revisão, teste ou feedback).***

  - ***Recusa frequente em colaborar será registrada — 3 registros no semestre levam à reavaliação do papel na equipe.***
  
## Compromisso com os Prazos de Entrega

  - Cumprimento de ***90%*** dos prazos individuais estipulados pela equipe.

  - Em caso de imprevisto, o aviso deve ser dado com pelo menos ***24h de antecedência.***

  - ***Mais de 2 atrasos sem justificativa por ciclo de projeto acarretam redistribuição das responsabilidades e registro de ocorrência.***

## Responsabilidade e Qualidade

  - Cada entrega deve passar por revisão cruzada (ao menos 1 colega revisa).

  - Se a entrega for reprovada em revisão por descuido evidente (erros básicos, falta de padrão), conta como ***1 ocorrência.***

  - ***3 ocorrências no semestre geram necessidade de plano de melhoria individual.***

## Proatividade

  - Cada membro deve trazer ao menos ***1 sugestão de melhoria*** ou ideia por mês (seja técnica, organizacional ou estética).

  - Participar de pelo menos ***1 ação voluntária de apoio*** (ajuda fora da própria tarefa, proposta de solução, antecipação de problema) a cada ciclo.

  - ***Atitude passiva recorrente será registrada — 2 registros seguidos já levam à conversa de alinhamento.***
