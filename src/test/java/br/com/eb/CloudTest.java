package br.com.eb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import br.com.eb.dto.Cloud;
import br.com.eb.dto.Person;
import br.com.eb.service.TokenAuthenticationService;

public class CloudTest extends BaseTest {
	
	private final String URL_CLOUD = "cloud";
	
	private final String responseMessageNotFound = "Instância não encontrada";
	
	@Before
    public void setUp() {
    	this.mongoTemplate.dropCollection(Cloud.class);
    	this.mongoTemplate.dropCollection(Person.class);
    }
	
	@Test
	public void shouldBeReturnStatus201WhenInsertingNewCloud() {
		this.setUrl(this.URL_CLOUD);
		Person person = this.insertPerson().get(0);
		String token = TokenAuthenticationService.generateToken(person.getUsername());
		
		Cloud cloud = new Cloud();
		cloud.setCapacity(30L);
		cloud.setOperationalSystem("Windows");
		cloud.setName("Cloud1");
		
		this.validateInsert(this.getHttpEntity(token, cloud), "cloud/");
	}
	
	@Test
    public void shouldBeReturnStatus500WhenInsertingNewCloudAndRequiredFieldIsBlank() {
    	this.setUrl(this.URL_CLOUD);
    	Person person = this.insertPerson().get(0);
		String token = TokenAuthenticationService.generateToken(person.getUsername());
    	
    	Cloud cloud = new Cloud();
		cloud.setCapacity(30L);
		cloud.setOperationalSystem("Windows");
		
		this.validateRequiredFields(HttpMethod.POST, this.getHttpEntity(token, cloud));
    }
    
    @Test
    public void shouldBeReturnStatus403WhenInsertingNewCloudAndTokenIsEmpty() {
    	this.setUrl(this.URL_CLOUD);
    	this.insertPerson();
    	
    	Cloud cloud = new Cloud();
		cloud.setCapacity(30L);
		cloud.setOperationalSystem("Windows");
		
		this.validateTokenIsEmpty(HttpMethod.POST, this.getHttpEntity(cloud));
    }
    
    @Test
    public void shouldBeReturnStatus500WhenInsertingNewCloudAndTokenIsInvalid() {
    	this.setUrl(this.URL_CLOUD);
    	this.insertPerson();
    	
    	Cloud cloud = new Cloud();
		cloud.setCapacity(30L);
		cloud.setOperationalSystem("Windows");
		cloud.setName("Cloud1");
		
		this.validateTokenIsInvalid(HttpMethod.POST, this.getHttpEntity("meu_ToKEN", cloud));
    }
	
    @Test
    public void shouldBeReturnStatus500WhenInsertingCloudAndTokenIsExpired() {
    	this.setUrl(this.URL_CLOUD);
    	Person person = this.insertPerson().get(0);
		String token = TokenAuthenticationService.generateToken(person.getUsername(), 0);
		
		Cloud cloud = new Cloud();
		cloud.setCapacity(30L);
		cloud.setOperationalSystem("Windows");
		cloud.setName("Cloud1");
		
		this.validateTokenIsExpired(HttpMethod.POST, this.getHttpEntity(token, cloud));
    }
    
