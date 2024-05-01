const accessToken = localStorage.getItem('accessToken');
const params = new URLSearchParams(window.location.search);
const workspaceName = params.get('roomId');
const queryParams = new URLSearchParams(window.location.search);
const roomId = decodeURIComponent(queryParams.get('roomId'));
const coeditorURL = 'http://34.230.138.53/coeditor';
// const coeditorURL = 'http://localhost:8888/coeditor';


document.addEventListener('DOMContentLoaded', function () {
    checkAuthentication()

    document.getElementById('delete-workspace-btn').addEventListener('click', deleteWorkspace);
    document.getElementById('file-creation-form').addEventListener('submit', function (event) {
        event.preventDefault();  // Prevent the default form submission
        const fileName = document.getElementById('file-name-input').value;
        const fileDescription = document.getElementById('file-description').value;
        const fileId = uuidv4();
        const data = {
            fileName: fileName,
            fileDescription: fileDescription,
            fileId: fileId,
            roomId: roomId
        };
        console.log(data)
        // Fetch API to send data and handle response
        fetch('/api/1.0/workspace/file', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log('Success:', data);
                alert('File created successfully!');

                // Uncomment the following line to redirect after success
                window.location.href = `${coeditorURL}/coeditor.html?roomId=${roomId}&fileId=${fileId}`;
            })
            .catch((error) => {
                console.error('Error:', error);
                alert('Failed to create file. Please try again.');
            });
    });
    renderWorkspaceFileList()
    renderWorkspaceInformation()
    getWorkspaceMember()
    getUserFriendsForAddingMembers()
});


async function deleteWorkspace() {

    try {
        const response = await fetch(`/api/1.0/workspace/workspace?workspaceName=${roomId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        });

        if (response.status === 403) {
            alert("Only the owner can delete the workspace")
            window.location.href = '/admin/home';
        } else if (!response.ok) {
            alert("Internal error")
            window.location.href = '/admin/home';
        }

        const result = await response.json();
        console.log('Workspace deleted successfully:', result);
        window.location.href = '/admin/home';
    } catch (error) {
        console.error('Failed to delete the workspace:', error);
        // Handle errors, such as displaying a message to the user
    }
}


async function renderWorkspaceFileList() {
    const response = await fetch(`/api/1.0/workspace/workspace?workspaceName=${workspaceName}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        }
    })
    const data = await response.json();

    if (Array.isArray(data.data)) {
        let html = '';
        data.data.forEach((file, index) => {
            html += `
                    <div class="file-entry" id="file${index}">
                        <p class="file-name-title"><strong>${file.fileTitle}</strong></p>
                        <p class="file-name-description">${file.fileDescription}</p>
                        <button aria-label="Edit File ${index}" class="edit-file-btn btn btn-primary " data-fileid="${file.fileId}">Edit</button>
                        <button aria-label="Delete File ${index}" class="delete-file-btn btn btn-danger" data-fileid="${file.fileId}">Delete</button>
                    </div>
                `;
        });
        document.getElementById("fileList").innerHTML += html;
        attachEditButtonListeners()
        attachDeleteButtonListeners()
    } else {
        console.error('No files in workspace', data.data);
    }


    function attachEditButtonListeners() {
        const editButtons = document.querySelectorAll('.edit-file-btn');
        editButtons.forEach(button => {
            button.addEventListener('click', function () {
                const fileId = this.getAttribute('data-fileid');
                const editUrl = `${coeditorURL}/coeditor.html?roomId=${workspaceName}&fileId=${fileId}`;
                window.location.href = editUrl;
            });
        });
    }


    function attachDeleteButtonListeners() {
        const deleteButtons = document.querySelectorAll('.delete-file-btn');
        deleteButtons.forEach(button => {
            button.addEventListener('click', async function () {
                console.log('delete button clicked')
                const fileId = this.getAttribute('data-fileid');
                const response = await fetch(`/api/1.0/workspace/file?fileId=${fileId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${accessToken}`
                    }
                });
                const data = await response.json();
                if (response.ok) {
                    alert('File deleted successfully');
                    const fileEntry = button.closest('.file-entry');
                    fileEntry.remove();
                } else {
                    alert('Failed to delete file: ' + data.error);
                }
            });
        });
    }


}

