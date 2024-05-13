const accessToken = localStorage.getItem('accessToken');
const params = new URLSearchParams(window.location.search);
const workspaceName = params.get('roomId');
const queryParams = new URLSearchParams(window.location.search);
const roomId = decodeURIComponent(queryParams.get('roomId'));
const roomNumber = params.get('roomNumber');
// const coeditorURL = 'https://sharewords.org/coeditor';
const coeditorURL = 'http://localhost:8888/coeditor';

if (await checkAuthentication()) {
    renderWorkspaceFileList()
    renderWorkspaceInformation()
    getWorkspaceMember()
    getUserFriendsForAddingMembers()
}


document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('delete-workspace-btn').addEventListener('click', deleteWorkspace);
    document.getElementById('file-creation-form').addEventListener('submit', handleFormSubmission);
    document.getElementById('edit-workspace-save-btn').addEventListener('click', updateWorkspaceInformation);
    document.getElementById('cancel-workspace-save-btn').addEventListener('click', toggleEdit);
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
        roomId: roomId,
        roomNumber: roomNumber

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
            window.location.href = `${coeditorURL}/coeditor.html?roomId=${roomId}&fileId=${fileId}&roomNumber=${roomNumber}`;
        }


    } catch (error) {
        console.error('Error:', error);
        alert('Failed to create file. Please try again.');
    }
}

async function deleteWorkspace() {

    try {
        const response = await fetch(`/api/1.0/workspace/workspace?workspaceName=${roomId}&roomNumber=${roomNumber}`, {
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
    const response = await fetch(`/api/1.0/workspace/workspace?workspaceName=${workspaceName}&roomNumber=${roomNumber}`, {
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
                        <div class="file-name-title">
                            <strong>${file.fileTitle}</strong>
                            <div class="dropdown-edit">
                                <div class="toggle-dropdown-btn" onclick="toggleDropdown(event)">
                                    <svg class="bi bi-three-dots-vertical" fill="black" height="32" viewBox="0 0 16 16"
                                         width="32" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0"/>
                                    </svg>
                                </div>
                                <div class="dropdown-menu-edit" style="display: none;">
                                    <button aria-label="Edit File ${index}" class="edit-file-btn btn  " data-fileid="${file.fileId}">Edit File</button>
                                    <button aria-label="Delete File ${index}" class="delete-file-btn btn " data-fileid="${file.fileId}">Delete File</button>
                                    <button aria-label="Share File ${index}" class="share-file-btn btn " data-fileid="${file.fileId}" >Share File</button>
                                </div>
                            </div>
                        </div>
                        <hr>
                        <p class="file-name-description">${file.fileDescription}</p>
                    </div>
                    <div class="file-entry-edit" id="edit-file${index}" data-fileid="${file.fileId}" style="display: none">
                        <div class="file-name-title"><strong>New file name</strong></div>
                        <input class="file-name-title" type="text">
                        <div class="file-name-title"><strong>New file description</strong></div>
                        <textarea class="file-name-description"></textarea>
                        <button aria-label="save File ${index}" class="edit-file-save-btn btn btn-success " data-fileid="${file.fileId}">Save</button>
                        <button aria-label="cancel File ${index}" class="delete-file-cancel-btn btn btn-danger" data-fileid="${file.fileId}">Cancel</button>
                    </div>
                    
                    <div class="modal" id="shareFileModal-${file.fileId}" tabindex="-1" role="dialog" aria-labelledby="modalLabel-${file.fileId}" aria-hidden="true">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="modalLabel-${file.fileId}">Share File</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form id="shareForm-${file.fileId}" data-fileid="${file.fileId}">
                                        <div class="form-group">
                                            <label for="email-${file.fileId}">Recipient's Email:</label>
                                            <input type="email" class="form-control" id="email-${file.fileId}" required>
                                        </div>
                                        <button type="submit" id="sendMarkdownToUser" class="btn btn-primary">Send</button>
                                    </form>
                                </div>
                            </div>
                        </div>
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


    document.querySelectorAll('.share-file-btn').forEach(button => {
        button.addEventListener('click', function (event) {
            console.log("share button clicked")
            event.preventDefault();
            event.stopPropagation();
            const fileId = this.getAttribute('data-fileid');
            const modalElement = document.getElementById(`shareFileModal-${fileId}`);
            const bootstrapModal = new bootstrap.Modal(modalElement);
            bootstrapModal.show();
        });
    });


    document.querySelectorAll('[id^=shareForm-]').forEach(form => {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            e.stopPropagation();
            const fileId = form.getAttribute('data-fileid');
            const emailInput = document.querySelector(`#email-${fileId}`);
            console.log('fileId:', fileId, 'email:', emailInput.value)
            try {
                const response = await fetch('/api/1.0/mailService/markdown', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        recipientEmail: emailInput.value,
                        fileId: fileId
                    })
                });

                if (!response.ok) {
                    throw new Error('Failed to send file');
                }
                const data = await response.json();
                console.log('Success:', data);
                $(`#shareFileModal-${fileId}`).modal('hide');
            } catch (error) {
                console.error('Error:', error);
            }
        });
    });

}


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
            const editUrl = `${coeditorURL}/coeditor.html?roomId=${workspaceName}&fileId=${fileId}&roomNumber=${roomNumber}`;
            window.location.href = editUrl;

        });
    });
}


