package pl.szczesniak.dominik.webtictactoe.games.domain;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

public interface TicTacToeGamesRepository {

	void create(TicTacToeGame ticTacToeGame);

	void update(TicTacToeGame ticTacToeGame);

	void remove(TicTacToeGameId ticTacToeGameId);

	TicTacToeGame getGame(TicTacToeGameId gameId);

	TicTacToeGameId getGameForPlayer(UserId playerId);
}

@Repository
interface SpringDataJDBCTicTacToeGamesRepository extends TicTacToeGamesRepository, CrudRepository<TicTacToeGame, TicTacToeGameId> {

	@Override
	default void create(TicTacToeGame ticTacToeGame) {
		save(ticTacToeGame);
	}

	@Override
	default void update(TicTacToeGame ticTacToeGame) {
		save(ticTacToeGame);
	}

	@Override
	default void remove(TicTacToeGameId ticTacToeGameId) {
		commandToRemoveGame(ticTacToeGameId.getValue());
	}

	@Override
	default TicTacToeGame getGame(TicTacToeGameId gameId) {
		return findById(gameId).orElseThrow(() -> new ObjectDoesNotExistException("Game does not exist"));
	}

	@Override
	default TicTacToeGameId getGameForPlayer(UserId playerId) {
		return null;
	}


	@Modifying
	@Query("DELETE FROM tic_tac_toe_game WHERE game_id = :id")
	void commandToRemoveGame(@Param("id") Long id);

}
