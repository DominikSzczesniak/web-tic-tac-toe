package pl.szczesniak.dominik.webtictactoe.games.domain;

public class TestTicTacToeRulesConfiguartion {

	static TicTacToeRules ticTacToeRules(final TicTacToeGame ticTacToeGame) {
		return new TicTacToeRules(ticTacToeGame);
	}

}
