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
import pl.szczesniak.dominik.webtictactoe.security.LoggedUserProvider;
import pl.szczesniak.dominik.webtictactoe.security.LoggedUserProvider.LoggedUser;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

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

	@Autowired
	private LoggedUserProvider userProvider;

	@Test
	void game_should_create_once_players_got_matched() {
		// given
		final LoggedUser loggedUser_1 = userProvider.getLoggedUser();
		final LoggedUser loggedUser_2 = userProvider.getLoggedUser();

		playersMatchedInvoker.playersMatched(new UserId(loggedUser_1.getUserId()), new UserId(loggedUser_2.getUserId()));

		// when
		final ResponseEntity<Long> gameForPlayerOneResponse = getGameForPlayerRest.getGameForPlayer(loggedUser_1.getToken());
		final ResponseEntity<Long> gameForPlayerTwoResponse = getGameForPlayerRest.getGameForPlayer(loggedUser_2.getToken());

		// then
		assertThat(gameForPlayerOneResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(gameForPlayerTwoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(gameForPlayerTwoResponse.getBody()).isEqualTo(gameForPlayerOneResponse.getBody());
	}

	@Test
	void game_should_finish_once_player_has_won() {
		// given
		final LoggedUser loggedUser_1 = userProvider.getLoggedUser();
		final LoggedUser loggedUser_2 = userProvider.getLoggedUser();
		final String playerOneId = loggedUser_1.getUserId();
		final String playerTwoId = loggedUser_2.getUserId();

		playersMatchedInvoker.playersMatched(new UserId(playerOneId), new UserId(playerTwoId));

		final ResponseEntity<Long> checkGameIsReadyResponse = getGameForPlayerRest.getGameForPlayer(loggedUser_1.getToken());
		final Long gameId = checkGameIsReadyResponse.getBody();

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_1 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerOneId).columnIndex(0).rowIndex(0).build(),
				loggedUser_1.getToken()
		);

		// then
		assertThat(makeMoveResult_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_1.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_2 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerTwoId).columnIndex(0).rowIndex(1).build(),
				loggedUser_2.getToken()
		);

		// then
		assertThat(makeMoveResult_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_2.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_3 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerOneId).columnIndex(1).rowIndex(0).build(),
				loggedUser_1.getToken()
		);

		// then
		assertThat(makeMoveResult_3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_3.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_4 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerTwoId).columnIndex(2).rowIndex(2).build(),
				loggedUser_2.getToken()
		);

		// then
		assertThat(makeMoveResult_4.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_4.getBody().getGameStatus()).isEqualToIgnoringCase("in_progress");

		// when
		final ResponseEntity<GameResultDto> makeMoveResult_5 = makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerOneId).columnIndex(2).rowIndex(0).build(),
				loggedUser_1.getToken()
		);

		// then
		assertThat(makeMoveResult_5.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(makeMoveResult_5.getBody().getGameStatus()).isEqualToIgnoringCase("win");
		assertThat(makeMoveResult_5.getBody().getPlayerThatWon()).isEqualTo(playerOneId);
		assertThat(makeMoveResult_5.getBody().getPlayerThatWon()).isNotEqualTo(playerTwoId);

		// when
		final ResponseEntity<GameInfoDTO> getGameInfoResponse = getGameInfoRest.getGameInfo(gameId, loggedUser_1.getToken());

		// then
		assertThat(getGameInfoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		final Character[][] boardView = getGameInfoResponse.getBody().getBoardView();
		assertThat(boardView[0]).containsExactly('O', 'O', 'O');
		assertThat(boardView[1]).containsExactly('X', null, null);
		assertThat(boardView[2]).containsExactly(null, null, 'X');

		// when
		final ResponseEntity<Void> closeGameResponse = closeGameRest.closeGame(gameId, loggedUser_1.getToken());

		// then
		assertThat(closeGameResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void should_get_which_player_to_move_before_the_player_moves() {
		// given
		final LoggedUser loggedUser_1 = userProvider.getLoggedUser();
		final LoggedUser loggedUser_2 = userProvider.getLoggedUser();
		final String playerOneId = loggedUser_1.getUserId();
		final String playerTwoId = loggedUser_2.getUserId();

		playersMatchedInvoker.playersMatched(new UserId(playerOneId), new UserId(playerTwoId));

		final ResponseEntity<Long> checkGameIsReadyResponse = getGameForPlayerRest.getGameForPlayer(loggedUser_2.getToken());
		final Long gameId = checkGameIsReadyResponse.getBody();

		// when
		final ResponseEntity<GameInfoDTO> gameInfoResponse_1 = getGameInfoRest.getGameInfo(gameId, loggedUser_1.getToken());

		// then
		assertThat(gameInfoResponse_1.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(gameInfoResponse_1.getBody().getUserId()).isEqualTo(playerOneId);

		// when
		makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerOneId).rowIndex(1).columnIndex(1).build(),
				loggedUser_1.getToken()
		);

		// then
		final ResponseEntity<GameInfoDTO> gameInfoResponse_2 = getGameInfoRest.getGameInfo(gameId, loggedUser_2.getToken());
		assertThat(gameInfoResponse_2.getBody().getUserId()).isEqualTo(playerTwoId);

		// when
		makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerTwoId).rowIndex(2).columnIndex(2).build(),
				loggedUser_2.getToken()
		);

		// then
		final ResponseEntity<GameInfoDTO> gameInfoResponse_3 = getGameInfoRest.getGameInfo(gameId, loggedUser_1.getToken());
		assertThat(gameInfoResponse_3.getBody().getUserId()).isEqualTo(playerOneId);

		// when
		makeMoveRest.makeMove(
				gameId,
				MakeMoveDto.builder().playerId(playerOneId).rowIndex(0).columnIndex(0).build(),
				loggedUser_1.getToken()
		);

		// then
		final ResponseEntity<GameInfoDTO> gameInfoResponse_4 = getGameInfoRest.getGameInfo(gameId, loggedUser_2.getToken());
		assertThat(gameInfoResponse_4.getBody().getUserId()).isEqualTo(playerTwoId);
	}

}
