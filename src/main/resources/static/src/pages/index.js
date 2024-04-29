document.getElementById('signupForm').addEventListener('submit', async function (event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const jsonData = {};
    formData.forEach((value, key) => {
        jsonData[key] = value;
    });
    console.log(jsonData)
    try {
        const response = await fetch('/api/1.0/user/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(jsonData)
        });
        console.log(response)

        if (response.ok) {
            const data = await response.json();
            const accessToken = data.data.access_token;
            localStorage.setItem('accessToken', accessToken);
            alert('Signup successful');
            window.location.href = '/admin/home';
        } else {
            const errorData = await response.json();
            console.log(errorData)
            alert('Signup failed : ' + JSON.stringify(errorData.error));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred. Please try again.');
    }
});

document.getElementById('signInForm').addEventListener('submit', async function (event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const jsonData = {};
    formData.forEach((value, key) => {
        jsonData[key] = value;
    });
    console.log(jsonData)
    try {
        const response = await fetch('/api/1.0/user/signIn', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(jsonData)
        });
        console.log(response)

        if (response.ok) {
            const data = await response.json();
            const accessToken = data.data.access_token;
            localStorage.setItem('accessToken', accessToken);
            alert('SignIn successful');
            window.location.href = '/admin/home';
        } else {
            const errorData = await response.json();
            console.log(errorData)
            console.log(errorData.error)
            alert('SignIn failed : ' + JSON.stringify(errorData.error));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred. Please try again.');
    }
});
document.getElementById('signInToggle').addEventListener('click', function (event) {
    event.preventDefault();
    document.getElementById('signupSection').style.display = 'none';
    document.getElementById('signInSection').style.display = 'block';
});

document.getElementById('signUpToggle').addEventListener('click', function (event) {
    event.preventDefault();
    document.getElementById('signInSection').style.display = 'none';
    document.getElementById('signupSection').style.display = 'block';
});