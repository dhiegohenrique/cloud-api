package br.com.eb.service;

import java.util.List;

import br.com.eb.dto.Cloud;

public interface ICloudService {

	Cloud insert(Cloud cloud);

	void update(Cloud cloud) throws Exception;

	Cloud getCloudById(String id);

	boolean isValidCloudByPerson(String username, String cloudId);

	Cloud findPersonByCloudId(String cloudId);

	void delete(String id) throws Exception;
	
	void deleteByPersonId(String personId);

	List<Cloud> getAllClouds(String personId);
}