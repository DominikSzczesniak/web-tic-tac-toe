package pl.szczesniak.dominik.webtictactoe.games.domain.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEvent;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameStatusInfo;

@Value
public class MoveMade implements DomainEvent {

	@NonNull Long gameId;
	@NonNull GameStatusInfo gameStatusInfo;

}
