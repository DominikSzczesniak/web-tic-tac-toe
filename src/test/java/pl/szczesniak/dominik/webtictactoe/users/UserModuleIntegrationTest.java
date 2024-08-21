package pl.szczesniak.dominik.webtictactoe.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.szczesniak.dominik.webtictactoe.security.JWTGenerator;
import pl.szczesniak.dominik.webtictactoe.users.infrastructure.adapters.incoming.rest.CreateUserRestInvoker;
import pl.szczesniak.dominik.webtictactoe.users.infrastructure.adapters.incoming.rest.CreateUserRestInvoker.CreateUserDto;
import pl.szczesniak.dominik.webtictactoe.users.infrastructure.adapters.incoming.rest.LoginUserRestInvoker;
import pl.szczesniak.dominik.webtictactoe.users.infrastructure.adapters.incoming.rest.LoginUserRestInvoker.LoginUserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserPasswordSample.createAnyUserPassword;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UsernameSample.createAnyUsername;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserModuleIntegrationTest {

	@Autowired
	private CreateUserRestInvoker createUserRest;

	@Autowired
	private LoginUserRestInvoker loginUserRest;

	@Autowired
	private JWTGenerator tokenGenerator;

	@Test
	void should_create_user_and_login_on_him() {
		// given
		final CreateUserDto userToCreate = createAnyUser();

		// when
		final ResponseEntity<String> createUserResponse = createUserRest.createUser(userToCreate);

		// then
		assertThat(createUserResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// when
		final ResponseEntity<String> loginUserResponse = loginUserRest.loginUser(
				LoginUserDto.builder().username(userToCreate.getUsername()).password(userToCreate.getPassword()).build());

		// then
		assertThat(loginUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(loginUserResponse.getBody()).isNotNull();
		assertThat(tokenGenerator.getUserIdFromJWT(loginUserResponse.getBody()).getValue()).isEqualTo(createUserResponse.getBody()); // todo: zapytac czy moge tak uzyc generatora, chcialem zwracac JSONa z tokentype: bearer itp, ale mialem problemy z parsowaniem wtedy
	}

	@Test
	void should_create_user_and_not_login() {
		// given
		final CreateUserDto userToCreate = createAnyUser();

		// when
		final ResponseEntity<String> createUserResponse = createUserRest.createUser(userToCreate);

		// then
		assertThat(createUserResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// when
		final ResponseEntity<String> loginUserResponse = loginUserRest.loginUser(
				LoginUserDto.builder().username(userToCreate.getUsername()).password(createAnyUserPassword().getValue()).build());

		// then
		assertThat(loginUserResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void should_not_create_user_with_same_username() {
		// given
		final CreateUserDto userToCreate = createAnyUser();

		// when
		final ResponseEntity<String> createUserResponse = createUserRest.createUser(userToCreate);

		// then
		assertThat(createUserResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// when
		final ResponseEntity<String> createUserFailedResponse = createUserRest.createUser(
				CreateUserDto.builder().username(userToCreate.getUsername()).password(createAnyUserPassword().getValue()).build());

		// then
		assertThat(createUserFailedResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	private static CreateUserDto createAnyUser() {
		return CreateUserDto.builder().username(createAnyUsername().getValue()).password(createAnyUserPassword().getValue()).build();
	}

}
