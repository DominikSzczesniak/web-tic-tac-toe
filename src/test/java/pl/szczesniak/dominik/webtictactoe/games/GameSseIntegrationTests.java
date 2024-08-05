package pl.szczesniak.dominik.webtictactoe.games;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyGameStatus;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.GetGameForPlayerRestInvoker;
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
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerIdSample.createAnyPlayerId;

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

	@BeforeEach
	void setUp() {
		Mockito.reset(sseService);
	}

	@Test
	void should_send_message_once_move_was_made() {
		// given
		final UserId playerOneId = createAnyPlayerId();
		final UserId playerTwoId = createAnyPlayerId();

		playersMatchedInvoker.playersMatched(playerOneId, playerTwoId);

		final ResponseEntity<Long> checkGameIsReadyResponse = getGameForPlayerRest.getGameForPlayer(playerTwoId.getValue());
		final Long gameId = checkGameIsReadyResponse.getBody();

		// when
		final ArgumentCaptor<MoveMadeDTO> captor = ArgumentCaptor.forClass(MoveMadeDTO.class);
		moveMadeInvoker.moveMade(gameId, new GameInfo(MyGameStatus.WIN, playerOneId));

		// then
		verify(sseService, times(1)).handleMoveMadeEvent(captor.capture());

		final MoveMadeDTO capturedArgument = captor.getValue();
		assertThat(gameId).isEqualTo(capturedArgument.getGameId());
		assertThat(playerOneId.getValue()).isEqualTo(capturedArgument.getWhoWon());
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
