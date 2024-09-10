package pl.szczesniak.dominik.webtictactoe.users.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.postgresql.shaded.com.ongres.scram.common.util.Preconditions.checkArgument;

@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserPassword {

	private String value;

	public UserPassword(final String value) {
		checkArgument(value != null, "Must contain password");
		checkArgument(value.trim().length() > 2, "Password must have at least 3 characters");
		this.value = value;
	}

}