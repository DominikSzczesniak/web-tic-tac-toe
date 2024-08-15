package pl.szczesniak.dominik.webtictactoe.users.domain;

import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.Username;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class InMemoryUserRepository implements UserRepository {

	private final Map<UserId, User> users = new HashMap<>();


	@Override
	public Optional<User> findBy(final Username username) {
		return users.values().stream().filter(user -> user.getUsername().equals(username)).findFirst();
	}

	@Override
	public void create(final User user) {
		setId(user, UUID.randomUUID().toString());
		users.put(user.getUserId(), user);
	}


	private void setId(final User user, final String id) {
		final Class<User> userClass = User.class;
		try {
			final Field userIdField = userClass.getDeclaredField("userId");
			userIdField.setAccessible(true);

			userIdField.set(user, id);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
