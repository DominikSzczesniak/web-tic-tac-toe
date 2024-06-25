package pl.szczesniak.dominik.webtictactoe.commons.infrastructure.incoming.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.GameOverException;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;

@ControllerAdvice
@Slf4j
public class RestExceptionsHandler {

	@ExceptionHandler(ObjectDoesNotExistException.class)
	public ResponseEntity<?> handleGameDoesNotExistException(final ObjectDoesNotExistException exception) {
		log.error("Object does not exist", exception);
		return ResponseEntity.status(404).body("Object does not exist");
	}

	@ExceptionHandler(OtherPlayerTurnException.class)
	public ResponseEntity<?> handleOtherPlayerTurnException(final OtherPlayerTurnException exception) {
		log.error("Other player's turn.", exception);
		return ResponseEntity.status(400).body("Other player's turn.");
	}

	@ExceptionHandler(GameOverException.class)
	public ResponseEntity<?> handleGameOverException(final GameOverException exception) {
		log.error("This game is already finished", exception);
		return ResponseEntity.status(404).body("This game is already finished");
	}

}
