package pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueForGameRestInvoker {

	private static final String URL = "/api/games/queue";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<Void> queueForGame(final String username) {
		return restTemplate.exchange(
				URL,
				HttpMethod.POST,
				new HttpEntity<>(username),
				Void.class
		);
	}

}
