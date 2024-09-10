import {useEffect, useState} from 'react';
import {jwtDecode} from "jwt-decode";

function QueueForGame({token, gameId, onLogout}) {
    const [inQueue, setInQueue] = useState(false);

    async function queueForGame() {
        setInQueue(true);
        try {
            const response = await fetch(`http://localhost:8080/api/queue`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token,
                }
            });

            if (!response.ok) {
                throw new Error('Bad request');
            }

            const data = await response.text();
            console.log('Player queued successfully with playerId:', data);
        } catch (error) {
            console.error(error);
        }
    }

    const handleQueueClick = () => {
        queueForGame();
    };

    const showLoading = !gameId && inQueue;

    return (
        <div className="queue-for-game-container">
            {showLoading ? (
                <div className="queue-message">In Queue...</div>
            ) : (
                <div>
                    <button className="queue-for-game-button" onClick={handleQueueClick}>Queue for Game</button>
                    <button className="logout-button" onClick={onLogout}>Logout</button>
                </div>
            )}
        </div>
    );
}

function GetGameForPlayer({token, onGameIdChange}) {
    useEffect(() => {
        if (token) {
            const interval = setInterval(() => {
                fetch(`http://localhost:8080/api/games`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': token,
                    }
                })
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
    }, [token]);

    return null;
}

function Board({gameId, playerId, onGameFinish, token}) {
    const [winner, setWinner] = useState(null);
    const [winningCells, setWinningCells] = useState(null);
    const [isPlayerTurn, setIsPlayerTurn] = useState(null);
    const [boardView, setBoardView] = useState([]);

    useEffect(() => {
        const eventSource = new EventSource(`http://localhost:8080/api/subscribe/games/${gameId}`);

        eventSource.onopen = (event) => {
            console.log('Connection opened', event);
        };

        eventSource.onmessage = (event) => {
            console.log('New message', event.data);
        };

        eventSource.addEventListener('moveMade', (event) => {
            console.log('Move made');
            if (event && event.data) {
                const eventData = JSON.parse(event.data);
                if (eventData && eventData.whoWon) {
                    if (eventData.whoWon === playerId) {
                        setWinner(playerId)
                    } else {
                        setWinner("Opponent")
                    }
                }
            }
            fetchGameInfo(gameId);
        });

        eventSource.onerror = (event) => {
            console.error('Error occurred', event);
            if (eventSource.readyState === EventSource.CLOSED) {
                console.log('Connection closed');
            }
        };

        return () => {
            eventSource.close();
        };
    }, [gameId]);

    useEffect(() => {
        if (boardView.length > 0) {
            const winningCells = checkForWinningLine(boardView);
            setWinningCells(winningCells);
        }
    }, [boardView]);

    function checkForWinningLine(board) {
        const winningLines = [
            [[0, 0], [0, 1], [0, 2]],
            [[1, 0], [1, 1], [1, 2]],
            [[2, 0], [2, 1], [2, 2]],
            [[0, 0], [1, 0], [2, 0]],
            [[0, 1], [1, 1], [2, 1]],
            [[0, 2], [1, 2], [2, 2]],
            [[0, 0], [1, 1], [2, 2]],
            [[0, 2], [1, 1], [2, 0]]
        ];

        for (const line of winningLines) {
            const symbols = line.map(([row, col]) => board[row][col]);
            if (symbols.every(val => val === symbols[0] && val !== null)) {
                return line;
            }
        }

        return null;
    }

    function isWinningMove(board) {
        return !!checkForWinningLine(board);
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

    function checkGameStatus(board) {
        if (isWinningMove(board)) {
            onGameFinish();
        } else if (isDraw(board)) {
            setWinner('Draw');
            onGameFinish();
        }
    }

    async function makeMove(rowIndex, columnIndex) {
        try {
            const response = await fetch(`http://localhost:8080/api/games/${gameId}/move`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token,
                },
                body: JSON.stringify({
                    rowIndex: rowIndex,
                    columnIndex: columnIndex,
                }),
            });
            if (!response.ok) {
                throw new Error('Error making move');
            }
            setIsPlayerTurn(false)
        } catch (error) {
            console.error('Error making move:', error);
        }
    }

    async function fetchGameInfo(gameId) {
        try {
            const response = await fetch(`http://localhost:8080/api/games/${gameId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token,
                }
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setBoardView(data.boardView);
            if (data.playerToMove === playerId) {
                console.log('PLAYER TO MOVE TERAZ: ' + data.playerToMove)
                setIsPlayerTurn(true);
            } else {
                console.log('PLAYER TO MOVE TERAZ: ' + data.playerToMove)
                setIsPlayerTurn(false);
            }
            console.log("updated board ", data);
            checkGameStatus(data.boardView);
        } catch (error) {
            console.error('Error fetching board view:', error);
        }
    }

    useEffect(() => {
        fetchGameInfo(gameId)
    }, [gameId]);

    return (
        <div className="board-container">
            {boardView && (
                <div>
                    <h2>Board View</h2>
                    <table className="board">
                        <tbody>
                        {boardView.map((row, rowIndex) => (
                            <tr key={rowIndex}>
                                {row.map((cell, columnIndex) => (
                                    <td
                                        key={columnIndex}
                                        onClick={() => makeMove(rowIndex, columnIndex)}
                                        className={winningCells && winningCells.some(([row, col]) => row === rowIndex && col === columnIndex) ? (playerId === winner ? 'winning-cell-green' : 'winning-cell-red') : ''}
                                        style={{filter: 'brightness(100%)', transition: 'filter 0.3s ease'}}
                                        onMouseEnter={(e) => e.target.style.filter = 'brightness(80%)'}
                                        onMouseLeave={(e) => e.target.style.filter = 'brightness(100%)'}
                                    >
                                        {cell === null ? '' : cell}
                                    </td>
                                ))}
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
            {(winner === null && !isDraw(boardView)) && (
                <h2>{isPlayerTurn ? 'Your turn' : 'Opponent\'s turn'}</h2>
            )}
            {winner && (
                <h2>
                    {winner === 'Draw' ? 'It\'s a draw!' : winner === playerId ? 'You won!' : 'You lost!'}
                </h2>
            )}
        </div>
    );
}

function CloseGameButton({gameId, onCloseGame, token}) {
    const handleClick = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/games/${gameId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token,
                }
            });
            if (response.ok) {
                onCloseGame();
                console.log('game closed, ', {gameId})
            } else {
                onCloseGame();
                throw new Error('Failed to close the game.');
            }
        } catch (error) {
            console.error('Error closing the game:', error);
        }
    };

    return (
        <button className="close-game-button" onClick={handleClick}>Close Game</button>
    );
}

function CreateAccountForm({onCreateAccount}) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    async function handleRegister(userDto) {
        try {
            const response = await fetch('http://localhost:8080/api/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userDto),
            });

            if (response.status === 201) {
                onCreateAccount();
                console.log('Registration successful! Please log in.');
            } else if (response.status === 400) {
                console.log('Username is already taken.');
            } else {
                console.log('Failed to register.');
            }
        } catch (error) {
            console.error('Error during registration:', error);
        }
    }

    const handleCreateClick = () => {
        handleRegister({username, password});
    };

    return (
        <div className="center-container">
            <div className="create-account-container">
                <div>
                    <label className="input-label">
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </label>
                    <button onClick={handleCreateClick}>Create account</button>
                </div>
            </div>
            <button className="back-to-login-button" onClick={onCreateAccount}>
                Back to login
            </button>
        </div>
    );
}

function LoginForm({onLogin, goToCreateAccount}) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    async function handleLogin(userDto) {
        try {
            const response = await fetch('http://localhost:8080/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userDto),
            });

            if (response.status === 200) {
                console.log('Logged in successfuly');
                const data = await response.text();
                onLogin(data)
            } else {
                console.log('Failed to login.');
            }
        } catch (error) {
            console.error('Error during login:', error);
        }
    }

    const handleLoginClick = () => {
        handleLogin({username, password});
    };

    return (
        <div className="center-container">
            <div className="create-account-container">
                <div>
                    <label className="input-label">
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </label>
                    <button onClick={handleLoginClick}>Login</button>
                </div>
            </div>
            <button className="back-to-login-button" onClick={goToCreateAccount}>
                Create account
            </button>
        </div>
    );
}

function App() {
    const [token, setToken] = useState(localStorage.getItem('token') || '');
    const [gameId, setGameId] = useState('');
    const [isGameFinished, setIsGameFinished] = useState(false);
    const [isRegistering, setIsRegistering] = useState(false);

    function handleGameFinished() {
        setIsGameFinished(true);
    }

    function handleLogin(token) {
        setToken(token)
        localStorage.setItem('token', token)
        setIsRegistering(false)
    }

    function handleRegisterAccount() {
        setIsRegistering(false);
    }

    function handleCloseGame() {
        setGameId('');
        setIsGameFinished(false)
    }

    function goToCreateAccount() {
        setIsRegistering(true);
    }

    function logout() {
        setToken('')
        localStorage.setItem('token', '')
        setIsRegistering(false)
    }

    function getUserIdFromToken() {
        const token = localStorage.getItem('token');
        if (token) {
            const decodedToken = jwtDecode(token);
            return decodedToken.userId;
        }
        return null;
    }

    const playerId = getUserIdFromToken();

    return (
        <div className="game-container">
            {isRegistering ? (
                <CreateAccountForm onCreateAccount={handleRegisterAccount}/>
            ) : !token ? (
                <LoginForm onLogin={handleLogin} goToCreateAccount={goToCreateAccount}/>
            ) : gameId ? (
                <div>
                    <Board
                        gameId={gameId}
                        playerId={playerId}
                        onGameFinish={handleGameFinished}
                        token={token}
                    />
                    {isGameFinished ? (
                        <CloseGameButton gameId={gameId} onCloseGame={handleCloseGame} token={token}/>
                    ) : (
                        <h2></h2>
                    )}
                </div>
            ) : (
                <div>
                    <QueueForGame
                        onLogout={logout}
                        token={token}
                        gameId={gameId}
                    />
                    <GetGameForPlayer
                        token={token}
                        onGameIdChange={setGameId}
                    />
                </div>
            )}
        </div>
    );
}

export default App;
