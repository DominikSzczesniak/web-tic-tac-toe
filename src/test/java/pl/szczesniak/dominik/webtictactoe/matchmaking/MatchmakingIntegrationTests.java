package pl.szczesniak.dominik.webtictactoe.matchmaking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pl.szczesniak.dominik.webtictactoe.matchmaking.infrastructure.adapters.incoming.rest.QueueForGameRestInvoker;
import pl.szczesniak.dominik.webtictactoe.security.LoggedUserProvider;
import pl.szczesniak.dominik.webtictactoe.security.LoggedUserProvider.LoggedUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class MatchmakingIntegrationTests {

	@Autowired
	private QueueForGameRestInvoker queueForGameRest;

	@Autowired
	private LoggedUserProvider loggedUserProvider;

	@Test
	void should_queue_for_game() {
		// given
		final LoggedUser loggedUser_1 = loggedUserProvider.getLoggedUser();
		final LoggedUser loggedUser_2 = loggedUserProvider.getLoggedUser();

		// when
		final ResponseEntity<String> playerQueuedResponse_1 = queueForGameRest.queueForGame(loggedUser_1.getToken());
		final ResponseEntity<String> playerQueuedResponse_2 = queueForGameRest.queueForGame(loggedUser_2.getToken());

		// then
		assertThat(playerQueuedResponse_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(playerQueuedResponse_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

}
