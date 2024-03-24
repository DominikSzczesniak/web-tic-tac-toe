package pl.szczesniak.dominik.webtictactoe.game.infrastructure.incoming.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.game.domain.TicTacToeGameService;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetGameForPlayerController {

	private final TicTacToeGameService ticTacToeGameService;

	@GetMapping("/api/games")
	public ResponseEntity<Long> getGameForPlayer(@RequestParam final String playerId) {
		final TicTacToeGameId gameReadyForPlayer = ticTacToeGameService.getGameForPlayer(UUID.fromString(playerId));
		return ResponseEntity.status(200).body(gameReadyForPlayer.getValue());
	}

}
