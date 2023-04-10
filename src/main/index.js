// WILL NOT BE NECESSARY PROBABLY
/**
const WebSocket = require('ws');
const http = require('http');

const server = http.createServer();

const ws = new WebSocket.Server({
    server,
    rejectUnauthorized: false,
    verifyClient: (info, cb) => {
        cb(true);
    },
});

ws.on('connection', (ws) => {
    console.log('Client connected');
});

server.listen(8080, '127.0.0.1', () => {
    console.log('Server listening on http://127.0.0.1:8080');
});**/