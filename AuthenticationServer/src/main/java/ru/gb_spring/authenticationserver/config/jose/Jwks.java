package ru.gb_spring.authenticationserver.config.jose;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * The type Jwks.
 */
public final class Jwks {

	private Jwks() {
	}

    /**
     * Generate rsa rsa key.
     *
     * @return the rsa key
     */
    public static RSAKey generateRsa() {
		KeyPair keyPair = KeyGeneratorUtils.generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		return new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
	}

    /**
     * Generate ec ec key.
     *
     * @return the ec key
     */
    public static ECKey generateEc() {
		KeyPair keyPair = KeyGeneratorUtils.generateEcKey();
		ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
		ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
		Curve curve = Curve.forECParameterSpec(publicKey.getParams());
		return new ECKey.Builder(curve, publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
	}

    /**
     * Generate secret octet sequence key.
     *
     * @return the octet sequence key
     */
    public static OctetSequenceKey generateSecret() {
		SecretKey secretKey = KeyGeneratorUtils.generateSecretKey();
		return new OctetSequenceKey.Builder(secretKey)
				.keyID(UUID.randomUUID().toString())
				.build();
	}
}
