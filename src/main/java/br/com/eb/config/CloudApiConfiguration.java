package br.com.eb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class CloudApiConfiguration {

	@Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        String activeProfile = System.getProperty("spring.profiles.active", "dev");
        String propertiesFilename = "application." + activeProfile + ".properties";
		
		final PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
	    configurer.setLocation(new ClassPathResource(propertiesFilename));
	    configurer.setIgnoreResourceNotFound(true);
	    configurer.setIgnoreUnresolvablePlaceholders(false);
	    return configurer;
    }
}