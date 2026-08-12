package br.com.textozen.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="usuarios", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class Usuario {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, length=80) private String nome;
  @Column(nullable=false, length=150) private String email;
  @Column(nullable=false) private String senha;
  @Column(nullable=false) private LocalDateTime criadoEm = LocalDateTime.now();
  public Long getId(){return id;} public String getNome(){return nome;} public void setNome(String v){nome=v;}
  public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getSenha(){return senha;}
  public void setSenha(String v){senha=v;} public LocalDateTime getCriadoEm(){return criadoEm;}
}
