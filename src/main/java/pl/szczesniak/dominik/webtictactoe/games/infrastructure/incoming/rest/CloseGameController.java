package pl.szczesniak.dominik.webtictactoe.games.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

@RequiredArgsConstructor
@RestController
public class CloseGameController {

	private final GamesFacade gamesFacade;

	@DeleteMapping("/api/games/{gameId}")
	public ResponseEntity<?> closeGame(@PathVariable final Long gameId) {
		gamesFacade.closeGame(new TicTacToeGameId(gameId));
		return ResponseEntity.status(204).build();
	}

}