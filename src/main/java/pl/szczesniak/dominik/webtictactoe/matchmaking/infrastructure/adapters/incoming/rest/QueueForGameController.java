package pl.szczesniak.dominik.webtictactoe.matchmaking.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.MatchmakingFacade;
import pl.szczesniak.dominik.webtictactoe.security.JWTGenerator;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RequiredArgsConstructor
@RestController
public class QueueForGameController {

	private final MatchmakingFacade matchmakingFacade;
	private final JWTGenerator tokenGenerator;

	@PostMapping("/api/queue")
	public ResponseEntity<String> queueForGame(@RequestHeader(name = "Authorization") final String token) {
		final UserId userId = tokenGenerator.getUserIdFromJWT(token);
		matchmakingFacade.queueToPlay(userId);
		return ResponseEntity.status(201).body(userId.getValue());
	}

}
