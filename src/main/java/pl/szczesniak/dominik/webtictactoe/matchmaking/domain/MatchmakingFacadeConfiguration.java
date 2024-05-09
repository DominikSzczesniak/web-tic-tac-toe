package pl.szczesniak.dominik.webtictactoe.matchmaking.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;

@Configuration
class MatchmakingFacadeConfiguration {

	@Bean
	MatchmakingFacade matchmakingFacade(final DomainEventsPublisher domainEventsPublisher) {
		return new MatchmakingFacade(new MatchmakingService(domainEventsPublisher));
	}

}
