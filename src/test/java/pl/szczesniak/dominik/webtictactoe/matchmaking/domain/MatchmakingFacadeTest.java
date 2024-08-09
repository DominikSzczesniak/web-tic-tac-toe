package pl.szczesniak.dominik.webtictactoe.matchmaking.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.szczesniak.dominik.webtictactoe.commons.domain.InMemoryEventPublisher;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.DomainEvent;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.events.PlayersMatched;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.UserIdSample.createAnyUserId;
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
		final UserId playerOne = createAnyUserId();
		final UserId playerTwo = createAnyUserId();
		tut.queueToPlay(playerOne);
		tut.queueToPlay(playerTwo);

		// then
		final DomainEvent publishedEvent = eventPublisher.getPublishedEvents().get(0);
		assertThat(publishedEvent.getClass()).isEqualTo(PlayersMatched.class);
		final PlayersMatched event = (PlayersMatched) publishedEvent;
		assertThat(event.getPlayerOne()).isEqualTo(playerOne);
		assertThat(event.getPlayerTwo()).isEqualTo(playerTwo);
	}

	@Test
	void should_not_publish_event_when_only_one_user_queued() {
		// when
		tut.queueToPlay(createAnyUserId());

		// then
		final List<DomainEvent> publishedEvents = eventPublisher.getPublishedEvents();
		assertThat(publishedEvents).hasSize(0);
	}

}