package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

@RequiredArgsConstructor
@RestController
public class GetGameInfoController {

	private final GamesFacade gamesFacade;

	@GetMapping("/api/games/{gameId}")
	public ResponseEntity<GameInfoDto> getGameState(@PathVariable final Long gameId) {
		final GameInfo gameInfo = gamesFacade.getGameInfo(new TicTacToeGameId(gameId));
		final GameInfoDto dto = toDto(gameInfo);
		return ResponseEntity.status(200).body(dto);
	}

	private GameInfoDto toDto(final GameInfo gameInfo) {
		return new GameInfoDto(gameInfo.getPlayerToMove().getValue(), gameInfo.getBoardView());
	}

	@Data
	public static class GameInfoDto {
		private final String userId;
		private final Character[][] boardView;
	}

}
