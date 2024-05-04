const accessToken = localStorage.getItem('accessToken');

function redirectToPage() {
    window.location.href = "/admin/createworkspace";
}

$(document).ready(async function () {
    if (await checkAuthentication()) {
        renderUserWorkspaceList();
        fetchUserInformation();

    }
});

async function renderUserWorkspaceList() {
    let userWorkspaceHTML = "";
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
                    <div class="workspace-entry" onclick="joinWorkspace('${workspace.workspace_name}')">
                        <div class="workspace-name-description-container">
                            <div class="workspace-name">Workspace Name: ${workspace.workspace_name}</div>
                            <div class="workspace-description">Workspace Description: ${workspace.workspace_description}</div>
                        </div>
                        <div class="workspace-name-description-btn-container">
                            <!-- Removed the join button -->
                            <button class="btn btn-danger" onclick="deleteWorkspaceFromUserWorkspace('${workspace.workspace_name}', event)">Delete</button>
                        </div>
                    </div>
                `;
        });
        $('#workspaceList').html(userWorkspaceHTML);
    } catch (error) {
        console.error('Error fetching user workspaces:', error);
        // Handle errors appropriately in your application context
    }
}

function joinWorkspace(workspaceName) {
    window.location.href = `/admin/workspace?roomId=${workspaceName}`
}

async function deleteWorkspaceFromUserWorkspace(workspaceName, event) {
    event.stopPropagation(); // avoid triggering the joinWorkspace event
    console.log("Attempting to delete workspace:", workspaceName);
    try {
        const response = await fetch('/api/1.0/workspace/workspaceUserSelfRemove', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({workspaceName})
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

    let userInfoHTML = `

            <div class="user-detail">
                <img src="${data.picture}" id="user-picture" alt="User Picture" >
                <input type="file" id="fileInput" name="file" style="display: none;" onchange="uploadFile()">
            </div>
            <div class="user-detail"><strong>Name:</strong> ${data.name}</div>
            <div class="user-detail"><strong>Email:</strong> ${data.email}</div>
            <div class="user-detail"><strong>Account Created:</strong> ${data.AccountCreatedDate}</div>

        `;

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