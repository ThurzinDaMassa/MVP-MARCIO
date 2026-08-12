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
      String prompt="""
        Atue como avaliador pedagógico especializado na Matriz de Referência da Redação do ENEM. Analise apenas evidências presentes no texto. Esta é uma estimativa educacional, não nota oficial.
        Atribua a cada competência EXATAMENTE um valor entre 0, 40, 80, 120, 160 ou 200. A nota total deve ser a soma das cinco.
        C1: modalidade escrita formal. C2: tema, tipo dissertativo-argumentativo e repertório. C3: projeto de texto e defesa do ponto de vista. C4: coesão e mecanismos linguísticos. C5: intervenção com agente, ação, meio/modo, finalidade, detalhamento e direitos humanos.
        Responda SOMENTE em JSON válido, sem markdown, com este formato exato:
        {"resumo":"diagnóstico geral objetivo","competencias":[{"nota":160,"analise":"evidências, acertos, perdas e como chegar ao próximo nível"},{"nota":160,"analise":"..."},{"nota":160,"analise":"..."},{"nota":160,"analise":"..."},{"nota":160,"analise":"..."}],"elogios":["elogio específico com evidência"],"correcoes":[{"trecho":"trecho exato","correcao":"forma recomendada","explicacao":"regra ou motivo"}],"planoMelhoria":["ação prioritária e prática"]}
        Não invente erros, não reescreva toda a redação e não use elogios vagos. Liste no máximo 10 correções mais importantes. Tema: %s\nRedação:\n%s
        """.formatted(tema, redacao);
      String body=json.writeValueAsString(Map.of("contents",new Object[]{Map.of("parts",new Object[]{Map.of("text",prompt)})},"generationConfig",Map.of("responseMimeType","application/json","temperature",0.25)));
      HttpRequest req=HttpRequest.newBuilder(URI.create("https://generativelanguage.googleapis.com/v1beta/models/"+model+":generateContent"))
        .timeout(Duration.ofSeconds(60)).header("Content-Type","application/json").header("x-goog-api-key",apiKey).POST(HttpRequest.BodyPublishers.ofString(body)).build();
      HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
      HttpResponse<String> res=http.send(req,HttpResponse.BodyHandlers.ofString());
      if(res.statusCode()/100!=2) throw new IllegalStateException("Gemini respondeu com código "+res.statusCode());
      String raw=json.readTree(res.body()).at("/candidates/0/content/parts/0/text").asText();
      JsonNode parsed=json.readTree(raw);
      JsonNode cs=parsed.path("competencias");
      int[] n=new int[5]; String[] d=new String[5];
      for(int i=0;i<5;i++){n[i]=normalizarNota(cs.path(i).path("nota").asInt());d[i]=cs.path(i).path("analise").asText("Análise não disponível.");}
      return new Correcao(n[0]+n[1]+n[2]+n[3]+n[4],parsed.path("resumo").asText(),n,d,
        juntarLista(parsed.path("elogios")),juntarCorrecoes(parsed.path("correcoes")),juntarLista(parsed.path("planoMelhoria")));
    } catch(Exception e){throw new IllegalStateException("Não foi possível corrigir agora. Confira a chave e tente novamente.",e);}
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
  public record Correcao(int nota,String feedback,int[] notas,String[] detalhes,String elogios,String correcoes,String planoMelhoria){}
}
