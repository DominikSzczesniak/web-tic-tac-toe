package pl.szczesniak.dominik.webtictactoe.commons.domain;

import pl.szczesniak.dominik.webtictactoe.commons.domain.model.DomainEvent;

import java.util.Collection;

public interface DomainEventsPublisher {

	void publish(DomainEvent event);

	default void publish(Collection<DomainEvent> events) {
		events.forEach(this::publish);
	}

}
