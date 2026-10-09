// SrihariMart Client Script

function fillCredentials(email, password) {
    const emailField = document.getElementById('email');
    const passField = document.getElementById('password');
    if (emailField && passField) {
        emailField.value = email;
        passField.value = password;
    }
}

function openModal(id) {
    const el = document.getElementById(id);
    if (el) {
        el.style.display = 'flex';
    }
}

function closeModal(id) {
    const el = document.getElementById(id);
    if (el) {
        el.style.display = 'none';
    }
}
