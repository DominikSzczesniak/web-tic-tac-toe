package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.CreateGame;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class GamesFacade {

	private final GamesService gamesService;

	public TicTacToeGameId createGame(final CreateGame createGame) {
		return gamesService.prepareGame(createGame);
	}

	public GameResult makeMove(final MakeMove command) {
		return gamesService.makeMove(command);
	}

	public UserId getPlayerToMove(final TicTacToeGameId gameId) {
		return gamesService.getPlayerToMove(gameId);
	}

	public Character[][] getBoardView(final Long gameId) {
		return gamesService.getBoardView(gameId);
	}

	public void closeGame(final TicTacToeGameId ticTacToeGameId) {
		gamesService.closeGame(ticTacToeGameId);
	}

	public TicTacToeGameId getGameForPlayer(final UserId playerId) {
		return gamesService.getGameForPlayer(playerId);
	}

}
