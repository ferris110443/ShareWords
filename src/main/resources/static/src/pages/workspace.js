const accessToken = localStorage.getItem('accessToken');
const params = new URLSearchParams(window.location.search);
const workspaceName = params.get('roomId');
const queryParams = new URLSearchParams(window.location.search);
const roomId = decodeURIComponent(queryParams.get('roomId'));
// const coeditorURL = 'https://sharewords.org/coeditor';
const coeditorURL = 'http://localhost:8888/coeditor';


document.addEventListener('DOMContentLoaded', function () {
    checkAuthentication()

    document.getElementById('delete-workspace-btn').addEventListener('click', deleteWorkspace);
    document.getElementById('file-creation-form').addEventListener('submit', handleFormSubmission);
    document.getElementById('edit-workspace-save-btn').addEventListener('click', updateWorkspaceInformation);
    document.getElementById('cancel-workspace-save-btn').addEventListener('click', toggleEdit);

    renderWorkspaceFileList()
    renderWorkspaceInformation()
    getWorkspaceMember()
    getUserFriendsForAddingMembers()
});

async function handleFormSubmission(event) {
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

    try {
        const response = await fetch('/api/1.0/workspace/file', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify(data)
        });

        const result = await response.json();
        console.log(response.status)
        if (response.status === 400) {
            alert(result.error)
        } else if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        } else {
            console.log('Success:', result);
            alert('File created successfully!');
            window.location.href = `${coeditorURL}/coeditor.html?roomId=${roomId}&fileId=${fileId}`;
        }


    } catch (error) {
        console.error('Error:', error);
        alert('Failed to create file. Please try again.');
    }
}

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
                    <div class="file-entry" id="file${index}" data-fileid="${file.fileId}">
                        <p class="file-name-title"><strong>${file.fileTitle}</strong></p>
                        <p class="file-name-description">${file.fileDescription}</p>
                        <button aria-label="Edit File ${index}" class="edit-file-btn btn btn-primary " data-fileid="${file.fileId}">Edit</button>
                        <button aria-label="Delete File ${index}" class="delete-file-btn btn btn-danger" data-fileid="${file.fileId}">Delete</button>
                    </div>
                    <div class="file-entry-edit" id="edit-file${index}" data-fileid="${file.fileId}" style="display: none">
                        <div class="file-name-title"><strong>New file name</strong></div>
                        <input class="file-name-title" type="text">
                        <div class="file-name-title"><strong>New file description</strong></div>
                        <textarea class="file-name-description"></textarea>
                        <button aria-label="save File ${index}" class="edit-file-save-btn btn btn-success " data-fileid="${file.fileId}">Save</button>
                        <button aria-label="cancel File ${index}" class="delete-file-cancel-btn btn btn-danger" data-fileid="${file.fileId}">Cancel</button>
                    </div>
                    
                `;
        });
        document.getElementById("fileList").innerHTML += html;

    } else {
        console.error('No files in workspace', data.data);
    }


    attachEnterWorkspaceBtn()
    attachDeleteButtonListeners()
    attachEditButtonListeners()


    function attachDeleteButtonListeners() {

        const deleteButtons = document.querySelectorAll('.delete-file-btn');
        deleteButtons.forEach(button => {
            button.addEventListener('click', async function (event) {
                event.preventDefault();
                event.stopPropagation(); // prevent the event from bubbling up or it will trigger the parent click event and direct to coeditor page
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

    function attachEnterWorkspaceBtn() {
        const fileEntries = document.querySelectorAll('.file-entry');
        fileEntries.forEach(entry => {
            entry.addEventListener('click', function (event) {
                event.stopPropagation();
                const fileId = entry.getAttribute('data-fileid');
                const editUrl = `${coeditorURL}/coeditor.html?roomId=${workspaceName}&fileId=${fileId}`;
                window.location.href = editUrl;

            });
        });
    }


    function attachEditButtonListeners() {
        const editButtons = document.querySelectorAll('.edit-file-btn');
        editButtons.forEach(button => {
            button.addEventListener('click', function (event) {
                event.preventDefault();
                event.stopPropagation();
                const fileId = this.getAttribute('data-fileid');
                console.log('edit button clicked', fileId);
                // Show the edit div for the specific file.
                const editDiv = document.querySelector(`div[id^='edit-file'][data-fileid="${fileId}"]`);
                if (editDiv) {
                    editDiv.style.display = 'block';
                }

                // Hide the file entry div for the specific file.
                const fileEntryDiv = document.querySelector(`div[id^='file'][data-fileid="${fileId}"]`);
                if (fileEntryDiv) {
                    fileEntryDiv.style.display = 'none';
                }
            });
        });
        const saveButtons = document.querySelectorAll('.edit-file-save-btn');
        saveButtons.forEach(button => {
            button.addEventListener('click', async function (event) {
                event.preventDefault();
                event.stopPropagation();
                const fileId = this.getAttribute('data-fileid');
                const editDiv = document.querySelector(`div[id^='edit-file'][data-fileid="${fileId}"]`);
                const newName = editDiv.querySelector('input.file-name-title').value;
                const newDescription = editDiv.querySelector('textarea.file-name-description').value;

                const response = await fetch(`/api/1.0/markdown/markdownInfo`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${accessToken}`
                    },
                    body: JSON.stringify({fileTitle: newName, fileDescription: newDescription, fileId: fileId})
                });

                const data = await response.json();
                if (response.ok) {
                    alert('File updated successfully');
                    const fileEntryDiv = document.querySelector(`div[id^='file'][data-fileid="${fileId}"]`);
                    fileEntryDiv.querySelector('.file-name-title strong').textContent = newName;
                    fileEntryDiv.querySelector('.file-name-description').textContent = newDescription;
                    editDiv.style.display = 'none';
                    fileEntryDiv.style.display = 'block';
                } else {
                    alert('Failed to update file: ' + data.error);
                }
            });
        });

        const cancelButtons = document.querySelectorAll('.delete-file-cancel-btn');
        cancelButtons.forEach(button => {
            button.addEventListener('click', function (event) {
                event.preventDefault();
                event.stopPropagation();
                const fileId = this.getAttribute('data-fileid');
                const editDiv = document.querySelector(`div[id^='edit-file'][data-fileid="${fileId}"]`);
                editDiv.style.display = 'none';

                const fileEntryDiv = document.querySelector(`div[id^='file'][data-fileid="${fileId}"]`);
                fileEntryDiv.style.display = 'block';
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
    // console.log("workspaceInformation : ", workspaceInformation)
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
        // console.log(data.data)
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

        // console.log(userFriendsInformation);
        // console.log(userId);

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
                    friendDiv.remove()
                    addFriendsToWorkspace(friendId)
                    moveMemberToWorkspaceList({
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

async function updateWorkspaceInformation() {
    const newName = document.getElementById('edit-name').value;
    const newDescription = document.getElementById('edit-description').value;
    const editData = {
        oldWorkspaceName: roomId,
        newWorkspaceName: newName,
        newWorkspaceNameDescription: newDescription,
    };

    try {
        const response = await fetch("/api/1.0/workspace/file", {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + accessToken
            },
            body: JSON.stringify(editData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json(); // Assuming the server responds with JSON-formatted data
        console.log('Update successful:', data);

        // Update UI with the new data
        document.getElementById('workspace-name').textContent = newName;
        document.getElementById('workspace-description').textContent = newDescription;

        toggleEdit();

        const baseUrl = window.location.href.split('?')[0];
        const newUrl = `${baseUrl}?roomId=${encodeURIComponent(newName)}`;
        window.history.pushState({path: newUrl}, '', newUrl);


    } catch (error) {
        console.error('Failed to update workspace:', error);
    }
}

function redirectToLogin() {
    window.location.href = '/admin/home';
}


function toggleEdit() {
    const infoDiv = document.getElementById('workspace-information');
    const formDiv = document.getElementById('edit-workspace-form');
    const editWorkspaceNameText = document.getElementById('edit-name');
    const editWorkspaceDescriptionText = document.getElementById('edit-description');
    editWorkspaceNameText.value = "";
    editWorkspaceDescriptionText.value = "";
    if (infoDiv.style.display === 'none') {
        infoDiv.style.display = 'block';
        formDiv.style.display = 'none';
    } else {
        infoDiv.style.display = 'none';
        formDiv.style.display = 'block';
    }
}

