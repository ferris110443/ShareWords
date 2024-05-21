# ShareWords - A collaborative markdown editor
![image](https://github.com/ferris110443/ShareWords/assets/58131832/99c1894b-094e-4955-b8cb-e623579b9dec)
ShareWords is a collborative, real-time markdown editor that the workspace members can edit and create markdown document together.

## Features
* Individual workspaces for different member groups to edit files and chat.
* Real-time notifications for friend requests and friend list updates.
![ezgif com-video-to-gif-converter (1)](https://github.com/ferris110443/ShareWords/assets/58131832/c4ce6521-396e-48c0-ab79-18997d6cc6a4)
* Group chat functionality for workspace members to discuss in real time.
![ezgif-7-a800fb1a43](https://github.com/ferris110443/ShareWords/assets/58131832/d49b294f-08b0-4193-80b2-ba8fed93c3a2)
* Collaborative markdown editor for multiple members to edit together.
* Provide preview page for markdown.
* Option to download .md files and .html files.
![markdown-ezgif com-video-to-gif-converter (1)](https://github.com/ferris110443/ShareWords/assets/58131832/0045b41b-96e0-40c2-a41c-10630c64ff85)
* External mail service to share .md files.

## Architectural diagram

* ShareWords employs Socket.IO on the client-side and Netty.io on the server-side to efficiently manage real-time functionalities such as notifications and group chatting. 
* Utilizing NGINX as a reverse proxy server for URL redirection.
* Quill editor binding for Yjs to accomplish collaborative editing through CRDT implementation
* Static content, including markdown images, user images, and logos, is stored in Amazon S3 and delivered to clients through CloudFront. It accelerates the content delivery, improving the user experienc by reducing load times and providing global content availability.

![image](https://github.com/ferris110443/ShareWords/assets/58131832/d709a7f7-ebc8-430e-b952-7827caad27cc)



## Database table schema design
![image](https://github.com/ferris110443/ShareWords/assets/58131832/d4042140-7da9-407e-9592-5d81fc2b207f)

## Tech Stack