    @Test
    public void shouldBeReturnStatus204WhenEditingCloud() {
    	Cloud cloud = this.insertCloud().get(0);
    	cloud.setName(cloud.getName() + " - editado");
    	String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername());
    	
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	this.validateUpdate(this.getHttpEntity(token, cloud));
    }
    
    @Test
    public void shouldBeReturnStatus500WhenEditingCloudAndRequiredFieldsIsBlank() {
    	Cloud cloud = this.insertCloud().get(0);
    	cloud.setName(null);
    	String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername());
    	
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	this.validateRequiredFields(HttpMethod.PUT, this.getHttpEntity(token, cloud));
    }
    
    @Test
    public void shouldBeReturnStatus403WhenEditingCloudAndTokenIsEmpty() {
    	Cloud cloud = this.insertCloud().get(0);
    	cloud.setName(cloud.getName() + " - editado");
    	
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	this.validateTokenIsEmpty(HttpMethod.PUT, this.getHttpEntity(cloud));
    }
    
    @Test
    public void shouldBeReturnStatus500WhenEditingCloudAndTokenIsInvalid() {
    	Cloud cloud = this.insertCloud().get(0);
    	cloud.setName(cloud.getName() + " - editado");
    	
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	this.validateTokenIsInvalid(HttpMethod.PUT, this.getHttpEntity("meuToken", cloud));
    }
    
    @Test
    public void shouldBeReturnStatus500WhenEditingPersonAndTokenIsExpired() {
    	Cloud cloud = this.insertCloud().get(0);
    	cloud.setName(cloud.getName() + " - editado");
    	
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername(), 0);
    	this.validateTokenIsExpired(HttpMethod.PUT, this.getHttpEntity(token, cloud));
    }
    
    @Test
    public void shouldBeReturnStatus403WhenEditingCloudAndTokenNotBelongPerson() {
		List<Cloud> listCloud = this.insertCloud();
		Cloud cloud = listCloud.get(0);
    	cloud.setName(cloud.getName() + " - editado");
    	
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	String token = TokenAuthenticationService.generateToken(listCloud.get(1).getPerson().getUsername());
    	
    	this.validateTokenNotBelongPerson(HttpMethod.PUT, this.getHttpEntity(token, cloud));
    }
    
    @Test
    public void shouldBeReturnStatus404WhenEditingACloudNotFound() {
    	List<Cloud> listCloud = this.insertCloud();
		Cloud cloud = listCloud.get(0);
    	cloud.setName(cloud.getName() + " - editado");
    	
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	
    	String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername());
    	this.mongoTemplate.remove(cloud);
    	
    	this.validateNotFound(HttpMethod.PUT, this.getHttpEntity(token, cloud), this.responseMessageNotFound);
	}
    
    @Test
    public void shouldBeReturnStatus204WhenDeletingACloud() {
    	Cloud cloud = this.insertCloud().get(0);
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	
    	String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername());
    	this.validateDelete(token);
    }
    
    @Test
    public void shouldBeReturnStatus403WhenDeletingACloudAndTokenIsEmpty() {
    	Cloud cloud = this.insertCloud().get(0);
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	
    	this.validateTokenIsEmpty(HttpMethod.DELETE);
    }
    
    @Test
    public void shouldBeReturnStatus500WhenDeletingACloudAndTokenIsInvalid() {
    	Cloud cloud = this.insertCloud().get(0);
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	this.validateTokenIsInvalid(HttpMethod.DELETE, "meuToken");
    }
    
    @Test
    public void shouldBeReturnStatus500WhenDeletingACloudAndTokenIsExpired() {
    	Cloud cloud = this.insertCloud().get(0);
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());

    	String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername(), 0);
    	this.validateTokenIsExpired(HttpMethod.DELETE, token);
    }
    
    @Test
    public void shouldBeReturnStatus403WhenDeletingACloudAndTokenNotBelongPerson() {
    	List<Cloud> listCloud = this.insertCloud();
		Cloud cloud = listCloud.get(0);
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());

    	String token = TokenAuthenticationService.generateToken(listCloud.get(1).getPerson().getUsername());
    	this.validateTokenNotBelongPerson(HttpMethod.DELETE, this.getHttpEntity(token, cloud));
    }
    
    @Test
    public void shouldBeReturnStatus404WhenDeletingACloudNotFound() {
		Cloud cloud = this.insertCloud().get(0);
    	this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	
    	String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername());
    	this.mongoTemplate.remove(cloud);
    	
    	this.validateNotFound(HttpMethod.DELETE, this.getHttpEntity(token, cloud), this.responseMessageNotFound);
	}
    
    @Test
    public void shouldBeReturnAllCloud() {
    	Cloud cloud = this.insertCloud().get(0);
    	this.setUrl(this.URL_CLOUD);
    	
    	String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername());
    	
    	ResponseEntity<Cloud[]> responseEntity = this.restTemplate.exchange(this.url, HttpMethod.GET, this.getHttpEntity(token), Cloud[].class);
		Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
		
		Cloud[] clouds = responseEntity.getBody();
		Assertions.assertThat(clouds).isNotEmpty();
		
		List<Cloud> listClouds = Arrays.asList(clouds);
		
		Condition<Cloud> condition = new Condition<Cloud>() {
			@Override
			public boolean matches(Cloud cloud) {
				return !StringUtils.isBlank(cloud.getId())
						&& (cloud.getCapacity() != null)
						&& (cloud.getCreateDate() != null)
						&& !StringUtils.isBlank(cloud.getName())
						&& !StringUtils.isBlank(cloud.getOperationalSystem())
						&& (cloud.getPerson() != null)
						&& (cloud.getUpdateDate() != null);
			}
		};
		
		Assertions.assertThat(listClouds).filteredOn(condition).hasSize(listClouds.size());
    }
    
    @Test
    public void shouldBeReturnCloudById() {
		Cloud cloud = this.insertCloud().get(0);
		this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
		
		String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername());
		
		ResponseEntity<Cloud> responseEntity = this.restTemplate.exchange(this.url, HttpMethod.GET, this.getHttpEntity(token), Cloud.class);
		Assertions.assertThat(responseEntity.getBody())
			.extracting("id", "name", "operationalSystem", "active", "createDate", "updateDate", "capacity", "person.id")
			.containsExactly(cloud.getId(), cloud.getName(), cloud.getOperationalSystem(), cloud.isActive(), cloud.getCreateDate(), cloud.getUpdateDate(), cloud.getCapacity(), cloud.getPerson().getId());
	}
    
    @Test
    public void shouldBeReturnStatus403WhenRecoveringCloudByIdAndTokenIsEmpty() {
    	Cloud cloud = this.insertCloud().get(0);
		this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
		
		this.validateTokenIsEmpty(HttpMethod.GET);
    }
    
    @Test
    public void shouldBeReturnStatus500WhenRecoveringCloudByIdAndTokenIsInvalid() {
    	Cloud cloud = this.insertCloud().get(0);
		this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
		
		this.validateTokenIsInvalid(HttpMethod.GET, "meu_ToKEN");
    }
    
    @Test
    public void shouldBeReturnStatus500WhenRecoveringCloudByIdAndTokenIsExpired() {
    	Cloud cloud = this.insertCloud().get(0);
		this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
		
		String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername(), 0);
		this.validateTokenIsExpired(HttpMethod.GET, token);
    }
    
    @Test
    public void shouldBeReturnStatus403WhenRecoveringCloudByIdAndTokenNotBelongPerson() {
    	List<Cloud> listCloud = this.insertCloud();
		Cloud cloud = listCloud.get(0);
		this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
    	
    	String token = TokenAuthenticationService.generateToken(listCloud.get(1).getPerson().getUsername());
    	this.validateTokenNotBelongPerson(HttpMethod.GET, token);
    }
    
    @Test
    public void shouldBeReturnStatus404WhenRecoveringCloudByIdNotFound() {
    	Cloud cloud = this.insertCloud().get(0);
		this.setUrl(this.URL_CLOUD + "/" + cloud.getId());
		
		String token = TokenAuthenticationService.generateToken(cloud.getPerson().getUsername());
		this.mongoTemplate.remove(cloud);
		
		this.validateNotFound(token);
    }
    
	private HttpEntity<Cloud> getHttpEntity(String token) {
    	return this.getHttpEntity(token, null);
    }
    
    private HttpEntity<Cloud> getHttpEntity(String token, Cloud cloud) {
    	MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    	if (!StringUtils.isBlank(token)) {
    		headers.add("Authorization", token);
    	}
    	
    	return new HttpEntity<>(cloud, headers);
    }
    
    private HttpEntity<Cloud> getHttpEntity(Cloud cloud) {
    	return this.getHttpEntity(null, cloud);
    }
    
    private List<Cloud> insertCloud() {
    	List<Person> listPerson = this.insertPerson();
    	List<Cloud> listCloud = new ArrayList<>();

    	for (int indice = 0; indice < 5; indice++) {
    		Person person = listPerson.get(indice);
    		
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
    	
    	return listCloud;
    }
}
