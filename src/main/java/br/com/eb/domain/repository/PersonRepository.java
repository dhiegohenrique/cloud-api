package br.com.eb.domain.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.com.eb.dto.Person;

@RepositoryRestResource(collectionResourceRel = "person")
public interface PersonRepository extends MongoRepository<Person, String> {

	@Query(fields = "{'password' : 1}")
	Person findPasswordByUsername(String username);
	
	@Query(fields = "{'id' : 1}")
	Person findIdByUsername(String username);
	
	@Query(value = "{}", fields = "{'name' : 1}")
	List<Person> findAll();
}
