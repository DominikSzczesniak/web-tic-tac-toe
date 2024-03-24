package pl.szczesniak.dominik.webtictactoe.games.domain;

class TicTacToeGamesServiceConfigurationTest {

	static TicTacToeGamesService ticTacToeGamesService() {
		return new TicTacToeGamesService(new MatchmakingService());
	}

}
