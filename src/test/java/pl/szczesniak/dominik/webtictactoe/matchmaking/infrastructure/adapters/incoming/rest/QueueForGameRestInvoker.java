package pl.szczesniak.dominik.webtictactoe.matchmaking.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueForGameRestInvoker {

	private static final String URL = "/api/queue";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<String> queueForGame(final String token) {
		final HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		return restTemplate.exchange(
				URL,
				HttpMethod.POST,
				new HttpEntity<>(headers),
				String.class
		);
	}

}
