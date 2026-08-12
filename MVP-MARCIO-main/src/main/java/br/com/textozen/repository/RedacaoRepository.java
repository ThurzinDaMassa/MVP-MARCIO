package br.com.textozen.repository;
import br.com.textozen.model.Redacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RedacaoRepository extends JpaRepository<Redacao,Long>{List<Redacao> findByUsuarioEmailOrderByCriadaEmDesc(String email); Optional<Redacao> findByIdAndUsuarioEmailIgnoreCase(Long id,String email); long countByUsuarioEmail(String email);}
