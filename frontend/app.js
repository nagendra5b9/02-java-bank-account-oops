// Java OOP Bank Account Frontend Controller
const state = {
  accounts: {
    savings: {
      number: 'SA-1001',
      holder: 'Alice Vance',
      balance: 1500.00,
      minBalance: 200.00,
      apy: 0.045
    },
    checking: {
      number: 'CA-2002',
      holder: 'Alice Vance',
      balance: 500.00,
      overdraftLimit: 300.00,
      overdraftFee: 25.00
    }
  },
  ledger: []
};

// DOM References
const savingsBalanceEl = document.getElementById('savingsBalance');
const checkingBalanceEl = document.getElementById('checkingBalance');
const odStatusEl = document.getElementById('odStatus');
const ledgerBodyEl = document.getElementById('ledgerBody');
const toastEl = document.getElementById('toastMessage');

function formatCurrency(val) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
}

function showToast(message, isSuccess = true) {
  toastEl.textContent = message;
  toastEl.className = `toast ${isSuccess ? 'success' : 'error'}`;
  setTimeout(() => {
    toastEl.className = 'toast hidden';
  }, 4000);
}

function updateUI() {
  savingsBalanceEl.textContent = formatCurrency(state.accounts.savings.balance);
  checkingBalanceEl.textContent = formatCurrency(state.accounts.checking.balance);

  if (state.accounts.checking.balance < 0) {
    odStatusEl.textContent = `Overdraft Active: Negative Balance ${formatCurrency(state.accounts.checking.balance)}`;
    odStatusEl.style.color = '#f87171';
  } else {
    odStatusEl.textContent = 'Status: In Normal Range';
    odStatusEl.style.color = '#34d399';
  }

  renderLedger();
}

function addLedgerEntry(accountKey, type, amount, balanceAfter, memo) {
  const id = 'TX-' + Math.random().toString(36).substring(2, 8).toUpperCase();
  const timestamp = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  const accNum = state.accounts[accountKey].number;

  state.ledger.unshift({ id, timestamp, accNum, type, amount, balanceAfter, memo });
  updateUI();
}

function renderLedger() {
  if (state.ledger.length === 0) {
    ledgerBodyEl.innerHTML = '<tr><td colspan="7" style="text-align:center; padding:20px; color:#94a3b8;">No activity yet.</td></tr>';
    return;
  }

  ledgerBodyEl.innerHTML = state.ledger.map(tx => {
    let badgeClass = 'badge-deposit';
    if (tx.type.includes('WITHDRAW')) badgeClass = 'badge-withdrawal';
    if (tx.type.includes('TRANSFER')) badgeClass = 'badge-transfer';

    return `
      <tr>
        <td style="font-family:'JetBrains Mono', monospace; font-size:0.8rem; color:#93c5fd;">${tx.id}</td>
        <td>${tx.timestamp}</td>
        <td><strong>${tx.accNum}</strong></td>
        <td><span class="badge-tag ${badgeClass}">${tx.type}</span></td>
        <td style="font-weight:700;">${formatCurrency(tx.amount)}</td>
        <td style="font-family:'JetBrains Mono', monospace;">${formatCurrency(tx.balanceAfter)}</td>
        <td>${tx.memo}</td>
      </tr>
    `;
  }).join('');
}

