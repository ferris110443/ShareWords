
const roomId = new URLSearchParams(window.location.search).get('roomId') || 'general';
const title = "title need modify";
let editor = ace.edit("editor");

let debounceTimeout = 3000;
let debouncedSaveData = debounce(saveDataToServer, debounceTimeout);



// =================== Load Markdown Text ===================
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


// =================== Image Upload in markdown ===================
document.getElementById('editor').addEventListener('paste', function(event) {
    let clipboardData = event.clipboardData || window.clipboardData;
    if (clipboardData && clipboardData.items) { //clipboardData.items is a list of all the items that were copied (text,image, etc.
        for (let i = 0; i < clipboardData.items.length; i++) {
            let item = clipboardData.items[i];
            console.log('Item type: ', item.type);
            console.log('Item type indexOf ', item.type.indexOf('image'));
            if (item.type.indexOf('image') === 0) {  // Check if the item is an image
                let blob = item.getAsFile();
                let reader = new FileReader();
                reader.onload = function(e) {
                    // console.log('File content: ', e.target.result);
                    uploadImage(e.target.result).then(url => {
                        insertImageMarkdown(url);
                    });
                };
                reader.readAsDataURL(blob);
                event.preventDefault(); // Prevent the default behavior of pasting
            }
        }
    }
});


function insertImageMarkdown(url) {
    const markdownText = `![Image](${url})`;
    editor.insert(markdownText);
}

async function uploadImage(data) {
    try {
        const response = await fetch('api/1.0/upload/Image', {
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



