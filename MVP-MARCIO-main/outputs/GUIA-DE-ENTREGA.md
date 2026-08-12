# TextoZen — Guia de entrega

O projeto completo está na pasta principal desta atividade.

## Arquivos importantes

- `README.md`: instalação, execução e testes.
- `ROTEIRO-DA-ATIVIDADE.md`: resposta organizada para as sete partes da apresentação.
- `BANCO-DE-DADOS.sql`: estrutura completa do banco.
- `src/main/java`: código Java do servidor.
- `src/main/resources/templates`: páginas do site.
- `src/main/resources/static/css/style.css`: visual moderno e responsivo.

## Execução rápida no VS Code

1. Abra a pasta do projeto no VS Code.
2. Instale **Extension Pack for Java** e **Maven for Java**.
3. Instale o Maven 3.6.3 ou mais recente caso `mvn -version` não funcione.
4. Abra um terminal PowerShell dentro do VS Code.
5. Defina a chave apenas nessa sessão: `$env:GEMINI_API_KEY="SUA_CHAVE"`.
6. Inicie: `mvn spring-boot:run`.
7. Acesse `http://localhost:8080`.

Nunca publique a chave no GitHub, no JavaScript, em prints ou no material entregue.

## Teste obrigatório

1. Criar uma conta.
2. Sair e entrar novamente.
3. Enviar uma redação de teste com mais de 100 caracteres.
4. Conferir nota e feedback.
5. Confirmar que a redação aparece no histórico.
6. Fechar e reabrir o servidor, confirmando que os dados continuam salvos.
7. Executar `mvn test`.

## Banco

O H2 cria as tabelas automaticamente. Para inspecioná-las, abra `http://localhost:8080/h2-console`, use a URL JDBC `jdbc:h2:file:./data/textozen`, usuário `sa` e senha vazia.
