package pl.szczesniak.dominik.webtictactoe.game.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.game.domain.TicTacToeGameService;
import pl.szczesniak.dominik.webtictactoe.game.domain.TicTacToeGame;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.NotEnoughPlayersException;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:63342")
public class CheckGameIsReadyController {

	private final TicTacToeGameService ticTacToeGameService;

	@GetMapping("/api/games")
	public ResponseEntity<TicTacToeGameDto> checkGameIsReady() {
		try {
			final TicTacToeGame game = ticTacToeGameService.prepareGame();
			final TicTacToeGameDto ticTacToeGameDto = toDto(game);
			return ResponseEntity.status(201).body(ticTacToeGameDto);

		} catch (NotEnoughPlayersException e) {
			return ResponseEntity.status(404).body(null);
		}
	}

	private static TicTacToeGameDto toDto(final TicTacToeGame game) {
		return new TicTacToeGameDto(
				game.getPlayerOne().getName().getValue(),
				game.getPlayerTwo().getName().getValue(),
				true,
				game.getGameId().getValue()
		);
	}

	@Value
	private static class TicTacToeGameDto {
		String playerOne;
		String playerTwo;
		Boolean gameIsReady;
		Long gameId;
	}

}
