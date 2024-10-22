package pl.szczesniak.dominik.webtictactoe.matchmaking.domain;

import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;

class TestMatchmakingFacadeConfiguration {

	static MatchmakingFacade matchmakingFacade(final DomainEventsPublisher publisher) {
		return new MatchmakingFacade(publisher, new InMemoryPlayersInQueueRepository());
	}

}
