let intervalForFetchingBoard;

function queueForGame() {
    const username = document.getElementById('usernameInput').value;

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
        clearAndHideQueueForm()
        intervalForFetchingBoard = setInterval(async () => {
            updatePlayerToMove(gameId, playerId);
            const board = await getBoardView(gameId, playerId);
            renderBoard(board, gameId, playerId);
        }, 1000);
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
            showQueueForm(); // todo: to jest dla tego, co przegrywa
        }

        const data = await response.json();
        console.log('Board view for game ID', gameId, ':', data);
        return data;

    } catch (error) {
        console.error('Error getting board view:', error);
        clearInterval(intervalForFetchingBoard);
    }
}

function renderBoard(board, gameId, playerId) {
    const boardContainer = document.getElementById("boardContainer");
    boardContainer.innerHTML = "";

    const table = document.createElement("table");
    table.classList.add("board");

    board.forEach(function (row, rowIndex) {
        const tr = document.createElement("tr");

        row.forEach(function (cell, colIndex) {
            const td = document.createElement("td");
            td.textContent = cell;

            // Check for win if required
            const winningLine = checkForWin(board, rowIndex, colIndex);
            if (winningLine && winningLine.some(([r, c]) => r === rowIndex && c === colIndex)) {
                td.classList.add("winning-cell");
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

function checkForWin(board, row, col) {
    const playerSymbol = board[row][col];
    const directions = [
        [-1, 0], // vertical
        [0, -1], // horizontal
        [-1, -1], // diagonal \
        [-1, 1] // diagonal /
    ];

    if (playerSymbol !== "O" && playerSymbol !== "X") {
        return null;
    }

    for (const [dx, dy] of directions) {
        let count = 0;
        let winningLine = [[row, col]];
        let r = row + dx;
        let c = col + dy;

        while (r >= 0 && r < board.length && c >= 0 && c < board[0].length && board[r][c] === playerSymbol) {
            count++;
            winningLine.push([r, c]);
            r += dx;
            c += dy;
        }

        r = row - dx;
        c = col - dy;

        while (r >= 0 && r < board.length && c >= 0 && c < board[0].length && board[r][c] === playerSymbol) {
            count++;
            winningLine.push([r, c]);
            r -= dx;
            c -= dy;
        }

        if (count >= 2) {
            return winningLine;
        }
    }

    return null;
}

function makeMove(gameId, playerId, rowIndex, columnIndex) {
    const makeMoveDto = {
        playerId: playerId,
        rowIndex: rowIndex,
        columnIndex: columnIndex
    };
    console.log('moj log', playerId, rowIndex, columnIndex);

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
            if (gameStatus === 'DRAW' || gameStatus === 'WIN') { // todo: usunac czyja tura, zamienic na kto wygral
                const board = await getBoardView(gameId, playerId);
                renderBoard(board, gameId, playerId);
                setTimeout(function () {
                    closeGame(gameId);
                    showQueueForm()
                }, 3000);
            }
            console.log('Game result:', gameStatus);
        })
        .catch(error => {
            console.error('Error making move:', error);
        });
}

function clearAndHideQueueForm() {
    document.getElementById('queueForm').style.display = 'none';
    document.getElementById('boardContainer').style.display = 'block';

}
function showQueueForm() {
    document.getElementById('queueForm').style.display = 'block';
    document.getElementById('boardContainer').style.display = 'none';
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