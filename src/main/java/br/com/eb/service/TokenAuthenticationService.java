package br.com.eb.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenAuthenticationService {

	static final int EXPIRATION_SECONDS = 3600;
	
	static final String SECRET = "ClOUd&%Api@#";
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";
	
	@Autowired
	private ILoginService loginService;
	
	@Autowired
	private ICloudService cloudService;

	public static void addAuthentication(HttpServletResponse response, String username) {
		String JWT = generateToken(username);
		response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
	}
	
	public static String generateToken(String username) {
		return generateToken(username, EXPIRATION_SECONDS);
	}
	
	public static String generateToken(String username, int expirationSeconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, expirationSeconds);
		
		String JWT = Jwts.builder().setSubject(username)
				.setExpiration(calendar.getTime())
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		
		return JWT;
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (token == null) {
			return null;
		}

		String username = getUsername(token);
		if (StringUtils.isBlank(username)) {
			return null;
		}
		
		String requestURI = request.getRequestURI();
		if (requestURI.contains("person")) {
			String personId = StringUtils.substringAfterLast(requestURI, "/");
			if (!this.loginService.isValidTokenByPerson(username, personId)) {
				return null;
			}
		}
		
		if (requestURI.contains("cloud")) {
			String cloudId = StringUtils.substringAfterLast(requestURI, "/");
			if (!this.cloudService.isValidCloudByPerson(username, cloudId)) {
				return null;
			}
		}
		
		return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
	}
	
	public static String getUsername(String token) {
		return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody().getSubject();
	}
}