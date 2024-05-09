package pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions;

public class ObjectDoesNotExistException extends RuntimeException {

	public ObjectDoesNotExistException(final String message) {
		super(message);
	}
}
