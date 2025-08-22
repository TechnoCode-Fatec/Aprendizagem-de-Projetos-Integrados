# Repositório Referente à Documentação da Equipe
## Critérios de Permanência da Equipe:
### Assiduidade
  - Todos os membros da equipe devem participar ativamente nas reuniões presenciais ou virtuais da equipe.
  - Caso não seja possível comparecer, o membro deve avisar com antecedência e buscar atualização do que foi discutido.
  - Ausências frequentes ou sem justificativa serão avaliadas pela equipe e podem comprometer a permanência no projeto.
  
### Cordialidade
  - É fundamental manter o respeito e a empatia entre todos os membros da equipe.
  - As discussões devem ser construtivas, sempre com foco no projeto e nunca em ataques pessoais.
  - Não serão toleradas atitudes de desrespeito, preconceito ou qualquer forma de discriminação.

### Cooperatividade
  - Espera-se que cada membro auxilie os colegas sempre que possível, compartilhando conhecimento e contribuindo para a resolução de problemas
  - A colaboração deve se sobrepor ao individualismo: o sucesso do grupo é prioridade sobre conquistas pessoais.
  - Todos devem estar abertos a feedbacks e dispostos a adaptar-se às necessidades da equipe.
  
### Compromisso com os Prazos de Entrega
  - Cada tarefa atribuída deve ser cumprida dentro do prazo estabelecido.
  - Caso surja algum imprevisto, o membro deve comunicar o grupo de imediato para reorganização das responsabilidades.
  - Atrasos recorrentes sem justificativa podem gerar reavaliação do papel do membro na equipe.

### Responsabilidade e Qualidade
  - As entregas devem ser feitas com atenção e cuidado, visando qualidade e não apenas quantidade.
  - Cada membro é responsável pelo impacto do seu trabalho no resultado final.
  - A revisão mútua entre colegas é incentivada para garantir a excelência do projeto.

### Proatividade
  - Membros devem buscar antecipar problemas e propor soluções, em vez de esperar que terceiros os apontem.
  - Sugestões de melhorias e ideias inovadoras são sempre bem-vindas e valorizadas.
  - Atitude passiva ou falta de engajamento pode comprometer o desenvolvimento do projeto.

## Estratégia de Branches 
### Branches Principais
  - **`main`** - Sendo a Branch principal, representando a versão final do Projeto.
  - **`develop`** - Sendo a Branch de integração contínua, onde todas as atualizações são consolidadas e validadas antes de serem unificadas ao **`main`**

### Branches de Sprint
  - Cada Sprint terá sua respectiva Branch criada a partir do **`main`**
  - Essa Branch é responsável pela validação de todas as entregas da Sprint em questão.

### Branches de Feature/Tarefa
  - Cada item do Backlog terá sua própria Branch criada a partir da Branch da Sprint.
  - Nomeclatura padrão:
      - **`feature/nome-da-feature`**
      - **`bugfix/nome-do-bug`**
  - Após concluída, a feature é mergeada de volta na branch da Sprint via Pull Request.

## Regras:
  - Nunca Commitar diretamente no **`main`** ou no **`develop`**
  - Branches de feature/bugfix são temporárias e devem ser deletadas após o merge.
  - Commits pequenos e descritivos para facilitar o histórico.
  - Pull Requests precisam de revisão por pelo menos 1 membro da equipe.
  
## Imagem de Apoio para Padronização dos Commits
![Padronização de Commits](PadronizacaoCommits.png)
