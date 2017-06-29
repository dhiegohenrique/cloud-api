package br.com.eb.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class Login {
	
	@NotNull(message = "Informe o username.")
	@ApiModelProperty(example = "maria.silva", required = true)
	private String username;
	
	@NotNull(message = "Informe a senha.")
	@ApiModelProperty(example = "123qwe", required = true)
	private String password;
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}