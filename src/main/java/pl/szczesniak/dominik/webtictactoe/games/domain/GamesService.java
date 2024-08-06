package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.SingleGame;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Symbol;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyGameStatus;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyPlayerMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.CreateGame;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GamesService {

	private final TicTacToeGamesRepository ticTacToeGamesRepository;
	private final DomainEventsPublisher domainEventsPublisher;

	TicTacToeGameId createGame(final CreateGame command) {
		final TicTacToeGame ticTacToeGame = new TicTacToeGame(command.getPlayerOne(), command.getPlayerTwo());
		ticTacToeGamesRepository.create(ticTacToeGame);
		return ticTacToeGame.getGameId();
	}

	GameInfo makeMove(final MakeMove command) {
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(command.getGameId());

		final PlayerMove playerMove = command.getPlayerMove();
		final GameInfo gameResult = makeMoveInLibrary(getTicTacToeGame(command.getGameId()), playerMove);
		ticTacToeGame.addMove(new MyPlayerMove(playerMove.getRowIndex(), playerMove.getColumnIndex(), command.getPlayerId()));

		ticTacToeGamesRepository.update(ticTacToeGame);
		domainEventsPublisher.publish(new MoveMade(command.getGameId().getValue(), gameResult));
		return gameResult;
	}

	private GameInfo makeMoveInLibrary(final TicTacToeGame ticTacToeGame, final PlayerMove playerMove) {
		final Map<UserId, Player> players = preparePlayers(ticTacToeGame);
		final SingleGame singleGame = prepareSingleGame(players.get(ticTacToeGame.getPlayerOne()), players.get(ticTacToeGame.getPlayerTwo()));
		ticTacToeGame.getMoves().forEach(move -> executeHistoryMove(players, singleGame, move));
		return makeNewMove(ticTacToeGame.getNextPlayerToMove(), players.get(ticTacToeGame.getNextPlayerToMove()), playerMove, singleGame);
	}

	private GameInfo makeNewMove(final UserId playerId, final Player player, final PlayerMove playerMove, final SingleGame singleGame) {
		final GameResult gameResult = singleGame.makeMove(player, playerMove);
		return toMyGameResult(playerId, gameResult);
	}

	private static GameInfo toMyGameResult(final UserId userId, final GameResult gameResult) {
		try {
			if (gameResult.getWhoWon() != null) {
				return new GameInfo(MyGameStatus.fromGameStatus(gameResult.getGameStatus()), userId);
			}
		} catch (NullPointerException e) {
			return new GameInfo(MyGameStatus.fromGameStatus(gameResult.getGameStatus()), null);
		}
		return null;
	}

	UserId getPlayerToMove(final TicTacToeGameId gameId) {
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(gameId);
		return ticTacToeGame.getNextPlayerToMove();
	}

	Character[][] getBoardView(final TicTacToeGameId gameId) {
		return replayTheGameForBoard(gameId);
	}

	private Character[][] replayTheGameForBoard(final TicTacToeGameId gameId) {
		final TicTacToeGame ticTacToeGame = ticTacToeGamesRepository.getGame(gameId);
		final Map<UserId, Player> players = preparePlayers(ticTacToeGame);
		final SingleGame singleGame = prepareSingleGame(players.get(ticTacToeGame.getPlayerOne()), players.get(ticTacToeGame.getPlayerTwo()));
		ticTacToeGame.getMoves().forEach(move -> executeHistoryMove(players, singleGame, move));
		return singleGame.getBoardView();
	}

	private static Map<UserId, Player> preparePlayers(final TicTacToeGame ticTacToeGame) {
		final Map<UserId, Player> players = new ConcurrentHashMap<>();
		final Player playerOne = new Player(new Symbol('O'), new PlayerName("asd"));
		final Player playerTwo = new Player(new Symbol('X'), new PlayerName("qwe"));
		players.put(ticTacToeGame.getPlayerOne(), playerOne);
		players.put(ticTacToeGame.getPlayerTwo(), playerTwo);
		return players;
	}

	private static void executeHistoryMove(final Map<UserId, Player> players, final SingleGame singleGame, final MyPlayerMove move) {
		singleGame.makeMove(players.get(move.getPlayer()), new PlayerMove(move.getRow(), move.getColumn()));
	}

	private static SingleGame prepareSingleGame(final Player playerOne, final Player playerTwo) {
		return new SingleGame(playerOne, playerTwo, 3);
	}

	void closeGame(final TicTacToeGameId ticTacToeGameId) {
		ticTacToeGamesRepository.remove(ticTacToeGameId);
	}

	private TicTacToeGame getTicTacToeGame(final TicTacToeGameId gameId) {
		return ticTacToeGamesRepository.getGame(gameId);
	}

	TicTacToeGameId getGameForPlayer(final UserId playerId) {
		return ticTacToeGamesRepository.getGameForPlayer(playerId);
	}

}
