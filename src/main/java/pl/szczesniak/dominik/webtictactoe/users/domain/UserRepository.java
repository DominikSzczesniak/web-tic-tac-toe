package pl.szczesniak.dominik.webtictactoe.users.domain;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.Username;

import java.util.Optional;

public interface UserRepository {

	Optional<User> findBy(Username username);

	void create(User user);
}

@Repository
interface SpringDataJDBCUserRepository extends UserRepository, CrudRepository<User, String> {

	@Override
	default Optional<User> findBy(Username username) {
		return Optional.ofNullable(findByUsername(username.getValue()));
	}

	@Override
	default void create(User user) {
		save(user);
	}

	@Query("SELECT * FROM user_table u WHERE u.username_value = :username")
	User findByUsername(@Param("username") String username);

//	@Modifying
//	@Query("INSERT INTO user_table (user_id, username_value, password_value) VALUES (:#{#user.userId}, :#{#user.username.value}, :#{#user.password.value})")
//	void update(User user);

}