async function renderWorkspaceInformation() {
    const response = await fetch(`/api/1.0/workspace/workspaceInformation?workspaceName=${workspaceName}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        }
    })
    if (!response.ok) {
        alert("Failed to fetch workspace information. Please try again.")
        window.location.reload();
    }
    const data = await response.json();
    const workspaceInformation = data.data;
    console.log("workspaceInformation : ", workspaceInformation)
    document.getElementById('workspace-name').textContent = workspaceInformation.workspaceName;
    document.getElementById('workspace-description').textContent = workspaceInformation.workspaceDescription;
    document.getElementById('workspace-owner').textContent = workspaceInformation.workspaceOwner;
    document.getElementById('workspace-creation-date').textContent = workspaceInformation.workspaceCreatedAt.substring(0, 10);
}

async function getWorkspaceMember() {
    try {
        const response = await fetch(`/api/1.0/workspace/workspaceMembers?workspaceName=${roomId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        console.log(data.data)
        showWorkspaceMemberList(data.data);

    } catch (error) {
        console.error('Failed to update user:', error);
    }
}

async function getUserFriendsForAddingMembers() {
    try {
        // Fetch the current members of the workspace
        const membersResponse = await fetch(`/api/1.0/workspace/workspaceMembers?workspaceName=${roomId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
        });
        const membersData = await membersResponse.json();
        const currentMemberIds = new Set(membersData.data.map(member => member.userId));

        // Fetch user friends
        const friendsResponse = await fetch(`/api/1.0/user/friendsRelationShip`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
        });

        if (!friendsResponse.ok) {
            throw new Error(`HTTP error! status: ${friendsResponse.status}`);
        }

        const friendsData = await friendsResponse.json();
        const userFriendsInformation = friendsData.data;
        const userId = friendsData.userId;

        console.log(userFriendsInformation);
        console.log(userId);

        // Display friends who are not already in the workspace
        userFriendsInformation.forEach(item => {
            if ((userId === item.userId && item.status === 'accepted' && !currentMemberIds.has(item.friendId)) ||
                (userId === item.friendId && item.status === 'accepted' && !currentMemberIds.has(item.userId))) {
                const friendDiv = document.createElement('div');
                friendDiv.classList.add('friend');
                const friendName = userId === item.userId ? item.friendName : item.userName;
                const friendEmail = userId === item.userId ? item.friendEmail : item.userEmail;
                const friendId = userId === item.userId ? item.friendId : item.userId;
                friendDiv.innerHTML = `
                    <div class="member-info">
                        <div class ="friend-name">User Name : ${friendName}</div>
                        <div class ="friend-email">User Email : ${friendEmail}</div>
                    </div>
                    <button class="add-member-btn btn ">
                        <img class="add-member-btn-img" src="/logo/plus.png" alt="plus Friend">
                    </button>`;
                const addButton = friendDiv.querySelector('.add-member-btn');

                addButton.addEventListener('click', function () {
                    friendDiv.remove() // remove from UI
                    addFriendsToWorkspace(friendId) // update in DB
                    moveMemberToWorkspaceList({ // update in UI
                        userId: friendId,
                        name: friendName,
                        email: friendEmail
                    });
                });
                document.getElementById('add-friend-member').appendChild(friendDiv);
            }
        });

    } catch (error) {
        console.error('Failed to fetch data:', error);
    }
}

async function addFriendsToWorkspace(userId) {
    try {
        const response = await fetch('/api/1.0/workspace/workspaceMembers', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({
                userId: userId,
                workspaceName: roomId
            })
        });

        const data = await response.json();

        if (!response.ok) {
            alert('Failed to add user to workspace: ' + data.error);
        } else {
            alert('User added to workspace successfully.');
            // window.location.reload();
        }


    } catch (error) {
        console.error('Error:', error);
        alert('Failed to add user to workspace.');
    }
}

async function removeMemberFromWorkspace(member) {
    try {
        const response = await fetch(`/api/1.0/workspace/workspaceMembers`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({
                userId: member.userId,
                workspaceName: roomId
            })
        });
        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.error);
        }
        // If the remove was successful, update the UI
        moveMemberToAddList(member);
    } catch (error) {
        console.error('Error:', error);
    }
}


function showWorkspaceMemberList(members) {
    const userListDiv = document.getElementById('userList');
    userListDiv.innerHTML = '';

    // Add new members to the list
    members.forEach(member => {

        const memberDiv = document.createElement('div');
        memberDiv.classList.add('member');
        memberDiv.innerHTML = `
            <div class="member-info">
                <div class="workspace-member-name">User Name : ${member.name}</div>
                <div class="workspace-member-email">User Email : ${member.email}</div>
            </div>
            <button class="remove-member-btn btn " data-memberid="${member.userId}">
                <img class="remove-member-btn-img" src="/logo/remove.png" alt="Remove Friend">
            </button>
            `;
        userListDiv.appendChild(memberDiv);

        const removeBtn = memberDiv.querySelector('.remove-member-btn');
        removeBtn.addEventListener('click', function () {
            memberDiv.remove()
            removeMemberFromWorkspace(member);
        });


    });
}


function moveMemberToAddList(member) {
    const addListDiv = document.getElementById('add-friend-member');
    const memberDiv = document.createElement('div');
    memberDiv.classList.add('member');
    memberDiv.innerHTML = `
        <div class="member-info">
            <div class="friend-name">User Name : ${member.name}</div>
            <div class="friend-email">User Email : ${member.email}</div>
        </div>
        <button class="add-member-btn btn ">
            <img class="add-member-btn-img" src="/logo/plus.png" alt="plus Friend">
        </button>
    `;
    // Add the event listener to the new button
    const addButton = memberDiv.querySelector('.add-member-btn');
    addButton.addEventListener('click', function () {
        memberDiv.remove();
        addFriendsToWorkspace(member.userId);
    });
    // Append the new div to the add list
    addListDiv.appendChild(memberDiv);
}

function moveMemberToWorkspaceList(member) {
    const workspaceListDiv = document.getElementById('userList');
    const memberDiv = document.createElement('div');
    memberDiv.classList.add('member');
    memberDiv.innerHTML = `
        <div class="member-info">
            <div class="workspace-member-name">User Name : ${member.name}</div>
            <div class="workspace-member-email">User Email : ${member.email}</div>
        </div>
        <button class="remove-member-btn btn" data-memberid="${member.userId}">
            <img class="remove-member-btn-img" src="/logo/remove.png" alt="Remove Friend">
        </button>
    `;
    // Add the event listener to the new remove button
    const removeButton = memberDiv.querySelector('.remove-member-btn');
    removeButton.addEventListener('click', function () {
        memberDiv.remove(); // remove from UI
        removeMemberFromWorkspace(member); // update in DB

    });
    // Append the new div to the workspace members list
    workspaceListDiv.appendChild(memberDiv);
}

function uuidv4() {
    return "10000000-1000-4000-8000-100000000000".replace(/[018]/g, c =>
        (+c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> +c / 4).toString(16)
    );
}

async function checkAuthentication() {
    if (!accessToken) {
        alert('No access token found. Please login.');
        redirectToLogin();
        return;
    }

    try {
        const response = await fetch(`/api/1.0/validation/workspace?workspaceName=${roomId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        });
        const data = await response.json();
        if (!response.ok) {
            alert('You are not authenticated in this workspace. Redirecting to home page.');
            redirectToLogin();
        } else {
            document.querySelector('body').classList.remove('hidden');
        }


    } catch (error) {
        console.error('Error checking authentication:', error);
        alert('Error checking authentication. Please try again.');
    }
}


function redirectToLogin() {
    window.location.href = '/admin/home';
}

function redirectToPage() {
    window.location.href = `/admin/workspace?roomId=${roomId}`;
}