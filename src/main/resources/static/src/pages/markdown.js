// const params = new URLSearchParams(window.location.search);
// const fileId = params.get('fileId');
// const roomId = params.get('roomId') || 'general';
// let editor = ace.edit("editor");
// let debounceTimeout = 10000;
// let fileList = '';
//
//
// // =================== Load Markdown Text ===================
// document.addEventListener('DOMContentLoaded', function () {
//     fetchCurrentWorkspaceFiles(roomId, fileId)
//     // fetchFileContentAndRenderMarkdown(roomId, fileId)
// });
//
// async function fetchCurrentWorkspaceFiles(roomId, fileId) {
//     const response = await fetch(`/api/1.0/markdown/workspaceFiles?roomId=${roomId}&fileId=${fileId}`, {
//         method: 'GET',
//     });
//     const data = await response.json();
//     data.data.forEach(file => {
//         fileList += `
//         <div id="${file.fileId}">
//             ${file.fileTitle}
//             <button class="edit" data-fileid="${file.fileId}">Edit</button>
//             <button class="delete" data-fileid="${file.fileId}">Delete</button>
//         </div>
//         `
//     });
//
//     $("#FileList").html(fileList);
//
// }
//
// async function fetchFileContentAndRenderMarkdown(roomId, fileId) {
//     const response = await fetch(`/api/1.0/markdown/markdown?roomId=${roomId}&fileId=${fileId}`, {
//         method: 'GET',
//     });
//     const data = await response.json();
//     console.log(data.data);
//     if (data.data) {
//         currentContent = data.data.content;
//         editor.setValue(data.data.content, 1); // 1 is the starting position cursor
//         updatePreview();
//     }
//
// }
//
//
// // =================== Load Markdown Text ===================
//
// let debouncedSaveData = debounce(saveDataToServer, debounceTimeout);
//
// editor.getSession().on('change', function () {
//     let currentContent = editor.getValue();
//     let title = document.getElementById("fileName").value;
//     console.log('currentContent', currentContent);
//     console.log('title', title);
//     debouncedSaveData(currentContent, fileId, roomId, title);
//     updatePreview();
// });
//
//
// function debounce(func, timeout) {
//     let timer;
//     return function (...args) {
//         const context = this; //global window object
//         clearTimeout(timer); // Clear the previous timer
//         timer = setTimeout(() => {
//             func.apply(context, args);
//         }, timeout);
//     };
// }
//
// function updatePreview() {
//     let markdownText = editor.getValue(); // Get the text from Ace Editor
//     let htmlContent = marked.parse(markdownText); // Parse markdown to HTML
//     document.getElementById("preview").innerHTML = htmlContent; // Display the HTML in the preview div
// }
//
// async function saveDataToServer(currentContent, fileId, roomId, title) {
//     const response = await fetch('/api/1.0/markdown/markdown', {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify({markdownText: currentContent, fileId: fileId, roomId: roomId, title: title})
//     });
//     console.log(response);
// }
//
//
// // =================== Image Upload in markdown ===================
// document.getElementById('editor').addEventListener('paste', function (event) {
//     let clipboardData = event.clipboardData || window.clipboardData;
//     if (clipboardData && clipboardData.items) { //clipboardData.items is a list of all the items that were copied (text,image, etc.
//         for (let i = 0; i < clipboardData.items.length; i++) {
//             let item = clipboardData.items[i];
//             console.log('Item type: ', item.type);
//             console.log('Item type indexOf ', item.type.indexOf('image'));
//             if (item.type.indexOf('image') === 0) {  // Check if the item is an image
//                 let blob = item.getAsFile();
//                 let reader = new FileReader();
//                 reader.onload = function (e) {
//                     // console.log('File content: ', e.target.result);
//                     uploadImage(e.target.result).then(url => {
//                         insertImageMarkdown(url);
//                     });
//                 };
//                 reader.readAsDataURL(blob);
//                 event.preventDefault(); // Prevent the default behavior of pasting
//             }
//         }
//     }
// });
//
//
// function insertImageMarkdown(url) {
//     const markdownText = `![Image](${url})`;
//     editor.insert(markdownText);
// }
//
// async function uploadImage(data) {
//     try {
//         const response = await fetch('api/1.0/upload/Image', {
//             method: 'POST',
//             body: JSON.stringify({image: data}),
//             headers: {'Content-Type': 'application/json'}
//         });
//         const responseData = await response.json();
//         // console.log("responseData" + JSON.stringify(responseData));
//         return responseData["imageURL"];
//     } catch (error) {
//         console.error("Error uploading image:", error);
//         return null;
//     }
// }
//
//
//
