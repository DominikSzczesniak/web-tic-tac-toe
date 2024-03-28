let intervalForFetchingBoard;
let intervalForUpdatingPlayerToMove;
let username;

function queueForGame() {
    username = document.getElementById('usernameInput').value;

    fetch(`http://localhost:8080/api/games/queue?username=${encodeURIComponent(username)}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('Bad request');
            }
        })
        .then(playerId => {
            hideQueueForm();
            showInQueue();
            console.log('Player queued successfully with playerId:', playerId);
            const intervalId = setInterval(() => {
                getGameForPlayer(playerId)
                    .then(gameId => {
                        clearInterval(intervalId);
                        handleGameForPlayer(gameId, playerId);
                    })
                    .catch(() => {
                        console.error('Error getting game for player:', playerId);
                    });
            }, 5000);
        })
        .catch(error => {
            console.error('Error queuing player:', error);
        });
}

function getGameForPlayer(playerId) {
    return new Promise((resolve, reject) => {
        fetch(`http://localhost:8080/api/games?playerId=${encodeURIComponent(playerId)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (response.ok) {
                    hideInQueue()
                    return response.json();
                } else {
                    throw new Error('Bad request');
                }
            })
            .then(data => {
                resolve(data);
            })
            .catch(error => {
                reject(error);
            });
    });
}

function handleGameForPlayer(gameId, playerId) {
    if (gameId) {
        console.log('Game found for player with ID:', playerId);
        hideQueueForm()
        intervalForFetchingBoard = setInterval(async () => {
            const board = await getBoardView(gameId, playerId);
            renderBoard(board, gameId, playerId);
        }, 1000);
        intervalForUpdatingPlayerToMove = setInterval(async () => {
            await updatePlayerToMove(gameId, playerId);
        }, 999);
    }
}

async function getBoardView(gameId) {
    try {
        const response = await fetch(`http://localhost:8080/api/games/${gameId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            showQueueForm();
        }

        const data = await response.json();
        console.log('Board view for game ID', gameId, ':', data);
        return data;

    } catch (error) {
        console.error('Error getting board view:', error);
        clearInterval(intervalForFetchingBoard);
        showQueueForm();
    }
}

function renderBoard(board, gameId, playerId, whoWon) {
    const boardContainer = document.getElementById("boardContainer");
    boardContainer.innerHTML = "";

    const table = document.createElement("table");
    table.classList.add("board");

    board.forEach(function (row, rowIndex) {
        const tr = document.createElement("tr");

        row.forEach(function (cell, colIndex) {
            const td = document.createElement("td");
            td.textContent = cell;

            const winningLine = getWinningLine(board, rowIndex, colIndex);
            if (winningLine && winningLine.some(([r, c]) => r === rowIndex && c === colIndex)) {
                colorCells(whoWon, td);
                if (whoWon !== username) {
                    clearInterval(intervalForUpdatingPlayerToMove);
                    document.getElementById('playerThatWon').innerText = 'Opponent won';
                    hidePlayerToMove();
                }
            }

            if (isBoardFull(board)) {
                clearInterval(intervalForUpdatingPlayerToMove);
                document.getElementById('playerThatWon').innerText = 'Draw';
                hidePlayerToMove();
            }

            td.addEventListener('click', function () {
                makeMove(gameId, playerId, rowIndex, colIndex);
            });

            tr.appendChild(td);
        });

        table.appendChild(tr);
    });

    boardContainer.appendChild(table);
}

function getWinningLine(board, row, col) {
    const playerSymbol = board[row][col];
    const directions = [
        [-1, 0],
        [0, -1],
        [-1, -1],
        [-1, 1]
    ];

    if (playerSymbol !== "O" && playerSymbol !== "X") {
        return null;
    }

    for (const [dx, dy] of directions) {
        let count = 0;
        let winningLine = [[row, col]];
        let currentRow = row + dx;
        let currentColumn = col + dy;

        while (currentRow >= 0 && currentRow < board.length && currentColumn >= 0
        && currentColumn < board[0].length && board[currentRow][currentColumn] === playerSymbol) {
            count++;
            winningLine.push([currentRow, currentColumn]);
            currentRow += dx;
            currentColumn += dy;
        }

        currentRow = row - dx;
        currentColumn = col - dy;

        while (currentRow >= 0 && currentRow < board.length && currentColumn >= 0
        && currentColumn < board[0].length && board[currentRow][currentColumn] === playerSymbol) {
            count++;
            winningLine.push([currentRow, currentColumn]);
            currentRow -= dx;
            currentColumn -= dy;
        }

        if (count >= 2) {
            return winningLine;
        }
    }

    return null;
}

function isBoardFull(board) {
    for (let row = 0; row < board.length; row++) {
        for (let col = 0; col < board[0].length; col++) {
            if (board[row][col] === null) {
                return false;
            }
        }
    }
    return true;
}

function colorCells(whoWon, td) {
    if (whoWon === username) {
        td.classList.add("winning-cell");
    } else {
        td.classList.add("losing-cell")
    }
}

function makeMove(gameId, playerId, rowIndex, columnIndex) {
    const makeMoveDto = {
        playerId: playerId,
        rowIndex: rowIndex,
        columnIndex: columnIndex
    };

    fetch(`http://localhost:8080/api/games/${gameId}/move`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(makeMoveDto)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Bad request - illegal move/wrong player');
            }
        })
        .then(async data => {
            const gameStatus = data.gameStatus;
            const whoWon = data.playerThatWon;
            await handleGameFinished(data, gameId, playerId, whoWon);
            console.log('Game result:', gameStatus);
        })
        .catch(error => {
            console.error('Error making move:', error);
        });
}

