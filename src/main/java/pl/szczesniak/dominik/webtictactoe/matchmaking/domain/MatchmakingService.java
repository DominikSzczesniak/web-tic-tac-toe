package pl.szczesniak.dominik.webtictactoe.matchmaking.domain;

import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.PlayerInQueue;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.events.PlayersMatched;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

@RequiredArgsConstructor
class MatchmakingService {

	private final DomainEventsPublisher domainEventsPublisher;
	private final Queue<PlayerInQueue> playersInQueue = new LinkedList<>();

	UserId addPlayerToQueue(final PlayerName playerName) {
		final UserId queuedPlayer = new UserId(UUID.randomUUID());
		playersInQueue.add(new PlayerInQueue(queuedPlayer, playerName));

		tryMatchPlayersForGame();

		return queuedPlayer;
	}

	private void tryMatchPlayersForGame() {
		if (checkCanMatchPlayersForGame()) {
			matchPlayersForGame();
		}
	}

	private boolean checkCanMatchPlayersForGame() {
		return playersInQueue.size() >= 2;
	}

	private void matchPlayersForGame() {
		final List<PlayerInQueue> playersForGame = getPlayersForGame();
		domainEventsPublisher.publish(new PlayersMatched(
				playersForGame.get(0).getUserId(), playersForGame.get(0).getPlayerName(),
				playersForGame.get(1).getUserId(), playersForGame.get(1).getPlayerName()
		));
	}

	private List<PlayerInQueue> getPlayersForGame() {
		final List<PlayerInQueue> firstTwoPlayers = new ArrayList<>();
		firstTwoPlayers.add(playersInQueue.poll());
		firstTwoPlayers.add(playersInQueue.poll());
		return firstTwoPlayers;
	}

}
