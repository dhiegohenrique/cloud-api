package br.com.eb.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.eb.service.ILoginService;

public class UsernameValidator implements ConstraintValidator<IUsernameValidator, String> {

	public static final String message = "O username informado já está cadastrado.";
	private final String messageUsernameEmpty = "Informe o username.";
	
	@Autowired
	private ILoginService loginService;

	public void initialize(IUsernameValidator constraintAnnotation) {
	}

	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {
		if (StringUtils.isBlank(username)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(this.messageUsernameEmpty).addConstraintViolation();
			return false;
		}
		
		return !this.loginService.findByUsername(username);
	}
}