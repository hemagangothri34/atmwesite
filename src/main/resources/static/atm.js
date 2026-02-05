const API_URL = "http://localhost:8080/atm";

/* ======================
   LOGIN
====================== */
function login() {
    const cardNumber = document.getElementById("cardNumber").value;
    const pin = document.getElementById("pin").value;

    fetch(`${API_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ cardNumber, pin })
    })
    .then(res => {
        if (!res.ok) throw new Error("Invalid Card Number or PIN");
        return res.text();
    })
    .then(() => {
        window.location.href = "index.html";
    })
    .catch(err => alert(err.message));
}


/* ======================
   LOAD BALANCE
====================== */
function loadBalance() {
    fetch(`${API_URL}/balance`, {
        method: "GET",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("Session expired. Please login again.");
        }
        return res.text(); // backend returns plain text
    })
    .then(balance => {
        document.getElementById("balance").innerText = balance;
    })
    .catch(() => {
        window.location.href = "login.html";
    });
}


/* ======================
   DEPOSIT
====================== */
function deposit() {
    const amount = document.getElementById("depositAmount").value;

    if (amount <= 0) {
        alert("Enter valid amount");
        return;
    }

    fetch(`${API_URL}/deposit/${amount}`, {
        method: "POST",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) throw new Error("Deposit failed");
        return res.text();
    })
    .then(msg => {
        alert(msg);
        loadBalance();
    })
    .catch(err => alert(err.message));
}


/* ======================
   WITHDRAW
====================== */
function withdraw() {
    const amount = document.getElementById("withdrawAmount").value;

    if (amount <= 0) {
        alert("Enter valid amount");
        return;
    }

    fetch(`${API_URL}/withdraw/${amount}`, {
        method: "POST",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) throw new Error("Insufficient balance");
        return res.text();
    })
    .then(msg => {
        alert(msg);
        loadBalance();
    })
    .catch(err => alert(err.message));
}


/* ======================
   LOGOUT
====================== */
function logout() {
    fetch(`${API_URL}/logout`, {
        method: "POST",
        credentials: "include"
    })
    .then(() => {
        window.location.href = "login.html";
    });
}
