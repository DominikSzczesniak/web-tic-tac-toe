package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetGameForPlayerController {

	private final GamesFacade gamesFacade;

	@GetMapping("/api/games")
	public ResponseEntity<Long> getGameForPlayer(@RequestParam final String playerId) {
		try {
			final TicTacToeGameId gameReadyForPlayer = gamesFacade.getGameForPlayer(new UserId(UUID.fromString(playerId)));
			return ResponseEntity.status(200).body(gameReadyForPlayer.getValue());
		} catch (IllegalArgumentException | NullPointerException e) {
			return ResponseEntity.status(400).body(null);
		}
	}

}
