package pl.szczesniak.dominik.webtictactoe.commons.domain;

import pl.szczesniak.dominik.webtictactoe.commons.domain.model.DomainEvent;

import java.util.ArrayList;
import java.util.List;

public class InMemoryEventPublisher implements DomainEventsPublisher {

	private final List<DomainEvent> publishedEvents = new ArrayList<>();

	@Override
	public void publish(final DomainEvent event) {
		publishedEvents.add(event);
	}

	public List<DomainEvent> getPublishedEvents() {
		return publishedEvents;
	}

}
