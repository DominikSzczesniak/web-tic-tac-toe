package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.outgoing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class ServerSentEventsController {

	private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

	@GetMapping("/api/subscribe/games/{gameId}")
	public SseEmitter subscribeToGame(@PathVariable final Long gameId) {
		final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.computeIfAbsent(gameId, k -> new ArrayList<>()).add(emitter);

		emitter.onCompletion(() -> removeEmitter(gameId, emitter));
		emitter.onTimeout(() -> removeEmitter(gameId, emitter));

		return emitter;
	}

	private void removeEmitter(final Long gameId, final SseEmitter emitter) {
		final List<SseEmitter> gameEmitters = emitters.getOrDefault(gameId, new ArrayList<>());
		if (!gameEmitters.isEmpty()) {
			gameEmitters.remove(emitter);
			if (gameEmitters.isEmpty()) {
				emitters.remove(gameId);
			}
		}
	}

	public void handleMoveMadeEvent(final MoveMade event) {
		final List<SseEmitter> gameEmitters = emitters.getOrDefault(event.getGameId(), new ArrayList<>());
		gameEmitters.forEach(emitter -> {
			try {
				emitter.send(SseEmitter.event().name("moveMade").data(event.getGameStatusInfo().getWhoWon()));
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		});
	}

}
