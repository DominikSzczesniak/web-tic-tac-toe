package pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@Value
public class PlayerInQueue {

	@NonNull UserId userId;
	@NonNull PlayerName playerName;

}
