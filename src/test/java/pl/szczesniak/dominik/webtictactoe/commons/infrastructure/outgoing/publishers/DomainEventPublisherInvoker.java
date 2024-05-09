package pl.szczesniak.dominik.webtictactoe.commons.infrastructure.outgoing.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEvent;

@Service
@RequiredArgsConstructor
public class DomainEventPublisherInvoker {

	private final SpringDomainEventsPublisher domainEventsPublisher;

	public void publish(final DomainEvent event) {
		domainEventsPublisher.publish(event);
	}

}
