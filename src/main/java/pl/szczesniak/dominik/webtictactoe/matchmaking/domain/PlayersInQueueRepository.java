package pl.szczesniak.dominik.webtictactoe.matchmaking.domain;

import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.PlayerInQueue;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

public interface PlayersInQueueRepository {

	UserId addPlayerToQueue(UserId userId);

	int getNumberOfPlayersInQueue();

	PlayerInQueue pollFirstPlayer();

}
