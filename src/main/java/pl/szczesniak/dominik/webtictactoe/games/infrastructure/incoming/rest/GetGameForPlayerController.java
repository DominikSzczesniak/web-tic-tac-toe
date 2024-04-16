package pl.szczesniak.dominik.webtictactoe.games.infrastructure.incoming.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.TicTacToeGamesService;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class GetGameForPlayerController {

	private final TicTacToeGamesService ticTacToeGamesService;

	@GetMapping("/api/games")
	public ResponseEntity<Long> getGameForPlayer(@RequestParam final String playerId) {
		final TicTacToeGameId gameReadyForPlayer = ticTacToeGamesService.getGameForPlayer(UUID.fromString(playerId));
		return ResponseEntity.status(200).body(gameReadyForPlayer.getValue());
	}

}
