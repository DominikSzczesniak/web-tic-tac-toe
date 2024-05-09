package pl.szczesniak.dominik.webtictactoe.matchmaking.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MatchmakingFacade {

	private final MatchmakingService matchmakingService;

	public UUID queueToPlay(final PlayerName playerName) {
		return matchmakingService.addPlayerToQueue(playerName).getId();
	}

}
