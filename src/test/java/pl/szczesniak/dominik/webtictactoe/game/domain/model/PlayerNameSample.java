package pl.szczesniak.dominik.webtictactoe.game.domain.model;

import org.apache.commons.lang3.RandomStringUtils;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;

public class PlayerNameSample {

	public static PlayerName createAnyPlayerName() {
		return new PlayerName(RandomStringUtils.randomAlphabetic(5));
	}

}
