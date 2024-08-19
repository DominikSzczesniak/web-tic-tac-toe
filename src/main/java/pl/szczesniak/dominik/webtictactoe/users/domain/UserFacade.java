package pl.szczesniak.dominik.webtictactoe.users.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.CreateUser;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserPassword;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.Username;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.exceptions.InvalidCredentialsException;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.exceptions.UsernameIsTakenException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserFacade {

	private final UserRepository repository;

	public UserId createUser(final CreateUser command) {
		final User user = createFrom(command);
		isUsernameTaken(command.getUsername());
		repository.create(user);
		return user.getUserId();
	}

	private User createFrom(final CreateUser command) {
		return new User(command.getUsername(), new UserPassword(command.getUserPassword().getValue()));
	}

	public UserId login(final Username username, final UserPassword userPassword) {
		return repository.findBy(username)
				.filter(user -> user.getPassword().equals(userPassword))
				.orElseThrow(() -> new InvalidCredentialsException("Invalid credentials, could not log in.")).getUserId();
	}

	public boolean isUsernameTaken(final Username username) {
		if (repository.findBy(username).isPresent()) {
			throw new UsernameIsTakenException("Username: [" + username + "] is taken.");
		}
		return false;
	}

	public User getUserBy(final Username username) {
		return repository.findBy(username).orElseThrow(() -> new ObjectDoesNotExistException("User: [" + username + "] not found."));
	}

}
