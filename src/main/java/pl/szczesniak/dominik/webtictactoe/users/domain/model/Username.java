package pl.szczesniak.dominik.webtictactoe.users.domain.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.postgresql.shaded.com.ongres.scram.common.util.Preconditions.checkArgument;

@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Username {

	private String value;

	public Username(final String value) {
		checkArgument(value != null, "Must contatin username");
		checkArgument(usernameContainsOnlyLetters(value), "Username can contain only letters");
		checkArgument(value.trim().length() > 1 && value.trim().length() < 26, "Username must be 1-25 letters long");
		this.value = value;
	}

	private boolean usernameContainsOnlyLetters(String name) {
		return name.matches("(?i)[a-z]([- ',.a-z]{0,23}[a-z])?");
	}

}
