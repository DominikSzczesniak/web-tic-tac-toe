package pl.szczesniak.dominik.webtictactoe.users.domain.model;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
public class UserId {

	@NonNull UUID id;

}
