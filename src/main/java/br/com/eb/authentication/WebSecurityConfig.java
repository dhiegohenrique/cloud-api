package br.com.eb.authentication;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.eb.filter.JWTAuthenticationFilter;
import br.com.eb.filter.JWTLoginFilter;

@Configuration
@EnableWebSecurity
//@EnableWebMvc
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomAuthenticationProvider provider;
	
	@Autowired
	private JWTAuthenticationFilter jwtAuthenticationFilter;
	
	public WebSecurityConfig() {
	    super(false);
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable().authorizeRequests().antMatchers("/").permitAll()
			.antMatchers(HttpMethod.POST, "/person").permitAll()
			.antMatchers("/person/{id}").authenticated()
			.antMatchers("/cloud").authenticated()
			.antMatchers("/cloud/*").authenticated()
			.antMatchers(HttpMethod.POST, "/login").permitAll().and()
			.addFilterBefore(new JWTLoginFilter("/login", this.authenticationManager()), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
//		httpSecurity.cors().configurationSource(new CorsConfigurationSource() {
//			
//			@Override
//			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//				CorsConfiguration corsConfiguration = new CorsConfiguration();
//				corsConfiguration.addAllowedOrigin("http://localhost:3001");
//				return corsConfiguration;
//			}
//		});
		httpSecurity.cors().configurationSource(this.corsConfigurationSource());
	}
	
	@Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:3001");
        corsConfiguration.addAllowedOrigin("https://cloud-client.herokuapp.com");
        corsConfiguration.addAllowedMethod(HttpMethod.GET);
        corsConfiguration.addAllowedMethod(HttpMethod.POST);
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
        corsConfiguration.addExposedHeader("Authorization");
        corsConfiguration.addExposedHeader("Location");
//        corsConfiguration.addExposedHeader("Access-Control-Expose-Headers");
//        corsConfiguration.setExposedHeaders(Arrays.asList("teste"));
//        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization"));

//        source.registerCorsConfiguration("/**", corsConfiguration);
		source.registerCorsConfiguration("/**", corsConfiguration.applyPermitDefaultValues());
//		source.registerCorsConfiguration("/login", corsConfiguration);
        return source;
    }

	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
	    return new ProviderManager(Arrays.asList((AuthenticationProvider) this.provider));
	}
	
	@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**");
    }
}