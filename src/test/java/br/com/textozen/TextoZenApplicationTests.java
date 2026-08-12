package br.com.textozen;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:testdb","gemini.api-key="}) class TextoZenApplicationTests{@Test void contextLoads(){}}
