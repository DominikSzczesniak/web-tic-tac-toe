package pl.szczesniak.dominik.webtictactoe.users.infrastructure.adapters.incoming.rest;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.szczesniak.dominik.webtictactoe.users.domain.UserFacade;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.CreateUser;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserPassword;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.Username;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.exceptions.InvalidCredentialsException;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.exceptions.UsernameIsTakenException;

@RequiredArgsConstructor
@RestController
public class UserRestController {

	private final UserFacade userService;

	@PostMapping("/api/users")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> createUser(@RequestBody final CreateUserDTO userDto) {
		try {
			final UserId value = userService.createUser(new CreateUser(new Username(userDto.getUsername()), new UserPassword(userDto.getPassword())));
			return ResponseEntity.status(HttpStatus.CREATED).body(value.getValue());
		} catch (UsernameIsTakenException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is taken.");
		}
	}

	@PostMapping("/api/login")
	public ResponseEntity<String> loginUser(@RequestBody final LoginUserDTO userDto) {
		try {
			final UserId userId = userService.login(new Username(userDto.getUsername()), new UserPassword(userDto.getPassword()));
			return ResponseEntity.status(HttpStatus.OK).body(userId.getValue());
		} catch (InvalidCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
	}

	@GetMapping("/api/users/{username}")
	public ResponseEntity<String> isUsernameTaken(@PathVariable final String username) {
		boolean check = userService.isUsernameTaken(new Username(username));
		return ResponseEntity.status(HttpStatus.OK).body("username is taken: " + check);
	}

	@ExceptionHandler(UsernameIsTakenException.class)
	public ResponseEntity<?> handleUsernameIsTakenException() {
		return ResponseEntity.badRequest().build();
	}

	@Data
	public static class CreateUserDTO {
		private String username;
		private String password;
	}

	@Value
	public static class LoginUserDTO {
		String username;
		String password;
	}

}
