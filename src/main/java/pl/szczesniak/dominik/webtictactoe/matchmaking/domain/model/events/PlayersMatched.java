package pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEvent;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@Value
public class PlayersMatched implements DomainEvent {

	@NonNull UserId playerOne;
	@NonNull UserId playerTwo;

}
