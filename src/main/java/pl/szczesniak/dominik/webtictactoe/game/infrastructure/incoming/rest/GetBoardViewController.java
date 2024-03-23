package pl.szczesniak.dominik.webtictactoe.game.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.game.domain.TicTacToeGameService;

@RequiredArgsConstructor
@RestController
public class GetBoardViewController {

	private final TicTacToeGameService ticTacToeGameService;

	@GetMapping("/api/games/{gameId}")
	public ResponseEntity<Character[][]> getBoardView(@PathVariable final Long gameId) {
		final Character[][] boardView = ticTacToeGameService.getBoardView(gameId);
		return ResponseEntity.status(200).body(boardView);
	}

}
