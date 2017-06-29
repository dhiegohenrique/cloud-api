package br.com.eb.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.eb.service.ILoginService;

@Service
@Configurable
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private ILoginService loginService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        boolean findByUsername = this.loginService.findByUsername(username);
        if (!findByUsername) {
        	throw new UsernameNotFoundException("Não foi encontrado o username: " + username);
        }
        
        boolean findByUsernameAndPassword = this.loginService.findByUsernameAndPassword(username, password);
        if (!findByUsernameAndPassword) {
        	throw new BadCredentialsException("Senha inválida.");
        }
        
		return new UsernamePasswordAuthenticationToken(username, password);
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}