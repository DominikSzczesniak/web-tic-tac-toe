package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.outgoing.ServerSentEventsController;

@Component
@RequiredArgsConstructor
class MoveMadeEventListener {

	private final ServerSentEventsController serverSentEventsController;

	@EventListener(MoveMade.class)
	public void handleMoveMadeEvent(final MoveMade event) {
		serverSentEventsController.handleMoveMadeEvent(event);
	}

}
