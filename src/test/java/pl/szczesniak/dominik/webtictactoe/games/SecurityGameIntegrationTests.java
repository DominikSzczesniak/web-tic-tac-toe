package pl.szczesniak.dominik.webtictactoe.games;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.CloseGameRestInvoker;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.GetGameForPlayerRestInvoker;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.GetGameInfoRestInvoker;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker.GameResultDto;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker.MakeMoveDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class SecurityGameIntegrationTests {

	@Autowired
	private GetGameForPlayerRestInvoker getGameForPlayerRest;

	@Autowired
	private MakeMoveRestInvoker makeMoveRest;

	@Autowired
	private CloseGameRestInvoker closeGameRest;

	@Autowired
	private GetGameInfoRestInvoker getGameInfoRest;

	@Test
	void should_unauthorize_endpoints_when_invalid_token() {
		// given
		final Long gameId = 1L;
		final String invalidToken = "invalidToken";

		// when
		final ResponseEntity<Long> getGameForPlayerResponse = getGameForPlayerRest.getGameForPlayer(invalidToken);
		final ResponseEntity<Void> closeGameResponse = closeGameRest.closeGame(gameId, invalidToken);
		final ResponseEntity<GameResultDto> makeMoveRestResponse = makeMoveRest.makeMove(
				gameId, MakeMoveDto.builder().build(), invalidToken);
		final ResponseEntity<GetGameInfoRestInvoker.GameInfoDTO> getGameInfoResponse = getGameInfoRest.getGameInfo(
				gameId, invalidToken);

		// then
		assertThat(getGameForPlayerResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(closeGameResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(makeMoveRestResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(getGameInfoResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

}
