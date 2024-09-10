package pl.szczesniak.dominik.webtictactoe.users.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserPassword;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.Username;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.commands.CreateUserSample;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.exceptions.InvalidCredentialsException;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.exceptions.UsernameIsTakenException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static pl.szczesniak.dominik.webtictactoe.users.domain.TestUserFacadeConfiguration.userFacade;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserPasswordSample.createAnyUserPassword;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UsernameSample.createAnyUsername;

class UserFacadeTest {

	private UserFacade tut;

	@BeforeEach
	void setUp() {
		tut = userFacade();
	}

	@Test
	void should_not_be_able_to_create_user_with_same_username() {
		// given
		final Username username = createAnyUsername();
		tut.createUser(CreateUserSample.builder().username(username).build());

		// when
		final Throwable thrown = catchThrowable(() -> tut.createUser(CreateUserSample.builder().username(username).build()));

		// then
		assertThat(thrown).isInstanceOf(UsernameIsTakenException.class);
	}

	@Test
	void should_login_user_when_credentials_are_correct() {
		// given
		final Username createdUsername = createAnyUsername();
		final UserPassword createdPassword = createAnyUserPassword();
		final UserId createdUserId = tut.createUser(CreateUserSample.builder().username(createdUsername).userPassword(createdPassword).build());

		// when
		final UserId loggedUserId = tut.login(createdUsername, createdPassword);

		// then
		assertThat(loggedUserId).isEqualTo(createdUserId);
	}

	@Test
	void should_throw_exception_when_given_wrong_username() {
		// given
		final UserPassword password = createAnyUserPassword();
		final Username username = new Username("Dominik");
		final Username wrongUsername = new Username("asd");

		tut.createUser(CreateUserSample.builder().username(username).userPassword(password).build());

		// when
		final Throwable differentUsername = catchThrowable(() -> tut.login(wrongUsername, password));

		// then
		assertThat(differentUsername).isInstanceOf(InvalidCredentialsException.class);
	}

	@Test
	void should_throw_exception_when_given_wrong_password() {
		// given
		final Username username = createAnyUsername();
		final UserPassword password = new UserPassword("password");
		final UserPassword wrongPassword = new UserPassword("asd");

		tut.createUser(CreateUserSample.builder().username(username).userPassword(password).build());

		// when
		final Throwable differentPassword = catchThrowable(() -> tut.login(username, wrongPassword));

		// then
		assertThat(differentPassword).isInstanceOf(InvalidCredentialsException.class);
	}

}