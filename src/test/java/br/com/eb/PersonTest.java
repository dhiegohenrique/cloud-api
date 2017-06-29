package br.com.eb;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import br.com.eb.dto.Cloud;
import br.com.eb.dto.Person;
import br.com.eb.service.ICloudService;
import br.com.eb.service.TokenAuthenticationService;

public class PersonTest extends BaseTest {
	
	private final String URL_PERSON = "person";
	private final String responseMessageNotFound = "Pessoa não encontrada";
	
	@Autowired
	private ICloudService cloudService;
	
    @Before
    public void setUp() {
    	this.mongoTemplate.dropCollection(Person.class);
    }
    
    @Test
	public void shouldBeReturnStatus201WhenInsertingNewPerson() {
		this.setUrl(this.URL_PERSON);
		
		Person person = new Person();
		person.setName("Frederico");
		person.setPassword("123qweASDF_");
		person.setUsername("fred.silva");
		
		this.validateInsert(this.getHttpEntity(person), "person/");
	}
    
    @Test
    public void shouldBeReturnStatus500WhenInsertingNewPersonAndRequiredFieldIsBlank() {
    	this.setUrl(this.URL_PERSON);
    	
    	Person person = new Person();
		person.setName("Frederico");
		person.setPassword("123qweASDF_");
		
		this.validateRequiredFields(HttpMethod.POST, this.getHttpEntity(person));
    }
    
    @Test
    public void shouldBeReturnStatus500WhenInsertingNewPersonAndUsernameExists() {
    	this.setUrl(this.URL_PERSON);
    	List<Person> listPerson = this.insertPerson();
		
		Person newPerson = new Person();
		newPerson.setName("Maria");
		newPerson.setPassword("123qwe");
		newPerson.setUsername(listPerson.get(0).getUsername());
		
		ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(this.url, newPerson, String.class);
		Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
		Assertions.assertThat(responseEntity.getBody()).isNotBlank();
    }
    
	@Test
    public void shouldBeReturnAllPerson() {
    	this.setUrl(this.URL_PERSON);
    	this.insertPerson();
    	
//    	ParameterizedTypeReference<List<Person>> parameterizedTypeReference = new ParameterizedTypeReference<List<Person>>() {
//		};
//		ResponseEntity<List<Person>> responseEntity = this.restTemplate.exchange(this.url, HttpMethod.GET, null, parameterizedTypeReference);
    	
		ResponseEntity<Person[]> responseEntity = this.restTemplate.getForEntity(this.url, Person[].class);
		Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
		
		Person[] body = responseEntity.getBody();
		Assertions.assertThat(body).isNotEmpty();
		
		List<Person> listPerson = Arrays.asList(body);
		
		Condition<Person> condition = new Condition<Person>() {
			@Override
			public boolean matches(Person person) {
				boolean isValidFields = !StringUtils.isBlank(person.getId()) && !StringUtils.isBlank(person.getName());
				boolean isInvalidFields = StringUtils.isBlank(person.getUsername()) && StringUtils.isBlank(person.getPassword());
				return isValidFields && isInvalidFields;
			}
		};
		
		Assertions.assertThat(listPerson).filteredOn(condition).hasSize(listPerson.size());
    }
    
    @Test
    public void shouldBeReturnPersonById() {
		Person person = this.insertPerson().get(0);
		this.setUrl(this.URL_PERSON + "/" + person.getId());
		
		String token = TokenAuthenticationService.generateToken(person.getUsername());
		
		ResponseEntity<Person> responseEntity = this.restTemplate.exchange(this.url, HttpMethod.GET, this.getHttpEntity(token), Person.class);
		Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
		
		Person personResponse = responseEntity.getBody();
		Assertions.assertThat(personResponse).isEqualToIgnoringGivenFields(person, "password");
	}
	
	@Test
    public void shouldBeReturnStatus403WhenRecoveringPersonByIdAndTokenIsEmpty() {
		Person person = this.insertPerson().get(0);
		this.setUrl(this.URL_PERSON + "/" + person.getId());
		
		this.validateTokenIsEmpty(HttpMethod.GET);
    }
	
