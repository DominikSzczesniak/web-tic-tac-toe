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
public class MakeMoveRestInvoker {

	private static final String URL = "/api/games/{gameId}/move";

	public final TestRestTemplate restTemplate;

	public ResponseEntity<GameResultDto> makeMove(final Long gameId, final MakeMoveDto makeMoveDto) {
		return restTemplate.exchange(
				URL,
				HttpMethod.POST,
				new HttpEntity<>(makeMoveDto),
				GameResultDto.class,
				gameId
		);
	}

	@Data
	@Builder
	public static class MakeMoveDto {
		private String playerName;
		private Integer rowIndex;
		private Integer columnIndex;
	}

	@Value
	public static class GameResultDto {
		String gameStatus;
		String playerThatWon;
	}

}
