package br.com.eb.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.eb.validators.IUsernameValidator;
import io.swagger.annotations.ApiModelProperty;

@Document(collection = "person")
public class Person {
	
	@Id
	@ApiModelProperty(hidden = true)
	private String id;

	@NotNull(message = "Informe o nome.")
	@NotBlank(groups = {PersonUpdateGroup.class}, message = "Informe o nome.")
	@ApiModelProperty(example = "Maria", required = true)
	private String name;
	
	@IUsernameValidator
	@ApiModelProperty(example = "maria.silva", required = true)
	private String username;
	
	@NotNull(message = "Informe a senha.")
	@NotBlank(groups = {PersonUpdateGroup.class}, message = "Informe a senha.")
	@ApiModelProperty(example = "123qwe", required = true)
	private String password;
	
	public Person(String id) {
		this.id = id;
	}
	
	public Person() {
		
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public interface PersonUpdateGroup {
	}
}