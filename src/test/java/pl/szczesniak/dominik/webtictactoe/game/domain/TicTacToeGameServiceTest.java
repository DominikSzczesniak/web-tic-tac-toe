package pl.szczesniak.dominik.webtictactoe.game.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.commands.MakeMoveSample;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.GameDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.NotEnoughPlayersException;

import java.util.List;

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
		final List<PlayerName> queuedPlayers = tut.getQueuedPlayers();
		assertThat(queuedPlayers).hasSize(1);
	}

	@Test
	void both_queued_players_should_be_in_same_game() {
		// given
		final PlayerName playerOne = createAnyPlayerName();
		final PlayerName playerTwo = createAnyPlayerName();

		tut.queueToPlay(playerOne);
		tut.queueToPlay(playerTwo);

		// when
		final TicTacToeGame ticTacToeGame = tut.prepareGame();

		// then
		assertThat(ticTacToeGame.getPlayerOne().getName()).isEqualTo(playerOne);
		assertThat(ticTacToeGame.getPlayerTwo().getName()).isEqualTo(playerTwo);
		assertThat(tut.getQueuedPlayers()).hasSize(0);
	}

	@Test
	void should_not_create_game_for_one_player() {
		// given
		tut.queueToPlay(createAnyPlayerName());

		// when
		final Throwable thrown = catchThrowable(() -> tut.prepareGame());

		// then
		assertThat(thrown).isInstanceOf(NotEnoughPlayersException.class);
		assertThat(tut.getQueuedPlayers()).hasSize(1);
	}

	@Test
	void when_three_players_in_queue_only_two_should_be_removed_from_it_when_game_ready() {
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
	void should_create_two_games() {
		// given
		final PlayerName playerOne = createAnyPlayerName();
		final PlayerName playerTwo = createAnyPlayerName();
		final PlayerName playerThree = createAnyPlayerName();
		final PlayerName playerFour = createAnyPlayerName();

		tut.queueToPlay(playerOne);
		tut.queueToPlay(playerTwo);
		tut.queueToPlay(playerThree);
		tut.queueToPlay(playerFour);

		// when
		final TicTacToeGame ticTacToeGame_1 = tut.prepareGame();
		final TicTacToeGame ticTacToeGame_2 = tut.prepareGame();

		// then
		assertThat(ticTacToeGame_1.getPlayerOne().getName()).isEqualTo(playerOne);
		assertThat(ticTacToeGame_1.getPlayerTwo().getName()).isEqualTo(playerTwo);
		assertThat(ticTacToeGame_2.getPlayerOne().getName()).isEqualTo(playerThree);
		assertThat(ticTacToeGame_2.getPlayerTwo().getName()).isEqualTo(playerFour);
	}

	@Test
	void should_throw_exception_when_player_tries_to_move_in_alreadyClosedGame() {
		// given
		final PlayerName playerName = createAnyPlayerName();
		tut.queueToPlay(playerName);
		tut.queueToPlay(createAnyPlayerName());

		final TicTacToeGame ticTacToeGame = tut.prepareGame();
		tut.closeGame(ticTacToeGame.getGameId());

		// when
		final Throwable thrown = catchThrowable(() -> tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(ticTacToeGame.getGameId()).playerName(playerName).build()));

		// then
		assertThat(thrown).isInstanceOf(GameDoesNotExistException.class);
	}

	@Test
	void should_get_which_player_to_move() {
		// given
		final PlayerName playerOne = createAnyPlayerName();
		final PlayerName playerTwo = createAnyPlayerName();

		tut.queueToPlay(playerOne);
		tut.queueToPlay(playerTwo);

		final TicTacToeGame ticTacToeGame = tut.prepareGame();

		// when
		final PlayerName playerToMove = tut.getPlayerToMove(ticTacToeGame.getGameId());

		// then
		assertThat(playerToMove).isEqualTo(playerOne);

		// when
		tut.makeMove(MakeMoveSample.builder().playerName(playerOne).ticTacToeGameId(ticTacToeGame.getGameId()).build());

		// then
		assertThat(tut.getPlayerToMove(ticTacToeGame.getGameId())).isEqualTo(playerTwo);
	}

}