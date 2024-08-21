package pl.szczesniak.dominik.webtictactoe.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTGenerator {

	private final SecretKey key = Jwts.SIG.HS512.key().build();

	public String generateToken(final Authentication authentication, final String userId) {
		final String username = authentication.getName();
		final ZoneId zoneId = ZoneId.of("Europe/Warsaw");
		final LocalDateTime currentDateTime = LocalDateTime.now(zoneId);
		final LocalDateTime expirationDateTime = currentDateTime.plusMinutes(5);
		final Date expirationDate = Date.from(expirationDateTime.atZone(zoneId).toInstant());

		final Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userId);

		final String token = Jwts.builder()
				.claims(claims)
				.subject(username)
				.issuedAt(new Date())
				.expiration(expirationDate)
				.signWith(key)
				.compact();
		return token;
	}

	public String getUsernameFromJWT(final String token) {
		final Claims claims = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return claims.getSubject();
	}

	public UserId getUserIdFromJWT(final String token) {
		final Claims claims = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		final String userId = claims.get("userId", String.class);
		return new UserId(userId);
	}

	public boolean validateToken(final String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			throw new AuthenticationCredentialsNotFoundException("Token: " + token + " is either invalid, or expired");
		}
	}

}
