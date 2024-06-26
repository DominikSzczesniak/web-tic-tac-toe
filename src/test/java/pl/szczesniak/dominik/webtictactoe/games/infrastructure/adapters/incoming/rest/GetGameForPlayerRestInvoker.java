package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetGameForPlayerRestInvoker {

	private static final String URL = "/api/games";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<Long> getGameForPlayer(final String playerId) {
		final String urlWithParam = URL + "?playerId=" + playerId;
		return restTemplate.exchange(
				urlWithParam,
				HttpMethod.GET,
				null,
				Long.class
		);
	}

}