// Deposit / Withdraw Execution (Polymorphic Rules)
document.getElementById('operationForm').addEventListener('submit', (e) => {
  e.preventDefault();
  const accKey = document.getElementById('opAccount').value;
  const txType = document.querySelector('input[name="txType"]:checked').value;
  const amount = parseFloat(document.getElementById('opAmount').value);
  const note = document.getElementById('opNote').value.trim() || (txType === 'deposit' ? 'Cash Deposit' : 'Cash Withdrawal');

  if (isNaN(amount) || amount <= 0) {
    showToast('Invalid transaction amount!', false);
    return;
  }

  const account = state.accounts[accKey];

  if (txType === 'deposit') {
    account.balance += amount;
    addLedgerEntry(accKey, 'DEPOSIT', amount, account.balance, note);
    showToast(`Successfully deposited ${formatCurrency(amount)} into ${account.number}!`, true);
  } else {
    // Withdrawal polymorphic rules
    if (accKey === 'savings') {
      if ((account.balance - amount) < account.minBalance) {
        showToast(`Rule Violation: Savings Account requires $${account.minBalance} minimum balance!`, false);
        return;
      }
      account.balance -= amount;
      addLedgerEntry(accKey, 'WITHDRAWAL', amount, account.balance, note);
      showToast(`Withdrew ${formatCurrency(amount)} from Savings Account.`, true);
    } else {
      // Checking overdraft rule
      const maxAvailable = account.balance + account.overdraftLimit;
      if (amount > maxAvailable) {
        showToast(`Declined: Exceeds overdraft threshold of ${formatCurrency(maxAvailable)}!`, false);
        return;
      }
      account.balance -= amount;
      addLedgerEntry(accKey, 'WITHDRAWAL', amount, account.balance, note);

      if (account.balance < 0) {
        account.balance -= account.overdraftFee;
        addLedgerEntry(accKey, 'PENALTY', account.overdraftFee, account.balance, 'Overdraft Penalty Fee Applied');
        showToast(`Withdrew ${formatCurrency(amount)}. Notice: Overdraft activated ($25 fee applied)!`, false);
      } else {
        showToast(`Withdrew ${formatCurrency(amount)} from Checking Account.`, true);
      }
    }
  }

  document.getElementById('opAmount').value = '';
  document.getElementById('opNote').value = '';
});

// Inter-Account Transfer
document.getElementById('transferForm').addEventListener('submit', (e) => {
  e.preventDefault();
  const fromKey = document.getElementById('transferFrom').value;
  const toKey = document.getElementById('transferTo').value;
  const amount = parseFloat(document.getElementById('transferAmount').value);

  if (fromKey === toKey) {
    showToast('Source and destination accounts must be distinct!', false);
    return;
  }
  if (isNaN(amount) || amount <= 0) {
    showToast('Enter a valid transfer amount!', false);
    return;
  }

  const src = state.accounts[fromKey];
  const dst = state.accounts[toKey];

  // Validate source withdrawal rules
  if (fromKey === 'savings' && (src.balance - amount) < src.minBalance) {
    showToast(`Transfer Denied: Must maintain minimum balance of $${src.minBalance} in Savings!`, false);
    return;
  }
  if (fromKey === 'checking' && amount > (src.balance + src.overdraftLimit)) {
    showToast('Transfer Denied: Exceeds Checking overdraft limit!', false);
    return;
  }

  // Atomic simulation
  src.balance -= amount;
  dst.balance += amount;

  addLedgerEntry(fromKey, 'TRANSFER_OUT', amount, src.balance, `Transfer to ${dst.number}`);
  addLedgerEntry(toKey, 'TRANSFER_IN', amount, dst.balance, `Transfer from ${src.number}`);

  showToast(`Successfully transferred ${formatCurrency(amount)} from ${src.number} to ${dst.number}!`, true);
  document.getElementById('transferAmount').value = '';
});

// Monthly Interest Credit
document.getElementById('applyInterestBtn').addEventListener('click', () => {
  const savings = state.accounts.savings;
  const interest = savings.balance * (savings.apy / 12.0);
  if (interest > 0) {
    savings.balance += interest;
    addLedgerEntry('savings', 'INTEREST_CREDIT', interest, savings.balance, `Monthly Interest (4.5% APY)`);
    showToast(`Credited monthly interest of ${formatCurrency(interest)}!`, true);
  }
});

// Reset Data
document.getElementById('clearLedgerBtn').addEventListener('click', () => {
  state.accounts.savings.balance = 1500.00;
  state.accounts.checking.balance = 500.00;
  state.ledger = [];
  addLedgerEntry('savings', 'DEPOSIT', 1500.00, 1500.00, 'Initial Account Opening');
  addLedgerEntry('checking', 'DEPOSIT', 500.00, 500.00, 'Initial Account Opening');
  showToast('Reset bank state to initial demo configuration.', true);
});

// Initial load
addLedgerEntry('savings', 'DEPOSIT', 1500.00, 1500.00, 'Initial Account Opening');
addLedgerEntry('checking', 'DEPOSIT', 500.00, 500.00, 'Initial Account Opening');
updateUI();
