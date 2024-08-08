package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.CreateGame;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class GamesFacade {

	private final GamesService gamesService;

	public TicTacToeGameId createGame(final CreateGame createGame) {
		return gamesService.createGame(createGame);
	}

	public GameState makeMove(final MakeMove command) {
		return gamesService.makeMove(command);
	}

	public GameInfo getGameInfo(final TicTacToeGameId gameId) {
		return gamesService.getGameInfo(gameId);
	}

	public void closeGame(final TicTacToeGameId ticTacToeGameId) {
		gamesService.closeGame(ticTacToeGameId);
	}

	public TicTacToeGameId getGameForPlayer(final UserId playerId) {
		return gamesService.getGameForPlayer(playerId);
	}

}
