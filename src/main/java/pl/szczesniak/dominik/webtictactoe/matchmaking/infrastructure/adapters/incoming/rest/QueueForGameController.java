package pl.szczesniak.dominik.webtictactoe.matchmaking.infrastructure.adapters.incoming.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.MatchmakingFacade;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class QueueForGameController {

	private final MatchmakingFacade matchmakingFacade;

	@PostMapping("/api/queue")
	public ResponseEntity<String> queueForGame(@RequestParam final String username) {
		final UserId playerId = matchmakingFacade.queueToPlay(new PlayerName(username));
		return ResponseEntity.status(201).body(playerId.getId());
	}

}
