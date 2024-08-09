package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@Value
public class GameInfo {
	@NonNull UserId playerToMove;
	@NonNull Character[][] boardView;
}
