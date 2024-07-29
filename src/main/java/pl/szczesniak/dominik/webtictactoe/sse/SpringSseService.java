package pl.szczesniak.dominik.webtictactoe.sse;

import lombok.Value;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringSseService implements SseService {

	private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

	public SseEmitter subscribeToGame(final Long gameId) {
		final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.computeIfAbsent(gameId, k -> new ArrayList<>()).add(emitter);

		emitter.onCompletion(() -> removeEmitter(gameId, emitter));
		emitter.onTimeout(() -> removeEmitter(gameId, emitter));
		emitter.onError((e) -> removeEmitter(gameId, emitter));

		return emitter;
	}

	private void removeEmitter(final Long gameId, final SseEmitter emitter) {
		final List<SseEmitter> gameEmitters = getEmitters(gameId);
		if (!gameEmitters.isEmpty()) {
			gameEmitters.remove(emitter);
			if (gameEmitters.isEmpty()) {
				emitters.remove(gameId);
			}
		}
	}

	public void handleMoveMadeEvent(final MoveMadeDTO event) {
		final List<SseEmitter> emitters = getEmitters(event.getGameId());
		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("moveMade").data(event));
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		}
	}

	private List<SseEmitter> getEmitters(final Long gameId) {
		return emitters.getOrDefault(gameId, new ArrayList<>());
	}

	@Value
	public static class MoveMadeDTO {
		Long gameId;
		String whoWon;
	}

}
