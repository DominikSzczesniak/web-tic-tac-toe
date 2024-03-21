package pl.szczesniak.dominik.webtictactoe.game.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.game.domain.TicTacToeGameService;

@RequiredArgsConstructor
@RestController
public class QueueForGameController {

	private final TicTacToeGameService ticTacToeGameService;

	@PostMapping("/api/games/queue")
	public ResponseEntity<?> queueForGame(@RequestParam final String username) {
		ticTacToeGameService.queueToPlay(new PlayerName(username));
		return ResponseEntity.status(201).build();
	}

}
