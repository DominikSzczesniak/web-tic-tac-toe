package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.SingleGame;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Symbol;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.CreateGame;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GamesService {

	private final ConcurrentHashMap<TicTacToeGameId, SingleGame> gamesInProgress = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<TicTacToeGameId, TicTacToeGame> ticTacToeGames = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<UUID, UserId> playerIds = new ConcurrentHashMap<>();
	private final DomainEventsPublisher domainEventsPublisher;

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
				new TicTacToeGameId(new Random().nextLong(1, 10000000)),
				new Player(new Symbol('O'), command.getPlayerOneName()),
				new Player(new Symbol('X'), command.getPlayerTwoName())
		);
		playerIds.put(ticTacToeGame.getPlayerOne().getPlayerID(), command.getPlayerOne());
		playerIds.put(ticTacToeGame.getPlayerTwo().getPlayerID(), command.getPlayerTwo());
		ticTacToeGame.setNextPlayerToMove();
		ticTacToeGames.put(ticTacToeGame.getGameId(), ticTacToeGame);
		return ticTacToeGame;
	}

	GameInfo makeMove(final MakeMove command) {
		final SingleGame singleGame = getGameInProgress(command.getGameId());
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(command.getGameId());

		final GameInfo gameResult = makePlayerMove(command.getPlayerId(), command.getPlayerMove(), singleGame, ticTacToeGame);

		gamesInProgress.put(command.getGameId(), singleGame);
		ticTacToeGames.put(command.getGameId(), ticTacToeGame);
		domainEventsPublisher.publish(new MoveMade(command.getGameId().getValue(), gameResult));
		return gameResult;
	}

	private GameInfo makePlayerMove(final UserId playerId, final PlayerMove playerMove,
									final SingleGame singleGame, final TicTacToeGame ticTacToeGame) {
		final Player player = ticTacToeGame.getNextPlayerToMove();
		final UserId userId = convertToMyUserId(player.getPlayerID());
		if (userId.equals(playerId)) {
			final GameResult gameResult = singleGame.makeMove(player, playerMove);
			final GameInfo gameInfo = toMyGameResult(userId, gameResult);
			ticTacToeGame.setNextPlayerToMove();
			return gameInfo;
		}
		throw new OtherPlayerTurnException("Other player to move.");
	}

	private static GameInfo toMyGameResult(final UserId userId, final GameResult gameResult) {
		try {
			if (gameResult.getWhoWon() != null) {
				return new GameInfo(gameResult.getGameStatus(), userId);
			}
		} catch (NullPointerException e) {
			return new GameInfo(gameResult.getGameStatus(), null);
		}
		return null;
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
