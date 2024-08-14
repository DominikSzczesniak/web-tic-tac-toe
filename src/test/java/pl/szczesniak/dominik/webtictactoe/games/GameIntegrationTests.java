package pl.szczesniak.dominik.webtictactoe.games;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.CloseGameRestInvoker;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.GetGameForPlayerRestInvoker;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.GetGameInfoRestInvoker;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.GetGameInfoRestInvoker.GameInfoDTO;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker.GameResultDto;
import pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest.MakeMoveRestInvoker.MakeMoveDto;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserIdSample.createAnyUserId;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class GameIntegrationTests {

	@Autowired
	private GetGameForPlayerRestInvoker getGameForPlayerRest;

	@Autowired
	private MakeMoveRestInvoker makeMoveRest;

	@Autowired
	private CloseGameRestInvoker closeGameRest;

	@Autowired
	private GetGameInfoRestInvoker getGameInfoRest;

	@Autowired
	private PlayersMatchedInvoker playersMatchedInvoker;

	@Test
	void game_should_create_once_players_got_matched() {
		// given
		final UserId playerOneId = createAnyUserId();
		final UserId playerTwoId = createAnyUserId();

		playersMatchedInvoker.playersMatched(playerOneId, playerTwoId);

		// when
		final ResponseEntity<Long> gameForPlayerOneResponse = getGameForPlayerRest.getGameForPlayer(playerOneId.getValue());
		final ResponseEntity<Long> gameForPlayerTwoResponse = getGameForPlayerRest.getGameForPlayer(playerTwoId.getValue());

		// then
		assertThat(gameForPlayerTwoResponse.getBody()).isEqualTo(gameForPlayerOneResponse.getBody());
	}

	@Test
	void game_should_finish_once_player_has_won() {
		// given
		final UserId playerOneId = createAnyUserId();
		final UserId playerTwoId = createAnyUserId();

		playersMatchedInvoker.playersMatched(playerOneId, playerTwoId);

		final ResponseEntity<Long> checkGameIsReadyResponse = getGameForPlayerRest.getGameForPlayer(playerOneId.getValue());
		final Long gameId = checkGameIsReadyResponse.getBody();

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_1 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerOneId.getValue()).columnIndex(0).rowIndex(0).build()
		);

		// then
		assertThat(makeMoveResult_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_1.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_2 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerTwoId.getValue()).columnIndex(0).rowIndex(1).build()
		);

		// then
		assertThat(makeMoveResult_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_2.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_3 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerOneId.getValue()).columnIndex(1).rowIndex(0).build()
		);

		// then
		assertThat(makeMoveResult_3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_3.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_4 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerTwoId.getValue()).columnIndex(2).rowIndex(2).build()
		);

		// then
		assertThat(makeMoveResult_4.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_4.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_5 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerOneId.getValue()).columnIndex(2).rowIndex(0).build()
		);

		// then
		assertThat(makeMoveResult_5.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_5.getBody().getGameStatus()).isEqualToIgnoringCase("win");
		assertThat(makeMoveResult_5.getBody().getPlayerThatWon()).isEqualTo(playerOneId.getValue());
		assertThat(makeMoveResult_5.getBody().getPlayerThatWon()).isNotEqualTo(playerTwoId.getValue());

		// when
		final ResponseEntity<GameInfoDTO> getGameInfoResponse = getGameInfoRest.getGameInfo(gameId);

		// then
		assertThat(getGameInfoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		final Character[][] boardView = getGameInfoResponse.getBody().getBoardView();
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
		final UserId playerOneId = createAnyUserId();
		final UserId playerTwoId = createAnyUserId();

		playersMatchedInvoker.playersMatched(playerOneId, playerTwoId);

		final ResponseEntity<Long> checkGameIsReadyResponse = getGameForPlayerRest.getGameForPlayer(playerTwoId.getValue());
		final Long gameId = checkGameIsReadyResponse.getBody();

		// when
		final ResponseEntity<GameInfoDTO> gameInfoResponse_1 = getGameInfoRest.getGameInfo(gameId);

		// then
		assertThat(gameInfoResponse_1.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(gameInfoResponse_1.getBody().getUserId()).isEqualTo(playerOneId.getValue());

		// when
		makeMoveRest.makeMove(gameId, MakeMoveDto.builder().playerId(playerOneId.getValue()).rowIndex(1).columnIndex(1).build());

		// then
		final ResponseEntity<GameInfoDTO> gameInfoResponse_2 = getGameInfoRest.getGameInfo(gameId);
		assertThat(gameInfoResponse_2.getBody().getUserId()).isEqualTo(playerTwoId.getValue());

		// when
		makeMoveRest.makeMove(gameId, MakeMoveDto.builder().playerId(playerTwoId.getValue()).rowIndex(2).columnIndex(2).build());

		// then
		final ResponseEntity<GameInfoDTO> gameInfoResponse_3 = getGameInfoRest.getGameInfo(gameId);
		assertThat(gameInfoResponse_3.getBody().getUserId()).isEqualTo(playerOneId.getValue());

		// when
		makeMoveRest.makeMove(gameId, MakeMoveDto.builder().playerId(playerOneId.getValue()).rowIndex(0).columnIndex(0).build());

		// then
		final ResponseEntity<GameInfoDTO> gameInfoResponse_4 = getGameInfoRest.getGameInfo(gameId);
		assertThat(gameInfoResponse_4.getBody().getUserId()).isEqualTo(playerTwoId.getValue());
	}

}
