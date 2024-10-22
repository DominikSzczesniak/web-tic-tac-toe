package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.security.JWTGenerator;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RestController
@RequiredArgsConstructor
public class GetGameForPlayerController {

	private final GamesFacade gamesFacade;
	private final JWTGenerator tokenGenerator;

	@GetMapping("/api/games")
	public ResponseEntity<Long> getGameForPlayer(@RequestHeader(name = "Authorization") final String token) {
		try {
			final UserId userId = tokenGenerator.getUserIdFromJWT(token);
			final TicTacToeGameId gameReadyForPlayer = gamesFacade.getGameForPlayer(userId);
			return ResponseEntity.status(200).body(gameReadyForPlayer.getValue());
		} catch (IllegalArgumentException | NullPointerException e) {
			return ResponseEntity.status(400).body(null);
		}
	}

}
