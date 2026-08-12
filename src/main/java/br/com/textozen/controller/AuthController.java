package br.com.textozen.controller;

import br.com.textozen.model.Usuario;
import br.com.textozen.repository.UsuarioRepository;
import jakarta.validation.constraints.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
  private final UsuarioRepository usuarios; private final PasswordEncoder encoder; private final AuthenticationManager authenticationManager; private final SecurityContextRepository contextRepository;
  public AuthController(UsuarioRepository u,PasswordEncoder e,AuthenticationManager authenticationManager,SecurityContextRepository contextRepository){usuarios=u;encoder=e;this.authenticationManager=authenticationManager;this.contextRepository=contextRepository;}
  @GetMapping("/login") String login(){return "login";}
  @GetMapping("/cadastro") String cadastro(){return "cadastro";}
  @PostMapping("/cadastro") String criar(@RequestParam String nome,@RequestParam String email,@RequestParam String senha,RedirectAttributes ra,HttpServletRequest request,HttpServletResponse response){
    nome=nome.trim(); email=email.trim().toLowerCase();
    if(nome.length()<2 || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || senha.length()<6){ra.addFlashAttribute("erro","Preencha os dados corretamente. A senha deve ter 6 caracteres ou mais.");return "redirect:/cadastro";}
    if(usuarios.existsByEmailIgnoreCase(email)){ra.addFlashAttribute("erro","Este e-mail já está cadastrado.");return "redirect:/cadastro";}
    Usuario u=new Usuario();u.setNome(nome);u.setEmail(email);u.setSenha(encoder.encode(senha));usuarios.save(u);
    Authentication autenticacao=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,senha));
    SecurityContext contexto=SecurityContextHolder.createEmptyContext();contexto.setAuthentication(autenticacao);SecurityContextHolder.setContext(contexto);contextRepository.saveContext(contexto,request,response);
    return "redirect:/painel";
  }
}
