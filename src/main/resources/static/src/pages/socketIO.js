"use strict";

const socket = io('https://sharewords.org/');
// const socket = io('https://34.230.138.53:9cd092');
// const socket = io('http://localhost:9092');
let onlineUsers = {};

socket.on('connect', () => {
    const accessToken = localStorage.getItem('accessToken');
    console.log('Connected to Netty-SocketIO server');
    socket.emit('message', {accessToken: accessToken}, (ack) => {
        // console.log('Acknowledgment from server:', ack);
    });
    socket.on("onlineUsers", function (users) {
        onlineUsers = users;
        console.log('Online Users !!!', onlineUsers);
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
    // alert('You received a friend request from: ' + requestUserEmail + requestUserName);
    let message = `You have received a friend request <br>
                         User : <strong>${requestUserName}</strong><br> 
                         Email : <strong>${requestUserEmail}</strong>`;
    document.getElementById('friendRequestMessage').innerHTML = message;
    let modal = new bootstrap.Modal(document.getElementById('friendRequestModal'));
    modal.show();

    const friendsRequestList = document.getElementById("friends-request-list");
    const friendRequestElement = document.createElement('div');
    friendRequestElement.className = 'friend-request-item';
    friendRequestElement.innerHTML = `
            <div>
                <div class="rq-name-${requestUserName}"><strong>Name:</strong> ${requestUserName}</div>
                <div class="rq-email-${requestUserEmail}"><strong>Email:</strong> ${requestUserEmail}</div>
            </div>
            <div class="btn-accept-reject-container">
                <button id="btn-userId" class="btn btn-success accept-friend-btn-ws" >Confirm</button>
                <button id="btn-userId" class="btn btn-danger reject-friend-btn-ws" >Delete</button>
            </div>
        `;
    friendsRequestList.insertBefore(friendRequestElement, friendsRequestList.firstChild);

    const acceptButtons = document.querySelectorAll('.accept-friend-btn-ws');
    const rejectButtons = document.querySelectorAll('.reject-friend-btn-ws');
    acceptButtons.forEach(button => {
        button.addEventListener('click', function () {
            const friendRequestItem = this.closest('.friend-request-item');
            acceptFriendWS(accessToken, requestUserEmail, requestUserName)
                .then(() => {
                    friendRequestItem.remove(); // Remove the friend request from the DOM after successful operation


                    socket.emit('confirmFriendRequest', {
                        accessToken: accessToken,
                        requestUserEmail: requestUserEmail
                    }, (ack) => {
                        console.log('Acknowledgment from server:', ack);
                    });


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

                    socket.emit('confirmFriendRequest', {
                        accessToken: accessToken,
                        requestUserEmail: requestUserEmail
                    }, (ack) => {
                        console.log('Acknowledgment from server:', ack);
                    });

                })
                .catch(error => {
                    console.error('Failed to remove friend request:', error);
                });

        });
    });

    async function acceptFriendWS(accessToken, requestUserEmail, requestUserName) {
        socket.emit('acceptFriendRequestWS', {accessToken: accessToken, requestUserEmail: requestUserEmail}, (ack) => {
            console.log('Acknowledgment from server:', ack);
            let userEmail = ack.userEmail;
            let userId = ack.userId;
            let friendEmail = ack.requestUserEmail;
            let friendId = ack.requestUserId;
            let friendName = requestUserName;


            const friendElement = document.createElement('div');
            friendElement.className = 'friend-item';
            friendElement.innerHTML = `
                    <div>
                        <div><strong>Name:</strong> ${friendName}</div>
                        <div><strong>Email:</strong> ${friendEmail}</div>
                        <div class="status-offline">Offline</div>
                    </div>
                    <div>
                        <button id=btn-userId-${friendId} class="btn remove-friend-btn" data-user-id="${userId}" data-friend-id="${friendId}" data-email="${friendEmail}">
                            <img class="remove-friend-btn-img" src="/logo/remove-user.png" alt="Remove Friend">
                        </button>
                    </div>
                `;

            document.getElementById('friends-list').insertBefore(friendElement, document.getElementById('friends-list').firstChild);
            updateOnlineStatus();

        });
    }

    async function rejectFriendWS(accessToken, requestUserEmail) {
        socket.emit('rejectFriendRequestWS', {accessToken: accessToken, requestUserEmail: requestUserEmail}, (ack) => {
            console.log('Acknowledgment from server:', ack);


        });
    }

});


socket.on('addMemberRequest', function (memberEmail, memberId, roomNumber) {
    console.log(memberEmail, memberId, roomNumber);
    renderUserWorkspaceList();
});

socket.on('confirmFriendRequest', function (userEmail, userName, friendEmail) {
    console.log('confirmFriendRequest', userEmail);
    console.log('confirmFriendRequest', userName);
    console.log('confirmFriendRequest', friendEmail);
    getUserInformation(friendEmail)
    checkFriendshipStatus()
})


function updateOnlineStatus() {

    document.querySelectorAll('.friend-item').forEach(item => {
        const email = item.querySelector('button').getAttribute('data-email');
        const isOnline = onlineUsers[email];
        // console.log(onlineUsers);
        const statusDiv = item.querySelector('.status-online, .status-offline');

        // console.log(email + " " + isOnline + " " + statusDiv);
        if (statusDiv) {
            statusDiv.textContent = isOnline ? "Online" : "Offline";
            statusDiv.className = isOnline ? "status-online" : "status-offline";
        }
    });
}



