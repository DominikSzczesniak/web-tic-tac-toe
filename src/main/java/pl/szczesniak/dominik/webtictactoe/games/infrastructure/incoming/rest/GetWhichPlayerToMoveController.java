package pl.szczesniak.dominik.webtictactoe.games.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
public class GetWhichPlayerToMoveController {

	private final GamesFacade gamesFacade;

	@GetMapping("/api/games/{gameId}/move")
	public ResponseEntity<String> getWhichPlayerToMove(@PathVariable final Long gameId) {
		final UserId playerToMove = gamesFacade.getPlayerToMove(new TicTacToeGameId(gameId));
		return ResponseEntity.status(200).body(playerToMove.getId().toString());
	}

}
