package pl.szczesniak.dominik.webtictactoe.game;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.GetGameForPlayer;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.PrepareGameControllerRestInvoker;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.PrepareGameControllerRestInvoker.TicTacToeGameDto;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.CloseGameRestInvoker;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.GetBoardViewRestInvoker;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.GetWhichPlayerToMoveRestInvoker;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker.GameResultDto;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker.MakeMoveDto;
import pl.szczesniak.dominik.webtictactoe.game.infrastructure.adapters.incoming.rest.QueueForGameRestInvoker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static pl.szczesniak.dominik.webtictactoe.game.domain.model.PlayerNameSample.createAnyPlayerName;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class GameIntegrationTests {

	@Autowired
	private QueueForGameRestInvoker queueForGameRest;

	@Autowired
	private GetGameForPlayer getGameForPlayer;

	@Autowired
	private PrepareGameControllerRestInvoker prepareGameForPlayers;

	@Autowired
	private MakeMoveRestInvoker makeMoveRest;

	@Autowired
	private CloseGameRestInvoker closeGameRest;

	@Autowired
	private GetBoardViewRestInvoker getBoardViewRest;

	@Autowired
	private GetWhichPlayerToMoveRestInvoker getWhichPlayerToMoveRest;

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
		final ResponseEntity<TicTacToeGameDto> prepareGameResponse = prepareGameForPlayers.prepareGame();

		// then
		assertThat(prepareGameResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(prepareGameResponse.getBody().getPlayerOne()).isEqualTo(playerOne);
		assertThat(prepareGameResponse.getBody().getPlayerTwo()).isEqualTo(playerTwo);
	}

	@Test
	void game_should_be_ready_for_players() {
		// given
		final String playerOne = createAnyPlayerName().getValue();
		final String playerTwo = createAnyPlayerName().getValue();
		queueForGameRest.queueForGame(playerOne);
		queueForGameRest.queueForGame(playerTwo);

		final ResponseEntity<TicTacToeGameDto> prepareGameResponseEntity = prepareGameForPlayers.prepareGame();

		// when
		final ResponseEntity<Long> isGameReadyForPlayerOneResponse = getGameForPlayer.getGameForPlayer(playerOne);
		final ResponseEntity<Long> isGameReadyForPlayerTwoResponse = getGameForPlayer.getGameForPlayer(playerTwo);

		// then
		assertThat(isGameReadyForPlayerOneResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(isGameReadyForPlayerTwoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(isGameReadyForPlayerOneResponse.getBody()).isEqualTo(prepareGameResponseEntity.getBody().getGameId());
		assertThat(isGameReadyForPlayerOneResponse.getBody()).isEqualTo(isGameReadyForPlayerTwoResponse.getBody());
	}

	@Test
	void game_should_finish_once_player_has_won() {
		// given
		final String playerOne = createAnyPlayerName().getValue();
		final String playerTwo = createAnyPlayerName().getValue();
		queueForGameRest.queueForGame(playerOne);
		queueForGameRest.queueForGame(playerTwo);

		final ResponseEntity<TicTacToeGameDto> checkGameIsReadyResponse = prepareGameForPlayers.prepareGame();
		final Long gameId = checkGameIsReadyResponse.getBody().getGameId();

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_1 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerName(playerOne).columnIndex(0).rowIndex(0).build()
		);

		// then
		assertThat(makeMoveResult_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_1.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_2 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerName(playerTwo).columnIndex(0).rowIndex(1).build()
		);

		// then
		assertThat(makeMoveResult_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_2.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_3 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerName(playerOne).columnIndex(1).rowIndex(0).build()
		);

		// then
		assertThat(makeMoveResult_3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_3.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_4 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerName(playerTwo).columnIndex(2).rowIndex(2).build()
		);

		// then
		assertThat(makeMoveResult_4.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_4.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_5 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerName(playerOne).columnIndex(2).rowIndex(0).build()
		);

		// then
		assertThat(makeMoveResult_5.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_5.getBody().getGameStatus()).isEqualToIgnoringCase("win");
		assertThat(makeMoveResult_5.getBody().getPlayerThatWon()).isEqualTo(playerOne);
		assertThat(makeMoveResult_5.getBody().getPlayerThatWon()).isNotEqualTo(playerTwo);

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
	}

	@Test
	void should_get_which_player_to_move_before_the_player_moves() {
		// given
		final String playerOne = createAnyPlayerName().getValue();
		final String playerTwo = createAnyPlayerName().getValue();
		queueForGameRest.queueForGame(playerOne);
		queueForGameRest.queueForGame(playerTwo);

		final ResponseEntity<TicTacToeGameDto> checkGameIsReadyResponse = prepareGameForPlayers.prepareGame();
		final Long gameId = checkGameIsReadyResponse.getBody().getGameId();

		// when
		final ResponseEntity<String> playerToMoveResponse_1 = getWhichPlayerToMoveRest.getWhichPlayerToMove(gameId);

		// then
		assertThat(playerToMoveResponse_1.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(playerToMoveResponse_1.getBody()).isEqualTo(playerOne);

		// when
		makeMoveRest.makeMove(gameId, MakeMoveDto.builder().playerName(playerOne).rowIndex(1).columnIndex(1).build());

		// then
		final ResponseEntity<String> playerToMoveResponse_2 = getWhichPlayerToMoveRest.getWhichPlayerToMove(gameId);
		assertThat(playerToMoveResponse_2.getBody()).isEqualTo(playerTwo);

		// when
		makeMoveRest.makeMove(gameId, MakeMoveDto.builder().playerName(playerTwo).rowIndex(2).columnIndex(2).build());

		// then
		final ResponseEntity<String> playerToMoveResponse_3 = getWhichPlayerToMoveRest.getWhichPlayerToMove(gameId);
		assertThat(playerToMoveResponse_3.getBody()).isEqualTo(playerOne);

		// when
		makeMoveRest.makeMove(gameId, MakeMoveDto.builder().playerName(playerOne).rowIndex(0).columnIndex(0).build());

		// then
		final ResponseEntity<String> playerToMoveResponse_4 = getWhichPlayerToMoveRest.getWhichPlayerToMove(gameId);
		assertThat(playerToMoveResponse_4.getBody()).isEqualTo(playerTwo);
	}

}
