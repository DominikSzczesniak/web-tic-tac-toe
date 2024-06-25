package pl.szczesniak.dominik.webtictactoe.games.domain;

import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

public class InMemoryTicTacToeGamesRepository implements TicTacToeGamesRepository {

	private final ConcurrentHashMap<TicTacToeGameId, TicTacToeGame> ticTacToeGames = new ConcurrentHashMap<>();

	@Override
	public void create(final TicTacToeGame ticTacToeGame) {
		ticTacToeGames.put(ticTacToeGame.getGameId(), ticTacToeGame);
	}

	@Override
	public void update(final TicTacToeGame ticTacToeGame) {
		ticTacToeGames.put(ticTacToeGame.getGameId(), ticTacToeGame);
	}

	@Override
	public void remove(final TicTacToeGameId ticTacToeGameId) {
		ticTacToeGames.remove(ticTacToeGameId);
	}

	@Override
	public TicTacToeGame getGame(final TicTacToeGameId gameId) {
		return ofNullable(ticTacToeGames.get(gameId)).orElseThrow(
				() -> new ObjectDoesNotExistException("Game with gameId=" + gameId + " does not exist"));
	}

	@Override
	public TicTacToeGameId getGameForPlayer(final UserId playerId) {
		final Optional<TicTacToeGame> game = ticTacToeGames.values().stream()
				.filter(ticTacToeGame ->
						ticTacToeGame.getPlayerOne().equals(playerId)
								|| ticTacToeGame.getPlayerTwo().equals(playerId))
				.findFirst();
		return game.orElseThrow(() -> new ObjectDoesNotExistException("Game for player=" + playerId + " does not exist")).getGameId();
	}
}
