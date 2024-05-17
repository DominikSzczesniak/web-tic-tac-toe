package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class MakeMoveController {

	private final GamesFacade gamesFacade;

	@PostMapping("/api/games/{gameId}/move")
	public ResponseEntity<?> makeMove(@PathVariable final Long gameId, @RequestBody final MakeMoveDto makeMoveDto) {
		try {
			final GameInfo gameResult = gamesFacade.makeMove(new MakeMove(
					new TicTacToeGameId(gameId),
					new UserId(UUID.fromString(makeMoveDto.getPlayerId())),
					new PlayerMove(makeMoveDto.getRowIndex(), makeMoveDto.getColumnIndex()))
			);

			final GameResultDto gameResultDto = toDto(gameResult);

			return ResponseEntity.status(201).body(gameResultDto);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch (NullPointerException e) {
			return ResponseEntity.status(400).body("Cannot pass null as an argument");
		}
	}

	private static GameResultDto toDto(final GameInfo gameResult) {
		final String winnerId = gameResult.getWhoWon()
				.map(player -> player.getId().toString())
				.orElse(null);
		return new GameResultDto(gameResult.getGameStatus().toString(), winnerId);
	}

	@Data
	static class MakeMoveDto {
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
