document.querySelector('#searchBox').addEventListener('input', () => {
    const searchQuery = document.querySelector('#searchBox').value;
    getUserInformation(searchQuery);
});

const accessToken = localStorage.getItem('accessToken');

async function getUserInformation(searchQuery) {
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

        // Display the user list
        console.log(data);
        document.getElementById('userList').innerHTML = '';

        // Dynamically create user entries in the userList div
        data.forEach(user => {
            const userElement = document.createElement('div');
            userElement.className = 'user-item';
            userElement.innerHTML = `
                <div><strong>Name:</strong> ${user.name}</div>
                <div><strong>Email:</strong> ${user.email}</div>
                <div><strong>Last Online:</strong> ${user.lastOnlineTime}</div>
                <button class="btn btn-primary add-friend-btn" data-user-id="${user.id}">Add as Friend</button>
            `;
            document.getElementById('userList').appendChild(userElement);
        });
        document.querySelectorAll('.add-friend-btn').forEach(button => {
            button.addEventListener('click', sendAddFriendRequest);
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