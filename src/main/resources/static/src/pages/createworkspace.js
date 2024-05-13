// const accessToken = localStorage.getItem('accessToken');
// $(document).ready(function () {
//     checkAuthentication()
//     fetchUserInformation()
// });
//
// $(document).on('submit', 'form', async function (event) {
//     event.preventDefault();
//     const formData = new FormData(event.target);
//     const jsonData = {};
//     formData.forEach((value, key) => {
//         jsonData[key] = value;
//     });
//     try {
//         let response = await fetch("/api/1.0/workspace/workspace", {
//             method: "POST",
//             headers: {
//                 'Content-Type': 'application/json',
//                 'Authorization': `Bearer ${accessToken}`
//             },
//
//             body: JSON.stringify(jsonData)
//         });
//         let result = await response.json();
//         if (response.ok) {
//             alert('Workspace created successfully');
//             await updateUserWorkspaceList(result.workspaceName);
//             window.location.href = '/admin/workspace?roomId=' + result.workspaceName;
//
//
//         } else {
//             alert('Workspace creation failed');
//         }
//     } catch (e) {
//         console.error(e);
//     }
// });
//
//
// async function updateUserWorkspaceList(workspaceName) {
//     console.log(workspaceName)
//     const response = await fetch("/api/1.0/user/userWorkspace", {
//         method: "POST",
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': `Bearer ${accessToken}`
//         },
//
//         body: JSON.stringify(
//             {
//                 "workspaceName": workspaceName,
//                 "accessToken": accessToken
//             }
//         )
//     });
//     const data = await response.json();
//     if (response.ok) {
//         console.log("user workspace list updated successfully");
//     } else {
//         console.error(data);
//     }
//
// }
//
// async function fetchUserInformation() {
//     const response = await fetch('/api/1.0/user/userPersonalInformation', {
//         method: 'GET',
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': `Bearer ${accessToken}`
//         }
//     });
//     if (!response.ok) throw new Error('Failed to fetch user information');
//     const data = await response.json();
//     console.log(data);
//
//     let userInfoHTML = `
//
//             <div class="user-detail">
//                 <img src="${data.picture}" id="user-picture" alt="User Picture" >
//                 <input type="file" id="fileInput" name="file" style="display: none;" onchange="uploadFile()">
//             </div>
//             <div class="user-detail"><strong>Name:</strong> ${data.name}</div>
//             <div class="user-detail"><strong>Email:</strong> ${data.email}</div>
//             <div class="user-detail"><strong>Account Created:</strong> ${data.AccountCreatedDate}</div>
//         `;
//
//     $('#user-information-detail').html(userInfoHTML);
//     document.getElementById('user-picture').addEventListener('click', function () {
//         document.getElementById('fileInput').click();
//         console.log('File input clicked')
//     });
// }
//
// async function checkAuthentication() {
//     if (!accessToken) {
//         alert('No access token found. Please login.');
//         redirectToLogin();
//         return;
//     }
//
//     try {
//         const response = await fetch(`/api/1.0/validation/user`, {
//             method: 'GET',
//             headers: {
//                 'Authorization': `Bearer ${accessToken}`
//             }
//         });
//         if (!response.ok) {
//             alert('You are not authenticated. Redirecting to login page.');
//             redirectToLogin();
//             return false;
//         }
//         return true;
//     } catch (error) {
//         console.error('Error checking authentication:', error);
//         alert('Error checking authentication. Please try again.');
//         redirectToLogin();
//         return false;
//     }
// }
//
//
// async function uploadFile() {
//     const fileInput = document.getElementById('fileInput');
//     if (fileInput.files.length === 0) {
//         alert('No file selected!');
//         return;
//     }
//     const formData = new FormData();
//     formData.append('file', fileInput.files[0]);
//
//     try {
//         const response = await fetch('/api/1.0/upload/userImage', {
//             method: 'POST',
//             headers: {
//                 "Authorization": `Bearer ${accessToken}`
//             },
//             body: formData,
//         });
//         if (!response.ok) {
//             throw new Error('Upload failed: ' + response.statusText);
//         }
//         const data = await response.json();
//         console.log(data);
//         alert(data.message);  // Display the server response message
//     } catch (error) {
//         console.error('Error:', error);
//         alert('Error uploading file: ' + error.message);
//     }
// }
//
//
// function redirectToLogin() {
//     window.location.href = '/index.html';
// }