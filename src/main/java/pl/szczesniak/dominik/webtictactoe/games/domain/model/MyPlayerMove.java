package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import lombok.Value;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_EMPTY;

@Value
@Table("player_move")
public class MyPlayerMove {

	Integer row;

	Integer column;

	@Embedded(onEmpty = USE_EMPTY, prefix = "player_")
	UserId player;

}
