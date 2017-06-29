package br.com.eb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import br.com.eb.domain.repository.CloudRepository;
import br.com.eb.dto.Cloud;

@Service
public class CloudServiceImpl implements ICloudService {

	@Autowired
	private CloudRepository cloudRepository;
	
	@Autowired
	private IPersonService personService;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public Cloud insert(Cloud cloud) {
		cloud.setActive(true);
		
		Date createDate = cloud.getCreateDate();
		if (createDate == null) {
			createDate = new Date();
		}
		cloud.setCreateDate(createDate);
		
		Date updateDate = cloud.getUpdateDate();
		if (updateDate == null) {
			updateDate = new Date();
		}
		
		cloud.setUpdateDate(updateDate);
		this.cloudRepository.save(cloud);
		return cloud;
	}
	
	@Override
	public void update(Cloud cloud) throws Exception {
		if (!this.cloudRepository.exists(cloud.getId())) {
			throw new Exception("Instância não encontrada");
		}
		
		Cloud cloudById = this.getCloudById(cloud.getId());
		cloudById.setActive(cloud.isActive());
		cloudById.setCapacity(cloud.getCapacity());
		cloudById.setName(cloud.getName());
		cloudById.setOperationalSystem(cloud.getOperationalSystem());
		cloudById.setUpdateDate(new Date());
		this.insert(cloudById);
	}
	
	@Override
	public Cloud getCloudById(String id) {
		return this.cloudRepository.findOne(id);
	}
	
	@Override
	public boolean isValidCloudByPerson(String username, String cloudId) {
		Cloud cloud = this.findPersonByCloudId(cloudId);
		if (cloud == null) {
			return true;
		}

		String personId = this.personService.getIdByUsername(username);
		return cloud.getPerson().getId().equals(personId);

//		Cloud cloud = new Cloud();
//		cloud.setId(cloudId);
//		cloud.setPerson(new Person(personId));
//		
//		ExampleMatcher exampleMatcher = ExampleMatcher.matching()
//										.withMatcher("person", GenericPropertyMatchers.exact())
//										.withMatcher("id", GenericPropertyMatchers.exact());
//		
//		Example<Cloud> example = Example.<Cloud>of(cloud, exampleMatcher);
//		return this.cloudRepository.exists(example);
	}

	@Override
	public Cloud findPersonByCloudId(String cloudId) {
		return this.cloudRepository.findPersonByCloudId(cloudId);
	}
	
	@Override
	public void delete(String id) throws Exception {
		if (!this.cloudRepository.exists(id)) {
			throw new Exception("Instância não encontrada");
		}
		
		this.cloudRepository.delete(id);
	}
	
	@Override
	public void deleteByPersonId(String personId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("person").is(personId));
		this.mongoTemplate.remove(query, Cloud.class);
	}
	
	@Override
	public List<Cloud> getAllClouds(String personId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("person").is(personId));
		return this.mongoTemplate.find(query, Cloud.class);
	}
}