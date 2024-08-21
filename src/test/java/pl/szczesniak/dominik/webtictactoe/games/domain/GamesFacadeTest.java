package pl.szczesniak.dominik.webtictactoe.games.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.webtictactoe.commons.domain.InMemoryEventPublisher;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.DomainEvent;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameInfo;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyGameStatus;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.CreateGameSample;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.MakeMoveSample;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static pl.szczesniak.dominik.webtictactoe.games.domain.TestGamesFacadeConfiguration.gamesFacade;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerMoveSample.createAnyPlayerMove;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserIdSample.createAnyUserId;

class GamesFacadeTest {

	private GamesFacade tut;
	private InMemoryEventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		eventPublisher = new InMemoryEventPublisher();
		tut = gamesFacade(eventPublisher);
	}

	@Test
	void both_queued_players_should_be_in_same_game() {
		// given
		final UserId playerOne = createAnyUserId();
		final UserId playerTwo = createAnyUserId();

		// when
		final TicTacToeGameId ticTacToeGame = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// then
		final TicTacToeGameId gameForPlayerOne = tut.getGameForPlayer(playerOne);
		final TicTacToeGameId gameForPlayerTwo = tut.getGameForPlayer(playerTwo);
		assertThat(gameForPlayerOne).isEqualTo(gameForPlayerTwo);
		assertThat(gameForPlayerOne).isEqualTo(ticTacToeGame);
		assertThat(gameForPlayerTwo).isEqualTo(ticTacToeGame);
	}

	@Test
	void first_player_should_be_first_to_move() {
		// given
		final UserId playerOne = createAnyUserId();
		final UserId playerTwo = createAnyUserId();

		// when
		final TicTacToeGameId ticTacToeGameId = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// then
		final GameInfo gameInfo = tut.getGameInfo(ticTacToeGameId);
		assertThat(gameInfo.getPlayerToMove()).isEqualTo(playerOne);
	}

	@Test
	void should_not_be_able_to_close_the_game_if_not_part_of_the_game() {
		// given
		final UserId playerOne = createAnyUserId();
		final UserId playerTwo = createAnyUserId();
		final UserId playerThree = createAnyUserId();

		final TicTacToeGameId ticTacToeGameId = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// when
		final Throwable thrown = catchThrowable(() -> tut.closeGame(ticTacToeGameId, playerThree));

		// then
		assertThat(thrown).isInstanceOf(ObjectDoesNotExistException.class);
	}

	@Test
	void should_throw_exception_when_player_tries_to_move_in_already_closed_game() {
		// given
		final UserId playerOne = createAnyUserId();
		final UserId playerTwo = createAnyUserId();

		final TicTacToeGameId ticTacToeGame = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());
		tut.closeGame(ticTacToeGame, playerOne);

		// when
		final Throwable thrown = catchThrowable(() -> tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(ticTacToeGame).playerMove(createAnyPlayerMove(playerOne)).build()));

		// then
		assertThat(thrown).isInstanceOf(ObjectDoesNotExistException.class);
	}

	@Test
	void should_get_which_player_to_move() {
		// given
		final UserId playerOne = createAnyUserId();
		final UserId playerTwo = createAnyUserId();

		final TicTacToeGameId ticTacToeGame = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// when
		final GameInfo gameInfo = tut.getGameInfo(ticTacToeGame);

		// then
		assertThat(gameInfo.getPlayerToMove()).isEqualTo(playerOne);

		// when
		tut.makeMove(MakeMoveSample.builder().ticTacToeGameId(ticTacToeGame).playerMove(createAnyPlayerMove(playerOne)).build());

		// then
		assertThat(tut.getGameInfo(ticTacToeGame).getPlayerToMove()).isEqualTo(playerTwo);
	}

	@Test
	void should_throw_exception_when_wrong_player_moves() {
		// given
		final UserId playerOne = createAnyUserId();
		final UserId playerTwo = createAnyUserId();

		final TicTacToeGameId ticTacToeGame = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());
		final GameInfo gameInfo = tut.getGameInfo(ticTacToeGame);
		assertThat(gameInfo.getPlayerToMove()).isEqualTo(playerOne);

		// when
		final Throwable thrown = catchThrowable(() -> tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(ticTacToeGame).playerMove(createAnyPlayerMove(playerTwo)).build()));

		// then
		assertThat(thrown).isInstanceOf(OtherPlayerTurnException.class);
	}

	@Test
	void should_throw_exception_when_game_not_ready_for_player() {
		// given
		final UserId player = createAnyUserId();

		// when
		final Throwable thrown = catchThrowable(() -> tut.getGameForPlayer(player));

		// then
		assertThat(thrown).isInstanceOf(ObjectDoesNotExistException.class);
	}

	@Test
	void players_should_play_full_game() {
		// given
		final UserId playerOne = createAnyUserId();
		final UserId playerTwo = createAnyUserId();

		final TicTacToeGameId gameId = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// when
		final GameState gameState_1 = tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(gameId).playerMove(new GameMove(0, 0, playerOne)).build());

		// then
		assertThat(gameState_1.getGameStatus()).isEqualTo(MyGameStatus.IN_PROGRESS);

		// when
		final GameState gameState_2 = tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(gameId).playerMove(new GameMove(0, 1, playerTwo)).build());

		// then
		assertThat(gameState_2.getGameStatus()).isEqualTo(MyGameStatus.IN_PROGRESS);

		// when
		final GameState gameState_3 = tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(gameId).playerMove(new GameMove(1, 0, playerOne)).build());

		// then
		assertThat(gameState_3.getGameStatus()).isEqualTo(MyGameStatus.IN_PROGRESS);

		// when
		final GameState gameState_4 = tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(gameId).playerMove(new GameMove(0, 2, playerTwo)).build());

		// then
		assertThat(gameState_4.getGameStatus()).isEqualTo(MyGameStatus.IN_PROGRESS);

		// when
		final GameState gameStateFinish = tut.makeMove(MakeMoveSample.builder()
				.ticTacToeGameId(gameId).playerMove(new GameMove(2, 0, playerOne)).build());

		// then
		assertThat(gameStateFinish.getGameStatus()).isEqualTo(MyGameStatus.WIN);
		assertThat(gameStateFinish.getWhoWon().get()).isEqualTo(playerOne);
	}

	@Test
	void should_publish_event_when_player_makes_move() {
		// given
		final UserId playerOne = createAnyUserId();
		final UserId playerTwo = createAnyUserId();

		final TicTacToeGameId ticTacToeGame = tut.createGame(CreateGameSample.builder().playerOne(playerOne).playerTwo(playerTwo).build());

		// when
		tut.makeMove(MakeMoveSample.builder().ticTacToeGameId(ticTacToeGame).playerMove(createAnyPlayerMove(playerOne)).build());

		// then
		final DomainEvent publishedEvent = eventPublisher.getPublishedEvents().get(0);
		assertThat(publishedEvent.getClass()).isEqualTo(MoveMade.class);
	}

}