package br.com.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.eb.domain.repository.PersonRepository;
import br.com.eb.dto.Person;

@Service
public class PersonServiceImpl implements IPersonService {

	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public List<Person> getAllPerson() {
		return this.personRepository.findAll();
	}
	
	@Override
	public Person getPersonById(String id) {
		return this.personRepository.findOne(id);
	}
	
	@Override
	public Person insert(Person person) {
		person.setPassword(this.encryptPassword(person.getPassword()));
		this.personRepository.save(person);
		return person;
	}
	
	private String encryptPassword(String password) {
		return this.passwordEncoder.encode(password);
	}
	
	@Override
	public void update(Person person) throws Exception {
		if (!this.personRepository.exists(person.getId())) {
			throw new Exception("Pessoa não encontrada");
		}
		
		Person personById = this.getPersonById(person.getId());
		personById.setName(person.getName());
		
		if (!personById.getPassword().equals(person.getPassword())) {
			personById.setPassword(this.encryptPassword(person.getPassword()));
		}
		
		this.personRepository.save(personById);
	}
	
	@Override
	public void delete(String id) throws Exception {
		if (!this.personRepository.exists(id)) {
			throw new Exception("Pessoa não encontrada");
		}
		
		this.personRepository.delete(id);
	}

	@Override
	public String getIdByUsername(String username) {
		Person person = this.personRepository.findIdByUsername(username);
		return person.getId();
	}
}