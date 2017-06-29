package br.com.eb.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.eb.dto.Cloud;
import br.com.eb.dto.Person;
import br.com.eb.service.ICloudService;
import br.com.eb.service.IPersonService;
import br.com.eb.service.TokenAuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
@CrossOrigin(origins = {"http://localhost:3001", "https://cloud-client.herokuapp.com"})
@RequestMapping(value="/cloud")
@Api(value = "Cloud", description = "Cloud")
public class CloudController extends BaseController {
	
	@Autowired
	private ICloudService cloudService;
	
	@Autowired
	private IPersonService personService;
	
	@GetMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(method=RequestMethod.POST)
	@ApiOperation(
			value = "Cadastra uma instância."
			)
	@ApiResponses(value = { 
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Campos obrigatórios não preenchidos ou token inválido."),
			@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Instância cadastrada com sucesso.", responseHeaders = @ResponseHeader(name = "Location", description = "URI da instância cadastrada", response = String.class)),
			@ApiResponse(code = HttpServletResponse.SC_FORBIDDEN, message = "Acesso não autorizado. Informe um token válido."),
	})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Token válido da pessoa", required = true, paramType = "header"),
	})
	public ResponseEntity<String> insert(@RequestBody @Valid @ApiParam(value="Dados da instância", required = true) Cloud cloud, BindingResult bindingResult, UriComponentsBuilder uriComponentsBuilder, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
		this.setPerson(cloud, authorization);
		
		if (bindingResult.hasErrors()) {
			List<String> listErrors = this.getListErrors(bindingResult);
			return new ResponseEntity<String>(this.getJson(listErrors), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		this.cloudService.insert(cloud);
		
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponentsBuilder.path("/cloud/{id}").buildAndExpand(cloud.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
	
	@GetMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(value = "{id}", method=RequestMethod.PUT)
	@ApiOperation(
			value = "Atualiza uma instância."
			)
	@ApiResponses(value = { 
			@ApiResponse(code = HttpServletResponse.SC_FORBIDDEN, message = "Acesso não autorizado. Informe um token válido."),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Campos obrigatórios não preenchidos ou token inválido."),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Instância não encontrada."),
			@ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Instância atualizada com sucesso.")
	})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Token válido da pessoa", required = true, paramType = "header"),
	})
	public ResponseEntity<String> update(@PathVariable("id") String id, @RequestBody @ApiParam(value="Dados da instância", required = true) @Valid Cloud cloud, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			if (bindingResult.hasErrors()) {
				List<String> listErrors = this.getListErrors(bindingResult);
				return new ResponseEntity<String>(this.getJson(listErrors), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		try {
			cloud.setId(id);
			this.cloudService.update(cloud);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(value = "{id}", method=RequestMethod.DELETE)
	@ApiOperation(
			value = "Deleta uma instância."
			)
	@ApiResponses(value = { 
			@ApiResponse(code = HttpServletResponse.SC_FORBIDDEN, message = "Acesso não autorizado. Informe um token válido."),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Token inválido."),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Instância não encontrada."),
			@ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Instância deletada com sucesso.")
	})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Token válido da pessoa", required = true, paramType = "header"),
	})
	public ResponseEntity<String> delete(@PathVariable("id") String id) {
		try {
			this.cloudService.delete(id);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(method=RequestMethod.GET)
	@ApiOperation(
			value = "Retorna todas as instâncias cadastradas para uma pessoa.",
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE
			)
	@ApiResponses(value = { 
			@ApiResponse(
						code = HttpServletResponse.SC_OK, message = "Instâncias retornadas com sucesso.", 
						response = Cloud.class, responseContainer = "List"
					)
	})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Token válido da pessoa", required = true, paramType = "header"),
	})
	public ResponseEntity<List<Cloud>> clouds(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
		Person person = this.getPerson(authorization);
		List<Cloud> listCloud = this.cloudService.getAllClouds(person.getId());
		return new ResponseEntity<>(listCloud, HttpStatus.OK);
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(value = "{id}", method=RequestMethod.GET)
	@ApiOperation(
			value = "Retorna a instância da cloud pelo ID.",
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE
			)
	@ApiResponses(value = {
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Instância não encontrada."),
			@ApiResponse(code = HttpServletResponse.SC_FORBIDDEN, message = "Acesso não autorizado. Informe um token válido."),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Token inválido."),
			@ApiResponse(code = HttpServletResponse.SC_OK, message = "Instância retornada com sucesso.", response = Cloud.class)
	})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Token válido da pessoa", required = true, paramType = "header"),
	    @ApiImplicitParam(name = "id", value = "ID da instância", required = true, paramType = "path")
	})
	public ResponseEntity<Cloud> cloudById(@PathVariable("id") String id) {
		Cloud cloud = this.cloudService.getCloudById(id);
		if (cloud == null) {
			return new ResponseEntity<Cloud>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Cloud>(cloud, HttpStatus.OK);
	}
	
	private void setPerson(Cloud cloud, String authorization) {
		Person person = cloud.getPerson();
		if (person != null) {
			return;
		}
		
		cloud.setPerson(this.getPerson(authorization));
	}
	
	private Person getPerson(String authorization) {
		String username = TokenAuthenticationService.getUsername(authorization);
		String personId = this.personService.getIdByUsername(username);

		Person person = new Person();
		person.setId(personId);
		return person;
	}
}
