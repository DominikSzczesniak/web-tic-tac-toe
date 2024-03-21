package pl.szczesniak.dominik.webtictactoe.game.infrastructure.incoming.rest;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.game.domain.TicTacToeGameService;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.GameDoesNotExistException;

@RequiredArgsConstructor
@RestController
public class MakeMoveController {

	private final TicTacToeGameService ticTacToeGameService;

	@PostMapping("/api/games/{gameId}/move")
	public ResponseEntity<?> makeMove(@PathVariable final Long gameId, @RequestBody final MakeMoveDto makeMoveDto) {
		try {
			final GameResult gameResult = ticTacToeGameService.makeMove(new MakeMove(
					new TicTacToeGameId(gameId),
					new PlayerName(makeMoveDto.getPlayerName()),
					new PlayerMove(makeMoveDto.getRowIndex(), makeMoveDto.getColumnIndex()))
			);
			final GameResultDto gameResultDto = toDto(gameResult);
			return ResponseEntity.status(201).body(gameResultDto);
		} catch (GameDoesNotExistException e) {
			return ResponseEntity.status(404).body(e.toString());
		}
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
		private final String playerName;
		private final Integer rowIndex;
		private final Integer columnIndex;
	}

	@Value
	private static class GameResultDto {
		String gameStatus;
		String playerThatWon;
	}

}