function attachEditButtonListeners() {
    const editButtons = document.querySelectorAll('.edit-file-btn');
    editButtons.forEach(button => {
        button.addEventListener('click', function (event) {
            // event.preventDefault();
            event.stopPropagation();
            const fileId = this.getAttribute('data-fileid');
            console.log('edit button clicked', fileId);
            // Show the edit div for the specific file.
            const editDiv = document.querySelector(`div[id^='edit-file'][data-fileid="${fileId}"]`);
            if (editDiv) {
                editDiv.style.display = '';
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
                fileEntryDiv.style.display = '';
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
            fileEntryDiv.style.display = '';
        });
    });

}

async function renderWorkspaceInformation() {
    const response = await fetch(`/api/1.0/workspace/workspaceInformation?workspaceName=${workspaceName}&roomNumber=${roomNumber}`, {
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
        const response = await fetch(`/api/1.0/validation/workspace?workspaceName=${roomId}&roomNumber=${roomNumber}`, {
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
    // console.log("updateWorkspaceInformation : + is clicked")
    const newName = document.getElementById('edit-name').value;
    const newDescription = document.getElementById('edit-description').value;
    const editData = {
        oldWorkspaceId: roomNumber,
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
        const newUrl = `${baseUrl}?roomId=${encodeURIComponent(newName)}&roomNumber=${roomNumber}`;
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
        infoDiv.style.display = '';
        formDiv.style.display = 'none';
    } else {
        infoDiv.style.display = 'none';
        formDiv.style.display = '';
    }
}


// ================= friend list =================

async function getWorkspaceMember() {
    try {
        const response = await fetch(`/api/1.0/workspace/workspaceMembers?workspaceName=${roomId}&roomNumber=${roomNumber}`, {
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
        const members = data.data;
        showWorkspaceMemberList(members);

    } catch (error) {
        console.error('Failed to update user:', error);
    }
}

function showWorkspaceMemberList(members) {
    const userListDiv = document.getElementById('userList');
    userListDiv.innerHTML = '';
    members.forEach(member => {
        const memberDiv = createMemberDiv(member, true);
        userListDiv.appendChild(memberDiv);

        const removeBtn = memberDiv.querySelector('.remove-member-btn');
        addRemoveMemberListener(removeBtn, memberDiv, 'remove');
        // removeBtn.addEventListener('click', function () {
        //     memberDiv.remove()
        //     const userId = this.closest('.member').querySelector('.workspace-member-img').getAttribute('data-user-id');
        //     console.log("userId:" + userId)
        //     removeMemberFromWorkspace(userId, memberDiv);
        //
        //
        // });
    });
}

function createMemberDiv(member, isWorkspaceMember = true) {
    const memberDiv = document.createElement('div');
    memberDiv.classList.add(isWorkspaceMember ? 'member' : 'member');
    memberDiv.innerHTML = `
        <div class="workspace-member-info">
            <img class="workspace-member-img" data-user-id = ${member.userId} src=${member.userImageUrl} alt="User Image" width="50px" height="50px">
            <div class="member-info">
                <div class="workspace-member-name">User Name : ${member.name}</div>
                <div class="workspace-member-email">User Email : ${member.email}</div>
            </div>
        </div>
        <button class="${isWorkspaceMember ? 'remove-member-btn' : 'add-member-btn'} btn">
            <img class="${isWorkspaceMember ? 'remove-member-btn-img' : 'add-member-btn-img'}" src="/logo/${isWorkspaceMember ? 'remove.png' : 'plus.png'}" alt="${isWorkspaceMember ? 'Remove Friend' : 'Add Friend'}">
        </button>
    `;
    return memberDiv;
}


function createMemberDivForAddFriend(friendName, friendEmail, friendId, friendImageUrl, isWorkspaceMember = false) {
    const memberDiv = document.createElement('div');
    memberDiv.classList.add(isWorkspaceMember ? 'member' : 'member');
    memberDiv.innerHTML = `
        <div class="workspace-member-info">
            <img class="workspace-member-img" data-user-id = ${friendId} src=${friendImageUrl} alt="User Image" width="50px" height="50px">
            <div class="member-info">
                <div class="workspace-member-name">User Name : ${friendName}</div>
                <div class="workspace-member-email">User Email : ${friendEmail}</div>
            </div>
        </div>
        <button class="${isWorkspaceMember ? 'remove-member-btn' : 'add-member-btn'} btn">
            <img class="${isWorkspaceMember ? 'remove-member-btn-img' : 'add-member-btn-img'}" src="/logo/${isWorkspaceMember ? 'remove.png' : 'plus.png'}" alt="${isWorkspaceMember ? 'Remove Friend' : 'Add Friend'}">
        </button>
    `;
    return memberDiv;
}


async function getUserFriendsForAddingMembers() {
    try {
        // Fetch current workspace members
        const membersResponse = await fetch(`/api/1.0/workspace/workspaceMembers?workspaceName=${roomId}&roomNumber=${roomNumber}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
        });

        if (!membersResponse.ok) {
            throw new Error(`HTTP error! status: ${membersResponse.status}`);
        }
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
        const userId = friendsData.userId; // current user id
        const memberDivContainer = document.getElementById('add-friend-member');
        memberDivContainer.innerHTML = ''; // Clear the container once

        // Filter and display friends not in the current workspace
        userFriendsInformation.forEach(item => {
            const friendStatusAccepted = item.status === 'accepted';
            const isCurrentUser = userId === item.userId;
            const friendId = isCurrentUser ? item.friendId : item.userId;

            if (friendStatusAccepted && !currentMemberIds.has(friendId)) {
                const friendName = isCurrentUser ? item.friendName : item.userName;
                const friendEmail = isCurrentUser ? item.friendEmail : item.userEmail;
                const friendImageUrl = isCurrentUser ? item.friendImageUrl : item.userImageUrl;

                const memberDiv = createMemberDivForAddFriend(friendName, friendEmail, friendId, friendImageUrl, false);
                memberDivContainer.appendChild(memberDiv);

                const addBtn = memberDiv.querySelector('.add-member-btn');
                addRemoveMemberListener(addBtn, memberDiv, 'add');

            }
        });

    } catch (error) {
        console.error('Failed to fetch data:', error);
    }
}


async function addFriendsToWorkspace(userId, memberDiv) {
    try {
        console.log(userId, memberDiv)
        const response = await fetch('/api/1.0/workspace/workspaceMembers', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({
                userId: userId,
                workspaceName: roomId,
                roomNumber: roomNumber
            })
        });

        const data = await response.json();
        console.log(response.status, data)
        if (!response.ok) {
            alert.log('Failed to add user to workspace: ' + data.error);
        } else {
            alert('User added to workspace successfully.');
            // window.location.reload();
        }
        moveMemberToWorkspaceList(memberDiv);
        socket.emit('addMemberRequest', {userId: userId, roomNumber: roomNumber, accessToken: accessToken});


    } catch (error) {
        console.error('Error:', error);
        alert('Failed to add user to workspace.');
    }
}

async function removeMemberFromWorkspace(userId, memberDiv) {
    try {
        const response = await fetch(`/api/1.0/workspace/workspaceMembers`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({
                userId: userId,
                workspaceName: roomId,
                roomNumber: roomNumber
            })
        });
        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.error);
        }

        moveMemberToAddList(memberDiv);
    } catch (error) {
        console.error('Error:', error);
    }
}


function moveMemberToAddList(memberDiv) {
    const addListDiv = document.getElementById('add-friend-member');
    const addButton = memberDiv.querySelector('.remove-member-btn');

    // Update classes and attributes
    memberDiv.classList.add('member');
    addButton.classList.add('add-member-btn');
    addButton.classList.remove('remove-member-btn');
    const addButtonImage = addButton.querySelector('img');
    addButtonImage.src = '/logo/plus.png';
    addButtonImage.alt = 'Add Friend';
    addButtonImage.classList.add('add-member-btn-img');
    addButtonImage.classList.remove('remove-member-btn-img');
    const userId = memberDiv.querySelector('.workspace-member-img').getAttribute('data-user-id');

    const addBtn = memberDiv.querySelector('.add-member-btn');
    addRemoveMemberListener(addBtn, memberDiv, 'add');

    console.log("userId:" + userId, memberDiv)
    addListDiv.appendChild(memberDiv);
}

function moveMemberToWorkspaceList(memberDiv) {
    const workspaceListDiv = document.getElementById('userList');
    memberDiv.classList.add('member');

    // Find the button within the memberDiv and update its classes
    const addButton = memberDiv.querySelector('.add-member-btn');
    if (addButton) {
        addButton.classList.add('remove-member-btn');
        addButton.classList.remove('add-member-btn');
    }

    // Find the image within the button and update its attributes
    const addButtonImg = memberDiv.querySelector('.add-member-btn-img');
    if (addButtonImg) {
        addButtonImg.src = '/logo/remove.png';
        addButtonImg.alt = 'Remove Friend';
        addButtonImg.classList.add('remove-member-btn-img');
        addButtonImg.classList.remove('add-member-btn-img');
    }

    // Append the modified memberDiv to the workspace list
    workspaceListDiv.appendChild(memberDiv);

    // Attach event listener directly to the remove button in the modified memberDiv
    const removeButton = memberDiv.querySelector('.remove-member-btn');
    if (removeButton) {
        // removeButton.addEventListener('click', function () {
        //     const userId = this.closest('.member').querySelector('.workspace-member-img').getAttribute('data-user-id');
        //     removeMemberFromWorkspace(userId, memberDiv);
        //     memberDiv.remove();
        // });
        addRemoveMemberListener(removeButton, memberDiv, 'remove');
    }
}


function addRemoveMemberListener(button, memberDiv, action) {
    const userId = memberDiv.querySelector('.workspace-member-img').getAttribute('data-user-id');

    // Removing any existing event listeners
    button.removeEventListener('click', handleRemoveMemberClick);
    button.removeEventListener('click', handleAddMemberClick);

    // Adding the appropriate event listener
    if (action === 'add') {
        button.addEventListener('click', handleAddMemberClick);
    } else if (action === 'remove') {
        button.addEventListener('click', handleRemoveMemberClick);
    }
}

function handleAddMemberClick() {
    const memberDiv = this.closest('.member');
    const userId = memberDiv.querySelector('.workspace-member-img').getAttribute('data-user-id');
    addFriendsToWorkspace(userId, memberDiv);
    memberDiv.remove();
}

function handleRemoveMemberClick() {
    const memberDiv = this.closest('.member');
    const userId = memberDiv.querySelector('.workspace-member-img').getAttribute('data-user-id');
    removeMemberFromWorkspace(userId, memberDiv);
    memberDiv.remove();
}