package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@Value
public class GameMoveToMake {

	@NonNull
	Integer row;
	@NonNull
	Integer column;
	@NonNull
	UserId player;

}
