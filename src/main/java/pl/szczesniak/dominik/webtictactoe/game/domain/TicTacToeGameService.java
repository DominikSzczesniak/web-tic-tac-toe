package pl.szczesniak.dominik.webtictactoe.game.domain;

import org.springframework.stereotype.Service;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.SingleGame;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Symbol;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.GameDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.NotEnoughPlayersException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Optional.ofNullable;

@Service
public class TicTacToeGameService {

	private final ConcurrentHashMap<Long, SingleGame> singleGamesInProgress = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<TicTacToeGameId, TicTacToeGame> ticTacToeGames = new ConcurrentHashMap<>();
	private final List<PlayerName> playersInQueue = new ArrayList<>();
	private final AtomicLong id = new AtomicLong(1);

	public void queueToPlay(final PlayerName playerName) {
		playersInQueue.add(playerName);
	}

	public TicTacToeGame prepareGame() {
		checkEnoughPlayersForAGameInQueue();

		final TicTacToeGame ticTacToeGame = createTicTacToeGame();
		final SingleGame game = new SingleGame(
				ticTacToeGame.getPlayerOne(),
				ticTacToeGame.getPlayerTwo(),
				3
		);

		singleGamesInProgress.put(ticTacToeGame.getGameId().getValue(), game);
		removePlayersFromQueue();

		return ticTacToeGame;
	}

	private void checkEnoughPlayersForAGameInQueue() {
		if (playersInQueue.size() < 2) {
			throw new NotEnoughPlayersException("Not enough players in queue to start a game.");
		}
	}

	private TicTacToeGame createTicTacToeGame() {
		final TicTacToeGame ticTacToeGame = new TicTacToeGame(
				new Player(new Symbol('O'), playersInQueue.get(0)),
				new Player(new Symbol('X'), playersInQueue.get(1)),
				new TicTacToeGameId(id.getAndIncrement())
		);
		ticTacToeGame.setNextPlayerToMove();
		ticTacToeGames.put(ticTacToeGame.getGameId(), ticTacToeGame);
		return ticTacToeGame;
	}

	private void removePlayersFromQueue() {
		playersInQueue.remove(1);
		playersInQueue.remove(0);
	}

	public GameResult makeMove(final MakeMove command) {
		final SingleGame singleGame = getSingleGame(command.getGameId());
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(command.getGameId());

		final GameResult gameResult = resolvePlayerMove(command.getPlayerName(), command.getPlayerMove(), singleGame, ticTacToeGame);

		singleGamesInProgress.put(command.getGameId().getValue(), singleGame);
		ticTacToeGames.put(command.getGameId(), ticTacToeGame);
		return gameResult;
	}

	private static GameResult resolvePlayerMove(final PlayerName playerName, final PlayerMove playerMove,
												final SingleGame singleGame, final TicTacToeGame ticTacToeGame) {
		final Player player = ticTacToeGame.getPlayerByName(playerName);
		final GameResult gameResult = singleGame.makeMove(player, playerMove);
		ticTacToeGame.setNextPlayerToMove();
		return gameResult;
	}

	public PlayerName getPlayerToMove(final TicTacToeGameId gameId) {
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(gameId);
		return ticTacToeGame.getNextPlayerToMove().getName();
	}

	public Character[][] getBoardView(final Long gameId) {
		final SingleGame singleGame = getSingleGame(new TicTacToeGameId(gameId));
		return singleGame.getBoardView();
	}

	List<PlayerName> getQueuedPlayers() {
		return playersInQueue;
	}

	public void closeGame(final TicTacToeGameId ticTacToeGameId) {
		singleGamesInProgress.remove(ticTacToeGameId.getValue());
		ticTacToeGames.remove(ticTacToeGameId);
	}

	private SingleGame getSingleGame(final TicTacToeGameId gameId) {
		return ofNullable(singleGamesInProgress.get(gameId.getValue())).orElseThrow((
				() -> new GameDoesNotExistException("No game with this gameId exists: " + gameId)));
	}

	private TicTacToeGame getTicTacToeGame(final TicTacToeGameId gameId) {
		return ofNullable(ticTacToeGames.get(gameId)).orElseThrow(
				() -> new GameDoesNotExistException("Game with gameId=" + gameId.toString() + " does not exist"));
	}

	public TicTacToeGameId getGameForPlayer(final PlayerName playerName) {
		final Optional<TicTacToeGame> game = ticTacToeGames.values().stream()
				.filter(ticTacToeGame ->
						ticTacToeGame.getPlayerOne().getName().equals(playerName)
						|| ticTacToeGame.getPlayerTwo().getName().equals(playerName))
				.findFirst();
		return game.orElseThrow(() -> new GameDoesNotExistException("Game for player=" + playerName.getValue() + " does not exist")).getGameId();
	}

}