async function handleGameFinished(data, gameId, playerId, whoWon) {
    const gameStatus = data.gameStatus;
    if (gameStatus === 'DRAW' || gameStatus === 'WIN') {
        showWhoWon(data);
        hidePlayerToMove();
        clearInterval(intervalForFetchingBoard);
        clearInterval(intervalForUpdatingPlayerToMove);
        const board = await getBoardView(gameId, playerId);
        renderBoard(board, gameId, playerId, whoWon);
        setTimeout(function () {
            closeGame(gameId);
            hideBoard();
            showQueueForm();
        }, 3000);
    }
}

function closeGame(gameId) {
    fetch(`http://localhost:8080/api/games/${gameId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                console.log(`Game with ID ${gameId} successfully closed.`);
            } else {
                throw new Error(`Failed to close game with ID ${gameId}`);
            }
        })
        .catch(error => {
            console.error('Error closing game:', error);
        });
}

async function updatePlayerToMove(gameId, playerId) {
    const playerToMoveElement = document.getElementById('playerToMove');
    const playerToMove = await fetchPlayerToMove(gameId);
    if (playerToMove !== null) {
        if (playerToMove === playerId) {
            playerToMoveElement.textContent = "Your turn";
        } else {
            playerToMoveElement.textContent = "Opponent's turn";
        }
    } else {
        playerToMoveElement.textContent = 'Failed to fetch player to move.';
    }
}

async function fetchPlayerToMove(gameId) {
    try {
        const response = await fetch(`http://localhost:8080/api/games/${gameId}/move`);
        const data = await response.text();
        return data;
    } catch (error) {
        console.error('Error fetching player to move:', error);
        return null;
    }
}

function prepareGame() {
    fetch('http://localhost:8080/api/games', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Not enough players in queue');
            }
            return response.json();
        })
        .then(data => {
            console.log('Game prepared with ID:', data);
        })
        .catch(error => {
            console.error('Something went wrong with creating the game:', error);
        });
}

function showWhoWon(data) {
    const whoWon = data.playerThatWon;
    const gameStatus = data.gameStatus;
    if (gameStatus === 'DRAW') {
        document.getElementById('playerThatWon').innerText = 'Draw'
    } else if (whoWon === username) {
        document.getElementById('playerThatWon').innerText = 'You won'
    } else {
        document.getElementById('playerThatWon').innerText = 'Opponent won'
    }
}

function hidePlayerToMove() {
    document.getElementById('playerToMove').innerText = '';
}

function hideQueueForm() {
    document.getElementById('queueForm').style.display = 'none';
    document.getElementById('boardContainer').style.display = 'block';
    document.getElementById('playerToMove').innerText = ''

}

function showQueueForm() {
    document.getElementById('queueForm').style.display = 'block';
    document.getElementById('playerToMove').innerText = ''
    document.getElementById('playerThatWon').innerText = ''
}

function hideBoard() {
    document.getElementById('boardContainer').innerText = '';
}

function showInQueue() {
    const queueBox = document.getElementById('queueBox');
    queueBox.style.display = 'block';
}

function hideInQueue() {
    const queueBox = document.getElementById('queueBox');
    queueBox.style.display = 'none';
}




