package br.com.textozen;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.textozen.model.Usuario;
import br.com.textozen.service.PlanoEstudosPdfService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlanoEstudosPdfServiceTests {
  @Test void geraPdfValido() throws Exception {
    Usuario usuario = new Usuario(); usuario.setNome("Estudante de exemplo");
    byte[] pdf = new PlanoEstudosPdfService().gerar(usuario, List.of());
    assertThat(pdf).startsWith("%PDF".getBytes()).hasSizeGreaterThan(2_000);
    Files.write(Path.of("target", "plano-estudos-preview.pdf"), pdf);
  }
}
