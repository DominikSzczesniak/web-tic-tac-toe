package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.outgoing;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.szczesniak.dominik.webtictactoe.sse.SseService;

@RestController
@RequiredArgsConstructor
public class ServerSentEventsController {

	private final SseService sseService;

	@GetMapping("/api/subscribe/games/{gameId}")
	public SseEmitter subscribeToGame(@PathVariable final Long gameId) {
		return sseService.subscribeToGame(gameId);
	}
}
