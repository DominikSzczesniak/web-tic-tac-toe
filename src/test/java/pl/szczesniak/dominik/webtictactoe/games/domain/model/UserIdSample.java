package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.UUID;

public class UserIdSample {

	public static UserId createAnyUserId() {
		return new UserId(UUID.randomUUID().toString());
	}

}
