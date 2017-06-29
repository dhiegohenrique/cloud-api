package br.com.eb.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.com.eb.dto.Cloud;

@RepositoryRestResource(collectionResourceRel = "cloud")
public interface CloudRepository extends MongoRepository<Cloud, String> {

	@Query(value = "{'id' : ?0}", fields = "{'person' : 1}")
	Cloud findPersonByCloudId(String cloudId);
}
