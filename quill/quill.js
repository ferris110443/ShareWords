/* eslint-env browser */

import * as Y from 'yjs'
import { WebsocketProvider } from 'y-websocket'
import { QuillBinding } from 'y-quill'
import Quill from 'quill'
import QuillCursors from 'quill-cursors'

Quill.register('modules/cursors', QuillCursors)

window.addEventListener('load', () => {
  const ydoc = new Y.Doc()
  // const roomName = "testing";
  const params = new URLSearchParams(window.location.search)
  const roomId = params.get('roomId') || 'general'
  console.log('roomId:', roomId)
  const fileId = params.get('fileId') || 'general'
  console.log('fileId:', fileId)

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
      toolbar: [
        [{ header: [1, 2, false] }],
        ['bold', 'italic', 'underline'],
        ['image', 'code-block']
      ],
      history: {
        userOnly: true
      }
    },
    placeholder: 'Start collaborating...',
    theme: 'snow' // or 'bubble'
  })

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
  window.example = { provider, ydoc, ytext, binding, Y }
})
