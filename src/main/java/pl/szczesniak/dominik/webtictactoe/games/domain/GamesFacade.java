package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.CreateGame;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class GamesFacade {

	private final TicTacToeGamesRepository ticTacToeGamesRepository;
	private final DomainEventsPublisher domainEventsPublisher;

	@Transactional
	public TicTacToeGameId createGame(final CreateGame command) {
		final TicTacToeGame ticTacToeGame = new TicTacToeGame(command.getPlayerOne(), command.getPlayerTwo());
		ticTacToeGamesRepository.create(ticTacToeGame);
		return ticTacToeGame.getGameId();
	}

	@Transactional
	public GameState makeMove(final MakeMove command) {
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(command.getGameId());
		final TicTacToeRules ticTacToeRules = new TicTacToeRules(ticTacToeGame);

		final GameState gameState = ticTacToeRules.makeMove(command.getMove());
		ticTacToeGame.addMove(command.getMove());

		ticTacToeGamesRepository.update(ticTacToeGame);
		domainEventsPublisher.publish(new MoveMade(command.getGameId(), gameState));
		return gameState;
	}

	public GameInfo getGameInfo(final TicTacToeGameId gameId) {
		final TicTacToeGame ticTacToeGame = getTicTacToeGame(gameId);
		final TicTacToeRules ticTacToeRules = new TicTacToeRules(ticTacToeGame);
		return ticTacToeRules.gameInfo();
	}

	private TicTacToeGame getTicTacToeGame(final TicTacToeGameId gameId) {
		return ticTacToeGamesRepository.getGame(gameId).orElseThrow(() -> new ObjectDoesNotExistException("Game " + gameId + " does not exist"));
	}

	@Transactional
	public void closeGame(final TicTacToeGameId ticTacToeGameId, final UserId userId) {
		TicTacToeGame ticTacToeGame = getTicTacToeGame(ticTacToeGameId);
		ticTacToeGame.checkUserIsPartOfTheGame(userId);
		ticTacToeGamesRepository.remove(ticTacToeGameId);
	}

	public TicTacToeGameId getGameForPlayer(final UserId playerId) {
		return ticTacToeGamesRepository.getGameForPlayer(playerId);
	}

}
