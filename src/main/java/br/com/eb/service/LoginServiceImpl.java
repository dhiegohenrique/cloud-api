package br.com.eb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.eb.domain.repository.PersonRepository;
import br.com.eb.dto.Person;

@Service
public class LoginServiceImpl implements ILoginService {
	
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public boolean findByUsername(String username) {
		Person person = new Person();
		person.setUsername(username);
		
		ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("username", GenericPropertyMatchers.exact());
		Example<Person> example = Example.<Person>of(person, exampleMatcher);
		
		return this.personRepository.exists(example);
	}

	@Override
	public boolean findByUsernameAndPassword(String username, String password) {
		Person person = this.personRepository.findPasswordByUsername(username);
		return this.passwordEncoder.matches(password, person.getPassword());
	}
	
	@Override
	public boolean isValidTokenByPerson(String username, String personId) {
		Person person = this.personRepository.findIdByUsername(username);
		if (person == null) {
			return true;
		}
		
		return personId.equals(person.getId());
	}
}