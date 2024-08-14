package pl.szczesniak.dominik.webtictactoe.users.domain.model.commands;

import lombok.Builder;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.CreateUser;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserPassword;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.Username;

import static java.util.Optional.ofNullable;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserPasswordSample.createAnyUserPassword;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UsernameSample.createAnyUsername;

public class CreateUserSample {

	@Builder
	private static CreateUser build(final Username username, final UserPassword userPassword) {
		return new CreateUser(
				ofNullable(username).orElse(createAnyUsername()),
				ofNullable(userPassword).orElse(createAnyUserPassword())
		);
	}

}
