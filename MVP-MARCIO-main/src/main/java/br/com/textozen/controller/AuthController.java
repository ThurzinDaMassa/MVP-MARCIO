package br.com.textozen.controller;

import br.com.textozen.model.Usuario;
import br.com.textozen.repository.UsuarioRepository;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
  private final UsuarioRepository usuarios; private final PasswordEncoder encoder;
  public AuthController(UsuarioRepository u,PasswordEncoder e){usuarios=u;encoder=e;}
  @GetMapping("/login") String login(){return "login";}
  @GetMapping("/cadastro") String cadastro(){return "cadastro";}
  @PostMapping("/cadastro") String criar(@RequestParam String nome,@RequestParam String email,@RequestParam String senha,RedirectAttributes ra){
    nome=nome.trim(); email=email.trim().toLowerCase();
    if(nome.length()<2 || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || senha.length()<6){ra.addFlashAttribute("erro","Preencha os dados corretamente. A senha deve ter 6 caracteres ou mais.");return "redirect:/cadastro";}
    if(usuarios.existsByEmailIgnoreCase(email)){ra.addFlashAttribute("erro","Este e-mail já está cadastrado.");return "redirect:/cadastro";}
    Usuario u=new Usuario();u.setNome(nome);u.setEmail(email);u.setSenha(encoder.encode(senha));usuarios.save(u);
    ra.addFlashAttribute("sucesso","Conta criada! Agora entre com seu e-mail e senha.");return "redirect:/login";
  }
}
