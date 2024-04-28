const accessToken = localStorage.getItem('accessToken');

function redirectToPage() {
    window.location.href = "/admin/createworkspace";
}

$(document).ready(function () {
    renderUserWorkspaceList()
    fetchUserInformation()
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
                    <div class="workspace-entry">
                        <div class="workspace-name-description-container">
                            <div class="workspace-name">Workspace Name : ${workspace.workspace_name}</div>
                            <div class="workspace-description">Workspace Description : ${workspace.workspace_description}</div>
                            
                        </div>
                        <div class="workspace-name-description-btn-container">
                            <button class="btn btn-primary" onclick="joinWorkspace('${workspace.workspace_name}')">Join</button>
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
    const response = await fetch('http://localhost:8080/api/1.0/user/userPersonalInformation', {
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
<!--                <strong>Picture</strong>-->
            </div>
            <div class="user-detail"><strong>Name:</strong> ${data.name}</div>
            <div class="user-detail"><strong>Email:</strong> ${data.email}</div>
            <div class="user-detail"><strong>Account Created:</strong> ${data.AccountCreatedDate}</div>

        `;

    $('#user-information-detail').html(userInfoHTML);

}