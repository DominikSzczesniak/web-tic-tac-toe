package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RequiredArgsConstructor
@RestController
public class GetWhichPlayerToMoveController {

	private final GamesFacade gamesFacade;

	@GetMapping("/api/games/{gameId}/move")
	public ResponseEntity<String> getWhichPlayerToMove(@PathVariable final Long gameId) {
		final UserId playerToMove = gamesFacade.getPlayerToMove(new TicTacToeGameId(gameId));
		return ResponseEntity.status(200).body(playerToMove.getId().toString());
	}

}
