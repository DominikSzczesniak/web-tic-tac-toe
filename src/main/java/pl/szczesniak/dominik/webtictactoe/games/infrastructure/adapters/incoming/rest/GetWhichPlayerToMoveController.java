package pl.szczesniak.dominik.webtictactoe.games.infrastructure.adapters.incoming.rest;

//
//@RequiredArgsConstructor
//@RestController
//public class GetWhichPlayerToMoveController {
//
//	private final GamesFacade gamesFacade;
//
//	@GetMapping("/api/games/{gameId}/move")
//	public ResponseEntity<String> getWhichPlayerToMove(@PathVariable final Long gameId) {
//		final UserId playerToMove = gamesFacade.getPlayerToMove(new TicTacToeGameId(gameId));
//		return ResponseEntity.status(200).body(playerToMove.getValue().toString());
//	}
//
//}
