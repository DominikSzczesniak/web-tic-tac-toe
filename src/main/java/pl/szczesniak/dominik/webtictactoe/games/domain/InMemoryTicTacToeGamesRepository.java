package pl.szczesniak.dominik.webtictactoe.games.domain;

import org.springframework.stereotype.Repository;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Optional.ofNullable;

@Repository
class InMemoryTicTacToeGamesRepository implements TicTacToeGamesRepository {

	private final AtomicLong nextId = new AtomicLong(0);
	private final Map<TicTacToeGameId, TicTacToeGame> ticTacToeGames = new HashMap<>();

	@Override
	public void create(final TicTacToeGame ticTacToeGame) {
		setId(ticTacToeGame, nextId.incrementAndGet());
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
		return game.orElseThrow(
				() -> new ObjectDoesNotExistException("Game for player=" + playerId + " does not exist")).getGameId();
	}

	private void setId(final TicTacToeGame game, final Long id) {
		final Class<TicTacToeGame> movieClass = TicTacToeGame.class;
		try {
			final Field movieId = movieClass.getDeclaredField("gameId");
			movieId.setAccessible(true);
			movieId.set(game, id);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
