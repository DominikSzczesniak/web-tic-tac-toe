package pl.szczesniak.dominik.webtictactoe.games.infrastructure.websocket;

import lombok.Value;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

	@MessageExceptionHandler
	@SendTo("/topic/errors")
	String handleException(final Exception e) {
		return e.getMessage();
	}

	@MessageMapping("/game")
	@SendTo("/topic/move")
	MadeMoveResponse makeMove() {
		System.out.println("move made informed");
		return new MadeMoveResponse("move was made");
	}

	@Value
	private static class MadeMoveResponse {
		String move;
	}

}
