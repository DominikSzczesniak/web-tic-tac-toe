package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import lombok.NonNull;
import lombok.Value;
import org.springframework.data.annotation.Id;

@Value
public class TicTacToeGameId {

	@Id
	@NonNull Long value;

}
