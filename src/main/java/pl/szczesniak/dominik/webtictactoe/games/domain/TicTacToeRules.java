package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.SingleGame;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Symbol;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyGameStatus;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMove;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class TicTacToeRules {

	private final TicTacToeGame ticTacToeGame;


	GameState makeMove(final GameMove playerMove) {
		final Map<UserId, Player> players = preparePlayers();
		final SingleGame singleGame = prepareSingleGame(players.get(ticTacToeGame.getPlayerOne()), players.get(ticTacToeGame.getPlayerTwo()));
		ticTacToeGame.getMoves().forEach(move -> executeHistoryMove(players, singleGame, move));
		final GameResult gameResult = singleGame.makeMove(players.get(playerMove.getPlayer()), new PlayerMove(playerMove.getRow(), playerMove.getColumn()));
		return toGameState(playerMove.getPlayer(), gameResult);
	}

	private GameState toGameState(final UserId userId, final GameResult gameResult) {
		try {
			if (gameResult.getWhoWon() != null) {
				return new GameState(MyGameStatus.fromGameStatus(gameResult.getGameStatus()), userId);
			}
		} catch (NullPointerException e) {
			return new GameState(MyGameStatus.fromGameStatus(gameResult.getGameStatus()), null);
		}
		return null;
	}

	GameInfo gameInfo() {
		return recreateTheGameForBoard(preparePlayers());
	}

	private GameInfo recreateTheGameForBoard(final Map<UserId, Player> players) {
		final SingleGame singleGame = prepareSingleGame(players.get(ticTacToeGame.getPlayerOne()), players.get(ticTacToeGame.getPlayerTwo()));
		ticTacToeGame.getMoves().forEach(move -> executeHistoryMove(players, singleGame, move));
		return new GameInfo(getNextPlayerToMove(), singleGame.getBoardView());
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

	private void executeHistoryMove(final Map<UserId, Player> players, final SingleGame singleGame, final GameMove move) {
		singleGame.makeMove(players.get(move.getPlayer()), new PlayerMove(move.getRow(), move.getColumn()));
	}

	private Map<UserId, Player> preparePlayers() {
		final Map<UserId, Player> players = new ConcurrentHashMap<>();
		final Player playerOne = new Player(new Symbol('O'), new PlayerName("asd"));
		final Player playerTwo = new Player(new Symbol('X'), new PlayerName("qwe"));
		players.put(ticTacToeGame.getPlayerOne(), playerOne);
		players.put(ticTacToeGame.getPlayerTwo(), playerTwo);
		return players;
	}

	private static SingleGame prepareSingleGame(final Player playerOne, final Player playerTwo) {
		return new SingleGame(playerOne, playerTwo, 3);
	}
}
