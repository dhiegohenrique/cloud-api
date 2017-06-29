package br.com.eb.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.com.eb.dto.Person;

public class PersonUpdateValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Person.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
	}
}