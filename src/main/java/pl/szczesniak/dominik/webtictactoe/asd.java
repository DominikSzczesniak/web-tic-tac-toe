package pl.szczesniak.dominik.webtictactoe;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;

public class asd {

	public static void main(String[] args) {
		WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());

		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		String url = "ws://localhost:8080/websocket-server";

		StompSessionHandler sessionHandler = new MyStompSessionHandler();

		stompClient.connect(url, sessionHandler);
	}

	private static class MyStompSessionHandler implements StompSessionHandler {
		@Override
		public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
			System.out.println("Connected to the WebSocket server");
		}

		@Override
		public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
			System.err.println("Error while connecting to the WebSocket server: " + throwable.getMessage());
		}

		@Override
		public void handleTransportError(StompSession stompSession, Throwable throwable) {
			System.err.println("Transport error: " + throwable.getMessage());
		}

		@Override
		public Type getPayloadType(final StompHeaders headers) {
			return String.class;
		}

		@Override
		public void handleFrame(StompHeaders stompHeaders, Object o) {
			System.out.println("Received message: " + o.toString());
		}
	}

}
