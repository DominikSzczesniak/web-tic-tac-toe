package pl.szczesniak.dominik.webtictactoe.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.szczesniak.dominik.webtictactoe.sse.SpringSseService.MoveMadeDTO;

public interface SseService {

	SseEmitter subscribeToGame(final Long gameId);

	void handleMoveMadeEvent(final MoveMadeDTO event);

}
