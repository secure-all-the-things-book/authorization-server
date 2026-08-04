package com.example.authorization_server;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.Set;
import java.util.UUID;

@Configuration
class ClientsConfiguration {

	// <1>
	@Bean
	JdbcRegisteredClientRepository registeredClientRepository(JdbcTemplate template) {
		return new JdbcRegisteredClientRepository(template);
	}

	// <2>
	@Bean
	ApplicationRunner clientsRunner(PasswordEncoder pwe, RegisteredClientRepository repository) {
		return _ -> {
			var clientId = "spring";
			if (repository.findByClientId(clientId) == null) {
				var crmClientSecret = pwe.encode("spring");
				repository.save(RegisteredClient.withId(UUID.randomUUID().toString())
					.clientId(clientId)
					.clientSecret(crmClientSecret) // <.>
					.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
					.authorizationGrantTypes(
							grantTypes -> grantTypes.addAll(Set.of(AuthorizationGrantType.CLIENT_CREDENTIALS, //
									AuthorizationGrantType.AUTHORIZATION_CODE, //
									AuthorizationGrantType.REFRESH_TOKEN, //
									AuthorizationGrantType.DEVICE_CODE //
				)))
					.redirectUri("http://127.0.0.1:8080/login/oauth2/code/spring")
					.scopes(scopes -> scopes.addAll( //
							Set.of(OidcScopes.OPENID, OidcScopes.PROFILE, //
									OidcScopes.EMAIL, "user.write", "user.read")))
					.build());
			}
		};
	}

}
