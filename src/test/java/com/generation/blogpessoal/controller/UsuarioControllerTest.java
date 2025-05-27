package com.generation.blogpessoal.controller;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
 
import java.util.Optional;
 
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
 
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.TestBuilder;
 
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
 
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private static final String USUARIO_ROOT_EMAIL = "root@email.com";
	private static final String USUARIO_ROOT_SENHA = "rootroot";
	private static final String BASE_URL_USUARIOS = "/usuarios";
	
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuarioRoot());
	}
	
	@Test
	@DisplayName("Deve cadastrar um novo usuário com sucesso")
	public void deveCasdastrarUsuario() {
		
		//Given
		Usuario usuario = TestBuilder.criarUsuario(null, "Alex", "alex@email.com", "12345678");
		
		//When
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		
		//Then
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertEquals("Alex", resposta.getBody().getNome());
		assertEquals("alex@email.com", resposta.getBody().getUsuario());
	}
	
	@Test
	@DisplayName("Não deve permitir a duplicação do usuário")
	public void naoDeveDuplicarUsuario() {
		//Given
		Usuario usuario = TestBuilder.criarUsuario(null, "Renata", "renata@email.com", "12345678");
		usuarioService.cadastrarUsuario(usuario);
		
		//When
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
				
		//Then
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Deve atualizar um usuário existente")
	public void deveAtualizarUmUsuario() {
		
		//Given
		Usuario usuario = TestBuilder.criarUsuario(null, "Juliana Andrews", "juliana_andrews@email.com.br",
				"juliana123");
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
		
		Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Juliana Ramos",
				"juliana_ramos@email.com.br", "juliana123");
 
		//When
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuarioUpdate);
 
		ResponseEntity<Usuario> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_USUARIOS + "/atualizar", HttpMethod.PUT, requisicao, Usuario.class);
 
		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals("Juliana Ramos", resposta.getBody().getNome());
		assertEquals("juliana_ramos@email.com.br", resposta.getBody().getUsuario());
	}
	
	@Test
	@DisplayName("Deve listar todos os usuários com sucesso")
	public void deveListarTodosUsuarios() {
		//Given
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Jovani Almeida", "jovanialmeida@email.com", "12345678"));
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Carlos Garcia", "carlosgarcia@email.com", "12345678"));
		
		ResponseEntity<Usuario[]> resposta = testRestTemplate.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_USUARIOS + "/all", HttpMethod.GET, null, Usuario[].class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
		
	}
	
//	@Test
//	@DisplayName("Deve buscar usuário pelo ID")
//	public void deveBuscarUsuarioId() {
//		//Given
//		Usuario usuario = TestBuilder.criarUsuario(null, "Joao", "joao@email.com", "12345678");
//		
//		//When
//		ResponseEntity<Usuario> resposta = testRestTemplate.withBasicAuth(USUARIO_ROOT_EMAIL, BASE_URL_USUARIOS)
//				.exchange(BASE_URL_USUARIOS + "/id", HttpMethod.GET, )
//	}
}