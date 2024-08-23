package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RequiredArgsConstructor
@RestController
public class GetGameInfoController {

	private final GamesFacade gamesFacade;

	@GetMapping("/api/games/{gameId}")
	public ResponseEntity<GameInfoDto> getGameState(@PathVariable final Long gameId,
													@RequestHeader(name = "Authorization") final String token) {
		final GameInfo gameInfo = gamesFacade.getGameInfo(new TicTacToeGameId(gameId));
		final GameInfoDto dto = toDto(gameInfo);
		return ResponseEntity.status(200).body(dto);
	}

	private GameInfoDto toDto(final GameInfo gameInfo) {
		final String winnerId = gameInfo.getGameState().getWhoWon()
				.map(UserId::getValue)
				.orElse(null);
		return new GameInfoDto(gameInfo.getPlayerToMove().getValue(), gameInfo.getBoardView(), gameInfo.getGameState().getGameStatus().toString(), winnerId);
	}

	@Data
	public static class GameInfoDto {
		private final String playerToMove;
		private final Character[][] boardView;
		private final String gameStatus;
		private final String playerThatWon;
	}

}
