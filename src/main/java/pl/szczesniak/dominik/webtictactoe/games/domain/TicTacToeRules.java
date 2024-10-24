package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.SingleGame;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameStatus;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Symbol;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMoveToMake;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyGameStatus;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class TicTacToeRules {

	private final TicTacToeGame ticTacToeGame;


	GameState makeMove(final GameMoveToMake playerMove) {
		final PlayersInGame players = preparePlayers();
		final SingleGame singleGame = prepareSingleGame(players.playerOne, players.playerTwo);

		final GameResult gameResult = applyMoveToGame(playerMove, players.players, singleGame);

		return toGameState(playerMove.getPlayer(), gameResult);
	}

	private GameResult applyMoveToGame(final GameMoveToMake playerMove, final Map<UserId, Player> players, final SingleGame singleGame) {
		final List<GameMove> moves = getMovesInOrder();
		moves.forEach(move -> executeHistoryMove(players, singleGame, move));
		return singleGame.makeMove(players.get(playerMove.getPlayer()), new PlayerMove(playerMove.getRow(), playerMove.getColumn()));
	}

	GameInfo gameInfo() {
		final PlayersInGame players = preparePlayers();
		final SingleGame singleGame = prepareSingleGame(players.playerOne, players.playerTwo);
		final GameResult gameResult = getGameResult(players.players, singleGame);
		final UserId presumedWinner = getLastMove().map(GameMove::getPlayer).orElse(ticTacToeGame.getPlayerOne());
		return new GameInfo(getNextPlayerToMove(), singleGame.getBoardView(), toGameState(presumedWinner, gameResult));
	}

	private GameState toGameState(final UserId presumedWinner, final GameResult gameResult) {
		try {
			if (gameResult.getWhoWon() != null) {
				return new GameState(MyGameStatus.fromGameStatus(gameResult.getGameStatus()), presumedWinner);
			}
		} catch (NullPointerException e) {
			return new GameState(MyGameStatus.fromGameStatus(gameResult.getGameStatus()), null);
		}
		return null;
	}

	private GameResult getGameResult(final Map<UserId, Player> players, final SingleGame singleGame) {
		GameResult gameResult = null;
		final List<GameMove> moves = getMovesInOrder();
		for (GameMove move : moves) {
			gameResult = executeHistoryMove(players, singleGame, move);
		}
		if (gameResult == null) {
			gameResult = new GameResult(GameStatus.IN_PROGRESS, null);
		}
		return gameResult;
	}

	private List<GameMove> getMovesInOrder() {
		final List<GameMove> moves = ticTacToeGame.getMoves();
		moves.sort(Comparator.comparingInt(GameMove::getMoveNumber));
		return moves;
	}

	private GameResult executeHistoryMove(final Map<UserId, Player> players, final SingleGame singleGame, final GameMove move) {
		return singleGame.makeMove(players.get(move.getPlayer()), new PlayerMove(move.getRow(), move.getColumn()));
	}

	private UserId getNextPlayerToMove() {
		final Optional<UserId> lastPlayer = getLastMove().map(GameMove::getPlayer);
		if (lastPlayer.isEmpty()) {
			return ticTacToeGame.getPlayerOne();
		}
		return lastPlayer.get().equals(ticTacToeGame.getPlayerOne()) ? ticTacToeGame.getPlayerTwo() : ticTacToeGame.getPlayerOne();
	}

	private Optional<GameMove> getLastMove() {
		final List<GameMove> moves = ticTacToeGame.getMoves();
		return moves.isEmpty() ? Optional.empty() : Optional.of(moves.get(moves.size() - 1));
	}

	private PlayersInGame preparePlayers() {
		final Map<UserId, Player> players = new ConcurrentHashMap<>();
		final Player playerOne = new Player(new Symbol('O'), new PlayerName("asd"));
		final Player playerTwo = new Player(new Symbol('X'), new PlayerName("qwe"));
		players.put(ticTacToeGame.getPlayerOne(), playerOne);
		players.put(ticTacToeGame.getPlayerTwo(), playerTwo);
		return new PlayersInGame(players, playerOne, playerTwo);
	}

	private static SingleGame prepareSingleGame(final Player playerOne, final Player playerTwo) {
		return new SingleGame(playerOne, playerTwo, 3);
	}

	private record PlayersInGame(Map<UserId, Player> players, Player playerOne, Player playerTwo) {}

}
