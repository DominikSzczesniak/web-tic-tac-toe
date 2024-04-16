package pl.szczesniak.dominik.webtictactoe.games.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.TicTacToeGamesService;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class GetBoardViewController {

	private final TicTacToeGamesService ticTacToeGamesService;

	@GetMapping("/api/games/{gameId}")
	public ResponseEntity<Character[][]> getBoardView(@PathVariable final Long gameId) {
		final Character[][] boardView = ticTacToeGamesService.getBoardView(gameId);
		return ResponseEntity.status(200).body(boardView);
	}

}
