# Roteiro de apresentação — TextoZen

> Substituam os campos entre colchetes pelos dados reais da equipe. Não afirmem que uma funcionalidade está concluída antes de testá-la.

## Parte 1 — Identificação

- **Nome:** TextoZen — Correção Inteligente de Redações.
- **Integrantes:** [nomes da equipe].
- **Objetivo:** ajudar estudantes a melhorar a escrita por meio de correções rápidas, educativas e organizadas.
- **Problema:** o retorno de uma correção manual pode demorar, e muitos alunos não sabem exatamente onde precisam melhorar.
- **Público-alvo:** estudantes do Ensino Médio, vestibulandos e professores.

## Parte 2 — Demonstração

Ordem recomendada, com aproximadamente cinco minutos:

1. Mostrar a página inicial e explicar a proposta.
2. Criar uma conta de demonstração.
3. Entrar e apresentar o painel vazio.
4. Enviar uma redação com título, tema e texto.
5. Mostrar a nota e o feedback gerados.
6. Voltar ao painel e comprovar que a redação ficou salva no histórico.
7. Abrir o console H2 para demonstrar as tabelas `USUARIOS` e `REDACOES`.

## Parte 3 — Arquitetura

- **Java 21:** regras da aplicação, segurança, conexão com banco e comunicação com a IA.
- **Spring Boot 3.5:** framework que inicializa o servidor web e organiza as camadas.
- **HTML + Thymeleaf:** estrutura das páginas e inserção dos dados vindos do Java.
- **CSS:** identidade visual responsiva e moderna.
- **JavaScript:** contador de palavras e estado visual durante a correção.
- **H2:** banco de dados gratuito, local e persistente, adequado à demonstração.
- **Gemini API:** serviço de IA chamado pelo servidor. A chave não aparece no navegador.

Organização das pastas:

- `controller`: recebe ações das páginas e escolhe as respostas.
- `service`: concentra a integração com o Gemini.
- `model`: descreve usuários e redações.
- `repository`: salva e consulta os dados.
- `config`: configura login e segurança.
- `templates`: páginas HTML.
- `static/css` e `static/js`: aparência e interações.

## Parte 4 — Funcionalidades

| Funcionalidade | Status sugerido | Observações para a apresentação |
|---|---|---|
| Página inicial responsiva | Concluído | Apresenta proposta e chamada para cadastro |
| Cadastro de usuário | Em testes | Valida dados e impede e-mail repetido |
| Login e logout | Em testes | Senhas protegidas com BCrypt |
| Envio de redação | Em testes | Valida título, tema e tamanho mínimo |
| Correção com Gemini | Em testes | Depende de chave e limite gratuito da API |
| Nota e feedback | Em testes | Retorno em formato estruturado |
| Histórico de redações | Em testes | Cada usuário acessa somente seus textos |
| Recuperação de senha | Não iniciado | Próxima etapa |
| Painel do professor | Não iniciado | Possível evolução futura |

Após realizarem o roteiro de testes, mudem para **Concluído** tudo que funcionar sem erros.

## Parte 5 — Dificuldades

- **Maior dificuldade técnica:** integrar páginas, autenticação, banco e IA sem expor a chave do Gemini.
- **Solução:** separar a aplicação em camadas e realizar a chamada à IA somente no servidor Java, usando variável de ambiente.
- **Problema pendente possível:** a faixa gratuita da IA possui limites e depende de internet; por isso existe um modo local de demonstração para testar o restante do fluxo.

## Parte 6 — Próximas etapas

| Tarefa | Responsável | Prazo |
|---|---|---|
| Executar testes completos e registrar erros | [integrante] | [data] |
| Melhorar o prompt conforme critérios da professora | [integrante] | [data] |
| Criar recuperação de senha | [integrante] | [data] |
| Criar filtros e gráfico de evolução | [integrante] | [data] |
| Preparar apresentação e redação de demonstração | Todos | [data] |

## Parte 7 — Organização da equipe

Cada pessoa deve preencher e falar sua própria linha:

| Integrante | Participação | Arquivos/módulos | Funcionalidades feitas | Próxima responsabilidade |
|---|---|---|---|---|
| [nome] | [descrição real] | [arquivos] | [funções] | [próxima tarefa] |
| [nome] | [descrição real] | [arquivos] | [funções] | [próxima tarefa] |

## Perguntas que o professor pode fazer

- **Por que H2?** É gratuito, simples, persiste os dados e não exige instalar um servidor de banco para a atividade.
- **Por que Spring Boot?** Reduz configuração e oferece módulos consolidados para páginas, banco e segurança.
- **Onde a senha é armazenada?** No banco, como hash BCrypt; nunca em texto legível.
- **Onde fica a chave do Gemini?** Em uma variável de ambiente no computador que executa o servidor.
- **O que acontece sem internet?** Login, banco e telas continuam testáveis; a correção real pela API precisa de internet.
- **A nota da IA substitui um professor?** Não. É uma orientação assistida e deve ser apresentada como apoio educacional.
