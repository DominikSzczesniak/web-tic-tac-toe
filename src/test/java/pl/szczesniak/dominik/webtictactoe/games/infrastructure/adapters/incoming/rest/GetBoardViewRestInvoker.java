package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetBoardViewRestInvoker {

	private static final String URL = "/api/games/{gameId}";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<Character[][]> getBoardView(final Long gameId) {
		return restTemplate.exchange(
				URL,
				HttpMethod.GET,
				new HttpEntity<>(null),
				Character[][].class,
				gameId
		);
	}

}
