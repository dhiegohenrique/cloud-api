package br.com.eb.service;

public interface ILoginService {

	boolean findByUsername(String username);
	
	boolean findByUsernameAndPassword(String username, String password);

	boolean isValidTokenByPerson(String username, String personId);
}