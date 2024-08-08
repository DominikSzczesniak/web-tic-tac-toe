package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@Value
public class GameInfo {
	UserId playerToMove;
	Character[][] boardView;
}
