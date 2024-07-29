package pl.szczesniak.dominik.webtictactoe.sse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SseConfiguration {

	@Bean
	public SseService sseService() {
		return new SpringSseService();
	}

}
