const API_URL = "/atm";

let currentUser = null;

// ====================== TOAST ALERTS ======================
function showToast(message, isError = false) {
    const toast = document.getElementById("toast");
    if (!toast) return;
    toast.innerText = message;
    toast.style.backgroundColor = isError ? "#c0392b" : "#2c3e50";
    toast.style.display = "block";
    setTimeout(() => {
        toast.style.display = "none";
    }, 3500);
}

// ====================== LOGIN ======================
function login() {
    const cardNumber = document.getElementById("cardNumber").value.trim();
    const pin = document.getElementById("pin").value.trim();

    if (!cardNumber || !pin) {
        showToast("Please enter Card Number and PIN", true);
        return;
    }

    fetch(`${API_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ cardNumber, pin })
    })
    .then(res => {
        if (!res.ok) {
            return res.text().then(msg => { throw new Error(msg || "Invalid Card Number or PIN"); });
        }
        return res.text();
    })
    .then(() => {
        window.location.href = "index.html";
    })
    .catch(err => showToast(err.message, true));
}

// ====================== USER INFO & BALANCE ======================
function loadUserData() {
    fetch(`${API_URL}/user-info`, {
        method: "GET",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("Session expired. Please login again.");
        }
        return res.json();
    })
    .then(user => {
        currentUser = user;
        
        // Update Navbar User Badge
        const userBadge = document.getElementById("userBadge");
        if (userBadge) userBadge.innerText = "Customer: " + (user.name || "Card Holder");

        // Update Balance
        const balanceEl = document.getElementById("balance");
        if (balanceEl) balanceEl.innerText = Number(user.balance).toLocaleString('en-IN', { minimumFractionDigits: 2 });

        // Update Card Display
        const cardDisplay = document.getElementById("cardDisplay");
        if (cardDisplay && user.cardNumber) {
            const raw = user.cardNumber;
            cardDisplay.innerText = raw.length >= 10 ? 
                raw.substring(0, 4) + " •••• •••• " + raw.substring(raw.length - 2) : raw;
        }

        const cardHolderDisplay = document.getElementById("cardHolderDisplay");
        if (cardHolderDisplay) cardHolderDisplay.innerText = (user.name || "VALUED CUSTOMER").toUpperCase();

        const accountNumDisplay = document.getElementById("accountNumDisplay");
        if (accountNumDisplay) accountNumDisplay.innerText = user.accountNumber || "ACC-1002934";

        const accountTypeDisplay = document.getElementById("accountTypeDisplay");
        if (accountTypeDisplay) accountTypeDisplay.innerText = user.accountType || "Savings Account";

        const statusDisplay = document.getElementById("statusDisplay");
        if (statusDisplay) statusDisplay.innerText = user.status || "ACTIVE";
    })
    .catch(() => {
        window.location.href = "login.html";
    });
}

function loadBalance() {
    loadUserData();
}

// ====================== DEPOSIT ======================
function deposit() {
    const amountEl = document.getElementById("depositAmount");
    const amount = amountEl ? amountEl.value : 0;

    if (!amount || amount <= 0) {
        showToast("Please enter a valid deposit amount", true);
        return;
    }

    fetch(`${API_URL}/deposit/${amount}`, {
        method: "POST",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) return res.text().then(msg => { throw new Error(msg); });
        return res.text();
    })
    .then(msg => {
        showToast(msg);
        if (amountEl) amountEl.value = "";
        loadUserData();
        loadTransactions();
        showReceipt("CASH DEPOSIT", amount);
    })
    .catch(err => showToast(err.message, true));
}

// ====================== WITHDRAW ======================
function quickWithdraw(amount) {
    executeWithdraw(amount);
}

function withdraw() {
    const amountEl = document.getElementById("withdrawAmount");
    const amount = amountEl ? amountEl.value : 0;

    if (!amount || amount <= 0) {
        showToast("Please enter a valid withdrawal amount", true);
        return;
    }

    executeWithdraw(amount);
}

function executeWithdraw(amount) {
    fetch(`${API_URL}/withdraw/${amount}`, {
        method: "POST",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) return res.text().then(msg => { throw new Error(msg); });
        return res.text();
    })
    .then(msg => {
        showToast(msg);
        const amountEl = document.getElementById("withdrawAmount");
        if (amountEl) amountEl.value = "";
        loadUserData();
        loadTransactions();
        showReceipt("CASH WITHDRAWAL", amount);
    })
    .catch(err => showToast(err.message, true));
}

// ====================== FUND TRANSFER ======================
function transfer() {
    const targetCard = document.getElementById("targetCardNumber").value.trim();
    const amount = document.getElementById("transferAmount").value;

    if (!targetCard) {
        showToast("Please enter recipient card number", true);
        return;
    }
    if (!amount || amount <= 0) {
        showToast("Please enter a valid transfer amount", true);
        return;
    }

    fetch(`${API_URL}/transfer`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ targetCardNumber: targetCard, amount: parseFloat(amount) })
    })
    .then(res => {
        if (!res.ok) return res.text().then(msg => { throw new Error(msg); });
        return res.text();
    })
    .then(msg => {
        showToast(msg);
        document.getElementById("targetCardNumber").value = "";
        document.getElementById("transferAmount").value = "";
        loadUserData();
        loadTransactions();
        showReceipt("FUND TRANSFER", amount);
    })
    .catch(err => showToast(err.message, true));
}

// ====================== CHANGE PIN ======================
function changePin() {
    const currentPin = document.getElementById("currentPin").value.trim();
    const newPin = document.getElementById("newPin").value.trim();
    const confirmPin = document.getElementById("confirmPin").value.trim();

    if (!currentPin || !newPin || !confirmPin) {
        showToast("Please fill in all PIN fields", true);
        return;
    }
    if (newPin !== confirmPin) {
        showToast("New PIN and Confirm PIN do not match", true);
        return;
    }

    fetch(`${API_URL}/change-pin`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ currentPin, newPin })
    })
    .then(res => {
        if (!res.ok) return res.text().then(msg => { throw new Error(msg); });
        return res.text();
    })
    .then(msg => {
        showToast(msg);
        document.getElementById("currentPin").value = "";
        document.getElementById("newPin").value = "";
        document.getElementById("confirmPin").value = "";
        switchTab("dashboard");
    })
    .catch(err => showToast(err.message, true));
}

// ====================== TRANSACTIONS LIST ======================
function loadTransactions() {
    const tbody = document.getElementById("transactionsBody");
    if (!tbody) return;

    fetch(`${API_URL}/transactions`, {
        method: "GET",
        credentials: "include"
    })
    .then(res => res.json())
    .then(list => {
        if (!list || list.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-muted); padding: 20px;">No transactions recorded yet.</td></tr>`;
            return;
        }

        tbody.innerHTML = list.map(tx => {
            const badgeClass = tx.type === 'DEPOSIT' ? 'deposit' : (tx.type === 'WITHDRAW' ? 'withdraw' : 'transfer');
            const formattedDate = tx.timestamp ? new Date(tx.timestamp).toLocaleString() : 'N/A';
            const formattedAmount = '₹' + Number(tx.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 });
            const refNo = tx.referenceNo || ('TXN-' + tx.id);

            return `
                <tr>
                    <td><code>${refNo}</code></td>
                    <td>${formattedDate}</td>
                    <td><span class="txn-badge ${badgeClass}">${tx.type}</span></td>
                    <td>${tx.description || 'ATM Transaction'}</td>
                    <td><strong>${formattedAmount}</strong></td>
                    <td>
                        <button onclick="printRowReceipt('${tx.type}', ${tx.amount}, ${tx.balanceAfter || 0}, '${refNo}', '${formattedDate}')" 
                                style="background: #f1f5f9; border: 1px solid #cbd5e1; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 11px;">
                            🧾 Receipt
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    })
    .catch(() => {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #c0392b;">Failed to load transaction history.</td></tr>`;
    });
}

