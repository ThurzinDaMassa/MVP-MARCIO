package br.com.textozen.config;

import br.com.textozen.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.*;

@Configuration
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
  @Bean UserDetailsService users(UsuarioRepository repo){return email -> repo.findByEmailIgnoreCase(email)
    .map(u -> User.withUsername(u.getEmail()).password(u.getSenha()).roles("ALUNO").build())
    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));}
  @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception{return configuration.getAuthenticationManager();}
  @Bean SecurityContextRepository securityContextRepository(){return new HttpSessionSecurityContextRepository();}
  @Bean SecurityFilterChain security(HttpSecurity http,SecurityContextRepository contextRepository) throws Exception {
    return http.authorizeHttpRequests(a -> a.requestMatchers("/","/login","/cadastro","/css/**","/js/**","/images/**","/h2-console/**").permitAll().anyRequest().authenticated())
      .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/painel",true).failureUrl("/login?erro").permitAll())
      .logout(l -> l.logoutSuccessUrl("/?saiu")).csrf(c -> c.ignoringRequestMatchers("/h2-console/**"))
      .securityContext(c -> c.securityContextRepository(contextRepository))
      .headers(h -> h.frameOptions(frame -> frame.sameOrigin())).build();
  }
}
