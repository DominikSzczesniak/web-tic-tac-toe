package pl.szczesniak.dominik.webtictactoe.commons.domain;

import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.events.PlayersMatched;

import java.util.ArrayList;
import java.util.List;

public class InMemoryEventPublisher implements DomainEventsPublisher {

	private final List<PlayersMatched> publishedEvents = new ArrayList<>();

	@Override
	public void publish(final DomainEvent event) {
		publishedEvents.add((PlayersMatched) event);
	}

	public List<PlayersMatched> getPublishedEvents() {
		return publishedEvents;
	}

}