	@Test
    public void shouldBeReturnStatus500WhenRecoveringPersonByIdAndTokenIsInvalid() {
		Person person = this.insertPerson().get(0);
		this.setUrl(this.URL_PERSON + "/" + person.getId());
		
		this.validateTokenIsInvalid(HttpMethod.GET, "meu_ToKEN");
    }
	
	@Test
    public void shouldBeReturnStatus500WhenRecoveringPersonByIdAndTokenIsExpired() {
		Person person = this.insertPerson().get(0);
		this.setUrl(this.URL_PERSON + "/" + person.getId());
		
		String token = TokenAuthenticationService.generateToken(person.getUsername(), 0);
		this.validateTokenIsExpired(HttpMethod.GET, token);
    }
	
    @Test
    public void shouldBeReturnStatus403WhenRecoveringPersonByIdAndTokenNotBelongPerson() {
    	List<Person> listPerson = this.insertPerson();
    	this.setUrl(this.URL_PERSON + "/" + listPerson.get(0).getId());
    	
    	String token = TokenAuthenticationService.generateToken(listPerson.get(1).getUsername());
    	this.validateTokenNotBelongPerson(HttpMethod.GET, token);
    }
    
	@Test
    public void shouldBeReturnStatus404WhenRecoveringPersonByIdNotFound() {
		List<Person> listPerson = this.insertPerson();
		Person person = listPerson.get(0);
		this.setUrl(this.URL_PERSON + "/" + person.getId());
		
		String token = TokenAuthenticationService.generateToken(person.getUsername());
		this.mongoTemplate.remove(person);
		
		this.validateNotFound(token);
    }
    
    @Test
    public void shouldBeReturnStatus204WhenEditingPerson() {
    	Person person = this.insertPerson().get(0);
    	person.setName(person.getName() + " - editado");
    	person.setPassword("novaSenha");
    	
    	this.setUrl(this.URL_PERSON + "/" + person.getId());
    	String token = TokenAuthenticationService.generateToken(person.getUsername());
    	
    	this.validateUpdate(this.getHttpEntity(token, person));
    }
    
    @Test
    public void shouldBeReturnStatus500WhenEditingPersonAndRequiredFieldsIsBlank() {
    	Person person = this.insertPerson().get(0);
    	person.setName(null);
    	person.setPassword(null);
    	
    	this.setUrl(this.URL_PERSON + "/" + person.getId());
    	String token = TokenAuthenticationService.generateToken(person.getUsername());
    	this.validateRequiredFields(HttpMethod.PUT, this.getHttpEntity(token, person));
    }
    
    @Test
    public void shouldBeReturnStatus403WhenEditingPersonAndTokenIsEmpty() {
    	Person person = this.insertPerson().get(0);
    	person.setName(person.getName() + " - editado");
    	person.setPassword("novaSenha");
    	
    	this.setUrl(this.URL_PERSON + "/" + person.getId());
    	this.validateTokenIsEmpty(HttpMethod.PUT, this.getHttpEntity(person));
    }
    
    @Test
    public void shouldBeReturnStatus500WhenEditingPersonAndTokenIsInvalid() {
    	Person person = this.insertPerson().get(0);
    	person.setName(person.getName() + " - editado");
    	person.setPassword("novaSenha");
    	
    	this.setUrl(this.URL_PERSON + "/" + person.getId());
    	this.validateTokenIsInvalid(HttpMethod.PUT, this.getHttpEntity("meuToken", person));
    }
    
    @Test
    public void shouldBeReturnStatus500WhenEditingPersonAndTokenIsExpired() {
    	Person person = this.insertPerson().get(0);
    	person.setName(person.getName() + " - editado");
    	person.setPassword("novaSenha");
    	
    	this.setUrl(this.URL_PERSON + "/" + person.getId());
    	String token = TokenAuthenticationService.generateToken(person.getUsername(), 0);
    	this.validateTokenIsExpired(HttpMethod.PUT, this.getHttpEntity(token, person));
    }
    
