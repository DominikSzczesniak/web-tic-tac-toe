package pl.szczesniak.dominik.webtictactoe.game.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.game.domain.TicTacToeGameService;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;

@RequiredArgsConstructor
@RestController
public class CloseGameController {

	private final TicTacToeGameService ticTacToeGameService;

	@DeleteMapping("/api/games/{gameId}")
	public ResponseEntity<?> closeGame(@PathVariable final Long gameId) {
		ticTacToeGameService.closeGame(new TicTacToeGameId(gameId));
		return ResponseEntity.status(204).build();
	}

}
