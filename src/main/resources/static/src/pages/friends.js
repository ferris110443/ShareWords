const accessToken = localStorage.getItem('accessToken');
const friendsList = document.getElementById("friends-list");
const friendsRequestList = document.getElementById("friends-request-list");
const acceptButtons = document.querySelectorAll('.accept-friend-btn');
const rejectButtons = document.querySelectorAll('.reject-friend-btn');


document.addEventListener('DOMContentLoaded', function () {


    checkAuthentication()
    document.body.addEventListener('click', function (event) {
        if (event.target.matches('.accept-friend-btn')) {
            const userId = event.target.getAttribute('data-user-id');
            const friendId = event.target.getAttribute('data-friend-id');
            const friendName = event.target.getAttribute('data-friend-name');
            const friendEmail = event.target.getAttribute('data-friend-email');
            acceptFriend(userId, friendId, friendName, friendEmail);
        }

        if (event.target.matches('.reject-friend-btn')) {
            const userId = event.target.getAttribute('data-user-id');
            const friendId = event.target.getAttribute('data-friend-id');
            rejectFriend(userId, friendId);
        }
    });
    checkFriendshipStatus();
    updateOnlineStatus()
});

document.querySelector('#searchBox').addEventListener('input', () => {
    const searchQuery = document.querySelector('#searchBox').value;
    getUserInformation(searchQuery);
});
document.getElementById('friends-list').addEventListener('click', function (event) {
    // Check if the clicked element is a remove-friend-btn
    if (event.target.closest('.remove-friend-btn')) {
        const button = event.target.closest('.remove-friend-btn');
        const userId = button.dataset.userId;
        const friendId = button.dataset.friendId;
        removeFriend(userId, friendId);
    }
});


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


            let relationHTML = ``;

            const relation = dataRelationShip.data.find(item => (item.userId === user.id || item.friendId === user.id));
            if (relation) {
                if (relation.status === 'pending') {
                    userElement.className = 'user-item';
                    relationHTML += `<div>
                                        <div><strong>Name:</strong> ${user.name}</div>
                                        <div><strong>Email:</strong> ${user.email}</div>
                                    </div>
                                    <button id="btn-userId-${user.id}" class="btn btn-warning add-friend-btn"  data-user-name="${user.name}" data-user-email="${user.email}" data-user-id="${user.id}" disabled>Wait accepted</button>`;
                } else if (relation.status === 'accepted') {
                    relationHTML += `<div>
                                        <div style="display: none"><strong>Name:</strong> ${user.name}</div>
                                        <div style="display: none"><strong>Email:</strong> ${user.email}</div>
                                    </div>
                                    <button id="btn-userId-${user.id}" class="btn btn-secondary add-friend-btn"  data-user-name="${user.name}" data-user-email="${user.email}" data-user-id="${user.id}" style="display: none">Already Friends</button>`;
                } else if (relation.status === 'declined') {
                    userElement.className = 'user-item';
                    relationHTML += `<div>
                                        <div><strong>Name:</strong> ${user.name}</div>
                                        <div><strong>Email:</strong> ${user.email}</div>
                                     </div>
                                     <button id="btn-userId-${user.id}" class="btn btn-secondary add-friend-btn"  data-user-name="${user.name}" data-user-email="${user.email}" data-user-id="${user.id}" disabled >Request declined</button>`;
                }
            } else {
                userElement.className = 'user-item';
                relationHTML += `<div>
                                    <div><strong>Name:</strong> ${user.name}</div>
                                    <div><strong>Email:</strong> ${user.email}</div>
                                </div>
                                <button id="btn-userId-${user.id}" class="btn btn-primary add-friend-btn"  data-user-name="${user.name}" data-user-email="${user.email}" data-user-id="${user.id}">Add as Friend</button>`;
            }

            userElement.innerHTML = relationHTML;
            userList.appendChild(userElement);
        });

        // Add event listeners to newly created buttons
        document.querySelectorAll('.add-friend-btn').forEach(button => {
            button.addEventListener('click', function (event) {
                event.target.disabled = true;
                event.target.innerText = 'Wait accepted';
                event.target.classList.remove('btn-primary');
                event.target.classList.add('btn-warning');
                const userName = event.target.getAttribute('data-user-name');
                const userEmail = event.target.getAttribute('data-user-email');
                console.log(userName + " " + userEmail)
                socket.emit('addFriendRequest', {userEmail: userEmail, userName: userName, accessToken: accessToken});
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
        const isOnline = onlineUsers.hasOwnProperty(item.friendEmail); // Check if the friendId is in the onlineUsers array
        const friendStatus = isOnline ? "Online" : "Offline";
        const friendStatusClass = isOnline ? "status-online" : "status-offline";

        if (item.status === 'accepted') {
            const friendElement = document.createElement('div');
            friendElement.className = 'friend-item';
            if (item.userId === userId) {
                friendElement.innerHTML = `
                    <div>
                        <div><strong>Name:</strong> ${item.friendName}</div>
                        <div><strong>Email:</strong> ${item.friendEmail}</div>
                        <div class="${friendStatusClass}">${friendStatus}</div>
                    </div>
                    <button id="btn-userId-${item.friendId}" class="btn remove-friend-btn" data-user-id="${item.userId}" data-friend-id="${item.friendId}" data-email="${item.friendEmail}" >
                        <img class="remove-friend-btn-img" src="/logo/remove-user.png" alt="Remove Friend">
                    </button>
                `;

            } else if (item.friendId === userId) {
                friendElement.innerHTML = `
                    <div>
                        <div><strong>Name:</strong> ${item.userName}</div>
                        <div><strong>Email:</strong> ${item.userEmail}</div>
                        <div class="${friendStatusClass}">${friendStatus}</div>
                    </div>
                    <button id="btn-userId-${item.userId}" class="btn remove-friend-btn" data-user-id="${item.friendId}" data-friend-id="${item.userId}" data-email="${item.userEmail}" >
                        <img class="remove-friend-btn-img" src="/logo/remove-user.png" alt="Remove Friend">
                    </button>
                `;
            }

            friendsList.appendChild(friendElement);
        }

        if (item.status === 'pending' && item.userId !== userId) {
            const friendRequestElement = document.createElement('div');
            friendRequestElement.className = 'friend-request-item';
            friendRequestElement.innerHTML = `
            <div>
                <div class="rq-name-${item.userName}"><strong>Name:</strong> ${item.userName}</div>
                <div class="rq-email-${item.userEmail}"><strong>Email:</strong> ${item.userEmail}</div>
            </div>
            <div class="btn-accept-reject-container">
                <button id="btn-userId-${item.userId}" class="btn btn-success accept-friend-btn" data-friend-name="${item.userName}" data-friend-email="${item.userEmail}"  "data-user-id="${item.friendId}" data-friend-id="${item.userId}">Accept Friend</button>
                <button id="btn-userId-${item.userId}" class="btn btn-danger reject-friend-btn" data-friend-name="${item.userName}" data-friend-email="${item.userEmail}"  "data-user-id="${item.friendId}" data-friend-id="${item.userId}">Reject Friend</button>
            </div>
        `;
            friendsRequestList.appendChild(friendRequestElement);
        }
        updateOnlineStatus()
    });
}


async function acceptFriend(userId, friendId, friendName, friendEmail) {
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
        console.log('Friend request accepted:', data.data);
        const friendRequestDiv = document.querySelector(`#btn-userId-${friendId}`).closest('.friend-request-item');
        friendRequestDiv.remove();

        const friendElement = document.createElement('div');
        friendElement.className = 'friend-item';
        friendElement.innerHTML = `
                    <div>
                        <div><strong>Name:</strong> ${friendName}</div>
                        <div><strong>Email:</strong> ${friendEmail}</div>
                        <div class="status-offline">Offline</div>
                    </div>
                    <button id=btn-userId-${friendId} class="btn remove-friend-btn" data-user-id="${userId}" data-friend-id="${friendId}" data-email="${friendEmail}">
                        <img class="remove-friend-btn-img" src="/logo/remove-user.png" alt="Remove Friend">
                    </button>
                `;

        document.getElementById('friends-list').appendChild(friendElement);
        updateOnlineStatus();


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
            body: JSON.stringify({userId: userId, friendId: friendId})
        });

        const data = await response.json();
        console.log('Friend removed:', data);
        if (response.ok) {
            document.querySelector(`#btn-userId-${friendId}`).closest('.friend-item').remove();
        } else {
            alert('Failed to remove friend: ' + data.message);
        }
    } catch (error) {
        console.error('Error removing friend:', error);
    }
}


async function checkAuthentication() {
    if (!accessToken) {
        alert('No access token found. Please login.');
        redirectToLogin();
        return;
    }

    try {
        const response = await fetch(`/api/1.0/validation/user`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        });
        if (!response.ok) {
            alert('You are not authenticated. Redirecting to login page.');
            redirectToLogin();
            return false;
        }
        return true;
    } catch (error) {
        console.error('Error checking authentication:', error);
        alert('Error checking authentication. Please try again.');
        redirectToLogin();
        return false;
    }
}

function redirectToLogin() {
    window.location.href = '/index.html';
}