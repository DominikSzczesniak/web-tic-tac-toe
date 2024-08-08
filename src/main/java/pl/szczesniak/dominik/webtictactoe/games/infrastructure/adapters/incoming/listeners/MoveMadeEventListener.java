package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;
import pl.szczesniak.dominik.webtictactoe.sse.SpringSseService.MoveMadeDTO;
import pl.szczesniak.dominik.webtictactoe.sse.SseService;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@Component
@RequiredArgsConstructor
class MoveMadeEventListener {

	private final SseService sseService;

	@EventListener(MoveMade.class)
	public void handleMoveMadeEvent(final MoveMade event) {
		final MoveMadeDTO dto = toDto(event);
		sseService.handleMoveMadeEvent(dto);
	}

	private MoveMadeDTO toDto(final MoveMade event) {
		return new MoveMadeDTO(event.getGameId().getValue(), event.getGameState().getWhoWon().map(UserId::getValue).orElse(null));
	}

}
