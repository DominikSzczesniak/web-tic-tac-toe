package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.SingleGame;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Symbol;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.CreateGame;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GamesService {

	private final ConcurrentHashMap<TicTacToeGameId, SingleGame> gamesInProgress = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<TicTacToeGameId, TicTacToeGame> ticTacToeGames = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<UUID, UserId> playerIds = new ConcurrentHashMap<>();
	private final AtomicLong id = new AtomicLong(1);

	TicTacToeGameId prepareGame(final CreateGame command) {
		final TicTacToeGame ticTacToeGame = createTicTacToeGame(command);
		final SingleGame game = new SingleGame(
				ticTacToeGame.getPlayerOne(),
				ticTacToeGame.getPlayerTwo(),
				3
		);

		gamesInProgress.put(ticTacToeGame.getGameId(), game);
		return ticTacToeGame.getGameId();
	}

	private TicTacToeGame createTicTacToeGame(final CreateGame command) {
		final TicTacToeGame ticTacToeGame = new TicTacToeGame(
				new TicTacToeGameId(id.getAndIncrement()),
				new Player(new Symbol('O'), command.getPlayerOneName()),
				new Player(new Symbol('X'), command.getPlayerTwoName())
		);
		playerIds.put(ticTacToeGame.getPlayerOne().getPlayerID(), command.getPlayerOne());
		playerIds.put(ticTacToeGame.getPlayerTwo().getPlayerID(), command.getPlayerTwo());
		ticTacToeGame.setNextPlayerToMove();
		ticTacToeGames.put(ticTacToeGame.getGameId(), ticTacToeGame);
		return ticTacToeGame;
	}

	GameResult makeMove(final MakeMove command) {
		final SingleGame singleGame = getGameInProgress(command.getGameId());
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(command.getGameId());

		final GameResult gameResult = makePlayerMove(command.getPlayerId(), command.getPlayerMove(), singleGame, ticTacToeGame);

		gamesInProgress.put(command.getGameId(), singleGame);
		ticTacToeGames.put(command.getGameId(), ticTacToeGame);
		return gameResult;
	}

	private GameResult makePlayerMove(final UserId playerId, final PlayerMove playerMove,
									  final SingleGame singleGame, final TicTacToeGame ticTacToeGame) {
		final Player player = ticTacToeGame.getNextPlayerToMove();
		if (convertToMyUserId(player.getPlayerID()).equals(playerId)) {
			final GameResult gameResult = singleGame.makeMove(player, playerMove);
			ticTacToeGame.setNextPlayerToMove();
			return gameResult;
		}
		throw new OtherPlayerTurnException("Other player to move.");
	}

	UserId getPlayerToMove(final TicTacToeGameId gameId) {
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(gameId);
		return convertToMyUserId(ticTacToeGame.getNextPlayerToMove().getPlayerID());
	}

	Character[][] getBoardView(final Long gameId) {
		final SingleGame singleGame = getGameInProgress(new TicTacToeGameId(gameId));
		return singleGame.getBoardView();
	}

	void closeGame(final TicTacToeGameId ticTacToeGameId) {
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(ticTacToeGameId);
		playerIds.remove(ticTacToeGame.getPlayerOne().getPlayerID());
		playerIds.remove(ticTacToeGame.getPlayerTwo().getPlayerID());
		gamesInProgress.remove(ticTacToeGameId);
		ticTacToeGames.remove(ticTacToeGameId);
	}

	private SingleGame getGameInProgress(final TicTacToeGameId gameId) {
		return ofNullable(gamesInProgress.get(gameId)).orElseThrow((
				() -> new ObjectDoesNotExistException("No game with this gameId exists: " + gameId)));
	}

	private TicTacToeGame getTicTacToeGame(final TicTacToeGameId gameId) {
		return ofNullable(ticTacToeGames.get(gameId)).orElseThrow(
				() -> new ObjectDoesNotExistException("Game with gameId=" + gameId + " does not exist"));
	}

	TicTacToeGameId getGameForPlayer(final UserId playerId) {
		final Optional<TicTacToeGame> game = ticTacToeGames.values().stream()
				.filter(ticTacToeGame ->
						convertToMyUserId(ticTacToeGame.getPlayerOne().getPlayerID()).equals(playerId)
								|| convertToMyUserId(ticTacToeGame.getPlayerTwo().getPlayerID()).equals(playerId))
				.findFirst();
		return game.orElseThrow(() -> new ObjectDoesNotExistException("Game for player=" + playerId + " does not exist")).getGameId();
	}

	private UserId convertToMyUserId(final UUID playerId) {
		return playerIds.get(playerId);
	}

}
