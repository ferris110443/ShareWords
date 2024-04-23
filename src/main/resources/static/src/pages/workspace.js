document.getElementById('file-creation-form').addEventListener('submit', function (event) {
    event.preventDefault();  // Prevent the default form submission
    const params = new URLSearchParams(window.location.search);
    const roomId = params.get('roomId') || 'general';
    const fileName = document.getElementById('file-name-input').value;
    const fileDescription = document.getElementById('file-description').value;
    const fileId = uuidv4();
    const accessToken = localStorage.getItem('accessToken');
    const data = {
        fileName: fileName,
        fileDescription: fileDescription,
        fileId: fileId,
        roomId: roomId
    };
    console.log(data)
    // Fetch API to send data and handle response
    fetch('/api/1.0/workspace/file', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Success:', data);
            alert('File created successfully!');

            // Uncomment the following line to redirect after success
            window.location.href = `http://localhost:3000/coeditor.html?roomId=${roomId}&fileId=${fileId}`;
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Failed to create file. Please try again.');
        });
});


function uuidv4() {
    return "10000000-1000-4000-8000-100000000000".replace(/[018]/g, c =>
        (+c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> +c / 4).toString(16)
    );
}

