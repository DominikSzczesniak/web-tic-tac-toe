package pl.szczesniak.dominik.webtictactoe.users.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserPassword;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.Username;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
class User {

	private UserId userId;

	private final String id = UUID.randomUUID().toString();

	private final Username username;

	private final UserPassword userPassword;

	public User(final Username username, final UserPassword userPassword) {
		this.username = requireNonNull(username, "Username must not be null.");
		this.userPassword = requireNonNull(userPassword, "UserPassword must not be null.");
	}

}
