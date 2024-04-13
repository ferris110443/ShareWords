

// Create a new instance of the marked library
let editor = ace.edit("editor");
editor.getSession().on('change', function() {
    updatePreview();
});
function updatePreview() {
    let markdownText = editor.getValue(); // Get the text from Ace Editor
    let htmlContent = marked.parse(markdownText); // Parse markdown to HTML
    console.log(markdownText);
    console.log(htmlContent);
    document.getElementById("preview").innerHTML = htmlContent; // Display the HTML in the preview div
}
updatePreview();





let debouncedSaveData = debounce(saveData, 3000);

editor.getSession().on('change', function() {
    console.log("Change event triggered.");
    let currentContent = editor.getValue();
    console.log("Current Content: " + currentContent);
    debouncedSaveData(currentContent);
});

function debounce(func, timeout = 3000) {
    let timer;
    return function(...args) {
        const context = this;
        clearTimeout(timer);
        timer = setTimeout(() => {
            func.apply(context, args);
        }, timeout);
    };
}


async function saveData(data) {
    const response = await fetch('/api/1.0/markdown/saveMarkdownText', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({markdownText: data})
    });
    console.log(response);
}
