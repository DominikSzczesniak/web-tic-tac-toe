package pl.szczesniak.dominik.webtictactoe.games.infrastructure.incoming.rest;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.TicTacToeGamesService;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
public class MakeMoveController {

	private final TicTacToeGamesService ticTacToeGamesService;

	@PostMapping("/api/games/{gameId}/move")
	public ResponseEntity<?> makeMove(@PathVariable final Long gameId, @RequestBody final MakeMoveDto makeMoveDto) {
		final GameResult gameResult = ticTacToeGamesService.makeMove(new MakeMove(
				new TicTacToeGameId(gameId),
				UUID.fromString(makeMoveDto.getPlayerId()),
				new PlayerMove(makeMoveDto.getRowIndex(), makeMoveDto.getColumnIndex()))
		);
		final GameResultDto gameResultDto = toDto(gameResult);
		return ResponseEntity.status(201).body(gameResultDto);
	}

	private static GameResultDto toDto(final GameResult gameResult) {
		GameResultDto gameResultDto;
		try {
			gameResultDto = new GameResultDto(gameResult.getGameStatus().toString(), gameResult.getWhoWon().getValue());
		} catch (NullPointerException e) {
			gameResultDto = new GameResultDto(gameResult.getGameStatus().toString(), null);
		}
		return gameResultDto;
	}

	@Data
	private static class MakeMoveDto {
		private final String playerId;
		private final Integer rowIndex;
		private final Integer columnIndex;
	}

	@Value
	private static class GameResultDto {
		String gameStatus;
		String playerThatWon;
	}

}
