package com.example.authorization_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@SpringBootApplication
public class AuthorizationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServerApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	Customizer<HttpSecurity> httpSecurityCustomizer() {
		return http -> http.oauth2AuthorizationServer(a -> a.oidc(Customizer.withDefaults()));
	}

	@Bean
	InMemoryUserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder) {
		var josh = User //
			.builder()//
			.username("josh@joshlong.com") //
			.roles("USER")//
			.password(passwordEncoder.encode("pw"))//
			.build();
		var rob = User //
			.builder()//
			.username("rob@spring.security") //
			.roles("USER", "ADMIN")//
			.password(passwordEncoder.encode("pw"))//
			.build();
		var userDetails = List.of(rob, josh);
		return new InMemoryUserDetailsManager(userDetails);
	}

}
