package pl.szczesniak.dominik.webtictactoe.game;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.CheckGameIsReadyForPlayersRestInvoker;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.CheckGameIsReadyForPlayersRestInvoker.GameInfoDto;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.CheckGameIsReadyForPlayersRestInvoker.PlayersForTheGameDto;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.CloseGameRestInvoker;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.GetBoardViewRestInvoker;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker.GameResultDto;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.QueueForGameRestInvoker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static pl.szczesniak.dominik.webtictactoe.game.domain.model.PlayerNameSample.createAnyPlayerName;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class GameIntegrationTests {

	@Autowired
	private QueueForGameRestInvoker queueForGameRest;

	@Autowired
	private CheckGameIsReadyForPlayersRestInvoker checkGameIsReadyForPlayersRest;

	@Autowired
	private MakeMoveRestInvoker makeMoveRest;

	@Autowired
	private CloseGameRestInvoker closeGameRest;

	@Autowired
	private GetBoardViewRestInvoker getBoardViewRest;

	@Test
	void users_should_queue_for_game_and_get_paired() {
		// given
		final String playerOne = createAnyPlayerName().getValue();
		final String playerTwo = createAnyPlayerName().getValue();

		// when
		final ResponseEntity<Void> queueForGamePlayerOneResponse = queueForGameRest.queueForGame(playerOne);
		final ResponseEntity<Void> queueForGamePlayerTwoResponse = queueForGameRest.queueForGame(playerTwo);

		// then
		assertThat(queueForGamePlayerOneResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(queueForGamePlayerTwoResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// when
		final ResponseEntity<GameInfoDto> checkGameIsReadyResponse = checkGameIsReadyForPlayersRest.checkGameIsReady(
				PlayersForTheGameDto.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// then
		assertThat(checkGameIsReadyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(checkGameIsReadyResponse.getBody().getGameIsReady()).isTrue();
	}

	@Test
	void game_should_finish() {
		// given
		final String playerOne = createAnyPlayerName().getValue();
		final String playerTwo = createAnyPlayerName().getValue();
		queueForGameRest.queueForGame(playerOne);
		queueForGameRest.queueForGame(playerTwo);

		final ResponseEntity<GameInfoDto> checkGameIsReadyResponse = checkGameIsReadyForPlayersRest.checkGameIsReady(
				PlayersForTheGameDto.builder().playerOne(playerOne).playerTwo(playerTwo).build());
		final Long gameId = checkGameIsReadyResponse.getBody().getGameId();

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_1 = makeMoveRest.makeMove(
				gameId,
				MakeMoveRestInvoker.MakeMoveDto.builder().playerName(playerOne).columnIndex(0).rowIndex(0).build()
		);

		// then
		assertThat(makeMoveResult_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_1.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_2 = makeMoveRest.makeMove(
				gameId,
				MakeMoveRestInvoker.MakeMoveDto.builder().playerName(playerTwo).columnIndex(0).rowIndex(1).build()
		);

		// then
		assertThat(makeMoveResult_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_2.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_3 = makeMoveRest.makeMove(
				gameId,
				MakeMoveRestInvoker.MakeMoveDto.builder().playerName(playerOne).columnIndex(1).rowIndex(0).build()
		);

		// then
		assertThat(makeMoveResult_3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_3.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_4 = makeMoveRest.makeMove(
				gameId,
				MakeMoveRestInvoker.MakeMoveDto.builder().playerName(playerTwo).columnIndex(2).rowIndex(2).build()
		);

		// then
		assertThat(makeMoveResult_4.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_4.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_5 = makeMoveRest.makeMove(
				gameId,
				MakeMoveRestInvoker.MakeMoveDto.builder().playerName(playerOne).columnIndex(2).rowIndex(0).build()
		);

		// then
		assertThat(makeMoveResult_5.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_5.getBody().getGameStatus()).isEqualToIgnoringCase("win");

		// when
		final ResponseEntity<Character[][]> getBoardViewResponse = getBoardViewRest.getBoardView(gameId);

		// then
		assertThat(getBoardViewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		final Character[][] boardView = getBoardViewResponse.getBody();
		assertThat(boardView[0]).containsExactly('O', 'O', 'O');
		assertThat(boardView[1]).containsExactly('X', null, null);
		assertThat(boardView[2]).containsExactly(null, null, 'X');

		// when
		final ResponseEntity<Void> closeGameResponse = closeGameRest.closeGame(gameId);

		// then
		assertThat(closeGameResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// when
		final ResponseEntity<GameInfoDto> checkGameIsReadyResponseAfterClosing = checkGameIsReadyForPlayersRest.checkGameIsReady(
				PlayersForTheGameDto.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// then
		assertThat(checkGameIsReadyResponseAfterClosing.getBody().getGameIsReady()).isFalse();

	}


}
