package pl.szczesniak.dominik.webtictactoe.games;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyGameStatus;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.GetGameForPlayerRestInvoker;
import pl.szczesniak.dominik.webtictactoe.security.LoggedUserProvider;
import pl.szczesniak.dominik.webtictactoe.security.LoggedUserProvider.LoggedUser;
import pl.szczesniak.dominik.webtictactoe.sse.SpringSseService.MoveMadeDTO;
import pl.szczesniak.dominik.webtictactoe.sse.SseService;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class GameSseIntegrationTests {

	@Autowired
	private GetGameForPlayerRestInvoker getGameForPlayerRest;

	@Autowired
	private PlayersMatchedInvoker playersMatchedInvoker;

	@Autowired
	private MoveMadeInvoker moveMadeInvoker;

	@Autowired
	private SseService sseService;

	@Autowired
	private LoggedUserProvider loggedUserProvider;

	@BeforeEach
	void setUp() {
		Mockito.reset(sseService);
	}

	@Test
	void should_send_message_once_move_was_made() {
		// given
		final LoggedUser loggedUser_1 = loggedUserProvider.getLoggedUser();
		final LoggedUser loggedUser_2 = loggedUserProvider.getLoggedUser();

		playersMatchedInvoker.playersMatched(new UserId(loggedUser_1.getUserId()), new UserId(loggedUser_2.getUserId()));

		final ResponseEntity<Long> checkGameIsReadyResponse = getGameForPlayerRest.getGameForPlayer(loggedUser_1.getToken());
		final Long gameId = checkGameIsReadyResponse.getBody();

		// when
		final ArgumentCaptor<MoveMadeDTO> captor = ArgumentCaptor.forClass(MoveMadeDTO.class);
		moveMadeInvoker.moveMade(gameId, new GameState(MyGameStatus.WIN, new UserId(loggedUser_1.getUserId())));

		// then
		verify(sseService, times(1)).handleMoveMadeEvent(captor.capture());

		final MoveMadeDTO capturedArgument = captor.getValue();
		assertThat(gameId).isEqualTo(capturedArgument.getGameId());
		assertThat(loggedUser_1.getUserId()).isEqualTo(capturedArgument.getWhoWon());
		verifyFieldNames(capturedArgument);
	}

	private void verifyFieldNames(final MoveMadeDTO dto) {
		final List<String> expectedFieldNames = List.of("gameId", "whoWon");
		final List<String> actualFieldNames = Arrays.stream(dto.getClass().getDeclaredFields())
				.map(Field::getName)
				.toList();

		assertThat(actualFieldNames).containsExactlyInAnyOrderElementsOf(expectedFieldNames);
	}

}
