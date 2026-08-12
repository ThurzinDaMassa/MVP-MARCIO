package br.com.textozen.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="redacoes")
public class Redacao {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="usuario_id") private Usuario usuario;
  @Column(nullable=false, length=160) private String titulo;
  @Column(nullable=false, length=300) private String tema;
  @Lob @Column(nullable=false) private String texto;
  private Integer nota;
  private Integer competencia1;
  private Integer competencia2;
  private Integer competencia3;
  private Integer competencia4;
  private Integer competencia5;
  @Lob private String feedback;
  @Lob private String detalheCompetencia1;
  @Lob private String detalheCompetencia2;
  @Lob private String detalheCompetencia3;
  @Lob private String detalheCompetencia4;
  @Lob private String detalheCompetencia5;
  @Lob private String elogios;
  @Lob private String correcoes;
  @Lob private String planoMelhoria;
  @Column(nullable=false, length=30) private String status = "CORRIGIDA";
  @Column(nullable=false) private LocalDateTime criadaEm = LocalDateTime.now();
  public Long getId(){return id;} public Usuario getUsuario(){return usuario;} public void setUsuario(Usuario v){usuario=v;}
  public String getTitulo(){return titulo;} public void setTitulo(String v){titulo=v;} public String getTema(){return tema;}
  public void setTema(String v){tema=v;} public String getTexto(){return texto;} public void setTexto(String v){texto=v;}
  public Integer getNota(){return nota;} public void setNota(Integer v){nota=v;} public String getFeedback(){return feedback;}
  public void setFeedback(String v){feedback=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
  public LocalDateTime getCriadaEm(){return criadaEm;}
  public Integer getCompetencia1(){return competencia1;} public void setCompetencia1(Integer v){competencia1=v;}
  public Integer getCompetencia2(){return competencia2;} public void setCompetencia2(Integer v){competencia2=v;}
  public Integer getCompetencia3(){return competencia3;} public void setCompetencia3(Integer v){competencia3=v;}
  public Integer getCompetencia4(){return competencia4;} public void setCompetencia4(Integer v){competencia4=v;}
  public Integer getCompetencia5(){return competencia5;} public void setCompetencia5(Integer v){competencia5=v;}
  public String getDetalheCompetencia1(){return detalheCompetencia1;} public void setDetalheCompetencia1(String v){detalheCompetencia1=v;}
  public String getDetalheCompetencia2(){return detalheCompetencia2;} public void setDetalheCompetencia2(String v){detalheCompetencia2=v;}
  public String getDetalheCompetencia3(){return detalheCompetencia3;} public void setDetalheCompetencia3(String v){detalheCompetencia3=v;}
  public String getDetalheCompetencia4(){return detalheCompetencia4;} public void setDetalheCompetencia4(String v){detalheCompetencia4=v;}
  public String getDetalheCompetencia5(){return detalheCompetencia5;} public void setDetalheCompetencia5(String v){detalheCompetencia5=v;}
  public String getElogios(){return elogios;} public void setElogios(String v){elogios=v;}
  public String getCorrecoes(){return correcoes;} public void setCorrecoes(String v){correcoes=v;}
  public String getPlanoMelhoria(){return planoMelhoria;} public void setPlanoMelhoria(String v){planoMelhoria=v;}
}
