package com.example.authorization_server.keys;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Profile("keys")
@Configuration
class ConvertersConfiguration {

	@Bean
	JdbcRsaKeyPairRepository jdbcRsaKeyPairRepository(RsaKeyPairRowMapper rsaKeyPairRowMapper,
			RsaPublicKeyConverter rsaPublicKeyConverter, RsaPrivateKeyConverter rsaPrivateKeyConverter,
			JdbcTemplate template) {
		return new JdbcRsaKeyPairRepository(rsaKeyPairRowMapper, rsaPublicKeyConverter, rsaPrivateKeyConverter,
				template);
	}

	@Bean
	RsaKeyPairRepositoryJWKSource rsaKeyPairRepositoryJWKSource(RsaKeyPairRepository repository) {
		return new RsaKeyPairRepositoryJWKSource(repository);
	}

	@Bean
	RsaKeyPairRowMapper rsaKeyPairRowMapper(RsaPrivateKeyConverter rsaPrivateKeyConverter,
			RsaPublicKeyConverter rsaPublicKeyConverter) {
		return new RsaKeyPairRowMapper(rsaPrivateKeyConverter, rsaPublicKeyConverter);
	}

	@Bean
	RsaPublicKeyConverter rsaPublicKeyConverter(TextEncryptor textEncryptor) {
		return new RsaPublicKeyConverter(textEncryptor);
	}

	@Bean
	RsaPrivateKeyConverter rsaPrivateKeyConverter(TextEncryptor textEncryptor) {
		return new RsaPrivateKeyConverter(textEncryptor);
	}

}
