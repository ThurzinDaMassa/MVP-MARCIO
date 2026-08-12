package br.com.textozen.controller;

import br.com.textozen.model.*;
import br.com.textozen.repository.*;
import br.com.textozen.service.GeminiService;
import br.com.textozen.service.PlanoEstudosPdfService;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppController {
  private final UsuarioRepository usuarios; private final RedacaoRepository redacoes; private final GeminiService gemini; private final PlanoEstudosPdfService pdf;
  public AppController(UsuarioRepository u,RedacaoRepository r,GeminiService g,PlanoEstudosPdfService pdf){usuarios=u;redacoes=r;gemini=g;this.pdf=pdf;}
  @GetMapping("/") String inicio(){return "index";}
  @GetMapping("/painel") String painel(Principal p,Model m){
    Usuario u=usuarios.findByEmailIgnoreCase(p.getName()).orElseThrow(); var lista=redacoes.findByUsuarioEmailOrderByCriadaEmDesc(p.getName());
    m.addAttribute("usuario",u);m.addAttribute("redacoes",lista);m.addAttribute("total",lista.size());
    m.addAttribute("media",lista.stream().filter(x->x.getNota()!=null).mapToInt(Redacao::getNota).average().orElse(0));return "painel";
  }
  @GetMapping("/redacoes/nova") String nova(@RequestParam(required=false) Long base,Principal p,Model m){
    m.addAttribute("iaConfigurada",gemini.estaConfigurada());
    if(base!=null) redacoes.findByIdAndUsuarioEmailIgnoreCase(base,p.getName()).ifPresent(r->{m.addAttribute("tituloBase","Revisão — "+r.getTitulo());m.addAttribute("temaBase",r.getTema());m.addAttribute("textoBase",r.getTexto());});
    return "nova-redacao";
  }
  @GetMapping("/estudos") String estudos(){return "estudos";}
  @GetMapping(value="/estudos/plano.pdf",produces=MediaType.APPLICATION_PDF_VALUE)
  @ResponseBody ResponseEntity<byte[]> planoPdf(Principal p){
    Usuario u=usuarios.findByEmailIgnoreCase(p.getName()).orElseThrow();
    byte[] arquivo=pdf.gerar(u,redacoes.findByUsuarioEmailOrderByCriadaEmDesc(p.getName()));
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=plano-estudos-redacao.pdf").body(arquivo);
  }
  @GetMapping("/planejador") String planejador(){return "planejador";}
  @GetMapping("/perfil") String perfil(Principal p,Model m){
    Usuario u=usuarios.findByEmailIgnoreCase(p.getName()).orElseThrow();var lista=redacoes.findByUsuarioEmailOrderByCriadaEmDesc(p.getName());
    m.addAttribute("usuario",u);m.addAttribute("total",lista.size());m.addAttribute("media",media(lista.stream().map(Redacao::getNota).toList()));m.addAttribute("melhor",lista.stream().map(Redacao::getNota).filter(java.util.Objects::nonNull).max(Integer::compareTo).orElse(0));
    m.addAttribute("m1",media(lista.stream().map(Redacao::getCompetencia1).toList()));m.addAttribute("m2",media(lista.stream().map(Redacao::getCompetencia2).toList()));m.addAttribute("m3",media(lista.stream().map(Redacao::getCompetencia3).toList()));m.addAttribute("m4",media(lista.stream().map(Redacao::getCompetencia4).toList()));m.addAttribute("m5",media(lista.stream().map(Redacao::getCompetencia5).toList()));
    var ordem=new java.util.ArrayList<>(lista);java.util.Collections.reverse(ordem);m.addAttribute("notas",ordem.stream().map(r->String.valueOf(r.getNota()==null?0:r.getNota())).collect(Collectors.joining(",")));m.addAttribute("datas",ordem.stream().map(r->r.getCriadaEm().format(DateTimeFormatter.ofPattern("dd/MM"))).collect(Collectors.joining(",")));return "perfil";
  }
  private int media(java.util.List<Integer> valores){return (int)Math.round(valores.stream().filter(java.util.Objects::nonNull).mapToInt(Integer::intValue).average().orElse(0));}
  @PostMapping("/redacoes") String corrigir(@RequestParam String titulo,@RequestParam String tema,@RequestParam String texto,Principal p,RedirectAttributes ra){
    if(tema.isBlank()||texto.trim().length()<100){ra.addFlashAttribute("erro","Informe o tema e uma redação com pelo menos 100 caracteres.");return "redirect:/redacoes/nova";}
    try {var c=gemini.corrigir(tema,texto);Redacao r=new Redacao();r.setUsuario(usuarios.findByEmailIgnoreCase(p.getName()).orElseThrow());r.setTitulo(titulo.isBlank()?"Redação sem título":titulo.trim());r.setTema(tema.trim());r.setTexto(texto.trim());r.setNota(c.nota());r.setFeedback(c.feedback());r.setCompetencia1(c.notas()[0]);r.setCompetencia2(c.notas()[1]);r.setCompetencia3(c.notas()[2]);r.setCompetencia4(c.notas()[3]);r.setCompetencia5(c.notas()[4]);r.setDetalheCompetencia1(c.detalhes()[0]);r.setDetalheCompetencia2(c.detalhes()[1]);r.setDetalheCompetencia3(c.detalhes()[2]);r.setDetalheCompetencia4(c.detalhes()[3]);r.setDetalheCompetencia5(c.detalhes()[4]);r.setElogios(c.elogios());r.setCorrecoes(c.correcoes());r.setPlanoMelhoria(c.planoMelhoria());redacoes.save(r);return "redirect:/redacoes/"+r.getId();}
    catch(IllegalStateException e){ra.addFlashAttribute("erro",e.getMessage());return "redirect:/redacoes/nova";}
  }
  @PostMapping("/redacoes/dicas") @ResponseBody ResponseEntity<Map<String,Object>> dicas(@RequestParam String tema,@RequestParam String texto){
    if(tema.isBlank()||texto.trim().length()<100) return ResponseEntity.badRequest().body(Map.of("erro","Informe o tema e escreva ao menos 100 caracteres."));
    try {var dicas=gemini.gerarDicas(tema.trim(),texto.trim());return ResponseEntity.ok(Map.of("diagnostico",dicas.diagnostico(),"prioridades",dicas.prioridades(),"sugestoes",dicas.sugestoes()));}
    catch(IllegalStateException e){return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("erro",e.getMessage()));}
  }
  @GetMapping("/redacoes/{id}") String detalhe(@PathVariable Long id,Principal p,Model m){Redacao r=redacoes.findByIdAndUsuarioEmailIgnoreCase(id,p.getName()).orElseThrow();m.addAttribute("redacao",r);return "resultado";}
  @PostMapping("/redacoes/{id}/excluir") String excluir(@PathVariable Long id,Principal p,RedirectAttributes ra){
    Redacao r=redacoes.findByIdAndUsuarioEmailIgnoreCase(id,p.getName()).orElseThrow();redacoes.delete(r);ra.addFlashAttribute("sucesso","Redação excluída do histórico.");return "redirect:/painel";
  }
}
