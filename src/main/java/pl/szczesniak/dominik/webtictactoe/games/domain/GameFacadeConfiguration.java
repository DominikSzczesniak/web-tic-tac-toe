package pl.szczesniak.dominik.webtictactoe.games.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;

@Configuration
class GameFacadeConfiguration {

	@Bean
	public GamesFacade gamesFacade(final DomainEventsPublisher domainEventsPublisher) {
		return new GamesFacade(new GamesService(domainEventsPublisher));
	}

}
