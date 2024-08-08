package pl.szczesniak.dominik.webtictactoe.games.domain;

import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyPlayerMove;

import java.util.List;

public interface TicTacToeGameRules {

	GameState makeMove(List<MyPlayerMove> executedMoves, MyPlayerMove newMove);
	GameState getGameState(List<MyPlayerMove> executedMoves);

}
