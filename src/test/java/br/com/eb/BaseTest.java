package br.com.eb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import br.com.eb.dto.Cloud;
import br.com.eb.dto.Person;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, classes=CloudApiApplication.class)
public abstract class BaseTest {
    
	private static final String SERVER_ADDRESS = "http://localhost:";
	private static final String SERVER_CONTEXT = "/";
	
	@LocalServerPort 
	private int port;
	
	protected String url;
	
	protected TestRestTemplate restTemplate = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);
	
	@Autowired
    protected MongoTemplate mongoTemplate;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public void setUrl(String urlParam) {
    	this.url = this.getServerPath().concat(urlParam);
    }
    
    private String getServerPath() {
    	return SERVER_ADDRESS + this.port + SERVER_CONTEXT;
    }
    
    protected void validateToken(String token, int statusCode, String responseMessage) {
    	MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    	if (!StringUtils.isBlank(token)) {
    		headers.add("Authorization", token);
    	}
		
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.url, HttpMethod.GET, entity, String.class);

		Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(statusCode);
		Assertions.assertThat(responseEntity.getBody()).containsIgnoringCase(responseMessage);
    }
    
    protected void validateToken(HttpEntity<Person> httpEntity, HttpMethod httpMethod, int statusCode, String responseMessage) {
    	ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.url, httpMethod, httpEntity, String.class);
    	
    	Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(statusCode);
    	Assertions.assertThat(responseEntity.getBody()).containsIgnoringCase(responseMessage);
    }
    
    protected void validateRequiredFields(HttpMethod httpMethod, HttpEntity<?> httpEntity) {
    	ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.url, httpMethod, httpEntity, String.class);
    	Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    	Assertions.assertThat(responseEntity.getBody()).isNotBlank();
    }
    
    protected void validateInsert(HttpEntity<?> httpEntity, String responseMessage) {
    	ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.url, HttpMethod.POST, httpEntity, String.class);
    	
		Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
		Assertions.assertThat(responseEntity.getHeaders().getLocation().getPath()).containsIgnoringCase(responseMessage);
    }
    
    protected void validateTokenIsEmpty(HttpMethod httpMethod) {
		this.validateTokenIsEmpty(httpMethod, this.getHttpEntity(null));
    }
    
    protected void validateTokenIsEmpty(HttpMethod httpMethod, HttpEntity<?> httpEntity) {
		ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.url, httpMethod, httpEntity, String.class);

		Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.FORBIDDEN.value());
		Assertions.assertThat(responseEntity.getBody()).containsIgnoringCase("Access Denied");
    }
    
    protected void validateTokenIsInvalid(HttpMethod httpMethod, String token) {
		this.validateTokenIsInvalid(httpMethod, this.getHttpEntity(token));
    }
    
    protected void validateTokenIsInvalid(HttpMethod httpMethod, HttpEntity<?> httpEntity) {
    	ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.url, httpMethod, httpEntity, String.class);
    	
    	Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
		Assertions.assertThat(responseEntity.getBody()).containsIgnoringCase("MalformedJwtException");
    }
    
    protected void validateTokenIsExpired(HttpMethod httpMethod, String token) {
		this.validateTokenIsExpired(httpMethod, this.getHttpEntity(token));
    }
    
    protected void validateTokenIsExpired(HttpMethod httpMethod, HttpEntity<?> httpEntity) {
    	ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.url, httpMethod, httpEntity, String.class);
    	
    	Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
		Assertions.assertThat(responseEntity.getBody()).containsIgnoringCase("ExpiredJwtException");
    }
    
    protected void validateTokenNotBelongPerson(HttpMethod httpMethod, String token) {
		this.validateTokenNotBelongPerson(httpMethod, this.getHttpEntity(token));
    }

	protected void validateTokenNotBelongPerson(HttpMethod httpMethod, HttpEntity<?> httpEntity) {
		ResponseEntity<Object> responseEntity = this.restTemplate.exchange(this.url, httpMethod, httpEntity, Object.class);
    	Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	private HttpEntity<?> getHttpEntity(String token) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    	if (!StringUtils.isBlank(token)) {
    		headers.add("Authorization", token);
    	}
		
		return new HttpEntity<>(headers);
	}
	
	protected void validateNotFound(String token) {
		ResponseEntity<Object> responseEntity = this.restTemplate.exchange(this.url, HttpMethod.GET, this.getHttpEntity(token), Object.class);
		Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	protected void validateNotFound(HttpMethod httpMethod, HttpEntity<?> httpEntity, String responseMessage) {
		ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.url, httpMethod, httpEntity, String.class);
    	Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
    	Assertions.assertThat(responseEntity.getBody()).containsIgnoringCase(responseMessage);
	}
	
	protected void validateUpdate(HttpEntity<?> httpEntity) {
		ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.url, HttpMethod.PUT, httpEntity, String.class);
    	Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	protected void validateDelete(String token) {
		ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.url, HttpMethod.DELETE, this.getHttpEntity(token), String.class);
    	Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	protected List<Person> insertPerson() {
    	List<Person> listPerson = new ArrayList<>();
    	for (int indice = 0; indice < 5; indice++) {
    		String password = ("senha" + indice);
    		
    		Person person = new Person();
    		person.setName("Nome " + indice);
    		person.setPassword(this.passwordEncoder.encode(password));
    		person.setUsername("user." + indice);
    		
    		listPerson.add(person);

    		this.mongoTemplate.save(person);
    		this.insertCloud(person);
    		person.setPassword(password);
    	}
    	
    	return listPerson;
    }
	
	protected void insertCloud(Person person) {
    	List<Cloud> listCloud = new ArrayList<>();

    	for (int indice = 0; indice < 5; indice++) {
    		Cloud cloud = new Cloud();
			cloud.setPerson(person);
    		cloud.setCapacity(Long.valueOf((indice + 1) * 30));
    		cloud.setOperationalSystem("Windows");
    		cloud.setName("Cloud " + indice);
    		cloud.setCreateDate(new Date());
    		cloud.setUpdateDate(cloud.getCreateDate());
    		
    		listCloud.add(cloud);
    		this.mongoTemplate.save(cloud);
    	}
	}
}