package pl.szczesniak.dominik.webtictactoe.commons.domain;

public interface DomainEventsPublisher {

	void publish(DomainEvent event);

}
