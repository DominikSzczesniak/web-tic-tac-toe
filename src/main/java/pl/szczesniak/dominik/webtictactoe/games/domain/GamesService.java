package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.SingleGame;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
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
		ticTacToeGame.setNextPlayerToMove();
		ticTacToeGamesRepository.create(ticTacToeGame);
		return ticTacToeGame.getGameId();
	}

	GameInfo makeMove(final MakeMove command) {
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(command.getGameId());
		checkIsPlayerTurn(command.getPlayerId(), ticTacToeGame.getNextPlayerToMove());

		final MoveContext moveContext = replayTheGame(ticTacToeGame);
		final GameInfo gameResult = makePlayerMove(command.getPlayerId(), command.getPlayerMove(), ticTacToeGame, moveContext);

		ticTacToeGamesRepository.update(ticTacToeGame);
		domainEventsPublisher.publish(new MoveMade(command.getGameId().getValue(), gameResult));
		return gameResult;
	}

	private GameInfo makePlayerMove(final UserId playerId, final PlayerMove playerMove,
									final TicTacToeGame ticTacToeGame, final MoveContext moveContext) {
		final SingleGame singleGame = moveContext.getSingleGame();

		final GameResult gameResult = singleGame.makeMove(moveContext.getPlayerToMove(), playerMove);
		ticTacToeGame.setNextPlayerToMove();
		ticTacToeGame.addMove(new MyPlayerMove(playerMove.getRowIndex(), playerMove.getColumnIndex()));
		return toMyGameResult(playerId, gameResult);
	}

	private void checkIsPlayerTurn(final UserId playerId, final UserId playerToMove) {
		if (!playerId.equals(playerToMove)) {
			throw new OtherPlayerTurnException("Other player to move.");
		}
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
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(new TicTacToeGameId(gameId));
		final MoveContext moveContext = replayTheGame(ticTacToeGame);
		return moveContext.getSingleGame().getBoardView();
	}

	private MoveContext replayTheGame(final TicTacToeGame ticTacToeGame) {
		final Map<UserId, Player> players = new ConcurrentHashMap<>();
		final Player playerOne = new Player(new Symbol('O'), new PlayerName("asd"));
		final Player playerTwo = new Player(new Symbol('X'), new PlayerName("qwe"));
		players.put(ticTacToeGame.getPlayerOne(), playerOne);
		players.put(ticTacToeGame.getPlayerTwo(), playerTwo);

		final SingleGame singleGame = prepareSingleGame(playerOne, playerTwo);
		ticTacToeGame.setPlayerToMoveToDefault();
		ticTacToeGame.getMoves().forEach(move -> {
			singleGame.makeMove(players.get(ticTacToeGame.getNextPlayerToMove()), new PlayerMove(move.getRow(), move.getColumn()));
			ticTacToeGame.setNextPlayerToMove();
		});
		return new MoveContext(singleGame, players.get(ticTacToeGame.getNextPlayerToMove()));
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

	@Value
	private static class MoveContext {

		@NonNull SingleGame singleGame;
		@NonNull Player playerToMove;

	}

}
