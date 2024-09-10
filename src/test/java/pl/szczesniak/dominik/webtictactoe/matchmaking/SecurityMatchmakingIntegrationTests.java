package pl.szczesniak.dominik.webtictactoe.matchmaking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.szczesniak.dominik.webtictactoe.matchmaking.infrastructure.adapters.incoming.rest.QueueForGameRestInvoker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class SecurityMatchmakingIntegrationTests {

	@Autowired
	private QueueForGameRestInvoker queueForGameRest;

	@Test
	void should_unauthorize_endpoints_when_invalid_token() {
		// given
		final String invalidToken = "invalidToken";

		// when
		final ResponseEntity<String> queueForGameResponse = queueForGameRest.queueForGame(invalidToken);

		// then
		assertThat(queueForGameResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}
}