// ====================== RECEIPT MODAL ======================
function showReceipt(type, amount, balance = null, refNo = null) {
    const modal = document.getElementById("receiptModal");
    if (!modal) return;

    document.getElementById("rType").innerText = type;
    document.getElementById("rAmount").innerText = "₹" + Number(amount).toLocaleString('en-IN', { minimumFractionDigits: 2 });
    
    if (currentUser) {
        document.getElementById("rCustomer").innerText = currentUser.name || "VALUED CUSTOMER";
        const raw = currentUser.cardNumber || "";
        document.getElementById("rCard").innerText = raw.length >= 10 ? "•••• •••• •••• " + raw.substring(raw.length - 2) : "•••• " + raw;
        
        const currentBal = balance !== null ? balance : currentUser.balance;
        document.getElementById("rBalance").innerText = "₹" + Number(currentBal).toLocaleString('en-IN', { minimumFractionDigits: 2 });
    }

    document.getElementById("rRef").innerText = refNo || ("TXN-" + Math.floor(10000000 + Math.random() * 90000000));
    document.getElementById("receiptDate").innerText = new Date().toLocaleString();

    modal.style.display = "flex";
}

function printRowReceipt(type, amount, balance, refNo, date) {
    const modal = document.getElementById("receiptModal");
    if (!modal) return;

    document.getElementById("rType").innerText = type;
    document.getElementById("rAmount").innerText = "₹" + Number(amount).toLocaleString('en-IN', { minimumFractionDigits: 2 });
    document.getElementById("rBalance").innerText = "₹" + Number(balance).toLocaleString('en-IN', { minimumFractionDigits: 2 });
    document.getElementById("rRef").innerText = refNo;
    document.getElementById("receiptDate").innerText = date;

    if (currentUser) {
        document.getElementById("rCustomer").innerText = currentUser.name || "VALUED CUSTOMER";
        const raw = currentUser.cardNumber || "";
        document.getElementById("rCard").innerText = raw.length >= 10 ? "•••• •••• •••• " + raw.substring(raw.length - 2) : "•••• " + raw;
    }

    modal.style.display = "flex";
}

