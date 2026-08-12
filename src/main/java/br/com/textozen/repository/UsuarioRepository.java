package br.com.textozen.repository;
import br.com.textozen.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{Optional<Usuario> findByEmailIgnoreCase(String email); boolean existsByEmailIgnoreCase(String email);}
