package pl.szczesniak.dominik.webtictactoe.users.domain.model.exceptions;

public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException(final String message) {
		super(message);
	}

}
