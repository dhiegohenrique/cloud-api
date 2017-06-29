package br.com.eb.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.eb.dto.Login;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
@RequestMapping(value="/login")
@Api(value = "Login", description = "Login")
public class LoginController {
	
	@GetMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(method=RequestMethod.POST)
	@ApiOperation(
			value = "Realiza o login."
	)
	@ApiResponses(value = { 
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Campos obrigatórios não preenchidos."),
			@ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = "Acesso não autorizado: username ou senha inválidos."),
			@ApiResponse(code = HttpServletResponse.SC_OK, message = "Login realizado com sucesso.", responseHeaders = @ResponseHeader(name = "Authorization", description = "Token de autenticação", response = String.class))
	})
	public void login(@RequestBody @Valid @ApiParam(value="Dados do login", required = true) Login login) {
	}
}