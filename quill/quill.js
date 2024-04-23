/* eslint-env browser */

import * as Y from 'yjs'
import {WebsocketProvider} from 'y-websocket'
import {QuillBinding} from 'y-quill'
import Quill from 'quill'
import QuillCursors from 'quill-cursors'

Quill.register('modules/cursors', QuillCursors)
const ydoc = new Y.Doc()
// const roomName = "testing";
const params = new URLSearchParams(window.location.search)
const roomId = params.get('roomId') || 'general'
const fileId = params.get('fileId') || 'general'

window.addEventListener('load', () => {


    const provider = new WebsocketProvider(
        // 'wss://demos.yjs.dev/ws', // use the public ws server
        `ws${location.protocol.slice(4)}//${location.host}/ws`, // alternatively: use the local ws server (run `npm start` in root directory)
        `roomId=${roomId}fileId=${fileId}`,
        ydoc
    )
    const ytext = ydoc.getText('quill')
    const editorContainer = document.createElement('div')
    editorContainer.setAttribute('id', 'editor')
    document.body.insertBefore(editorContainer, null)

    const editor = new Quill(editorContainer, {
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

    // Get the editor's content-editable element
    const editorContent = editor.root;

// Disable pasting into the editor
//     editorContent.addEventListener('paste', function (e) {
//         e.preventDefault();
//         // Optionally, you can display a message to the user here.
//         alert('Pasting text is not allowed.');
//     });


    editor.root.addEventListener('paste', function (e) {
        // Prevent the default pasting action
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
                        const markdownImageText = `![Pasted Image](${base64image})`;
                        const range = editor.getSelection();
                        if (range) {
                            editor.insertText(range.index, markdownImageText);
                        }
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


    editor.on('text-change', () => {
        console.log('Text change!');
        console.log(editor.getText());
    });

    ydoc.transact(() => {
        if (ytext.toString() === '') { // Check if the document is empty
            ytext.insert(0, 'Welcome to the collaborative editor!');
        }
    });


    const binding = new QuillBinding(ytext, editor, provider.awareness)

    /*
    // Define user name and user name
    // Check the quill-cursors package on how to change the way cursors are rendered
    provider.awareness.setLocalStateField('user', {
      name: 'Typing Jimmy',
      color: 'blue'
    })
    */

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