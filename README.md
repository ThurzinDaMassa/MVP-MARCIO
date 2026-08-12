# TextoZen

Aplicação acadêmica para avaliar redações com IA, feita com Java, Spring Boot, JavaScript, CSS e H2.

## Como abrir no VS Code e executar

1. Instale no VS Code o pacote **Extension Pack for Java** e a extensão **Maven for Java**.
2. Instale o Maven 3.6.3 ou mais recente no Windows (ou use o Maven incluído pela sua IDE) e confirme com `mvn -version`.
3. Abra esta pasta no VS Code.
4. Configure a chave somente no terminal atual:
   - PowerShell: `$env:GEMINI_API_KEY="SUA_CHAVE"`
5. Execute `mvn spring-boot:run` no terminal do VS Code.
6. Abra `http://localhost:8080`.

Sem a chave, a aplicação funciona em **modo de demonstração**, gerando um feedback local simples. Isso permite testar todas as telas.

## Testes

- Teste automático: `mvn test`
- Site: `http://localhost:8080`
- Banco H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/textozen`
  - usuário: `sa`
  - senha: deixe vazia

## Fluxo para demonstrar

Página inicial → Criar conta → Entrar → Nova redação → Analisar com IA → Ver nota e feedback → Consultar histórico.

## Banco de dados

O banco é criado automaticamente na primeira execução pelas entidades `Usuario` e `Redacao`. Os arquivos ficam na pasta `data/` e persistem após fechar o programa.

## Segurança

Nunca coloque a chave do Gemini no JavaScript, no GitHub ou em arquivos enviados ao professor. Use a variável `GEMINI_API_KEY`. As senhas dos usuários são armazenadas com hash BCrypt.

## Deploy no Render

O projeto inclui um `Dockerfile` para executar com Java 21. No Render, crie um **Web Service**, conecte este repositório e selecione o ambiente **Docker**. Se esta pasta estiver dentro de outra pasta no repositório, informe `MVP-MARCIO-main` em **Root Directory**.

Em **Environment**, cadastre os segredos uma única vez:

- `GEMINI_API_KEY`: chave criada no Google AI Studio
- `GEMINI_MODEL`: `gemini-3.6-flash`

Não cadastre a chave no código, no `Dockerfile` nem no JavaScript. Depois de salvar as variáveis, execute um novo deploy. A aplicação usa automaticamente a porta fornecida pela hospedagem.

Importante: o H2 atual grava dados em arquivo local. Em uma instância gratuita do Render, contas e redações podem ser perdidas quando o serviço reiniciar. Para uso real, migre o banco para PostgreSQL persistente.
