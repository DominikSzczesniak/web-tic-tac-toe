package pl.szczesniak.dominik.webtictactoe.games.domain;

import org.springframework.stereotype.Service;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Symbol;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.exceptions.NotEnoughPlayersException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchmakingService {

	private final ConcurrentHashMap<UUID, Player> playersInQueue = new ConcurrentHashMap<>();

	UUID addPlayerToQueue(final PlayerName playerName) {
		final Player player;

		if (playersInQueue.size() == 0 || playersInQueue.size() % 2 == 0) {
			player = new Player(new Symbol('O'), playerName);
		} else {
			player = new Player(new Symbol('X'), playerName);
		}

		playersInQueue.put(player.getPlayerID(), player);
		return player.getPlayerID();
	}

	void checkEnoughPlayersForAGameInQueue() {
		if (playersInQueue.size() < 2) {
			throw new NotEnoughPlayersException("Not enough players in queue to start a game.");
		}
	}

	Player getPlayerWithSymbolO() {
		return playersInQueue.values().stream()
				.filter(player -> player.getSymbol().equals(new Symbol('O')))
				.findFirst()
				.orElseThrow(() -> new NotEnoughPlayersException("Not enough players in queue to start a game."));
	}

	Player getPlayerWithSymbolX() {
		return playersInQueue.values().stream()
				.filter(player -> player.getSymbol().equals(new Symbol('X')))
				.findFirst()
				.orElseThrow(() -> new NotEnoughPlayersException("Not enough players in queue to start a game."));
	}

	void removePlayersFromQueue(final UUID playerOne, final UUID playerTwo) {
		playersInQueue.remove(playerOne);
		playersInQueue.remove(playerTwo);
	}

	List<Player> getQueuedPlayers() {
		return playersInQueue.values().stream().toList();
	}

}
