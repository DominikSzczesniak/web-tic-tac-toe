package pl.szczesniak.dominik.webtictactoe.matchmaking.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.PlayerInQueue;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.events.PlayersMatched;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MatchmakingFacade {

	private final DomainEventsPublisher domainEventsPublisher;
	private final PlayersInQueueRepository repository;

	public UserId queueToPlay(final UserId userId) {
		repository.addPlayerToQueue(userId);
		if (canMatchPlayersForGame()) {
			matchPlayers();
		}
		return userId;
	}

	private boolean canMatchPlayersForGame() {
		return repository.getNumberOfPlayersInQueue() >= 2;
	}

	private void matchPlayers() {
		final List<PlayerInQueue> playersForGame = matchPlayersForGame();
		domainEventsPublisher.publish(new PlayersMatched(
				playersForGame.get(0).getUserId(),
				playersForGame.get(1).getUserId()
		));
	}

	// refactor to strategy pattern when second way of matchmaking is needed
	private List<PlayerInQueue> matchPlayersForGame() {
		final List<PlayerInQueue> firstTwoPlayers = new ArrayList<>();
		firstTwoPlayers.add(repository.pollFirstPlayer());
		firstTwoPlayers.add(repository.pollFirstPlayer());
		return firstTwoPlayers;
	}

}
