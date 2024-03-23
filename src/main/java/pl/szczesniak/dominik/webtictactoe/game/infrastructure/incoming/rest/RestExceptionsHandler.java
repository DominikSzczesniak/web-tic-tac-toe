package pl.szczesniak.dominik.webtictactoe.game.infrastructure.incoming.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.GameDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions.NotEnoughPlayersException;

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

}
