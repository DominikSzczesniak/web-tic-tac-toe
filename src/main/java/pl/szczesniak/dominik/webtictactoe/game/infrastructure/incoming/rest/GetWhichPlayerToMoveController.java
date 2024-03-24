package pl.szczesniak.dominik.webtictactoe.game.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.game.domain.TicTacToeGameService;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class GetWhichPlayerToMoveController {

	private final TicTacToeGameService ticTacToeGameService;

	@GetMapping("/api/games/{gameId}/move")
	public ResponseEntity<String> getWhichPlayerToMove(@PathVariable final Long gameId) {
		final UUID playerToMove = ticTacToeGameService.getPlayerToMove(new TicTacToeGameId(gameId));
		return ResponseEntity.status(200).body(playerToMove.toString());
	}

}
