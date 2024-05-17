package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;

@RequiredArgsConstructor
@RestController
public class GetBoardViewController {

	private final GamesFacade gamesFacade;

	@GetMapping("/api/games/{gameId}")
	public ResponseEntity<Character[][]> getBoardView(@PathVariable final Long gameId) {
		final Character[][] boardView = gamesFacade.getBoardView(gameId);
		return ResponseEntity.status(200).body(boardView);
	}

}
