package pl.szczesniak.dominik.webtictactoe.games;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.events.PlayersMatched;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@Component
@RequiredArgsConstructor
class PlayersMatchedInvoker {

	private final ApplicationEventPublisher applicationEventPublisher;

	void playersMatched(final UserId playerOneId, final UserId playerTwoId) {
		applicationEventPublisher.publishEvent(new PlayersMatched(playerOneId, playerTwoId));
	}

}
