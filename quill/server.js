const express = require('express');
const path = require('path');
const server = express();

// Serve static assets (JavaScript, CSS, images etc.)
server.use(express.static('dist'));

// Route to serve the coeditor.html
server.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'coeditor.html'));
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
