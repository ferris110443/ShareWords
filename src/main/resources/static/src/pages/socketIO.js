"use strict";

const roomId = new URLSearchParams(window.location.search).get('roomId') || 'general';
// const roomId = "test";
const socket = io('http://localhost:9092',{
  query: { roomId }
});

document.addEventListener("DOMContentLoaded", function() {
  let editor = ace.edit("editor", {
    mode: "ace/mode/javascript",
    selectionStyle: "text"
  });

  editor.setOptions({
    autoScrollEditorIntoView: true,
    copyWithEmptySelection: true,
  });



  // Optional: Resize editor based on its container's size
  window.onresize = function() {
    editor.resize();
  };
});

let rga


socket.on('connect', () => {
  console.log('Connected to Netty-SocketIO server in room:', roomId);
});
socket.io.on('reconnect', () => {
  console.log('reconnecting............');
});

socket.on("connect_error", (err) => {
  console.error(`connect_error due to ${err.message} in room:`, roomId);
});
socket.on('error', (error) => {
  console.error('Error received:', error);
  console.log('Error details:', error.message, error.stack);
});

// socket.on('init', (data) => {
//   console.log('Initialization data:', data);
// });

socket.on('disconnect', (error) => {
  console.log('Disconnected from Netty-SocketIO server' + error);
});






socket.on('init', ({ id, history }) => { // This is the event listener for a custom 'init' event from the server
  if (!rga) {
    editor.setWrapBehavioursEnabled(false) // disable wrap behaviours in the ACE editor
    rga = new RGA.AceEditorRGA(id, editor)

    rga.subscribe(op => {
      op.roomId = roomId;  // Append roomId to every operation
      socket.emit('message', op);  // Emit message with roomId
    });

    socket.on('message', op => {
      if (op.roomId === roomId) {  // Only process messages that belong to the same room
        rga.receive(op);
      }
    });    // console.log('history', history)
    socket.emit('message', { type: 'historyRequest', roomId });  // Request history for the room
  }

  editor.focus() //Sets focus to the Ace Editor so that the user can start typing immediately.
});
