package pl.szczesniak.dominik.webtictactoe.games.infrastructure.incoming.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.GameOverException;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.exceptions.GameDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.exceptions.NotEnoughPlayersException;

@ControllerAdvice
public class RestExceptionsHandler {

	@ExceptionHandler(GameDoesNotExistException.class)
	public ResponseEntity<?> handleGameDoesNotExistException() {
		return ResponseEntity.status(404).body("Game does not exist");
	}

	@ExceptionHandler(NotEnoughPlayersException.class)
	public ResponseEntity<?> handleNotEnoughPlayersException() {
		return ResponseEntity.status(404).body("Not enough players in queue.");
	}

	@ExceptionHandler(OtherPlayerTurnException.class)
	public ResponseEntity<?> handleOtherPlayerTurnException() {
		return ResponseEntity.status(404).body("Other player's turn.");
	}

	@ExceptionHandler(GameOverException.class)
	public ResponseEntity<?> handleGameOverException() {
		return ResponseEntity.status(404).body("This game is already finished");
	}

}
