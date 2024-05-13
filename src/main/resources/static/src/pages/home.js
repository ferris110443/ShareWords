// const accessToken = localStorage.getItem('accessToken');

checkAuthentication()
renderUserWorkspaceList();
fetchUserInformation();

document.addEventListener('DOMContentLoaded', async function () {
    const form = document.getElementById('create-new-workspace-form');

    if (form) {
        form.addEventListener('submit', async function (event) {
            event.preventDefault();
            const formData = new FormData(form);
            const jsonData = {};
            formData.forEach((value, key) => {
                jsonData[key] = value;
            });

            try {
                const response = await fetch("/api/1.0/workspace/workspace", {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${accessToken}`
                    },
                    body: JSON.stringify(jsonData)
                });

                const result = await response.json();
                if (response.ok) {
                    alert('Workspace created successfully');
                    await updateUserWorkspaceList(result.workspaceName, result.roomNumber);
                    window.location.href = '/admin/workspace?roomId=' + encodeURIComponent(result.workspaceName) + '&roomNumber=' + result.roomNumber;
                } else {
                    alert('Workspace creation failed');
                }
            } catch (e) {
                console.error(e);
                alert('Error creating workspace. Please try again.');
            }
        });
    }
});


async function renderUserWorkspaceList() {
    let userWorkspaceHTML = "";
    let userWorkspaceHTMLList = "";
    try {
        const response = await fetch('/api/1.0/workspace/userWorkspaceDetails', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        });
        if (!response.ok) throw new Error('Failed to fetch workspace details');
        const data = await response.json();
        data.data.forEach(workspace => {
            userWorkspaceHTML += `
                    <div class="workspace-entry" onclick="joinWorkspace('${workspace.workspace_name}','${workspace.workspace_id}')">
                        <div class="workspace-name-description-container">
                            <div>
                                <div class="workspace-name" data-workspace-id="${workspace.workspace_id}" >${workspace.workspace_name}</div>
                                <hr>
                                <div class="workspace-description">${workspace.workspace_description}</div>
                            </div>
                            <div class="workspace-owner">Workspace Owner : ${workspace.workspace_owner}</div>
                        </div>
                        <div class="workspace-name-description-btn-container">
<!--                            <button class="btn " onclick="deleteWorkspaceFromUserWorkspace('${workspace.workspace_name}', event)">-->
<!--                                <img src="../../logo/remove.png"  style="width: 36px; height: 36px;"">-->
<!--                            </button>-->
                            <div class="dropdown-edit">
                                <div class="toggle-dropdown-btn" onclick="toggleDropdown(event)">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="bi bi-three-dots-vertical" viewBox="0 0 16 16">
                                        <path d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0"/>
                                    </svg>
                                </div>
                                <div class="dropdown-menu-edit" style="display: none;">
                                    <button class="btn enter-workspace-btn" onclick="joinWorkspace('${workspace.workspace_name}')">
                                        Enter workspace
                                    </button>
                                    <button class="btn leave-workspace-btn" onclick="deleteWorkspaceFromUserWorkspace('${workspace.workspace_name}', '${workspace.workspace_id}',event)">
                                        Delete workspace
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            userWorkspaceHTMLList += `
<!--                    <tr class="workspace-entry-list" onclick="joinWorkspace('${workspace.workspace_name}')">-->
                    <tr class="workspace-entry-list" >
                        <td class = "workspace-name-td">${workspace.workspace_name}</td>
                        <td class = "workspace-description-td">
                            <div class = "workspace-description-div">
                                ${workspace.workspace_description}</td>
                            </div>
                        <td class = "workspace-owner-td">${workspace.workspace_owner}</td>
                        <td class="workspace-btn-td">
                            <div class="dropdown-edit">
                                <div class="toggle-dropdown-btn-edit" onclick="toggleDropdown(event)">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="bi bi-three-dots-vertical" viewBox="0 0 16 16">
                                        <path d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0"/>
                                    </svg>
                                </div>
                                <div class="dropdown-menu-edit" style="display: none;">
                                    <button class="btn enter-workspace-btn" onclick="joinWorkspace('${workspace.workspace_name}','${workspace.workspace_id}')">
                                        Enter workspace
                                    </button>
                                    <button class="btn leave-workspace-btn" onclick="deleteWorkspaceFromUserWorkspace('${workspace.workspace_name}', '${workspace.workspace_id}',event)">
                                        Delete workspace
                                    </button>
                                </div>
                            </div>
                        </td>
                    </tr>
                `;
        });
        $('.workspaceList-grid').html(userWorkspaceHTML);
        $('#workspaceList-list-tbody').html(userWorkspaceHTMLList);


    } catch (error) {
        console.error('Error fetching user workspaces:', error);
    }
}

function joinWorkspace(workspaceName, roomNumber) {
    window.location.href = `/admin/workspace?roomId=${workspaceName}+&roomNumber=${roomNumber}`;
}

async function deleteWorkspaceFromUserWorkspace(workspaceName, roomNumber, event) {
    event.stopPropagation(); // avoid triggering the joinWorkspace event
    console.log("Attempting to delete workspace:", workspaceName);
    console.log("Attempting to delete roomNumber:", roomNumber);
    try {
        const response = await fetch('/api/1.0/workspace/workspaceUserSelfRemove', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({
                "workspaceName": workspaceName,
                "roomNumber": roomNumber
            })
        });

        if (!response.ok) throw new Error('Failed to delete workspace');

        console.log("Workspace successfully deleted.");
        const data = await response.json();
        console.log(data);
        if (event) {
            const workspaceEntry = event.target.closest('.workspace-entry');
            if (workspaceEntry) {
                workspaceEntry.remove(); // Remove the workspace entry element
            }
        }

        if (event) {
            const workspaceEntry = event.target.closest('.workspace-entry-list');
            if (workspaceEntry) {
                workspaceEntry.remove(); // Remove the workspace entry element
            }
        }


    } catch (error) {
        console.error("Error during workspace deletion:", error.message);
    }
}

async function fetchUserInformation() {
    const response = await fetch('/api/1.0/user/userPersonalInformation', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        }
    });
    if (!response.ok) throw new Error('Failed to fetch user information');
    const data = await response.json();
    console.log(data);
    const date = data.AccountCreatedDate.substring(0, 10);

    let userInfoHTML = `

            <div class="user-detail">
                <img src="${data.picture}" id="user-picture" alt="User Picture" >
                <input type="file" id="fileInput" name="file" style="display: none;" onchange="uploadFile()">
            </div>
            <div id="user-detail-container">
                <div class="user-detail">Name : <span id="user-info-name">${data.name}</span></div>
                <div class="user-detail">Email : <span id="user-info-email">${data.email}</span></div>
                <div class="user-detail">Account Created : ${date} </div>
            </div>

        `;
    $('#rounded-circle').attr('src', data.picture);

    $('#user-information-detail').html(userInfoHTML);

    document.getElementById('user-picture').addEventListener('click', function () {
        document.getElementById('fileInput').click();
        console.log('File input clicked')
    });

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

async function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    if (fileInput.files.length === 0) {
        alert('No file selected!');
        return;
    }
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    try {
        const response = await fetch('/api/1.0/upload/userImage', {
            method: 'POST',
            headers: {
                "Authorization": `Bearer ${accessToken}`
            },
            body: formData,
        });
        if (!response.ok) {
            throw new Error('Upload failed: ' + response.statusText);
        }
        const data = await response.json();
        console.log(data);
        alert(data.message);  // Display the server response message
    } catch (error) {
        console.error('Error:', error);
        alert('Error uploading file: ' + error.message);
    }
}


function redirectToLogin() {
    window.location.href = '/index.html';
}


async function updateUserWorkspaceList(workspaceName, roomNumber) {
    console.log(workspaceName);
    const response = await fetch("/api/1.0/user/userWorkspace", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        },
        body: JSON.stringify({
            "workspaceName": workspaceName,
            "accessToken": accessToken,
            "roomNumber": roomNumber
        })
    });

    const data = await response.json();
    if (response.ok) {
        console.log("User workspace list updated successfully");
    } else {
        console.error("Failed to update user workspace list:", data);
    }
}


function toggleDropdown(event) {
    event.stopPropagation();
    let dropdown = event.currentTarget.parentNode.querySelector('.dropdown-menu-edit');
    const isVisible = dropdown.style.display === 'block';
    document.querySelectorAll('.dropdown-menu-edit').forEach(function (m) {
        m.style.display = 'none';
    });
    dropdown.style.display = isVisible ? 'none' : 'block';
}

window.onclick = function (event) {
    if (!event.target.closest('.dropdown-edit')) {
        document.querySelectorAll('.dropdown-menu-edit').forEach(function (menu) {
            menu.style.display = 'none';
        });
    }
};
