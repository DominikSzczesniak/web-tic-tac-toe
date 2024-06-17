package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.UUID;

public class PlayerIdSample {

	public static UserId createAnyPlayerId() {
		return new UserId(UUID.randomUUID().toString());
	}

}
