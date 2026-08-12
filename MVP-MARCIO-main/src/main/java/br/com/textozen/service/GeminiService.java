package br.com.textozen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {
  private final ObjectMapper json;
  @Value("${gemini.api-key:}") private String apiKey;
  @Value("${gemini.model:gemini-3.5-flash}") private String model;
  public GeminiService(ObjectMapper json){this.json=json;}

  public boolean estaConfigurada(){return apiKey!=null && !apiKey.isBlank();}

  public Correcao corrigir(String tema, String redacao){
    if(apiKey==null || apiKey.isBlank()) return avaliacaoDemonstracao(redacao);
    try {
      JsonNode avaliador=solicitarJson(promptCorrecao(tema,redacao,"AVALIADOR 1: aplique a matriz com precisão técnica e sem benevolência."),0.05);
      JsonNode auditor=solicitarJson(promptCorrecao(tema,redacao,"AUDITOR ADVERSARIAL: procure ativamente falhas que o primeiro avaliador poderia ignorar. Não presuma qualidade pelo estilo fluente."),0.05);
      int[] n=new int[5]; String[] d=new String[5];
      for(int i=0;i<5;i++){
        JsonNode c1=avaliador.path("competencias").path(i),c2=auditor.path("competencias").path(i);
        int nota1=notaValidada(c1),nota2=notaValidada(c2);n[i]=Math.min(nota1,nota2);
        d[i]=(nota2<=nota1?c2:c1).path("analise").asText("Análise não disponível.");
      }
      String feedback="Resultado conservador de duas correções independentes. "+auditor.path("resumo").asText(avaliador.path("resumo").asText());
      return new Correcao(java.util.Arrays.stream(n).sum(),feedback,n,d,juntarLista(avaliador.path("elogios")),juntarCorrecoes(auditor.path("correcoes")),juntarLista(auditor.path("planoMelhoria")));
    } catch(Exception e){throw new IllegalStateException("Não foi possível corrigir agora. Confira a chave e tente novamente.",e);}
  }
  private String promptCorrecao(String tema,String redacao,String papel){return """
    %s
    Use exclusivamente a Cartilha do Participante do ENEM 2025 e a Matriz de Referência do Inep. Isto é uma estimativa pedagógica, não uma nota oficial.

    REGRA CENTRAL: não avalie a intenção do autor nem a aparência de texto sofisticado. Avalie somente o que foi efetivamente realizado. Uma redação fluente, pronta ou produzida por IA ainda deve perder pontos quando for genérica, previsível, superficial, decorativa ou pouco autoral. Não aceite a nota que o autor diz desejar.

    Primeiro verifique fuga ao tema, não atendimento ao tipo dissertativo-argumentativo e sinais de situação de nota zero. Depois avalie separadamente as cinco competências. Use EXATAMENTE 0, 40, 80, 120, 160 ou 200.

    C1 - Norma-padrão: conte desvios, observe recorrência, construção sintática e precisão vocabular. Vocabulário rebuscado não compensa falhas.
    C2 - Tema e repertório: exija abordagem completa, tipo textual e repertório sociocultural produtivo. Citação solta, coringa, falsa, decorativa ou apenas mencionada não sustenta 200.
    C3 - Projeto e argumentação: exija tese delimitada, seleção, organização, progressão e desenvolvimento real. Exemplos genéricos, enumeração de causas e afirmações sem explicação limitam a nota.
    C4 - Coesão: avalie relações lógicas dentro e entre parágrafos. Quantidade de conectivos não significa qualidade; repetição, uso mecânico, ambiguidade ou salto lógico devem reduzir a nota.
    C5 - Intervenção: confira agente, ação, meio/modo, finalidade e detalhamento, vínculo com os argumentos e respeito aos direitos humanos. Elemento apenas implícito, vago ou inexequível não conta como plenamente desenvolvido.

    ÂNCORAS DE SEVERIDADE:
    - 120 indica domínio mediano, com desenvolvimento previsível, lacunas ou inadequações relevantes.
    - 160 indica bom domínio, mas ainda existe ao menos uma limitação perceptível.
    - 200 é nível excepcional, não o nível padrão de um texto bom. Só atribua 200 quando não houver limitação relevante naquela competência e houver evidência textual específica de domínio pleno.
    - Na dúvida entre 160 e 200, atribua 160. Na dúvida entre quaisquer níveis, escolha o menor sustentado.
    - Nota total 1000 deve ser raríssima. Ela exige excelência simultânea e comprovada nas cinco competências; não use 1000 como elogio.

    Para cada competência informe também "prova200" e "impedimento200". Se a nota for 200, prova200 deve citar evidência concreta e impedimento200 deve ser vazio. Se existir qualquer impedimento relevante, a nota máxima daquela competência é 160. Explique toda perda e o requisito que faltou para o nível seguinte.

    Responda SOMENTE em JSON válido, sem markdown, neste formato exato:
    {"resumo":"diagnóstico objetivo","competencias":[{"nota":160,"analise":"evidências, perdas e próximo nível","prova200":"","impedimento200":"limitação concreta"},{"nota":160,"analise":"...","prova200":"","impedimento200":"..."},{"nota":160,"analise":"...","prova200":"","impedimento200":"..."},{"nota":160,"analise":"...","prova200":"","impedimento200":"..."},{"nota":160,"analise":"...","prova200":"","impedimento200":"..."}],"elogios":["acerto específico com evidência"],"correcoes":[{"trecho":"trecho exato","correcao":"forma recomendada","explicacao":"regra ou motivo"}],"planoMelhoria":["ação prioritária e prática"]}
    Não invente erros ou qualidades e não reescreva a redação inteira. Liste no máximo 10 correções prioritárias.

    Tema: %s
    Redação:
    %s
    """.formatted(papel,tema,redacao);}
  private int notaValidada(JsonNode competencia){
    int nota=normalizarNota(competencia.path("nota").asInt());
    if(nota==200&&(competencia.path("prova200").asText().isBlank()||!competencia.path("impedimento200").asText().isBlank()))return 160;
    return nota;
  }
  public Dicas gerarDicas(String tema,String redacao){
    if(apiKey==null || apiKey.isBlank()) return new Dicas("O Gemini não está configurado no servidor.",new String[]{"Defina GEMINI_API_KEY no ambiente do deploy."},new String[]{"Depois, reinicie o serviço e solicite novas dicas."});
    try{
      String prompt="""
        Atue como orientador de Redação ENEM rigoroso e direto. Analise o rascunho sem atribuir nota e sem reescrever a redação inteira. Mostre como o estudante pode melhorar o próprio texto.
        Considere as cinco competências do Inep. Aponte no máximo 3 prioridades que realmente limitam o desempenho e até 5 sugestões concretas, citando trechos curtos quando necessário. Não invente erros nem repertórios.
        Responda SOMENTE em JSON válido neste formato: {"diagnostico":"uma síntese curta","prioridades":["problema e motivo"],"sugestoes":["ação específica de melhoria"]}
        Tema: %s
        Rascunho:
        %s
        """.formatted(tema,redacao);
      JsonNode parsed=solicitarJson(prompt,0.2);
      return new Dicas(parsed.path("diagnostico").asText("Revise o texto com atenção."),array(parsed.path("prioridades")),array(parsed.path("sugestoes")));
    }catch(Exception e){throw new IllegalStateException("Não foi possível gerar dicas agora. Confira a configuração do Gemini e tente novamente.",e);}
  }
  private JsonNode solicitarJson(String prompt,double temperatura)throws Exception{
    String body=json.writeValueAsString(Map.of("contents",new Object[]{Map.of("parts",new Object[]{Map.of("text",prompt)})},"generationConfig",Map.of("responseMimeType","application/json","temperature",temperatura)));
    HttpRequest req=HttpRequest.newBuilder(URI.create("https://generativelanguage.googleapis.com/v1beta/models/"+model+":generateContent")).timeout(Duration.ofSeconds(60)).header("Content-Type","application/json").header("x-goog-api-key",apiKey).POST(HttpRequest.BodyPublishers.ofString(body)).build();
    HttpResponse<String> res=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build().send(req,HttpResponse.BodyHandlers.ofString());
    if(res.statusCode()/100!=2)throw new IllegalStateException("Gemini respondeu com código "+res.statusCode());
    return json.readTree(json.readTree(res.body()).at("/candidates/0/content/parts/0/text").asText());
  }
  private Correcao avaliacaoDemonstracao(String texto){
    int palavras=texto.trim().isEmpty()?0:texto.trim().split("\\s+").length;
    int nota=Math.min(900,Math.max(400,450+palavras));
    int nivel=nota>=800?160:120;int[] ns={nivel,nivel,nivel,nivel,nivel};String[] ds={"Demonstração: configure a IA para uma análise baseada em evidências.","Demonstração: análise temática indisponível.","Demonstração: análise argumentativa indisponível.","Demonstração: análise de coesão indisponível.","Demonstração: análise da intervenção indisponível."};
    return new Correcao(nivel*5,"Modo de demonstração. Configure GEMINI_API_KEY para receber a avaliação completa.",ns,ds,"Seu texto foi salvo com sucesso.","Nenhuma correção real foi produzida no modo de demonstração.","Conecte o Gemini e envie uma nova redação para gerar um plano personalizado.");
  }
  private int normalizarNota(int valor){int[] niveis={0,40,80,120,160,200};int melhor=0;for(int n:niveis)if(Math.abs(valor-n)<Math.abs(valor-melhor))melhor=n;return melhor;}
  private String juntarLista(JsonNode lista){StringBuilder s=new StringBuilder();for(JsonNode item:lista)if(!item.asText().isBlank())s.append("• ").append(item.asText()).append("\n");return s.toString().trim();}
  private String juntarCorrecoes(JsonNode lista){StringBuilder s=new StringBuilder();for(JsonNode item:lista){s.append("“").append(item.path("trecho").asText()).append("” → “").append(item.path("correcao").asText()).append("” — ").append(item.path("explicacao").asText()).append("\n");}return s.toString().trim();}
  private String[] array(JsonNode lista){java.util.List<String> itens=new java.util.ArrayList<>();for(JsonNode item:lista)if(!item.asText().isBlank())itens.add(item.asText());return itens.toArray(String[]::new);}
  public record Correcao(int nota,String feedback,int[] notas,String[] detalhes,String elogios,String correcoes,String planoMelhoria){}
  public record Dicas(String diagnostico,String[] prioridades,String[] sugestoes){}
}