function closeReceipt() {
    const modal = document.getElementById("receiptModal");
    if (modal) modal.style.display = "none";
}

// ====================== HARDWARE KEYPAD SIMULATION ======================
function pressKey(key) {
    // Determine active input on screen
    const activeEl = document.activeElement;
    if (activeEl && (activeEl.tagName === 'INPUT')) {
        if (key === 'CLEAR') {
            activeEl.value = "";
        } else if (key === 'ENTER') {
            activeEl.blur();
        } else {
            activeEl.value += key;
        }
        return;
    }

    // Default target input depending on active tab
    const activeTab = document.querySelector(".view-section.active");
    if (!activeTab) return;

    const inputs = activeTab.querySelectorAll("input");
    if (inputs.length > 0) {
        const targetInput = inputs[0];
        if (key === 'CLEAR') {
            targetInput.value = "";
        } else if (key === 'ENTER') {
            targetInput.blur();
        } else {
            targetInput.value += key;
        }
    }
}

// ====================== SCREEN TAB NAVIGATION ======================
function switchTab(tabName) {
    const tabs = document.querySelectorAll(".tab-btn");
    tabs.forEach(t => t.classList.remove("active"));

    const views = document.querySelectorAll(".view-section");
    views.forEach(v => v.classList.remove("active"));

    const targetView = document.getElementById("view-" + tabName);
    if (targetView) targetView.classList.add("active");

    // Highlight matching tab button
    tabs.forEach(t => {
        if (t.getAttribute("onclick") && t.getAttribute("onclick").includes(`'${tabName}'`)) {
            t.classList.add("active");
        }
    });

    if (tabName === 'statement') {
        loadTransactions();
    }
}

// ====================== LOGOUT ======================
function logout() {
    fetch(`${API_URL}/logout`, {
        method: "POST",
        credentials: "include"
    })
    .then(() => {
        window.location.href = "login.html";
    });
}
