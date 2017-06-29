package br.com.eb.service;

import java.util.List;

import br.com.eb.dto.Person;

public interface IPersonService {

	List<Person> getAllPerson();

	Person getPersonById(String id);

	Person insert(Person person);

	void update(Person person) throws Exception;

	void delete(String id) throws Exception;
	
	String getIdByUsername(String username);
}