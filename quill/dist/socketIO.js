"use strict";

const socket = io('http://localhost:9092');
let onlineUsers = {};

socket.on('connect', () => {
    console.log('Connected to Netty-SocketIO server');
    socket.emit('message', {accessToken: accessToken}, (ack) => {
        console.log('Acknowledgment from server:', ack);
    });
    socket.on("onlineUsers", function (users) {
        // console.log("Online Users:", users);
        onlineUsers = users;  // Update the global variable
        // console.log('Online Users:!!!!!!!!!', onlineUsers);
        updateOnlineStatus();
    });
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

socket.on('friendRequest', function (requestUserEmail, requestUserName) {
    alert('You received a friend request from: ' + requestUserEmail + requestUserName);

    const friendsRequestList = document.getElementById("friends-request-list");
    const friendRequestElement = document.createElement('div');
    friendRequestElement.className = 'friend-request-item';
    friendRequestElement.innerHTML = `
            <div class="rq-name-${requestUserName}"><strong>Name:</strong> ${requestUserName}</div>
            <div class="rq-email-${requestUserEmail}"><strong>Email:</strong> ${requestUserEmail}</div>
            <button id="btn-userId" class="btn btn-primary accept-friend-btn-ws" >Accept Friend</button>
            <button id="btn-userId" class="btn btn-primary reject-friend-btn-ws" >Reject Friend</button>
        `;
    friendsRequestList.appendChild(friendRequestElement);

    const acceptButtons = document.querySelectorAll('.accept-friend-btn-ws');
    const rejectButtons = document.querySelectorAll('.reject-friend-btn-ws');
    acceptButtons.forEach(button => {
        button.addEventListener('click', function () {
            const friendRequestItem = this.closest('.friend-request-item');
            acceptFriendWS(accessToken, requestUserEmail)
                .then(() => {
                    friendRequestItem.remove(); // Remove the friend request from the DOM after successful operation
                })
                .catch(error => {
                    console.error('Failed to accept friend request:', error);
                });
        });
    });

    rejectButtons.forEach(button => {
        button.addEventListener('click', function () {
            const friendRequestItem = this.closest('.friend-request-item');
            rejectFriendWS(accessToken, requestUserEmail)
                .then(() => {
                    friendRequestItem.remove(); // Remove the friend request from the DOM after successful operation
                })
                .catch(error => {
                    console.error('Failed to remove friend request:', error);
                });

        });
    });

    async function acceptFriendWS(accessToken, requestUserEmail) {
        socket.emit('acceptFriendRequestWS', {accessToken: accessToken, requestUserEmail: requestUserEmail}, (ack) => {
            console.log('Acknowledgment from server:', ack);
            let userEmail = ack.userEmail;
            let userId = ack.userId;
            let friendName = ack.requestUserEmail;
            let friendId = ack.requestUserId;


            const friendElement = document.createElement('div');
            friendElement.className = 'friend-item';
            friendElement.innerHTML = `
                    <div><strong>Name:</strong> ${userEmail}</div>
                    <div><strong>Email:</strong> ${friendName}</div>
                    <div class="status-offline">Offline</div>
                    <button class="btn btn-primary remove-friend-btn" data-user-id="${userId}" data-friend-id="${friendId}" data-email="${requestUserEmail}">Remove Friend</button>
                `;

            document.getElementById('friends-list').appendChild(friendElement);
            updateOnlineStatus();

        });
    }

    async function rejectFriendWS(accessToken, requestUserEmail) {
        socket.emit('rejectFriendRequestWS', {accessToken: accessToken, requestUserEmail: requestUserEmail}, (ack) => {
            console.log('Acknowledgment from server:', ack);


        });
    }


});


socket.on('friendRequestAccepted', function (requestUserEmail) {
    alert('Your friend request to ' + requestUserEmail + ' has been accepted');
});

socket.on('friendRequestRejected', function (requestUserEmail) {
    alert('Your friend request to ' + requestUserEmail + ' has been rejected');
});

socket.on('friendRequestRemoved', function (requestUserEmail) {
    alert('Your friend request to ' + requestUserEmail + ' has been removed');
});


function updateOnlineStatus() {
    console.log("Updating online status...");
    document.querySelectorAll('.friend-item').forEach(item => {
        const email = item.querySelector('button').getAttribute('data-email');
        const isOnline = onlineUsers[email];
        const statusDiv = item.querySelector('.status-online, .status-offline');

        console.log(email + " " + isOnline + " " + statusDiv);

        if (statusDiv) {
            statusDiv.textContent = isOnline ? "Online" : "Offline";
            statusDiv.className = isOnline ? "status-online" : "status-offline";
        }
    });
}



