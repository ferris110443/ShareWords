/* eslint-env browser */

import * as Y from 'yjs'
import {WebsocketProvider} from 'y-websocket'
import {QuillBinding} from 'y-quill'
import Quill from 'quill'
import QuillCursors from 'quill-cursors'

Quill.register('modules/cursors', QuillCursors)
const ydoc = new Y.Doc()
const params = new URLSearchParams(window.location.search)
const roomId = params.get('roomId') || 'general'
const fileId = params.get('fileId') || 'general'

window.addEventListener('load', () => {


    const provider = new WebsocketProvider(
        'wss://demos.yjs.dev/ws', // use the public ws server
        // `ws${location.protocol.slice(4)}//${location.host}/ws`, // alternatively: use the local ws server (run `npm start` in root directory)
        `roomId=${roomId}fileId=${fileId}`,
        ydoc
    )
    const ytext = ydoc.getText('quill')
    const editor = new Quill('#editor', {
        modules: {
            cursors: true,
            toolbar: false,
            // toolbar: [
            //     [{header: [1, 2, 3, 4, 5, 6, false]}],
            //     ['bold', 'italic', 'underline'],
            //     ['image', 'code-block']
            // ],
            history: {
                userOnly: true
            }
        },
        placeholder: 'Start collaborating...',
        theme: 'snow' // or 'bubble'
    })

    // retrieve the markdown content from the server and render it in the editor for the first time
    fetchFileContentAndRenderMarkdown(roomId, fileId);

    async function fetchFileContentAndRenderMarkdown(roomId, fileId) {
        try {
            const response = await fetch(`http://localhost:8080/api/1.0/markdown/markdown?roomId=${encodeURIComponent(roomId)}&fileId=${encodeURIComponent(fileId)}`, {
                method: 'GET',
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            if (data.data && data.data.content) {
                ydoc.transact(() => {
                    console.log(ytext)
                    // Insert content if the document is empty
                    if (ytext && ytext._start && ytext._start.content && ytext._start.content.toString() === '') {
                        ytext.insert(0, data.data.content);
                        console.log("ytext is empty")
                    }
                });
                updatePreview(data.data.content);
            } else {
                console.log('No content available to update');
            }
        } catch (error) {
            console.error('Failed to fetch markdown content:', error);
            // Optionally, update the UI to show an error message
        }
    }


    editor.root.addEventListener('paste', function (e) {

        e.preventDefault();

        // Check for images and get the data URL
        let isImagePasted = false;
        if (e.clipboardData && e.clipboardData.items) {
            const items = e.clipboardData.items;
            for (let i = 0; i < items.length; i++) {
                if (items[i].type.indexOf('image') === 0) {
                    isImagePasted = true;
                    const blob = items[i].getAsFile();
                    const reader = new FileReader();
                    reader.onload = function (event) {
                        const base64image = event.target.result;
                        uploadImage(base64image).then(url => {
                            // const markdownImageText = `![Image](${base64image})`;
                            const markdownImageText = `![Image](${url})`;
                            const range = editor.getSelection();
                            if (range) {
                                editor.insertText(range.index, markdownImageText);
                            }
                        });
                    };
                    reader.readAsDataURL(blob);
                    break; // Once an image is found, no need to look further
                }
            }
        }

        // If no image is found in the pasted data, paste as plain text
        if (!isImagePasted) {
            const text = (e.clipboardData || window.clipboardData).getData('text/plain');
            editor.focus();
            const range = editor.getSelection();
            if (range) {
                editor.insertText(range.index, text);
            }
        }
    });
    let debounceTimeout = 500;
    let debouncedSaveData = debounce(saveText, debounceTimeout);

    editor.on('text-change', () => {
        let markdownText = editor.getText();
        console.log(markdownText)
        updatePreview(markdownText);
        debouncedSaveData(markdownText);


    });


    const binding = new QuillBinding(ytext, editor, provider.awareness)


    // Define user name and user name
    // Check the quill-cursors package on how to change the way cursors are rendered
    // provider.awareness.setLocalStateField('user', {
    //     name: 'Typing Jimmy',
    //     color: 'blue'
    // })


    const connectBtn = document.getElementById('y-connect-btn')
    connectBtn.addEventListener('click', () => {
        if (provider.shouldConnect) {
            provider.disconnect()
            connectBtn.textContent = 'Connect'
        } else {
            provider.connect()
            connectBtn.textContent = 'Disconnect'
        }
    })

    // @ts-ignore
    window.example = {provider, ydoc, ytext, binding, Y}
})


async function saveText(markdownText) {
    const response = await fetch(`http://localhost:8080/api/1.0/markdown/markdown`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "markdownText": markdownText,
            "roomId": roomId,
            "fileId": fileId,
        })
    });
    const data = await response.json();
    console.log(data);
}

function debounce(func, timeout) {
    let timer;
    return function (...args) {
        const context = this; //global window object
        clearTimeout(timer); // Clear the previous timer
        timer = setTimeout(() => {
            func.apply(context, args);
        }, timeout);
    };
}

function updatePreview(markdownText) {
    let htmlContent = marked.parse(markdownText); // Parse markdown to HTML
    document.getElementById("preview-text").innerHTML = htmlContent; // Display the HTML in the preview div
}

async function uploadImage(data) {
    try {
        const response = await fetch('http://localhost:8080/api/1.0/upload/Image', {
            method: 'POST',
            body: JSON.stringify({image: data}),
            headers: {'Content-Type': 'application/json'}
        });
        const responseData = await response.json();
        // console.log("responseData" + JSON.stringify(responseData));
        return responseData["imageURL"];
    } catch (error) {
        console.error("Error uploading image:", error);
        return null;
    }
}



