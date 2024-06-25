package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;
import pl.szczesniak.dominik.webtictactoe.sse.SseService;

@Component
@RequiredArgsConstructor
class MoveMadeEventListener {

	private final SseService sseService;

	@EventListener(MoveMade.class)
	public void handleMoveMadeEvent(final MoveMade event) {
		sseService.handleMoveMadeEvent(event);
	}

}
