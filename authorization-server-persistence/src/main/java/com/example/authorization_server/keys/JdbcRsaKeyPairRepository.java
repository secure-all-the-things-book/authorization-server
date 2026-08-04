package com.example.authorization_server.keys;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

class JdbcRsaKeyPairRepository implements RsaKeyPairRepository {

	private final JdbcClient jdbcClient;

	private final RsaPublicKeyConverter rsaPublicKeyConverter;

	private final RsaPrivateKeyConverter rsaPrivateKeyConverter;

	private final RowMapper<RsaKeyPair> keyPairRowMapper;

	JdbcRsaKeyPairRepository(RowMapper<RsaKeyPair> keyPairRowMapper, RsaPublicKeyConverter publicConverter,
			RsaPrivateKeyConverter privateConverter, JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
		this.keyPairRowMapper = keyPairRowMapper;
		this.rsaPublicKeyConverter = publicConverter;
		this.rsaPrivateKeyConverter = privateConverter;
	}

	// <.>
	@Override
	public List<RsaKeyPair> findKeyPairs() {
		return this.jdbcClient.sql("select * from rsa_key_pairs order by created desc")
			.query(this.keyPairRowMapper)
			.list();
	}

	// <.>
	@Override
	public void save(RsaKeyPair keyPair) {
		var sql = """
				insert into rsa_key_pairs (id, private_key, public_key, created)
				values (?, ?, ?, ?)
				on conflict on constraint rsa_key_pairs_id_created_key
				do nothing
				""";
		try (var privateBaos = new ByteArrayOutputStream(); var publicBaos = new ByteArrayOutputStream()) {
			this.rsaPrivateKeyConverter.serialize(keyPair.privateKey(), privateBaos);
			this.rsaPublicKeyConverter.serialize(keyPair.publicKey(), publicBaos);
			var updated = this.jdbcClient.sql(sql)
				.param(keyPair.id())
				.param(privateBaos.toString())
				.param(publicBaos.toString())
				.param(new Date(keyPair.created().toEpochMilli()))
				.update();
			Assert.state(updated == 0 || updated == 1, "no more than one record should have been updated");
		} //
		catch (IOException e) {
			throw new IllegalArgumentException("there's been an exception", e);
		}
	}

}