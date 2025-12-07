# ShareWords
ShareWords is an advanced, real-time markdown editor designed for seamless collaboration within teams. It enables workspace members to simultaneously edit and create detailed markdown documents, fostering an environment of collective creativity and productivity. 

<!---<img src="https://github.com/ferris110443/ShareWords/assets/58131832/99c1894b-094e-4955-b8cb-e623579b9dec" width="800">-->

Demo Account 

| Account          | Password |
| ---------------- |:--------:|
| Alice@gmail.com  |Alice     |
| Tony@gmail.com   |Tony      |


## Features
* Individual workspaces for different member groups to edit files and chat.
* Real-time notifications for friend requests and friend list updates.
* Group chat functionality for workspace members to discuss in real time.
* Collaborative markdown editor for multiple members to edit together.
* Provide preview page for markdown.
* Option to download .md files and .html files.
* External mail service to share .md files.

## Live Demo
* Demo video links : https://www.youtube.com/watch?v=5if8F4n3Na8

<img src="https://github.com/ferris110443/ShareWords/assets/58131832/c4ce6521-396e-48c0-ab79-18997d6cc6a4" width="800">
<img src="https://github.com/ferris110443/ShareWords/assets/58131832/d49b294f-08b0-4193-80b2-ba8fed93c3a2" width="800">
<img src="https://github.com/ferris110443/ShareWords/assets/58131832/663a3f8a-03e0-47d1-9c72-e2ec8e8a58ef" width="800">
<!---<img src="https://github.com/ferris110443/ShareWords/assets/58131832/0045b41b-96e0-40c2-a41c-10630c64ff85" width="800">-->

## Architectural diagram

* Implements instant notifications and real-time updates status through Socket.IO.
* Integrates the Quill editor with the Yjs library to support collaborative editing through CRDTs.
* Uses NGINX as a proxy server to route various requests to different services.
* Leverages AWS S3 and CloudFront to deliver images, enhancing user experience.
* Supports markdown files delivery through emails.
  
<img src="https://github.com/ferris110443/ShareWords/assets/58131832/e3203f06-755b-4c4b-aea9-10be00c2cdb4" width="800">

## Database table schema design
<!--<img src="https://github.com/ferris110443/ShareWords/assets/58131832/d4042140-7da9-407e-9592-5d81fc2b207f" width="800">-->
<img src = "https://github.com/ferris110443/ShareWords/assets/58131832/9230521f-a569-4b68-9346-1fe13c65e461" width = "800" >


## Tech Stack
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white) ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Express.js](https://img.shields.io/badge/express.js-%23404d59.svg?style=for-the-badge&logo=express&logoColor=%2361DAFB) ![Socket.io](https://img.shields.io/badge/Socket.io-black?style=for-the-badge&logo=socket.io&badgeColor=010101) ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white) ![Bootstrap](https://img.shields.io/badge/bootstrap-%23563D7C.svg?style=for-the-badge&logo=bootstrap&logoColor=white) ![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white) ![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white) ![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white) ![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
