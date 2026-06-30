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
	RegisteredClientRepository registeredClientRepository(JdbcTemplate template) {
		return new JdbcRegisteredClientRepository(template);
	}

	// <2>
	@Bean
	ApplicationRunner clientsRunner(PasswordEncoder pwe, RegisteredClientRepository repository) {
		return _ -> {
			var clientId = "crm";
			if (repository.findByClientId(clientId) == null) {
				var crmClientSecret = pwe.encode("crm");
				repository.save(RegisteredClient.withId(UUID.randomUUID().toString())
					.clientId(clientId)
					// you should get this from an environment variable!
					.clientSecret(crmClientSecret) // <.>
					.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
					.authorizationGrantTypes(grantTypes -> grantTypes.addAll(
							Set.of(AuthorizationGrantType.CLIENT_CREDENTIALS, AuthorizationGrantType.AUTHORIZATION_CODE,
									AuthorizationGrantType.REFRESH_TOKEN, AuthorizationGrantType.DEVICE_CODE)))
					.redirectUri("http://127.0.0.1:8080/login/oauth2/code/crm")
					.scopes(scopes -> scopes.addAll(
							Set.of("user.read", "user.write", OidcScopes.PROFILE, OidcScopes.EMAIL, OidcScopes.OPENID)))
					.build());
			}
		};
	}

}
