package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMoveToMake;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.security.JWTGenerator;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RequiredArgsConstructor
@RestController
public class MakeMoveController {

	private final GamesFacade gamesFacade;
	private final JWTGenerator tokenGenerator;

	@PostMapping("/api/games/{gameId}/move")
	public ResponseEntity<?> makeMove(@PathVariable final Long gameId, @RequestBody final MakeMoveDto makeMoveDto,
									  final @RequestHeader(name = "Authorization") String token) {
		try {
			final UserId userId = tokenGenerator.getUserIdFromJWT(token);
			final GameState gameResult = gamesFacade.makeMove(new MakeMove(
					new TicTacToeGameId(gameId),
					new GameMoveToMake(makeMoveDto.getRowIndex(), makeMoveDto.getColumnIndex(), userId)
			));

			final GameStateDto gameStateDto = toDto(gameResult);

			return ResponseEntity.status(201).body(gameStateDto);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		} catch (NullPointerException e) {
			return ResponseEntity.status(400).body("Cannot pass null as an argument");
		}
	}

	private static GameStateDto toDto(final GameState gameState) {
		final String winnerId = gameState.getWhoWon()
				.map(UserId::getValue)
				.orElse(null);
		return new GameStateDto(gameState.getGameStatus().toString(), winnerId);
	}

	@Data
	public static class MakeMoveDto {
		private final String playerId;
		private final Integer rowIndex;
		private final Integer columnIndex;
	}

	@Value
	private static class GameStateDto {
		String gameStatus;
		String playerThatWon;
	}

}
