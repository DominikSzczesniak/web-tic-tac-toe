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
interface SpringDataJDBCTicTacToeGamesRepository extends TicTacToeGamesRepository, CrudRepository<TicTacToeGame, Long> {

	@Override
	default void create(TicTacToeGame ticTacToeGame) {
//		commandToSaveGame(ticTacToeGame.getGameId().getValue(), ticTacToeGame.getPlayerOne().getValue(),
//				ticTacToeGame.getPlayerTwo().getValue(), ticTacToeGame.getNextPlayerToMove().getValue());
		save(ticTacToeGame);
	}

	@Override
	default void update(TicTacToeGame ticTacToeGame) {
//		updateGame(ticTacToeGame.getGameId().getValue(), ticTacToeGame.getPlayerOne().getValue(),
//				ticTacToeGame.getPlayerTwo().getValue(), ticTacToeGame.getNextPlayerToMove().getValue());
		save(ticTacToeGame);
	}

	@Override
	default void remove(TicTacToeGameId ticTacToeGameId) {
		commandToRemoveGame(ticTacToeGameId.getValue());
	}

	@Override
	default TicTacToeGame getGame(TicTacToeGameId gameId) {
		return findById(gameId.getValue()).orElseThrow(() -> new ObjectDoesNotExistException("Game does not exist"));
//		return findByGameId(gameId.getValue());
	}

	@Override
	default TicTacToeGameId getGameForPlayer(UserId playerId) {
		return new TicTacToeGameId(queryGetGameForPlayer(playerId.getValue()));
	}

	@Modifying
	@Query("DELETE FROM tic_tac_toe_game WHERE game_id = :id")
	void commandToRemoveGame(@Param("id") Long id);

	@Modifying
	@Query("INSERT INTO tic_tac_toe_game (game_id, player_onevalue, player_twovalue, player_to_movevalue) VALUES (:id, :playerOne, :playerTwo, :nextPlayer)")
	void commandToSaveGame(@Param("id") Long id, @Param("playerOne") String playerOne,
						   @Param("playerTwo") String playerTwo, @Param("nextPlayer") String nextPlayer);

	@Modifying
	@Query("UPDATE tic_tac_toe_game SET player_onevalue = :playerOne, player_twovalue = :playerTwo, player_to_movevalue = :nextPlayer WHERE game_id = :id")
	void updateGame(@Param("id") Long id, @Param("playerOne") String playerOne,
					@Param("playerTwo") String playerTwo, @Param("nextPlayer") String nextPlayer);

	@Query("SELECT game.game_id FROM tic_tac_toe_game game WHERE game.player_onevalue = :playerId OR game.player_twovalue = :playerId")
	Long queryGetGameForPlayer(@Param("playerId") String playerId);

	@Query("SELECT t FROM tic_tac_toe_game t WHERE t.game_id = :gameId")
	TicTacToeGame findByGameId(@Param("gameId") Long gameId);

}
