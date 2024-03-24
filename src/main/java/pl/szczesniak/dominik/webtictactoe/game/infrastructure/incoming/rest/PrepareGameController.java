package pl.szczesniak.dominik.webtictactoe.game.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.game.domain.TicTacToeGame;
import pl.szczesniak.dominik.webtictactoe.game.domain.TicTacToeGameService;

@RequiredArgsConstructor
@RestController
public class PrepareGameController {

	private final TicTacToeGameService ticTacToeGameService;

	@PostMapping("/api/games")
	public ResponseEntity<TicTacToeGameDto> prepareGame() {
		final TicTacToeGame game = ticTacToeGameService.prepareGame();
		final TicTacToeGameDto ticTacToeGameDto = toDto(game);
		return ResponseEntity.status(201).body(ticTacToeGameDto);
	}

	private static TicTacToeGameDto toDto(final TicTacToeGame game) {
		return new TicTacToeGameDto(
				game.getPlayerOne().getName().getValue(),
				game.getPlayerOne().getPlayerID().toString(),
				game.getPlayerTwo().getName().getValue(),
				game.getPlayerTwo().getPlayerID().toString(),
				game.getGameId().getValue()
		);
	}

	@Value
	private static class TicTacToeGameDto {
		String playerOne;
		String playerOneId;
		String playerTwo;
		String playerTwoId;
		Long gameId;
	}

}
