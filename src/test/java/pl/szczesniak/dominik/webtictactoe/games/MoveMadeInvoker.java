package pl.szczesniak.dominik.webtictactoe.games;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;

@Component
@RequiredArgsConstructor
public class MoveMadeInvoker {

	private final ApplicationEventPublisher applicationEventPublisher;

	void moveMade(final Long gameId, final GameInfo gameInfo) {
		applicationEventPublisher.publishEvent(new MoveMade(gameId, gameInfo));
	}

}
