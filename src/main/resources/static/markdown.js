
const roomId = new URLSearchParams(window.location.search).get('roomId') || 'general';
const title = "title need modify";
let editor = ace.edit("editor");

let debounceTimeout = 3000;
let debouncedSaveData = debounce(saveDataToServer, debounceTimeout);


editor.getSession().on('change', function() {
    updatePreview();
    let currentContent = editor.getValue();
    // console.log("Current Content: " + currentContent);
    debouncedSaveData(currentContent,title,roomId);
});



function updatePreview() {
    let markdownText = editor.getValue(); // Get the text from Ace Editor
    let htmlContent = marked.parse(markdownText); // Parse markdown to HTML
    // console.log(markdownText);
    // console.log(htmlContent);
    document.getElementById("preview").innerHTML = htmlContent; // Display the HTML in the preview div
}
updatePreview();

function debounce(func, timeout) {
    let timer;
    return function(...args) {
        const context = this;
        clearTimeout(timer);
        timer = setTimeout(() => {
            func.apply(context, args);
        }, timeout);
    };
}

async function saveDataToServer(data,title,roomId) {
    const response = await fetch('/api/1.0/markdown/saveMarkdownText', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({markdownText: data,title: title,roomId: roomId})
    });
    console.log(response);
}

