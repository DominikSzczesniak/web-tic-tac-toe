package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloseGameRestInvoker {

	private static final String URL = "/api/games/{gameId}";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<Void> closeGame(final Long gameId, final String token) {
		final HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		return restTemplate.exchange(
				URL,
				HttpMethod.DELETE,
				new HttpEntity<>(headers),
				Void.class,
				gameId
		);
	}

}
