"use strict";

const socket = io('http://localhost:9092');

socket.on('connect', () => {
    console.log('Connected to Netty-SocketIO server');
    // Emit an initial message right after connection
    socket.emit('message', {accessToken: accessToken});
});
socket.io.on('reconnect', () => {
    console.log('reconnecting............');
});

socket.on("connect_error", (err) => {
    console.error(`connect_error due to ${err.message} in room:`);
});
socket.on('error', (error) => {
    console.error('Error received:', error);
    console.log('Error details:', error.message, error.stack);
});

socket.on('disconnect', (error) => {
    console.log('Disconnected from Netty-SocketIO server' + error);
});


socket.on('message', (message) => {
    console.log('Received message:', message);
});


let onlineUsers = [];

// socket.on("onlineUsers", function (users) {
//     console.log("Online Users:", users);
//     onlineUsers = users;
//     updateOnlineStatus();
// });


socket.on("onlineUsers", function (users) {
    console.log("Online Users:", users);
    onlineUsers = users;
    updateOnlineStatus();
});