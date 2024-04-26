const accessToken = localStorage.getItem('accessToken');
const friendsList = document.getElementById("friends-list");
const friendsRequestList = document.getElementById("friends-request-list");
document.querySelector('#searchBox').addEventListener('input', () => {
    const searchQuery = document.querySelector('#searchBox').value;
    getUserInformation(searchQuery);
});
checkFriendshipStatus();


async function getUserInformation(searchQuery) {
    if (!searchQuery.trim()) {
        console.log("Search query is empty, no users will be fetched.");
        document.getElementById('userList').innerHTML = '';
        return;
    }

    try {
        const response = await fetch(`/api/1.0/user/userInformation?query=${encodeURIComponent(searchQuery)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        });
        if (!response.ok) throw new Error('Failed to fetch user list details');
        const data = await response.json();

        const responseRelationShip = await fetch(`/api/1.0/user/friendsRelationShip`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        });
        if (!responseRelationShip.ok) throw new Error('Failed to fetch relationships');
        const dataRelationShip = await responseRelationShip.json();

        // Clear previous user list
        const userList = document.getElementById('userList');
        userList.innerHTML = '';

        // Process and display each user
        data.forEach(user => {
            const userElement = document.createElement('div');
            userElement.className = 'user-item';

            let relationHTML = ``;

            const relation = dataRelationShip.data.find(item => (item.userId === user.id || item.friendId === user.id));
            if (relation) {
                if (relation.status === 'pending') {
                    relationHTML += `<div><strong>Name:</strong> ${user.name}</div>
                                <div><strong>Email:</strong> ${user.email}</div>
                                <button id="btn-userId-${user.id}" class="btn btn-primary add-friend-btn" data-user-email="${user.email}" data-user-id="${user.id}" disabled>Wait accepted</button>`;
                } else if (relation.status === 'accepted') {
                    relationHTML += `<div style="display: none"><strong>Name:</strong> ${user.name}</div>
                                <div style="display: none"><strong>Email:</strong> ${user.email}</div>
                                <button id="btn-userId-${user.id}" class="btn btn-primary add-friend-btn" data-user-email="${user.email}" data-user-id="${user.id}" style="display: none">Already Friends</button>`;
                } else if (relation.status === 'declined') {
                    relationHTML += `<div><strong>Name:</strong> ${user.name}</div>
                                <div><strong>Email:</strong> ${user.email}</div>
                                <button id="btn-userId-${user.id}" class="btn btn-primary add-friend-btn" data-user-email="${user.email}" data-user-id="${user.id}" disabled >Request declined</button>`;
                }
            } else {
                relationHTML += `<div><strong>Name:</strong> ${user.name}</div>
                                <div><strong>Email:</strong> ${user.email}</div>
                                <button id="btn-userId-${user.id}" class="btn btn-primary add-friend-btn" data-user-email="${user.email}" data-user-id="${user.id}">Add as Friend</button>`;
            }

            userElement.innerHTML = relationHTML;
            userList.appendChild(userElement);
        });

        // Add event listeners to newly created buttons
        document.querySelectorAll('.add-friend-btn').forEach(button => {
            button.addEventListener('click', function (event) {
                event.target.disabled = true;
                event.target.innerText = 'Wait accepted';

                const userEmail = event.target.getAttribute('data-user-email');
                socket.emit('addFriendRequest', {userEmail: userEmail, accessToken: accessToken});
                sendAddFriendRequest(event);
            });
        });

    } catch (error) {
        console.error('Error fetching user list:', error);
    }
}

async function sendAddFriendRequest(event) {
    const userId = event.target.getAttribute('data-user-id');
    const response = await fetch(`/api/1.0/user/friends`,
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({friendId: userId})
        });
    const data = await response.json();
    console.log(data)
    if (response.ok) {
        alert('Friend request sent successfully');
    } else {
        alert(data.message);
    }
}

async function checkFriendshipStatus(event) {
    const response = await fetch(`/api/1.0/user/friendsRelationShip`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        }
    });
    const data = await response.json();
    const userId = data.userId;
    friendsList.innerHTML = '';
    friendsRequestList.innerHTML = '';

    data.data.forEach(item => {
        const isOnline = onlineUsers.includes(item.friendId); // Check if the friendId is in the onlineUsers array
        const friendStatus = isOnline ? "Online" : "Offline";
        const friendStatusClass = isOnline ? "status-online" : "status-offline";

        if (item.status === 'accepted') {
            const friendElement = document.createElement('div');
            friendElement.className = 'friend-item';
            if (item.userId === userId) {

                friendElement.innerHTML = `
                    <div><strong>Name:</strong> ${item.friendName}</div>
                    <div><strong>Email:</strong> ${item.friendEmail}</div>
                    <div class="${friendStatusClass}">${friendStatus}</div>
                    <button id="btn-userId-${item.friendId}" class="btn btn-primary remove-friend-btn" data-user-id="${item.userId}" data-friend-id="${item.friendId}" data-email="${item.friendEmail}" >Remove Friend</button>
                `;

            } else if (item.friendId === userId) {
                friendElement.innerHTML = `
                    <div><strong>Name:</strong> ${item.userName}</div>
                    <div><strong>Email:</strong> ${item.userEmail}</div>
                    <div class="${friendStatusClass}">${friendStatus}</div>
                    <button id="btn-userId-${item.userId}" class="btn btn-primary remove-friend-btn" data-user-id="${item.friendId}" data-friend-id="${item.userId}" data-email="${item.userEmail}" >Remove Friend</button>
                `;
            }

            friendsList.appendChild(friendElement);
        }

        if (item.status === 'pending' && item.userId !== userId) {
            const friendRequestElement = document.createElement('div');
            friendRequestElement.className = 'friend-request-item';
            friendRequestElement.innerHTML = `
            <div class="rq-name-${item.userName}"><strong>Name:</strong> ${item.userName}</div>
            <div class="rq-email-${item.userEmail}"><strong>Email:</strong> ${item.userEmail}</div>
            <button id="btn-userId-${item.userId}" class="btn btn-primary accept-friend-btn" data-user-id="${item.friendId}" data-friend-id="${item.userId}">Accept Friend</button>
            <button id="btn-userId-${item.userId}" class="btn btn-primary reject-friend-btn" data-user-id="${item.friendId}" data-friend-id="${item.userId}">Reject Friend</button>
        `;
            friendsRequestList.appendChild(friendRequestElement);
        }


    });

    const acceptButtons = document.querySelectorAll('.accept-friend-btn');
    const rejectButtons = document.querySelectorAll('.reject-friend-btn');
    const removeButtons = document.querySelectorAll('.remove-friend-btn');


    removeButtons.forEach(button => {
        button.addEventListener('click', function () {
            const userId = this.getAttribute('data-user-id');
            const friendId = this.getAttribute('data-friend-id');
            removeFriend(userId, friendId);
        });
    })


    acceptButtons.forEach(button => {
        button.addEventListener('click', function () {

            const userId = this.getAttribute('data-user-id');
            const friendId = this.getAttribute('data-friend-id');
            acceptFriend(userId, friendId);


        });
    });

    rejectButtons.forEach(button => {
        button.addEventListener('click', function () {

            const userId = this.getAttribute('data-user-id');
            const friendId = this.getAttribute('data-friend-id');
            rejectFriend(userId, friendId);
        });
    });

}


async function acceptFriend(userId, friendId) {
    try {
        const response = await fetch(`/api/1.0/user/acceptFriendRequest`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({userId: userId, friendId: friendId, status: 'pending'})
        });
        const data = await response.json();
        console.log('Friend request accepted:', data);
        const friendRequestDiv = document.querySelector(`#btn-userId-${friendId}`).closest('.friend-request-item');
        friendRequestDiv.querySelector('.accept-friend-btn, .reject-friend-btn').remove(); // Remove accept and reject buttons
        friendRequestDiv.classList.replace('friend-request-item', 'friend-item'); // Change class if necessary
        friendsList.appendChild(friendRequestDiv);

    } catch (error) {
        console.error('Error accepting friend request:', error);
    }
}

async function rejectFriend(userId, friendId) {
    try {
        const response = await fetch(`/api/1.0/user/rejectFriendRequest`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({userId: userId, friendId: friendId, status: 'pending'})
        });

        const data = await response.json();
        console.log('Friend request rejected:', data);
        document.querySelector(`#btn-userId-${friendId}`).closest('.friend-request-item').remove();
    } catch (error) {
        console.error('Error rejecting friend request:', error);
    }
}


async function removeFriend(userId, friendId) {
    try {
        const response = await fetch(`/api/1.0/user/removeFriendRequest`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({userId: userId, friendId: friendId, status: 'pending'})
        });

        const data = await response.json();
        console.log('Friend relations removed :', data);
        document.querySelector(`#btn-userId-${friendId}`).closest('.friend-item').remove();

    } catch (error) {
        console.error('Error rejecting friend request:', error);
    }
}


function updateOnlineStatus() {
    console.log("Updating online status...")
    document.querySelectorAll('.friend-item').forEach(item => {
        const email = item.querySelector('button').getAttribute('data-email');
        const isOnline = onlineUsers[email];
        const statusDiv = item.querySelector('.status-online, .status-offline');
        if (statusDiv) {
            statusDiv.textContent = isOnline ? "Online" : "Offline";
            statusDiv.className = isOnline ? "status-online" : "status-offline";
        }
    });
}