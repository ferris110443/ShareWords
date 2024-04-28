const accessToken = localStorage.getItem('accessToken');
$(document).ready(function () {
    fetchUserInformation()
});

$(document).on('submit', 'form', async function (event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const jsonData = {};
    formData.forEach((value, key) => {
        jsonData[key] = value;
    });
    try {
        let response = await fetch("/api/1.0/workspace/workspace", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },

            body: JSON.stringify(jsonData)
        });
        let result = await response.json();
        if (response.ok) {
            alert('Workspace created successfully');
            await updateUserWorkspaceList(result.workspaceName);
            window.location.href = '/admin/workspace?roomId=' + result.workspaceName;


        } else {
            alert('Workspace creation failed');
        }
    } catch (e) {
        console.error(e);
    }
});


async function updateUserWorkspaceList(workspaceName) {
    console.log(workspaceName)
    const response = await fetch("/api/1.0/user/userWorkspace", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        },

        body: JSON.stringify(
            {
                "workspaceName": workspaceName,
                "accessToken": accessToken
            }
        )
    });
    const data = await response.json();
    if (response.ok) {
        console.log("user workspace list updated successfully");
    } else {
        console.error(data);
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
<!--                <strong>Picture</strong>-->
            </div>
            <div class="user-detail"><strong>Name:</strong> ${data.name}</div>
            <div class="user-detail"><strong>Email:</strong> ${data.email}</div>
            <div class="user-detail"><strong>Account Created:</strong> ${data.AccountCreatedDate}</div>
        `;

    $('#user-information-detail').html(userInfoHTML);

}