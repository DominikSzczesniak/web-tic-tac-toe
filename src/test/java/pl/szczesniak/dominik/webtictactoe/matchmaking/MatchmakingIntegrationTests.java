package pl.szczesniak.dominik.webtictactoe.matchmaking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pl.szczesniak.dominik.webtictactoe.matchmaking.infrastructure.adapters.incoming.rest.QueueForGameRestInvoker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerNameSample.createAnyPlayerName;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class MatchmakingIntegrationTests {

	@Autowired
	private QueueForGameRestInvoker queueForGameRest;

	@Test
	void should_queue_for_game() {
		// when
		final ResponseEntity<String> playerQueuedResponse_1 = queueForGameRest.queueForGame(createAnyPlayerName().getValue());
		final ResponseEntity<String> playerQueuedResponse_2 = queueForGameRest.queueForGame(createAnyPlayerName().getValue());

		// then
		assertThat(playerQueuedResponse_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(playerQueuedResponse_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

}
