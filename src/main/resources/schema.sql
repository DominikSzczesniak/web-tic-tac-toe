CREATE TABLE tic_tac_toe_game
(
    game_id SERIAL PRIMARY KEY,
    player_onevalue VARCHAR(255) NOT NULL,
    player_twovalue VARCHAR(255) NOT NULL,
    player_to_movevalue VARCHAR(255) NOT NULL

);