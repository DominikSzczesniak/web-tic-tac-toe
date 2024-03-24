package pl.szczesniak.dominik.webtictactoe.games.domain.model.exceptions;

public class NotEnoughPlayersException extends RuntimeException {

	public NotEnoughPlayersException(final String message) {
		super(message);
	}

}
