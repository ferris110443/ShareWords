# ShareWords - A collaborative markdown editor
ShareWords is an advanced, real-time markdown editor designed for seamless collaboration within teams. It enables workspace members to simultaneously edit and create detailed markdown documents, fostering an environment of collective creativity and productivity. 

<!---<img src="https://github.com/ferris110443/ShareWords/assets/58131832/99c1894b-094e-4955-b8cb-e623579b9dec" width="800">-->


## Features
* Individual workspaces for different member groups to edit files and chat.
* Real-time notifications for friend requests and friend list updates.
* Group chat functionality for workspace members to discuss in real time.
* Collaborative markdown editor for multiple members to edit together.
* Provide preview page for markdown.
* Option to download .md files and .html files.
* External mail service to share .md files.

<img src="https://github.com/ferris110443/ShareWords/assets/58131832/c4ce6521-396e-48c0-ab79-18997d6cc6a4" width="800">
<img src="https://github.com/ferris110443/ShareWords/assets/58131832/d49b294f-08b0-4193-80b2-ba8fed93c3a2" width="800">
<img src="https://github.com/ferris110443/ShareWords/assets/58131832/663a3f8a-03e0-47d1-9c72-e2ec8e8a58ef" width="800">
<!---<img src="https://github.com/ferris110443/ShareWords/assets/58131832/0045b41b-96e0-40c2-a41c-10630c64ff85" width="800">-->

## Architectural diagram

* Implements Socket.IO and Netty for instant notifications and real-time updates.
* Integrates the Quill editor with the Yjs library to support collaborative editing through CRDTs.
* Employs NGINX as a proxy server to route various requests to backend servers running on Spring Boot and Express.js.
* Static content, including markdown images, user images, and logos, is stored in Amazon S3 and delivered to clients through CloudFront. It enhances the content delivery, improving the user experienc by reducing load times and providing global content availability.
* Supports file delivery through emails using the third-party mail service Mailgun.
  
<img src="https://github.com/ferris110443/ShareWords/assets/58131832/e3203f06-755b-4c4b-aea9-10be00c2cdb4" width="800">

## Database table schema design
<img src="https://github.com/ferris110443/ShareWords/assets/58131832/d4042140-7da9-407e-9592-5d81fc2b207f" width="800">

## Tech Stack
