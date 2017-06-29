package br.com.eb;

import java.io.IOException;
import java.net.URISyntaxException;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.net.HttpHeaders;

import br.com.eb.dto.Login;
import br.com.eb.dto.Person;

public class LoginTest extends BaseTest {

	private final String URL_LOGIN = "login";
	
    @Before
    public void setUp() {
    	this.mongoTemplate.dropCollection(Person.class);
    	this.setUrl(this.URL_LOGIN);
    }
	
    @Test
    public void shouldBeReturnTokenWhenLoginSuccessfully() {
    	Login login = this.getLogin(this.insertPerson().get(0));
    	
    	ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(this.url, login, String.class);
    	Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    	Assertions.assertThat(responseEntity.getHeaders().get(HttpHeaders.AUTHORIZATION).toString()).containsIgnoringCase("Bearer");
    }
    
    @Test
    public void shouldBeReturnStatus401WhenUsernameNotFound() throws IOException, URISyntaxException {
    	Login login = this.getLogin(this.insertPerson().get(0));
    	login.setUsername("novoUserNAme");
    	
    	this.validateUnauthorized(login, "Não foi encontrado o username");
    }
    
    @Test
    public void shouldBeReturnStatus401WhenPasswordIsIncorrect() {
    	Login login = this.getLogin(this.insertPerson().get(0));
    	login.setPassword("Nova_SENha");
    	
    	this.validateUnauthorized(login, "Senha inválida");
    }
    
    @Test
    public void shouldBeReturnStatus500WhenRequiredFieldIsBlank() {
    	Login login = this.getLogin(this.insertPerson().get(0));
    	login.setPassword(null);
    	
    	this.validateRequiredFields(HttpMethod.POST, new HttpEntity<Login>(login));
    }
    
    private void validateUnauthorized(Login login, String responseMessage) {
    	ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(this.url, login, String.class);
    	Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    	Assertions.assertThat(responseEntity.getBody()).containsIgnoringCase(responseMessage);
    }
    
    private Login getLogin(Person person) {
    	Login login = new Login();
    	login.setUsername(person.getUsername());
    	login.setPassword(person.getPassword());
    	return login;
    }
}
