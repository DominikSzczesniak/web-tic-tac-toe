package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.security.JWTGenerator;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
public class CloseGameController {

	private final GamesFacade gamesFacade;
	private final JWTGenerator tokenGenerator;

	@DeleteMapping("/api/games/{gameId}")
	public ResponseEntity<?> closeGame(@PathVariable final Long gameId,
									   @RequestHeader(name = "Authorization") final String token) {
		final UserId userId = tokenGenerator.getUserIdFromJWT(token);
		gamesFacade.closeGame(new TicTacToeGameId(gameId), userId);
		return ResponseEntity.status(204).build();
	}

}