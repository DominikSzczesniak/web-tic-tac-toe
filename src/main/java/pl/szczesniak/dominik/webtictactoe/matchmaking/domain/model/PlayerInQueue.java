package pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@Value
public class PlayerInQueue {

	@NonNull UserId userId;

}