    @Test
    public void shouldBeReturnStatus403WhenEditingPersonAndTokenNotBelongPerson() {
    	List<Person> listPerson = this.insertPerson();
    	Person person = listPerson.get(0);
    	person.setName(person.getName() + " - editado");
    	person.setPassword("novaSenha");
    	
		this.setUrl(this.URL_PERSON + "/" + person.getId());
    	String token = TokenAuthenticationService.generateToken(listPerson.get(1).getUsername());
    	this.validateTokenNotBelongPerson(HttpMethod.PUT, this.getHttpEntity(token, person));
    }
    
    @Test
    public void shouldBeReturnStatus404WhenEditingAPersonNotFound() {
    	Person person = this.insertPerson().get(0);
    	person.setName(person.getName() + " - editado");
    	person.setPassword("novaSenha");
    	this.setUrl(this.URL_PERSON + "/" + person.getId());
    	
    	String token = TokenAuthenticationService.generateToken(person.getUsername());
    	this.mongoTemplate.remove(person);
    	
    	this.validateNotFound(HttpMethod.PUT, this.getHttpEntity(token, person), this.responseMessageNotFound);
	}
    
    @Test
    public void shouldBeReturnStatus204WhenDeletingAPerson() {
    	Person person = this.insertPerson().get(0);

    	this.setUrl(this.URL_PERSON + "/" + person.getId());
    	String token = TokenAuthenticationService.generateToken(person.getUsername());
    	
    	this.validateDelete(token);
    
    	List<Cloud> listClouds = this.cloudService.getAllClouds(person.getId());
    	Assertions.assertThat(listClouds).isEmpty();
    }
    
    @Test
    public void shouldBeReturnStatus403WhenDeletingAPersonAndTokenIsEmpty() {
    	Person person = this.insertPerson().get(0);
    	this.setUrl(this.URL_PERSON + "/" + person.getId());
    	
    	this.validateTokenIsEmpty(HttpMethod.DELETE);
    }
    
    @Test
    public void shouldBeReturnStatus500WhenDeletingAPersonAndTokenIsInvalid() {
    	Person person = this.insertPerson().get(0);
    	this.setUrl(this.URL_PERSON + "/" + person.getId());
    	this.validateTokenIsInvalid(HttpMethod.DELETE, "meuToken");
    }
    
    @Test
    public void shouldBeReturnStatus500WhenDeletingAPersonAndTokenIsExpired() {
    	Person person = this.insertPerson().get(0);
    	this.setUrl(this.URL_PERSON + "/" + person.getId());

    	String token = TokenAuthenticationService.generateToken(person.getUsername(), 0);
    	this.validateTokenIsExpired(HttpMethod.DELETE, token);
    }
    
    @Test
    public void shouldBeReturnStatus403WhenDeletingAPersonAndTokenNotBelongPerson() {
    	List<Person> listPerson = this.insertPerson();
    	Person person = listPerson.get(0);
    	
		this.setUrl(this.URL_PERSON + "/" + person.getId());
    	String token = TokenAuthenticationService.generateToken(listPerson.get(1).getUsername());
    	this.validateTokenNotBelongPerson(HttpMethod.DELETE, this.getHttpEntity(token, person));
    }
    
    @Test
    public void shouldBeReturnStatus404WhenDeletingAPersonNotFound() {
    	Person person = this.insertPerson().get(0);
    	this.setUrl(this.URL_PERSON + "/" + person.getId());
    	
    	String token = TokenAuthenticationService.generateToken(person.getUsername());
    	this.mongoTemplate.remove(person);
    	
    	this.validateNotFound(HttpMethod.DELETE, this.getHttpEntity(token, person), this.responseMessageNotFound);
	}
    
    private HttpEntity<Person> getHttpEntity(String token) {
    	return this.getHttpEntity(token, null);
    }
    
    private HttpEntity<Person> getHttpEntity(String token, Person person) {
    	MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    	if (!StringUtils.isBlank(token)) {
    		headers.add("Authorization", token);
    	}
    	
    	return new HttpEntity<>(person, headers);
    }
    
    private HttpEntity<Person> getHttpEntity(Person person) {
    	return this.getHttpEntity(null, person);
    }
}