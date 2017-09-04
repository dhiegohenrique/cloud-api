package br.com.eb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan({"br.com.eb"})
@EnableAutoConfiguration
@EnableSwagger2
public class CloudApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudApiApplication.class, args);
	}
	
	@Bean
    public Docket api() { 
        Docket docket = new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.basePackage("br.com.eb.controller"))              
          .paths(PathSelectors.any())                          
          .build()
          .apiInfo(this.getApiInfo())
          .useDefaultResponseMessages(false);
        
        List<ResponseMessage> listResponseMessage = new ArrayList<>();
        listResponseMessage.add(new ResponseMessageBuilder().code(HttpStatus.NOT_FOUND.value()).message("Usuários não encontrados.").build());
        
        docket.enableUrlTemplating(true);
        docket.useDefaultResponseMessages(false);
		return docket;                                           
    }

	@SuppressWarnings("deprecation")
	private ApiInfo getApiInfo() {
		ApiInfo apiInfo = new ApiInfo("Cloud API", 
				"Esta documentação descreve as operações da Cloud API.", 
				"1.0", 
				"", 
				"dhiego.henrique@hotmail.com", 
				"", 
				"");
		return apiInfo;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}	
}