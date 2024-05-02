package pl.szczesniak.dominik.webtictactoe.commons.infrastructure.outgoing.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEvent;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;

@Component
@RequiredArgsConstructor
public class SpringDomainEventsPublisher implements DomainEventsPublisher {

	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void publish(final DomainEvent event) {
		applicationEventPublisher.publishEvent(event);
	}

}
