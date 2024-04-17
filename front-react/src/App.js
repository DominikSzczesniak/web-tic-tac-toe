import {useEffect, useState} from 'react';

function QueueForGame({username, setUsername, setPlayerId}) {

    async function queueForGame() {
        try {
            const response = await fetch(`http://localhost:8080/api/games/queue?username=${encodeURIComponent(username)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error('Bad request');
            }

            const data = await response.text();
            setPlayerId(data);
            console.log('Player queued successfully with playerId:', data);
        } catch (error) {
            console.error(error);
        }
    }

    function handleUsernameChange(event) {
        setUsername(event.target.value);
    }

    const handleQueueClick = () => {
        queueForGame();
    };


    return (
        <div className="queue-for-game-container">
            <div>
                <label>
                    <input type="text" placeholder="username" value={username} onChange={handleUsernameChange}/>
                </label>
                <button onClick={handleQueueClick}>Queue for Game</button>
            </div>
        </div>
    );
}

function PrepareGame({handlePrepareGame}) {

    async function prepareGame() {
        try {
            const response = await fetch('http://localhost:8080/api/games', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error('Error preparing game');
            }

            const data = await response.text();
            console.log('Game prepared with ID:', data);
            handlePrepareGame(data);
        } catch (error) {
            console.error(error);
        }
    }

    return (
        <div className="prepare-game-container">
            <div>
                <button onClick={prepareGame}>Prepare Game</button>
            </div>
        </div>
    );
}

function GetGameForPlayer({playerId, onGameIdChange}) {
    useEffect(() => {
        if (playerId) {
            const interval = setInterval(() => {
                fetch(`http://localhost:8080/api/games?playerId=${playerId}`)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        onGameIdChange(data);
                        console.log("game fetched with id ", data)
                        clearInterval(interval);
                    })
                    .catch(error => {
                        console.error('Error fetching game:', error);
                    });
            }, 5000);

            return () => clearInterval(interval);
        }
    }, [playerId, onGameIdChange]);

    return null;
}


function Board({boardView, gameId, playerId, setPlayerToMove}) {

    async function makeMove(rowIndex, columnIndex) {
        try {
            const response = await fetch(`http://localhost:8080/api/games/${gameId}/move`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    playerId: playerId,
                    rowIndex: rowIndex,
                    columnIndex: columnIndex,
                }),
            });
            if (!response.ok) {
                throw new Error('Error making move');
            }
            setPlayerToMove(false)
        } catch (error) {
            console.error('Error making move:', error);
        }
    }

    return (
        <div>
            {boardView && (
                <div>
                    <h2>Board View</h2>
                    <table className="board">
                        <tbody>
                        {boardView.map((row, rowIndex) => (
                            <tr key={rowIndex}>
                                {row.map((cell, columnIndex) => (
                                    <td key={columnIndex} onClick={() => makeMove(rowIndex, columnIndex)}>
                                        {cell === null ? '' : cell}
                                    </td>
                                ))}
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

function App() {
    const [username, setUsername] = useState('');
    const [playerId, setPlayerId] = useState('');
    const [gameId, setGameId] = useState('');
    const [boardView, setBoardView] = useState([]);
    const [isPlayerTurn, setIsPlayerTurn] = useState(null);
    const [winner, setWinner] = useState(null);

    function isWinningMove(board) {
        const winningLines = [
            [board[0][0], board[0][1], board[0][2]],
            [board[1][0], board[1][1], board[1][2]],
            [board[2][0], board[2][1], board[2][2]],

            [board[0][0], board[1][0], board[2][0]],
            [board[0][1], board[1][1], board[2][1]],
            [board[0][2], board[1][2], board[2][2]],

            [board[0][0], board[1][1], board[2][2]],
            [board[0][2], board[1][1], board[2][0]]
        ];

        for (const winningLine of winningLines) {
            if (winningLine.every(symbol => symbol !== null && symbol === winningLine[0])) {
                return true;
            }
        }

        return false;
    }

    function isDraw(board) {
        for (const row of board) {
            for (const cell of row) {
                if (cell === null) {
                    return false;
                }
            }
        }
        return true;
    }

    function checkGameStatus(board, intervalId) {
        if (isWinningMove(board)) {
            setWinner(isPlayerTurn ? 'Opponent' : 'You');
            clearInterval(intervalId)
        } else if (isDraw(board)) {
            setWinner('Draw');
            clearInterval(intervalId)
        }
    }


    useEffect(() => {
        if (gameId) {
            fetchPlayerToMove(gameId);
        }
    }, [gameId, boardView]);

    async function fetchPlayerToMove(gameId) {
        try {
            const response = await fetch(`http://localhost:8080/api/games/${gameId}/move`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.text();
            if (data === playerId) {
                setIsPlayerTurn(true);
            } else {
                setIsPlayerTurn(false);
            }
        } catch (error) {
            console.error('Error fetching player to move:', error);
        }
    }

    useEffect(() => {
        let intervalId;
        if (gameId) {
            const fetchBoardView = async () => {
                try {
                    const response = await fetch(`http://localhost:8080/api/games/${gameId}`);
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    const data = await response.json();
                    setBoardView(data);
                    console.log("updated board ", data);
                    checkGameStatus(data, intervalId)
                } catch (error) {
                    console.error('Error fetching board view:', error);
                }
            };

            fetchBoardView();
            intervalId = setInterval(fetchBoardView, 2000);
        }
        return () => clearInterval(intervalId);
    }, [gameId, isPlayerTurn]);

    function handlePrepareGame(data) {
        setGameId(data);
    }

    return (
        <div>
            {gameId ? (
                <div>
                    <Board
                        boardView={boardView}
                        gameId={gameId}
                        playerId={playerId}
                        setPlayerToMove={setIsPlayerTurn}
                    />
                    {(winner === null && !isDraw(boardView)) && (
                        <h2>{isPlayerTurn ? 'Your turn' : 'Opponent\'s turn'}</h2>
                    )}
                    {winner && <h2>{winner === 'Draw' ? 'It\'s a draw!' : `${winner} won!`}</h2>}
                </div>
            ) : (
                <div>
                    <QueueForGame
                        username={username}
                        setUsername={setUsername}
                        playerId={playerId}
                        setPlayerId={setPlayerId}
                    />
                    <PrepareGame
                        gameId={gameId}
                        handlePrepareGame={handlePrepareGame}
                    />
                    <GetGameForPlayer
                        playerId={playerId}
                        onGameIdChange={setGameId}
                    />
                </div>
            )}
        </div>
    );
}

export default App;
