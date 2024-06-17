package pl.szczesniak.dominik.webtictactoe.matchmaking.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEvent;
import pl.szczesniak.dominik.webtictactoe.commons.domain.InMemoryEventPublisher;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.events.PlayersMatched;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerIdSample.createAnyPlayerId;
import static pl.szczesniak.dominik.webtictactoe.matchmaking.domain.TestMatchmakingFacadeConfiguration.matchmakingFacade;

class MatchmakingFacadeTest {

	private MatchmakingFacade tut;
	private InMemoryEventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		eventPublisher = new InMemoryEventPublisher();
		tut = matchmakingFacade(eventPublisher);
	}

	@Test
	void event_should_be_published_when_two_users_queued() {
		// when
		tut.queueToPlay(createAnyPlayerId());
		tut.queueToPlay(createAnyPlayerId());

		// then
		final DomainEvent publishedEvent = eventPublisher.getPublishedEvents().get(0);
		assertThat(publishedEvent.getClass()).isEqualTo(PlayersMatched.class);
	}

	@Test
	void should_not_publish_event_when_only_one_user_queued() {
		// when
		tut.queueToPlay(createAnyPlayerId());

		// then
		final List<DomainEvent> publishedEvents = eventPublisher.getPublishedEvents();
		assertThat(publishedEvents).hasSize(0);
	}

}