package pl.szczesniak.dominik.webtictactoe.users.domain;

import pl.szczesniak.dominik.webtictactoe.users.domain.model.Username;

import java.util.Optional;

public interface UserRepository {

	Optional<User> findBy(Username username);

	void create(User user);
}
