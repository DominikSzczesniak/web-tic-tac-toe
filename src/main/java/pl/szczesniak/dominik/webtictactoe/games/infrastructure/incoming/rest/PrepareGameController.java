package pl.szczesniak.dominik.webtictactoe.games.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.TicTacToeGamesService;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

@RequiredArgsConstructor
@RestController
public class PrepareGameController {

	private final TicTacToeGamesService ticTacToeGamesService;

	@PostMapping("/api/games")
	public ResponseEntity<Long> prepareGame() {
		final TicTacToeGameId gameId = ticTacToeGamesService.prepareGame();
		return ResponseEntity.status(201).body(gameId.getValue());
	}

}
