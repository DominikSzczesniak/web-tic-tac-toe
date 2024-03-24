package pl.szczesniak.dominik.webtictactoe.game.domain;

import org.springframework.stereotype.Service;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.SingleGame;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Symbol;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.GameDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.NotEnoughPlayersException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Optional.ofNullable;

@Service
public class TicTacToeGameService {

	private final ConcurrentHashMap<Long, SingleGame> singleGamesInProgress = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<TicTacToeGameId, TicTacToeGame> ticTacToeGames = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<UUID, Player> playersInQueue = new ConcurrentHashMap<>();
	private final AtomicLong id = new AtomicLong(1);

	public UUID queueToPlay(final PlayerName playerName) {
		final Player player;

		if (playersInQueue.size() == 0 || playersInQueue.size() % 2 == 0) {
			player = new Player(new Symbol('O'), playerName);
		} else {
			player = new Player(new Symbol('X'), playerName);
		}

		playersInQueue.put(player.getPlayerID(), player);
		return player.getPlayerID();
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
		removePlayersFromQueue(ticTacToeGame.getPlayerOne().getPlayerID(), ticTacToeGame.getPlayerTwo().getPlayerID());

		return ticTacToeGame;
	}

	private void checkEnoughPlayersForAGameInQueue() {
		if (playersInQueue.size() < 2) {
			throw new NotEnoughPlayersException("Not enough players in queue to start a game.");
		}
	}

	private TicTacToeGame createTicTacToeGame() {
		final Player player1 = getPlayerWithSymbolO();
		final Player player2 = getPlayerWithSymbolX();
		final TicTacToeGame ticTacToeGame = new TicTacToeGame(
				player1,
				player2,
				new TicTacToeGameId(id.getAndIncrement())
		);
		ticTacToeGame.setNextPlayerToMove();
		ticTacToeGames.put(ticTacToeGame.getGameId(), ticTacToeGame);
		return ticTacToeGame;
	}

	private Player getPlayerWithSymbolO() {
		return playersInQueue.values().stream()
				.filter(player -> player.getSymbol().equals(new Symbol('O')))
				.findFirst()
				.orElseThrow(() -> new NotEnoughPlayersException("Not enough players in queue to start a game."));
	}

	private Player getPlayerWithSymbolX() {
		return playersInQueue.values().stream()
				.filter(player -> player.getSymbol().equals(new Symbol('X')))
				.findFirst()
				.orElseThrow(() -> new NotEnoughPlayersException("Not enough players in queue to start a game."));
	}

	private void removePlayersFromQueue(final UUID playerOne, final UUID playerTwo) {
		playersInQueue.remove(playerOne);
		playersInQueue.remove(playerTwo);
	}

	public GameResult makeMove(final MakeMove command) {
		final SingleGame singleGame = getSingleGame(command.getGameId());
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(command.getGameId());

		final GameResult gameResult = resolvePlayerMove(command.getPlayerId(), command.getPlayerMove(), singleGame, ticTacToeGame);

		singleGamesInProgress.put(command.getGameId().getValue(), singleGame);
		ticTacToeGames.put(command.getGameId(), ticTacToeGame);
		return gameResult;
	}

	private static GameResult resolvePlayerMove(final UUID playerId, final PlayerMove playerMove,
												final SingleGame singleGame, final TicTacToeGame ticTacToeGame) {
		final Player player = ticTacToeGame.getNextPlayerToMove();
		if (player.getPlayerID().equals(playerId)) {
			final GameResult gameResult = singleGame.makeMove(player, playerMove);
			ticTacToeGame.setNextPlayerToMove();
			return gameResult;
		}
		throw new OtherPlayerTurnException("Other player to move.");
	}

	public UUID getPlayerToMove(final TicTacToeGameId gameId) {
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(gameId);
		return ticTacToeGame.getNextPlayerToMove().getPlayerID();
	}

	public Character[][] getBoardView(final Long gameId) {
		final SingleGame singleGame = getSingleGame(new TicTacToeGameId(gameId));
		return singleGame.getBoardView();
	}

	List<Player> getQueuedPlayers() {
		return playersInQueue.values().stream().toList();
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

	public TicTacToeGameId getGameForPlayer(final UUID playerId) {
		final Optional<TicTacToeGame> game = ticTacToeGames.values().stream()
				.filter(ticTacToeGame ->
						ticTacToeGame.getPlayerOne().getPlayerID().equals(playerId)
								|| ticTacToeGame.getPlayerTwo().getPlayerID().equals(playerId))
				.findFirst();
		return game.orElseThrow(() -> new GameDoesNotExistException("Game for player=" + playerId.toString() + " does not exist")).getGameId();
	}

}
