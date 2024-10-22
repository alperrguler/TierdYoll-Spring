package com.ajwalker.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;


import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class JwtManager {
	/**
	 * Token oluşturmak için gerekli parametreler
	 * SecretKey -> token imzalamak için gerekli şifre
	 * Issuer -> jwt token sahibine ait bilgi
	 * IssuerAt -> token üretilme zamanı
	 * ExpiresAt -> token geçerlilik son zamanı, bitiş anı
	 * Claim -> içerisinde KEY-VALUE şeklinde değer saklayan nesneler.
	 * NOT!!!
	 * claim nesneleri içerisinde bulunan değerler açık olarak tutulur bu nedenle
	 * gizli kalması gereken değerleri buraya eklemeyiniz.
	 * Sign -> imzalama için kullanılır mutlaka bir şifreleme algoritması vermek gerekir.
	 *
	 * []  [] -> sadece rakam ->
	 * 10  10 -> 100
	 * 50  50 -> 2.500
	 * [] [] [] [] [] [] [] [] -> 50^8 -> 1_953_125_000_000_000
	 * 50^7 -> 1sn
	 * 50^43 sn
	 */
	@Value("${java15.jwt.secret-key}")
	private String SecretKey;
	@Value("${java15.jwt.issuer}")
	private String Issuer;
	private final Long ExDate = 1000L * 300; // 5 dakika sonra iptal olsun
	
	public String createToken(Long authId){
		Date createdDate = new Date(System.currentTimeMillis());
		Date expirationDate = new Date(System.currentTimeMillis() + ExDate);
		Algorithm algorithm = Algorithm.HMAC512(SecretKey);
		String token = JWT.create()
		                  .withAudience()
		                  .withIssuer(Issuer)
		                  .withIssuedAt(createdDate)
		                  .withExpiresAt(expirationDate)
		                  .withClaim("authId", authId)
		                  .withClaim("key", "JX_15_TJJJ")
		                  .sign(algorithm);
		return token;
	}
	
	public Optional<Long> validateToken(String token){
		try{
			Algorithm algorithm = Algorithm.HMAC512(SecretKey);
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT decodedJWT = verifier.verify(token); // Bu token bize mi ait
			if(Objects.isNull(decodedJWT)) // Eğer Token doğrulanamaz ise null döner bizde empty olarak return ederiz.
				return Optional.empty();
			Long authId =  decodedJWT.getClaim("authId").asLong();
			return Optional.of(authId);
		}catch (Exception exception){
			return Optional.empty();
		}
	}
}