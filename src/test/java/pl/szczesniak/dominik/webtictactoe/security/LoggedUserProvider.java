package pl.szczesniak.dominik.webtictactoe.security;

import lombok.NonNull;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.szczesniak.dominik.webtictactoe.users.infrastructure.adapters.incoming.rest.CreateUserRestInvoker;
import pl.szczesniak.dominik.webtictactoe.users.infrastructure.adapters.incoming.rest.CreateUserRestInvoker.CreateUserDto;
import pl.szczesniak.dominik.webtictactoe.users.infrastructure.adapters.incoming.rest.LoginUserRestInvoker;
import pl.szczesniak.dominik.webtictactoe.users.infrastructure.adapters.incoming.rest.LoginUserRestInvoker.LoginUserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserPasswordSample.createAnyUserPassword;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UsernameSample.createAnyUsername;

@Component
public class LoggedUserProvider {

	@Autowired
	private CreateUserRestInvoker createUserRest;
	@Autowired
	private LoginUserRestInvoker loginUserRest;
	@Autowired
	private JWTGenerator tokenGenerator;

	public LoggedUser getLoggedUser() {
		final CreateUserDto build = CreateUserDto.builder()
				.username(createAnyUsername().getValue()).password(createAnyUserPassword().getValue()).build();
		final ResponseEntity<String> createUserResponse = createUserRest.createUser(build);
		assertThat(createUserResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


		final ResponseEntity<String> loggedUserResponse = loginUserRest.loginUser(
				LoginUserDto.builder().username(build.getUsername()).password(build.getPassword()).build());
		assertThat(loggedUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		final String token = loggedUserResponse.getBody();
		return new LoggedUser(token, tokenGenerator.getUserIdFromJWT(token).getValue());
	}

	@Value
	public static class LoggedUser {
		@NonNull String token;
		@NonNull String userId;
	}

}
