-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    address TEXT,
    kyc_status VARCHAR(20) DEFAULT 'PENDING',
    is_email_verified BOOLEAN DEFAULT FALSE,
    is_phone_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    biometric_enabled BOOLEAN DEFAULT FALSE,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create accounts table
CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    sort_code VARCHAR(10) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'GBP',
    overdraft_limit DECIMAL(15,2) DEFAULT 0.00,
    interest_rate DECIMAL(5,4) DEFAULT 0.0000,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create transactions table
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    description TEXT,
    category VARCHAR(100),
    reference VARCHAR(100) UNIQUE NOT NULL,
    external_id VARCHAR(100),
    merchant_name VARCHAR(200),
    merchant_category VARCHAR(100),
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Create cards table
CREATE TABLE cards (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    account_id UUID NOT NULL,
    card_number VARCHAR(20) UNIQUE NOT NULL,
    card_type VARCHAR(20) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    cvv VARCHAR(4) NOT NULL,
    pin VARCHAR(4) NOT NULL,
    is_active BOOLEAN DEFAULT FALSE,
    is_blocked BOOLEAN DEFAULT FALSE,
    delivery_status VARCHAR(20) DEFAULT 'PENDING',
    delivery_address TEXT,
    contactless_enabled BOOLEAN DEFAULT TRUE,
    online_payments_enabled BOOLEAN DEFAULT TRUE,
    atm_withdrawals_enabled BOOLEAN DEFAULT TRUE,
    magnetic_stripe_enabled BOOLEAN DEFAULT TRUE,
    activated_at TIMESTAMP,
    blocked_at TIMESTAMP,
    block_reason TEXT,
    delivered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Create pots table
CREATE TABLE pots (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    target_amount DECIMAL(15,2) NOT NULL,
    current_balance DECIMAL(15,2) DEFAULT 0.00,
    target_date TIMESTAMP,
    auto_deposit_enabled BOOLEAN DEFAULT FALSE,
    auto_deposit_amount DECIMAL(15,2),
    deposit_frequency VARCHAR(20),
    next_deposit_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(user_id, name)
);

-- Create investments table
CREATE TABLE investments (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    name VARCHAR(200) NOT NULL,
    asset_type VARCHAR(20) NOT NULL,
    quantity DECIMAL(15,6) NOT NULL,
    purchase_price DECIMAL(15,4) NOT NULL,
    current_price DECIMAL(15,4) NOT NULL,
    purchase_date TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create loans table
CREATE TABLE loans (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    loan_type VARCHAR(20) NOT NULL,
    principal_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,4) NOT NULL,
    term_in_months INTEGER NOT NULL,
    monthly_payment DECIMAL(15,2) NOT NULL,
    outstanding_balance DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING_APPROVAL',
    purpose TEXT,
    application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approval_date TIMESTAMP,
    disbursement_date TIMESTAMP,
    maturity_date TIMESTAMP NOT NULL,
    next_payment_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_users_kyc_status ON users(kyc_status);
CREATE INDEX idx_users_created_at ON users(created_at);

CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_accounts_account_number ON accounts(account_number);
CREATE INDEX idx_accounts_type ON accounts(account_type);
CREATE INDEX idx_accounts_created_at ON accounts(created_at);

CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_reference ON transactions(reference);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_category ON transactions(category);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_amount ON transactions(amount);

CREATE INDEX idx_cards_user_id ON cards(user_id);
CREATE INDEX idx_cards_account_id ON cards(account_id);
CREATE INDEX idx_cards_card_number ON cards(card_number);
CREATE INDEX idx_cards_type ON cards(card_type);
CREATE INDEX idx_cards_status ON cards(is_active, is_blocked);
CREATE INDEX idx_cards_expiry ON cards(expiry_date);

CREATE INDEX idx_pots_user_id ON pots(user_id);
CREATE INDEX idx_pots_name ON pots(user_id, name);
CREATE INDEX idx_pots_target_date ON pots(target_date);
CREATE INDEX idx_pots_auto_deposit ON pots(auto_deposit_enabled, next_deposit_date);
CREATE INDEX idx_pots_created_at ON pots(created_at);

CREATE INDEX idx_investments_user_id ON investments(user_id);
CREATE INDEX idx_investments_symbol ON investments(symbol);
CREATE INDEX idx_investments_asset_type ON investments(asset_type);
CREATE INDEX idx_investments_status ON investments(status);
CREATE INDEX idx_investments_purchase_date ON investments(purchase_date);

CREATE INDEX idx_loans_user_id ON loans(user_id);
CREATE INDEX idx_loans_type ON loans(loan_type);
CREATE INDEX idx_loans_status ON loans(status);
CREATE INDEX idx_loans_application_date ON loans(application_date);
CREATE INDEX idx_loans_next_payment ON loans(next_payment_date);
CREATE INDEX idx_loans_maturity ON loans(maturity_date);