package pl.szczesniak.dominik.webtictactoe.matchmaking.domain;

import org.springframework.stereotype.Repository;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.PlayerInQueue;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
class InMemoryPlayersInQueueRepository implements PlayersInQueueRepository {

	private final Queue<PlayerInQueue> playersInQueue = new ConcurrentLinkedQueue<>();

	@Override
	public UserId addPlayerToQueue(final UserId userId) {
		playersInQueue.add(new PlayerInQueue(userId));
		return userId;
	}

	@Override
	public int getNumberOfPlayersInQueue() {
		return playersInQueue.size();
	}

	@Override
	public PlayerInQueue pollFirstPlayer() {
		return playersInQueue.poll();
	}
}
