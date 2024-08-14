package pl.szczesniak.dominik.webtictactoe.users.infrastructure.adapters.incoming.rest;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginUserRestInvoker {

	private static final String URL = "/api/login";

	private final TestRestTemplate restTemplate;

	public ResponseEntity<String> loginUser(final LoginUserDto loginUserDto) {
		return restTemplate.exchange(
				URL,
				HttpMethod.POST,
				new HttpEntity<>(loginUserDto),
				String.class
		);
	}

	@Data
	@Builder
	public static class LoginUserDto {
		private final String username;
		private final String password;
	}

}
