package pl.szczesniak.dominik.webtictactoe.games.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.TicTacToeGamesService;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class GetWhichPlayerToMoveController {

	private final TicTacToeGamesService ticTacToeGamesService;

	@GetMapping("/api/games/{gameId}/move")
	public ResponseEntity<String> getWhichPlayerToMove(@PathVariable final Long gameId) {
		final UUID playerToMove = ticTacToeGamesService.getPlayerToMove(new TicTacToeGameId(gameId));
		return ResponseEntity.status(200).body(playerToMove.toString());
	}

}
