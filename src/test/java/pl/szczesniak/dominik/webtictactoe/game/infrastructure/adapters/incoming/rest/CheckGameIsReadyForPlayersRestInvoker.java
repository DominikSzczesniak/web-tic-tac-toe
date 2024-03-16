package pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckGameIsReadyForPlayersRestInvoker {

	private static final String URL = "/api/games";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<GameInfoDto> checkGameIsReady(final PlayersForTheGameDto playersForTheGameDto) {

		return restTemplate.exchange(
				URL,
				HttpMethod.GET,
				new HttpEntity<>(playersForTheGameDto),
				GameInfoDto.class
		);
	}

	@Value
	public static class GameInfoDto {
		Boolean gameIsReady;
		Long gameId;
	}

	@Data
	@Builder
	public static class PlayersForTheGameDto {
		private String playerOne;
		private String playerTwo;
	}

}
