package pl.szczesniak.dominik.webtictactoe.games;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pl.szczesniak.dominik.webtictactoe.sse.SpringSseService;
import pl.szczesniak.dominik.webtictactoe.sse.SseService;

@Configuration
public class TestSseConfiguration {

	@Primary
	@Bean("service.stub")
	public SseService sseService() {
		return Mockito.spy(new SpringSseService());
	}

}
