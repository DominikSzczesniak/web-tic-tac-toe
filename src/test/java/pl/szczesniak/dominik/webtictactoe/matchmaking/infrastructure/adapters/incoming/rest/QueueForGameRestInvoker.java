package pl.szczesniak.dominik.webtictactoe.matchmaking.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueForGameRestInvoker {

	private static final String URL = "/api/queue";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<String> queueForGame(final String username) {
		final String urlWithParam = URL + "?username=" + username;
		return restTemplate.exchange(
				urlWithParam,
				HttpMethod.POST,
				null,
				String.class
		);
	}

}
