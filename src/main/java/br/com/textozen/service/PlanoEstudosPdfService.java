package br.com.textozen.service;

import br.com.textozen.model.Redacao;
import br.com.textozen.model.Usuario;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Color;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

@Service
public class PlanoEstudosPdfService {
  private static final Color VERDE = new Color(8, 120, 98);
  private static final Color VERDE_CLARO = new Color(234, 248, 243);
  private static final Color TEXTO = new Color(24, 43, 42);

  public byte[] gerar(Usuario usuario, List<Redacao> redacoes) {
    try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
      Document documento = new Document(PageSize.A4, 48, 48, 38, 34);
      PdfWriter.getInstance(documento, saida);
      documento.open();

      Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, TEXTO);
      Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, VERDE);
      Font corpo = FontFactory.getFont(FontFactory.HELVETICA, 10.5f, TEXTO);
      Font pequeno = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, Color.DARK_GRAY);

      Paragraph marca = new Paragraph("COLA DE REDAÇÃO  /  PLANO PERSONALIZADO", subtitulo);
      marca.setSpacingAfter(12);
      documento.add(marca);
      documento.add(new Paragraph("Plano de estudos para Redação ENEM", titulo));
      Paragraph meta = new Paragraph("Estudante: " + usuario.getNome() + "   •   Gerado em " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), pequeno);
      meta.setSpacingBefore(7); meta.setSpacingAfter(14); documento.add(meta);

      int[] medias = medias(redacoes);
      int foco = menorCompetencia(medias);
      int mediaGeral = (int)Math.round(redacoes.stream().filter(r -> r.getNota() != null).mapToInt(Redacao::getNota).average().orElse(0));
      PdfPTable resumo = new PdfPTable(new float[]{1, 1, 2}); resumo.setWidthPercentage(100); resumo.setSpacingAfter(14);
      card(resumo, "REDAÇÕES", String.valueOf(redacoes.size()), corpo, subtitulo);
      card(resumo, "MÉDIA ATUAL", mediaGeral + " / 1000", corpo, subtitulo);
      card(resumo, "FOCO PRIORITÁRIO", "C" + foco + " - " + nomes()[foco - 1], corpo, subtitulo);
      documento.add(resumo);

      documento.add(secao("Estratégia de 4 semanas", subtitulo));
      String[][] semanas = plano(foco);
      for (int i = 0; i < semanas.length; i++) {
        PdfPTable bloco = new PdfPTable(new float[]{0.8f, 3.2f}); bloco.setWidthPercentage(100); bloco.setSpacingAfter(9);
        PdfPCell numero = celula("SEMANA\n" + (i + 1), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE), VERDE);
        numero.setHorizontalAlignment(Element.ALIGN_CENTER); numero.setVerticalAlignment(Element.ALIGN_MIDDLE);
        PdfPCell tarefa = celula(semanas[i][0] + "\n" + semanas[i][1], corpo, i % 2 == 0 ? VERDE_CLARO : Color.WHITE);
        bloco.addCell(numero); bloco.addCell(tarefa); documento.add(bloco);
      }

      documento.add(secao("Checklist de uma redação forte", subtitulo));
      String[] checklist = {"[  ] Tese clara e dois eixos de argumentação", "[  ] Repertório confiável, pertinente e ligado ao argumento", "[  ] Desenvolvimento com causa, consequência e evidência", "[  ] Conectivos variados sem repetição artificial", "[  ] Intervenção com agente, ação, meio, finalidade e detalhamento", "[  ] Revisão final de pontuação, concordância e ortografia"};
      for (String item : checklist) { Paragraph p = new Paragraph(item, corpo); p.setSpacingAfter(5); documento.add(p); }

      documento.add(secao("Meta semanal", subtitulo));
      Paragraph metaFinal = new Paragraph("Produza 1 redação completa, faça 2 exercícios da competência prioritária e reescreva o parágrafo que mais perdeu pontos. Compare a nova avaliação com a anterior.", corpo);
      metaFinal.setLeading(16); documento.add(metaFinal);
      Paragraph aviso = new Paragraph("Plano pedagógico baseado no histórico da plataforma. As notas são estimativas assistidas por IA e não substituem a correção oficial do Inep.", pequeno);
      aviso.setSpacingBefore(10); documento.add(aviso);

      documento.close();
      return saida.toByteArray();
    } catch (Exception e) { throw new IllegalStateException("Não foi possível gerar o plano em PDF.", e); }
  }

  private int[] medias(List<Redacao> rs) {
    int[] total = new int[5], quantidade = new int[5];
    for (Redacao r : rs) {
      Integer[] valores = {r.getCompetencia1(), r.getCompetencia2(), r.getCompetencia3(), r.getCompetencia4(), r.getCompetencia5()};
      for (int i = 0; i < 5; i++) if (valores[i] != null) { total[i] += valores[i]; quantidade[i]++; }
    }
    for (int i = 0; i < 5; i++) total[i] = quantidade[i] == 0 ? 0 : Math.round((float)total[i] / quantidade[i]);
    return total;
  }
  private int menorCompetencia(int[] medias) { int indice = 0; for (int i = 1; i < 5; i++) if (medias[i] < medias[indice]) indice = i; return indice + 1; }
  private String[] nomes() { return new String[]{"Norma-padrão", "Tema e repertório", "Argumentação", "Coesão", "Intervenção"}; }
  private String[][] plano(int foco) {
    return new String[][]{
      {"Diagnóstico e fundamento", "Revise a competência C" + foco + " (" + nomes()[foco - 1] + ") e refaça 3 exercícios específicos."},
      {"Projeto de texto", "Planeje tese, dois argumentos e repertórios antes de escrever. Produza introdução e dois desenvolvimentos."},
      {"Intervenção e revisão", "Crie 5 propostas completas e faça uma revisão linguística guiada em um texto antigo."},
      {"Simulado completo", "Escreva em até 90 minutos, envie para avaliação rigorosa e reescreva o trecho com maior perda."}
    };
  }
  private Paragraph secao(String texto, Font fonte) { Paragraph p = new Paragraph(texto, fonte); p.setSpacingBefore(8); p.setSpacingAfter(12); return p; }
  private void card(PdfPTable tabela, String rotulo, String valor, Font corpo, Font destaque) { PdfPCell c = new PdfPCell(); c.setBorderColor(new Color(223, 233, 229)); c.setPadding(12); c.addElement(new Phrase(rotulo, corpo)); c.addElement(new Phrase(new Chunk("\n" + valor, destaque))); tabela.addCell(c); }
  private PdfPCell celula(String texto, Font fonte, Color fundo) { PdfPCell c = new PdfPCell(new Phrase(texto, fonte)); c.setBackgroundColor(fundo); c.setBorderColor(new Color(223, 233, 229)); c.setPadding(12); return c; }
}
