package pl.szczesniak.dominik.webtictactoe.users.domain.model;

import lombok.NonNull;
import lombok.Value;

@Value
public class CreateUser {

	@NonNull Username username;

	@NonNull UserPassword userPassword;

}
