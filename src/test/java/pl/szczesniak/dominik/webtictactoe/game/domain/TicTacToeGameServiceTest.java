package pl.szczesniak.dominik.webtictactoe.game.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Symbol;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.commands.MakeMoveSample;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.GameDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.NotEnoughPlayersException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static pl.szczesniak.dominik.webtictactoe.game.domain.model.PlayerNameSample.createAnyPlayerName;

class TicTacToeGameServiceTest {

	private TicTacToeGameService tut;

	@BeforeEach
	void setUp() {
		tut = new TicTacToeGameService();
	}

	@Test
	void should_queue_to_play_the_game() {
		// when
		tut.queueToPlay(createAnyPlayerName());

		// then
		final List<Player> queuedPlayers = tut.getQueuedPlayers();
		assertThat(queuedPlayers).hasSize(1);
	}

	@Test
	void both_queued_players_should_be_in_same_game() {
		// given
		final UUID playerOne = tut.queueToPlay(createAnyPlayerName());
		final UUID playerTwo = tut.queueToPlay(createAnyPlayerName());

		// when
		final TicTacToeGame ticTacToeGame = tut.prepareGame();

		// then
		assertThat(ticTacToeGame.getPlayerOne().getPlayerID()).isEqualTo(playerOne);
		assertThat(ticTacToeGame.getPlayerTwo().getPlayerID()).isEqualTo(playerTwo);
		assertThat(tut.getQueuedPlayers()).hasSize(0);
	}

	@Test
	void first_player_to_queue_should_have_symbol_o_and_second_player_to_queue_should_have_symbol_x_when_matched() {
		// given
		final UUID playerOneId = tut.queueToPlay(createAnyPlayerName());
		final UUID playerTwoId = tut.queueToPlay(createAnyPlayerName());

		// when
		final TicTacToeGame ticTacToeGame = tut.prepareGame();

		// then
		assertThat(ticTacToeGame.getPlayerOne().getPlayerID()).isEqualTo(playerOneId);
		assertThat(ticTacToeGame.getPlayerOne().getSymbol()).isEqualTo(new Symbol('O'));
		assertThat(ticTacToeGame.getPlayerTwo().getPlayerID()).isEqualTo(playerTwoId);
		assertThat(ticTacToeGame.getPlayerTwo().getSymbol()).isEqualTo(new Symbol('X'));
	}

	@Test
	void should_not_prepare_game_when_only_one_player_in_queue() {
		// given
		tut.queueToPlay(createAnyPlayerName());

		// when
		final Throwable thrown = catchThrowable(() -> tut.prepareGame());

		// then
		assertThat(thrown).isInstanceOf(NotEnoughPlayersException.class);
		assertThat(tut.getQueuedPlayers()).hasSize(1);
	}

	@Test
	void when_three_players_in_queue_only_two_should_be_removed_from_it_when_game_prepared() {
		// given
		tut.queueToPlay(createAnyPlayerName());
		tut.queueToPlay(createAnyPlayerName());
		tut.queueToPlay(createAnyPlayerName());

		// when
		tut.prepareGame();

		// then
		assertThat(tut.getQueuedPlayers()).hasSize(1);
	}

	@Test
	void players_in_both_games_should_have_different_symbols() {
		// given
		tut.queueToPlay(createAnyPlayerName());
		tut.queueToPlay(createAnyPlayerName());
		tut.queueToPlay(createAnyPlayerName());
		tut.queueToPlay(createAnyPlayerName());

		// when
		final TicTacToeGame ticTacToeGame_1 = tut.prepareGame();
		final TicTacToeGame ticTacToeGame_2 = tut.prepareGame();

		// then
		assertThat(ticTacToeGame_1.getPlayerOne().getSymbol()).isNotEqualTo(ticTacToeGame_1.getPlayerTwo().getSymbol());
		assertThat(ticTacToeGame_2.getPlayerOne().getSymbol()).isNotEqualTo(ticTacToeGame_2.getPlayerTwo().getSymbol());
	}

	@Test
	void should_throw_exception_when_player_tries_to_move_in_already_closed_game() {
		// given
		final UUID player = tut.queueToPlay(createAnyPlayerName());
		tut.queueToPlay(createAnyPlayerName());

		final TicTacToeGame ticTacToeGame = tut.prepareGame();
		tut.closeGame(ticTacToeGame.getGameId());

		// when
		final Throwable thrown = catchThrowable(() -> tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(ticTacToeGame.getGameId()).playerId(player).build()));

		// then
		assertThat(thrown).isInstanceOf(GameDoesNotExistException.class);
	}

	@Test
	void should_get_which_player_to_move() {
		// given
		final UUID playerOne = tut.queueToPlay(createAnyPlayerName());
		final UUID playerTwo = tut.queueToPlay(createAnyPlayerName());

		final TicTacToeGame ticTacToeGame = tut.prepareGame();

		// when
		final UUID playerToMove = tut.getPlayerToMove(ticTacToeGame.getGameId());

		// then
		assertThat(playerToMove).isEqualTo(playerOne);

		// when
		tut.makeMove(MakeMoveSample.builder().playerId(playerOne).ticTacToeGameId(ticTacToeGame.getGameId()).build());

		// then
		assertThat(tut.getPlayerToMove(ticTacToeGame.getGameId())).isEqualTo(playerTwo);
	}

	@Test
	void should_throw_exception_when_wrong_player_moves() {
		// given
		final UUID playerOne = tut.queueToPlay(createAnyPlayerName());
		final UUID playerTwo = tut.queueToPlay(createAnyPlayerName());

		final TicTacToeGame ticTacToeGame = tut.prepareGame();
		final UUID playerToMove = tut.getPlayerToMove(ticTacToeGame.getGameId());
		assertThat(playerToMove).isEqualTo(playerOne);

		// when
		final Throwable thrown = catchThrowable(() -> tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(ticTacToeGame.getGameId()).playerId(playerTwo).build()));

		// then
		assertThat(thrown).isInstanceOf(OtherPlayerTurnException.class);
	}

	@Test
	void game_should_be_ready_for_players() {
		// given

		final UUID playerOne = tut.queueToPlay(createAnyPlayerName());
		final UUID playerTwo = tut.queueToPlay(createAnyPlayerName());

		final TicTacToeGame ticTacToeGame = tut.prepareGame();

		// when
		final TicTacToeGameId gameReadyForPlayerOne = tut.getGameForPlayer(playerOne);
		final TicTacToeGameId gameReadyForPlayerTwo = tut.getGameForPlayer(playerTwo);

		// then
		assertThat(ticTacToeGame.getGameId()).isEqualTo(gameReadyForPlayerOne);
		assertThat(ticTacToeGame.getGameId()).isEqualTo(gameReadyForPlayerTwo);
	}

	@Test
	void should_throw_exception_when_game_not_ready_for_player() {
		// given
		final UUID player = tut.queueToPlay(createAnyPlayerName());

		// when
		final Throwable thrown = catchThrowable(() -> tut.getGameForPlayer(player));

		// then
		assertThat(thrown).isInstanceOf(GameDoesNotExistException.class);
	}

}