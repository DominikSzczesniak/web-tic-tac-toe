package pl.szczesniak.dominik.webtictactoe.games.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GameFacadeConfiguration {

	@Bean
	public GamesFacade gamesFacade() {
		return new GamesFacade(new GamesService());
	}

}
