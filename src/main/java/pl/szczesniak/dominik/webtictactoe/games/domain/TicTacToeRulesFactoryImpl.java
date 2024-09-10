package pl.szczesniak.dominik.webtictactoe.games.domain;

import org.springframework.stereotype.Component;

@Component
public class TicTacToeRulesFactoryImpl implements TicTacToeRulesFactory {

	@Override
	public TicTacToeRules ticTacToeRulesFor(final TicTacToeGame ticTacToeGame) {
		return new TicTacToeRules(ticTacToeGame);
	}

}
