package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetGameInfoRestInvoker {

	private static final String URL = "/api/games/{gameId}";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<GameInfoDTO> getGameInfo(final Long gameId) {
		return restTemplate.exchange(
				URL,
				HttpMethod.GET,
				new HttpEntity<>(null),
				GameInfoDTO.class,
				gameId
		);
	}

	@Value
	public static class GameInfoDTO {
		@NonNull String playerToMove;
		@NonNull Character[][] boardView;
		String gameStatus;
		String playerThatWon;
	}

}
