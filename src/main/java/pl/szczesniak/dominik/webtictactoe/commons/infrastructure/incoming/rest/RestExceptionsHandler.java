package pl.szczesniak.dominik.webtictactoe.commons.infrastructure.incoming.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.GameOverException;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;

@ControllerAdvice
public class RestExceptionsHandler {

	@ExceptionHandler(ObjectDoesNotExistException.class)
	public ResponseEntity<?> handleGameDoesNotExistException() {
		return ResponseEntity.status(404).body("Object does not exist");
	}

	@ExceptionHandler(OtherPlayerTurnException.class)
	public ResponseEntity<?> handleOtherPlayerTurnException() {
		return ResponseEntity.status(400).body("Other player's turn.");
	}

	@ExceptionHandler(GameOverException.class)
	public ResponseEntity<?> handleGameOverException() {
		return ResponseEntity.status(404).body("This game is already finished");
	}

}
