package com.itgirls.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class JwtKeyConfig {

    public static final String RSA_ALGORITHM = "RSA";

    @Value("${spring.security.oauth2.resourceserver.jwt.public-key}")
    private String publicKeyPem;

    @Bean
    public PublicKey publicKey() throws Exception {
        publicKeyPem = publicKeyPem.replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPem);
        return KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(PublicKey publicKey) {
        return NimbusReactiveJwtDecoder.withPublicKey((RSAPublicKey) publicKey).build();
    }
}