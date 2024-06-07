package pl.szczesniak.dominik.webtictactoe.matchmaking.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MatchmakingFacade {

	private final MatchmakingService matchmakingService;

	public UserId queueToPlay(final UserId userId) {
		return matchmakingService.addPlayerToQueue(userId);
	}

}
