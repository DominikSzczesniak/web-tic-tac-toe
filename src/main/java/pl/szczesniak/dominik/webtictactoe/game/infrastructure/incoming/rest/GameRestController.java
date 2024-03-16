package pl.szczesniak.dominik.webtictactoe.game.infrastructure.incoming.rest;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class GameRestController {


	@PostMapping("/api/games/queue")
	public ResponseEntity<?> queueForGame(@RequestParam final String username) {
		return ResponseEntity.status(201).build();
	}

	@GetMapping("/api/games")
	public ResponseEntity<GameInfoDto> checkIsGameReady(@RequestBody final PlayersForTheGameDto playersForTheGameDto) {
		return ResponseEntity.status(200).body(null);
	}

	@GetMapping("/api/games/{gameId}")
	public ResponseEntity<Character[][]> getBoardView(@PathVariable final Long gameId) {
		return ResponseEntity.status(200).build();
	}

	@PostMapping("/api/games/{gameId}/move")
	public ResponseEntity<GameResultDto> makeMove(@PathVariable final Long gameId, @RequestBody final MakeMoveDto makeMoveDto) {
		return ResponseEntity.status(201).build();
	}

	@DeleteMapping("/api/games/{gameId}")
	public ResponseEntity<?> deleteGame(@PathVariable final Long gameId) {
		return ResponseEntity.status(204).build();
	}

	@Data
	private static class MakeMoveDto {
		private final String playerName;
		private final Long rowIndex;
		private final Long columnIndex;
	}

	@Value
	private static class GameInfoDto {
		Boolean gameIsReady;
		Long gameId;
	}

	@Value
	private static class PlayersForTheGameDto {
		String playerOne;
		String playerTwo;
	}

	@Value
	private static class GameResultDto {
		String gameStatus;
		String playerThatWon;
	}

}
