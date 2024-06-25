package pl.szczesniak.dominik.webtictactoe.games.domain.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.DomainEvent;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;

@Value
public class MoveMade implements DomainEvent {

	@NonNull Long gameId;
	@NonNull GameInfo gameInfo;

}
