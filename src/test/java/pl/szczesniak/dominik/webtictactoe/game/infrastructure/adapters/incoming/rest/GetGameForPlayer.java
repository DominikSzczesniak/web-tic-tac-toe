package pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetGameForPlayer {

	private static final String URL = "/api/games";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<Long> getGameForPlayer(final String playerName) {
		final String urlWithParam = URL + "?playerName=" + playerName;
		return restTemplate.exchange(
				urlWithParam,
				HttpMethod.GET,
				null,
				Long.class
		);
	}

}
