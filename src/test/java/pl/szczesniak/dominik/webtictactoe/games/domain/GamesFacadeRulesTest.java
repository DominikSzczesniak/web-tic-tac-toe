package pl.szczesniak.dominik.webtictactoe.games.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMoveToMake;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyGameStatus;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.szczesniak.dominik.webtictactoe.games.domain.TestGamesFacadeConfiguration.gamesFacade;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.UserIdSample.createAnyUserId;

public class GamesFacadeRulesTest {

	private InMemoryTicTacToeGamesRepository ticTacToeGamesRepository;
	private TicTacToeRulesFactory ticTacToeRulesFactory;
	private GamesFacade tut;
	private TicTacToeRules ticTacToeRules;

	@BeforeEach
	void setUp() {
		ticTacToeRules = mock(TicTacToeRules.class);
		ticTacToeRulesFactory = mock(TicTacToeRulesFactory.class);
		ticTacToeGamesRepository = new InMemoryTicTacToeGamesRepository();
		tut = gamesFacade(ticTacToeRulesFactory, ticTacToeGamesRepository);
	}

	@Test
	void testMakeMove() {
		// Arrange
		final UserId playerOne = createAnyUserId();
		final UserId playerTwo = createAnyUserId();
		final TicTacToeGame ticTacToeGame = new TicTacToeGame(playerOne, playerTwo);
		ticTacToeGamesRepository.create(ticTacToeGame);
		final TicTacToeGameId gameId = ticTacToeGame.getGameId();
		final MakeMove commandToMove = new MakeMove(gameId, new GameMoveToMake(1, 1, playerOne));
		final GameState expectedState = new GameState(MyGameStatus.IN_PROGRESS, playerOne);


		when(ticTacToeRulesFactory.ticTacToeRulesFor(ticTacToeGame)).thenReturn(ticTacToeRules);
		when(ticTacToeRules.makeMove(commandToMove.getMove())).thenReturn(expectedState);

		// Act
		GameState actualState = tut.makeMove(commandToMove);

		// Assert
		assertEquals(expectedState, actualState);
		verify(ticTacToeRules).makeMove(commandToMove.getMove());
	}

}
