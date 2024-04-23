package pl.szczesniak.dominik.webtictactoe.games.infrastructure.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.games.domain.TicTacToeGamesService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
public class QueueForGameController {

	private final TicTacToeGamesService ticTacToeGamesService;

	@PostMapping("/api/games/queue")
	public ResponseEntity<String> queueForGame(@RequestParam final String username) {
		final UUID uuid = ticTacToeGamesService.queueToPlay(new PlayerName(username));
		return ResponseEntity.status(201).body(uuid.toString());
	}

}
