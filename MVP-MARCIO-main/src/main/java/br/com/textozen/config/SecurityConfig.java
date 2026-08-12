package br.com.textozen.config;

import br.com.textozen.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
  @Bean UserDetailsService users(UsuarioRepository repo){return email -> repo.findByEmailIgnoreCase(email)
    .map(u -> User.withUsername(u.getEmail()).password(u.getSenha()).roles("ALUNO").build())
    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));}
  @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(a -> a.requestMatchers("/","/login","/cadastro","/css/**","/js/**","/images/**","/h2-console/**").permitAll().anyRequest().authenticated())
      .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/painel",true).failureUrl("/login?erro").permitAll())
      .logout(l -> l.logoutSuccessUrl("/?saiu")).csrf(c -> c.ignoringRequestMatchers("/h2-console/**"))
      .headers(h -> h.frameOptions(frame -> frame.sameOrigin())).build();
  }
}
