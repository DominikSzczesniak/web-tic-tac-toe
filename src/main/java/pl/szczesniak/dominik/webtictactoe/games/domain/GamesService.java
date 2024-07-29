package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.SingleGame;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameResult;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Symbol;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyGameStatus;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.CreateGame;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GamesService {

	private final ConcurrentHashMap<TicTacToeGameId, SingleGame> gamesInProgress = new ConcurrentHashMap<>();
	//	private final ConcurrentHashMap<TicTacToeGameId, TicTacToeGame> ticTacToeGames = new ConcurrentHashMap<>();
	private final TicTacToeGamesRepository ticTacToeGamesRepository;
	private final ConcurrentHashMap<UserId, Player> userIdToLibraryPlayer = new ConcurrentHashMap<>();
	private final DomainEventsPublisher domainEventsPublisher;

	TicTacToeGameId createGame(final CreateGame command) {
		final TicTacToeGame ticTacToeGame = createTicTacToeGame(command);
		final SingleGame game = new SingleGame(
				userIdToLibraryPlayer.get(ticTacToeGame.getPlayerOne()),
				userIdToLibraryPlayer.get(ticTacToeGame.getPlayerTwo()),
				3
		);

		gamesInProgress.put(ticTacToeGame.getGameId(), game);
		return ticTacToeGame.getGameId();
	}

	private TicTacToeGame createTicTacToeGame(final CreateGame command) {
		final TicTacToeGame ticTacToeGame = new TicTacToeGame(
				command.getPlayerOne(),
				command.getPlayerTwo()
		);
		userIdToLibraryPlayer.put(command.getPlayerOne(), new Player(new Symbol('O'), new PlayerName("asd")));
		userIdToLibraryPlayer.put(command.getPlayerTwo(), new Player(new Symbol('X'), new PlayerName("qwe")));
		ticTacToeGame.setNextPlayerToMove();
		ticTacToeGamesRepository.create(ticTacToeGame);
		return ticTacToeGame;
	}

	GameInfo makeMove(final MakeMove command) {
		final SingleGame singleGame = getGameInProgress(command.getGameId());
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(command.getGameId());

		final GameInfo gameResult = makePlayerMove(command.getPlayerId(), command.getPlayerMove(), singleGame, ticTacToeGame);

		gamesInProgress.put(command.getGameId(), singleGame);
//		ticTacToeGames.put(command.getGameId(), ticTacToeGame);
		ticTacToeGamesRepository.update(ticTacToeGame);
		domainEventsPublisher.publish(new MoveMade(command.getGameId().getValue(), gameResult));
		return gameResult;
	}

	private GameInfo makePlayerMove(final UserId playerId, final PlayerMove playerMove,
									final SingleGame singleGame, final TicTacToeGame ticTacToeGame) {
		final UserId player = ticTacToeGame.getNextPlayerToMove();
		if (playerId.equals(player)) {
			final GameResult gameResult = singleGame.makeMove(userIdToLibraryPlayer.get(playerId), playerMove);
			final GameInfo gameInfo = toMyGameResult(playerId, gameResult);
			ticTacToeGame.setNextPlayerToMove();
			return gameInfo;
		}
		throw new OtherPlayerTurnException("Other player to move.");
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

	Character[][] getBoardView(final Long gameId) {
		final SingleGame singleGame = getGameInProgress(new TicTacToeGameId(gameId));
		return singleGame.getBoardView();
	}

	void closeGame(final TicTacToeGameId ticTacToeGameId) {
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(ticTacToeGameId);
		userIdToLibraryPlayer.remove(ticTacToeGame.getPlayerOne());
		userIdToLibraryPlayer.remove(ticTacToeGame.getPlayerTwo());
		gamesInProgress.remove(ticTacToeGameId);
//		ticTacToeGames.remove(ticTacToeGameId);
		ticTacToeGamesRepository.remove(ticTacToeGameId);
	}

	private SingleGame getGameInProgress(final TicTacToeGameId gameId) {
		return ofNullable(gamesInProgress.get(gameId)).orElseThrow((
				() -> new ObjectDoesNotExistException("No game with this gameId exists: " + gameId)));
	}

	private TicTacToeGame getTicTacToeGame(final TicTacToeGameId gameId) {
//		return ofNullable(ticTacToeGames.get(gameId)).orElseThrow(
//				() -> new ObjectDoesNotExistException("Game with gameId=" + gameId + " does not exist"));
		return ticTacToeGamesRepository.getGame(gameId);
	}

	TicTacToeGameId getGameForPlayer(final UserId playerId) {
//		final Optional<TicTacToeGame> game = ticTacToeGames.values().stream()
//				.filter(ticTacToeGame ->
//						ticTacToeGame.getPlayerOne().equals(playerId)
//								|| ticTacToeGame.getPlayerTwo().equals(playerId))
//				.findFirst();
//		return game.orElseThrow(() -> new ObjectDoesNotExistException("Game for player=" + playerId + " does not exist")).getGameId();
		return ticTacToeGamesRepository.getGameForPlayer(playerId);
	}

}
