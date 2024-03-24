package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrepareGameControllerRestInvoker {

	private static final String URL = "/api/games";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<TicTacToeGameDto> prepareGame() {

		return restTemplate.exchange(
				URL,
				HttpMethod.POST,
				new HttpEntity<>(null),
				TicTacToeGameDto.class
		);
	}

	@Value
	public static class TicTacToeGameDto {
		String playerOne;
		String playerOneId;
		String playerTwo;
		String playerTwoId;
		Long gameId;
	}

}
