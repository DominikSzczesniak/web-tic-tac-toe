package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Getter
@ToString
@EqualsAndHashCode
public class GameInfo {

	@NonNull
	private final MyGameStatus gameStatus;
	private final UserId whoWon;

	public GameInfo(@NonNull final MyGameStatus gameStatus, final UserId whoWon) {
		this.gameStatus = gameStatus;
		this.whoWon = whoWon;
	}

	public Optional<UserId> getWhoWon() {
		return ofNullable(whoWon);
	}

}
