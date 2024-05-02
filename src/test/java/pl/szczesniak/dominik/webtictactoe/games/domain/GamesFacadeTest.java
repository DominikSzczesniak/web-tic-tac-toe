package pl.szczesniak.dominik.webtictactoe.games.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.CreateGameSample;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMoveSample;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static pl.szczesniak.dominik.webtictactoe.games.domain.TestGamesFacadeConfiguration.gamesFacade;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerIdSample.createAnyPlayerId;

class GamesFacadeTest {

	private GamesFacade tut;

	@BeforeEach
	void setUp() {
		tut = gamesFacade();
	}

	@Test
	void both_queued_players_should_be_in_same_game() {
		// given
		final UserId playerOne = createAnyPlayerId();
		final UserId playerTwo = createAnyPlayerId();

		// when
		final TicTacToeGameId ticTacToeGame = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// then
		final TicTacToeGameId gameForPlayerOne = tut.getGameForPlayer(playerOne);
		final TicTacToeGameId gameForPlayerTwo = tut.getGameForPlayer(playerTwo);
		assertThat(gameForPlayerOne).isEqualTo(gameForPlayerTwo);
		assertThat(gameForPlayerOne).isEqualTo(ticTacToeGame);
		assertThat(gameForPlayerTwo).isEqualTo(ticTacToeGame);
	}

	@Test
	void first_player_should_be_first_to_move() {
		// given
		final UserId playerOne = createAnyPlayerId();
		final UserId playerTwo = createAnyPlayerId();

		// when
		final TicTacToeGameId ticTacToeGameId = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// then
		final UserId playerToMove = tut.getPlayerToMove(ticTacToeGameId);
		assertThat(playerToMove).isEqualTo(playerOne);
	}

	@Test
	void should_throw_exception_when_player_tries_to_move_in_already_closed_game() {
		// given
		final UserId playerOne = createAnyPlayerId();
		final UserId playerTwo = createAnyPlayerId();

		final TicTacToeGameId ticTacToeGame = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());
		tut.closeGame(ticTacToeGame);

		// when
		final Throwable thrown = catchThrowable(() -> tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(ticTacToeGame).playerId(playerOne).build()));

		// then
		assertThat(thrown).isInstanceOf(ObjectDoesNotExistException.class);
	}

	@Test
	void should_get_which_player_to_move() {
		// given
		final UserId playerOne = createAnyPlayerId();
		final UserId playerTwo = createAnyPlayerId();

		final TicTacToeGameId ticTacToeGame = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// when
		final UserId playerToMove = tut.getPlayerToMove(ticTacToeGame);

		// then
		assertThat(playerToMove).isEqualTo(playerOne);

		// when
		tut.makeMove(MakeMoveSample.builder().playerId(playerOne).ticTacToeGameId(ticTacToeGame).build());

		// then
		assertThat(tut.getPlayerToMove(ticTacToeGame)).isEqualTo(playerTwo);
	}

	@Test
	void should_throw_exception_when_wrong_player_moves() {
		// given
		final UserId playerOne = createAnyPlayerId();
		final UserId playerTwo = createAnyPlayerId();

		final TicTacToeGameId ticTacToeGame = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());
		final UserId playerToMove = tut.getPlayerToMove(ticTacToeGame);
		assertThat(playerToMove).isEqualTo(playerOne);

		// when
		final Throwable thrown = catchThrowable(() -> tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(ticTacToeGame).playerId(playerTwo).build()));

		// then
		assertThat(thrown).isInstanceOf(OtherPlayerTurnException.class);
	}

	@Test
	void game_should_be_ready_for_players() {
		// given
		final UserId playerOne = createAnyPlayerId();
		final UserId playerTwo = createAnyPlayerId();

		final TicTacToeGameId ticTacToeGame = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// when
		final TicTacToeGameId gameReadyForPlayerOne = tut.getGameForPlayer(playerOne);
		final TicTacToeGameId gameReadyForPlayerTwo = tut.getGameForPlayer(playerTwo);

		// then
		assertThat(ticTacToeGame).isEqualTo(gameReadyForPlayerOne);
		assertThat(ticTacToeGame).isEqualTo(gameReadyForPlayerTwo);
	}

	@Test
	void should_throw_exception_when_game_not_ready_for_player() {
		// given
		final UserId player = createAnyPlayerId();

		// when
		final Throwable thrown = catchThrowable(() -> tut.getGameForPlayer(player));

		// then
		assertThat(thrown).isInstanceOf(ObjectDoesNotExistException.class);
	}

}