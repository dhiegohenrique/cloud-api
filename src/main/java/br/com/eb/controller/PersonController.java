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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.eb.dto.Person;
import br.com.eb.dto.Person.PersonUpdateGroup;
import br.com.eb.service.ICloudService;
import br.com.eb.service.IPersonService;
import br.com.eb.validators.PersonUpdateValidator;
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
@RequestMapping(value="/person")
@Api(value = "Pessoas", description = "Pessoas")
public class PersonController extends BaseController {
	
	@Autowired
	private IPersonService personService;
	
	@Autowired
	private ICloudService cloudService;
	
	@GetMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(method=RequestMethod.POST)
	@ApiOperation(
			value = "Cadastra uma pessoa."
			)
	@ApiResponses(value = { 
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Campos obrigatórios não preenchidos ou username já cadastrado."),
			@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Pessoa cadastrada com sucesso.", responseHeaders = @ResponseHeader(name = "Location", description = "URI da pessoa cadastrada", response = String.class)),
	})
	public ResponseEntity<String> insert(@RequestBody @Valid @ApiParam(value="Dados da pessoa", required = true) Person person, BindingResult bindingResult, UriComponentsBuilder uriComponentsBuilder) {
		if (bindingResult.hasErrors()) {
			List<String> listErrors = this.getListErrors(bindingResult);
			return new ResponseEntity<String>(this.getJson(listErrors), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		this.personService.insert(person);
		
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponentsBuilder.path("/person/{id}").buildAndExpand(person.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
	
	@GetMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(value = "{id}", method=RequestMethod.PUT)
	@ApiOperation(
			value = "Atualiza uma pessoa."
			)
	@ApiResponses(value = { 
			@ApiResponse(code = HttpServletResponse.SC_FORBIDDEN, message = "Acesso não autorizado. Informe um token válido."),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Campos obrigatórios não preenchidos ou token inválido."),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Pessoa não encontrada."),
			@ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Pessoa atualizada com sucesso.")
	})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Token válido da pessoa", required = true, paramType = "header"),
	})
	public ResponseEntity<String> update(@PathVariable("id") String id, @RequestBody @ApiParam(value="Dados da pessoa", required = true) @Validated(value = PersonUpdateGroup.class) Person person, BindingResult bindingResult) {
		PersonUpdateValidator validator = new PersonUpdateValidator();
		validator.validate(person, bindingResult);
		
		if (bindingResult.hasErrors()) {
			if (bindingResult.hasErrors()) {
				List<String> listErrors = this.getListErrors(bindingResult);
				return new ResponseEntity<String>(this.getJson(listErrors), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		try {
			person.setId(id);
			this.personService.update(person);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(value = "{id}", method=RequestMethod.DELETE)
	@ApiOperation(
			value = "Deleta uma pessoa e todas as instâncias relacionadas a ela."
			)
	@ApiResponses(value = { 
			@ApiResponse(code = HttpServletResponse.SC_FORBIDDEN, message = "Acesso não autorizado. Informe um token válido."),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Token inválido."),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Pessoa não encontrada."),
			@ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Pessoa deletada com sucesso.")
	})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Token válido da pessoa", required = true, paramType = "header"),
	})
	public ResponseEntity<String> delete(@PathVariable("id") String id) {
		try {
			this.personService.delete(id);
			this.cloudService.deleteByPersonId(id);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(method=RequestMethod.GET)
	@ApiOperation(
			value = "Retorna todas as pessoas cadastradas.",
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE
			)
	@ApiResponses(value = { 
			@ApiResponse(
						code = HttpServletResponse.SC_OK, message = "Pessoas retornadas com sucesso.", 
						response = Person.class, responseContainer = "List"
					)
	})
	public ResponseEntity<List<Person>> person() {
		List<Person> listPerson = this.personService.getAllPerson();
		return new ResponseEntity<>(listPerson, HttpStatus.OK);
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(value = "{id}", method=RequestMethod.GET)
	@ApiOperation(
			value = "Retorna a pessoa pelo ID.",
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE
			)
	@ApiResponses(value = {
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Pessoa não encontrada."),
			@ApiResponse(code = HttpServletResponse.SC_FORBIDDEN, message = "Acesso não autorizado. Informe um token válido."),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Token inválido."),
			@ApiResponse(code = HttpServletResponse.SC_OK, message = "Pessoa retornada com sucesso.", response = Person.class)
	})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Token válido da pessoa", required = true, paramType = "header"),
	    @ApiImplicitParam(name = "id", value = "ID da pessoa", required = true, paramType = "path")
	})
	public ResponseEntity<Person> personById(@PathVariable("id") String id) {
		Person person = this.personService.getPersonById(id);
		if (person == null) {
			return new ResponseEntity<Person>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Person>(person, HttpStatus.OK);
	}
}