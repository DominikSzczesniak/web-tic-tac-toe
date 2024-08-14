package pl.szczesniak.dominik.webtictactoe.users.domain.model;

import java.util.UUID;

public class UserIdSample {

	public static UserId createAnyUserId() {
		return new UserId(UUID.randomUUID().toString());
	}

}
