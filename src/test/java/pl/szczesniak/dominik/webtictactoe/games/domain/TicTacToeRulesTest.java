package pl.szczesniak.dominik.webtictactoe.games.domain;

import org.junit.jupiter.api.Test;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMoveToMake;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyGameStatus;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.GameMoveToMakeSample;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.szczesniak.dominik.webtictactoe.games.domain.TestTicTacToeRulesConfiguartion.ticTacToeRules;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserIdSample.createAnyUserId;

class TicTacToeRulesTest {

	private TicTacToeRules tut;

	@Test
	void should_make_move() {
		// given
		final UserId player_1 = createAnyUserId();
		final UserId player_2 = createAnyUserId();
		tut = ticTacToeRules(TicTacToeGameSample.builder().playerOne(player_1).playerTwo(player_2).build());

		// when
		final GameState gameState = tut.makeMove(GameMoveToMakeSample.builder().player(player_1).build());

		// then
		assertThat(gameState.getGameStatus()).isEqualTo(MyGameStatus.IN_PROGRESS);
		assertThat(gameState.getWhoWon()).isEqualTo(empty());
	}

	@Test
	void should_fetch_game_info_when_game_is_already_finished() {
		// given
		final UserId player_1 = createAnyUserId();
		final UserId player_2 = createAnyUserId();
		final TicTacToeGame game = TicTacToeGameSample.builder().playerOne(player_1).playerTwo(player_2).build();
		tut = ticTacToeRules(game);

		final GameMoveToMake move_1 = GameMoveToMakeSample.builder().player(player_1).row(0).column(1).build();
		final GameMoveToMake move_2 = GameMoveToMakeSample.builder().player(player_2).row(1).column(2).build();
		final GameMoveToMake move_3 = GameMoveToMakeSample.builder().player(player_1).row(0).column(0).build();
		final GameMoveToMake move_4 = GameMoveToMakeSample.builder().player(player_2).row(2).column(2).build();
		final GameMoveToMake move_5 = GameMoveToMakeSample.builder().player(player_1).row(0).column(2).build();
		addMovesToTheGame(game, move_1, move_2, move_3, move_4, move_5);

		// when
		final GameInfo gameInfo = tut.gameInfo();

		// then
		assertThat(gameInfo.getPlayerToMove()).isEqualTo(player_2);
		assertThat(gameInfo.getGameState()).isEqualTo(new GameState(MyGameStatus.WIN, player_1));
		final Character[][] boardView = gameInfo.getBoardView();
		assertThat(boardView[0]).containsExactly('O', 'O', 'O');
		assertThat(boardView[1]).containsExactly(null, null, 'X');
		assertThat(boardView[2]).containsExactly(null, null, 'X');
	}

	@Test
	void should_fetch_game_info_after_making_moves() {
		// given
		final UserId player_1 = createAnyUserId();
		final UserId player_2 = createAnyUserId();
		final TicTacToeGame game = TicTacToeGameSample.builder().playerOne(player_1).playerTwo(player_2).build();
		tut = ticTacToeRules(game);

		final GameMoveToMake move_1 = GameMoveToMakeSample.builder().player(player_1).row(1).column(1).build();
		final GameMoveToMake move_2 = GameMoveToMakeSample.builder().player(player_2).row(2).column(2).build();
		tut.makeMove(move_1);
		tut.makeMove(move_2);
		addMovesToTheGame(game, move_1, move_2);

		// when
		final GameInfo gameInfo = tut.gameInfo();

		// then
		assertThat(gameInfo.getPlayerToMove()).isEqualTo(player_1);
		assertThat(gameInfo.getGameState()).isEqualTo(new GameState(MyGameStatus.IN_PROGRESS, null));
		final Character[][] boardView = gameInfo.getBoardView();
		assertThat(boardView[0]).containsExactly(null, null, null);
		assertThat(boardView[1]).containsExactly(null, 'O', null);
		assertThat(boardView[2]).containsExactly(null, null, 'X');
	}


	private static void addMovesToTheGame(final TicTacToeGame game, final GameMoveToMake... movesToMake) {
		for (GameMoveToMake move : movesToMake) {
			game.addMove(move);
		}
	}

}