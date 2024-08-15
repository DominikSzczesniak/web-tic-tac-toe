package pl.szczesniak.dominik.webtictactoe.users.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserPassword;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.Username;

import java.util.UUID;

import static org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_EMPTY;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@Table("user_table")
class User {

	@Id
	private String userId;

	private final String id;

	@NonNull
	@Embedded(onEmpty = USE_EMPTY, prefix = "username_")
	private final Username username;

	@NonNull
	@Embedded(onEmpty = USE_EMPTY, prefix = "password_")
	private final UserPassword password;

	User(@NonNull final Username username, @NonNull final UserPassword password) {
		this.username = username;
		this.password = password;
		id = UUID.randomUUID().toString();
	}

	@PersistenceCreator
	User(@NonNull final UserPassword password, @NonNull final Username username, final String id, final String userId) {
		this.password = password;
		this.username = username;
		this.id = id;
		this.userId = userId;
	}

	public UserId getUserId() {
		return new UserId(userId);
	}

}
