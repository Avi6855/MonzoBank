# 💳 Monzo Inspired Banking App (Android - Jetpack Compose)

A production-inspired fintech banking application built using **Jetpack Compose**, focused on **real-world product engineering, scalability, and user trust.**


# 🚀 Overview

This project is a **Monzo-inspired Android banking app** developed as a self-initiated project.

Instead of building a static UI demo, the focus is on:

- Real-time data handling  
- Secure payment systems  
- Scalable architecture  
- Product-driven UX decisions  



# 🎥 Demo (Build in Public Series)

This project is documented through a **LinkedIn video series**, where each feature is implemented and demonstrated in real-time.

# 🔐 Part 1 — Secure Login  
[Watch Video](https://www.linkedin.com/feed/update/urn:li:activity:7441542299500847104/?originTrackingId=gtuPAKLjQqKIqumK3DTlow%3D%3D)

# 📊 Part 2 — Dashboard & Transactions  
[Watch Video](https://www.linkedin.com/feed/update/urn:li:activity:7443314840829501440/?originTrackingId=J5ZfMi97Q6uE5kbn9SMANg%3D%3D)

# 🏦 Part 3 — Accounts  
[Watch Video](https://www.linkedin.com/feed/update/urn:li:activity:7444387956733149185/?originTrackingId=2lhvKzetTPik22xpi5DNeA%3D%3D)

# 💳 Part 4 — Card Controls  
[Watch Video](https://www.linkedin.com/feed/update/urn:li:activity:7448778143902986240/?originTrackingId=SBKF5YB6TbWD8nXwlyFpsw%3D%3D)

# 💸 Part 5 — Bill Payments  
[Watch Video](https://www.linkedin.com/feed/update/urn:li:activity:7449335579231834112/?originTrackingId=MGC6rDlgQZqZWnED9JK31A%3D%3D)

# 📦 Part 6 — Bulk Payments  
[Watch Video](https://www.linkedin.com/feed/update/urn:li:activity:7449691663737876480/?originTrackingId=ZrRL4qCESK2BAV1fbPp5Ag%3D%3D)

# 🤝 Part 7 — P2P Transfer  
[Watch Video](https://www.linkedin.com/feed/update/urn:li:activity:7450064159980834816/?originTrackingId=89J%2BMmv%2FQVWgzBkQIhuKAA%3D%3D)

# 📷 Part 8 — QR Payments  
[Watch Video](https://www.linkedin.com/feed/update/urn:li:activity:7450422838294986752/?originTrackingId=YJN9Gt5OS9q8fXKhzSFGhg%3D%3D)

# Upcoming Part's is coming soon.



## 🎬 Video Thumbnails

# 🔐 Part 1 — Secure Login
[![Secure Login](https://github.com/Avi6855/MonzoBank/blob/955500504b8a582def44a2aa12bbff85c04ef8f2/Part_1.png)]

# 📊 Part 2 — Dashboard & Transactions
[![Dashboard](https://github.com/Avi6855/MonzoBank/blob/955500504b8a582def44a2aa12bbff85c04ef8f2/Part_2.png)]

# 🏦 Part 3 — Accounts
[![Accounts](https://github.com/Avi6855/MonzoBank/blob/955500504b8a582def44a2aa12bbff85c04ef8f2/Part_3.png)]

# 💳 Part 4 — Card Controls
[![Card Controls](https://github.com/Avi6855/MonzoBank/blob/955500504b8a582def44a2aa12bbff85c04ef8f2/Part_4.png)]

# 💸 Part 5 — Bill Payments
[![Bill Payments](https://github.com/Avi6855/MonzoBank/blob/955500504b8a582def44a2aa12bbff85c04ef8f2/Part_5.png)]

# 📦 Part 6 — Bulk Payments
[![Bulk Payments](https://github.com/Avi6855/MonzoBank/blob/955500504b8a582def44a2aa12bbff85c04ef8f2/Part_6.png)]

### 🤝 Part 7 — P2P Transfer
[![P2P Transfer](https://github.com/Avi6855/MonzoBank/blob/955500504b8a582def44a2aa12bbff85c04ef8f2/Part_7.png)]

# 📱 Part 8 — QR Payments
[![QR Payments](https://github.com/Avi6855/MonzoBank/blob/955500504b8a582def44a2aa12bbff85c04ef8f2/Part_8.png)]



# 🏗️ Architecture Diagram

            ┌────────────────────┐
            │   Jetpack Compose  │
            │   UI Layer         │
            └─────────┬──────────┘
                      ↓
            ┌────────────────────┐
            │   ViewModel Layer  │
            │   State Management │
            └─────────┬──────────┘
                      ↓
            ┌────────────────────┐
            │   UseCases Layer   │
            │   Business Logic   │
            └─────────┬──────────┘
                      ↓
            ┌────────────────────┐
            │   Repository Layer │
            │   Data Handling    │
            └─────────┬──────────┘
                      ↓
            ┌────────────────────┐
            │   API / Backend    │
            │   Server Layer     │
            └────────────────────┘


# 🔄 API Flow Diagram


User Action (UI)
↓
ViewModel Trigger
↓
UseCase Execution
↓
Repository Call
↓
API Request → Backend Server
↓
Response (Success / Error)
↓
Repository Mapping
↓
ViewModel State Update
↓
UI Recomposition (Compose)



# ✨ Features

# 🔐 Authentication
- Email validation  
- Secure password input  
- Error handling  
- Backend authentication  


# 📊 Dashboard
- Real-time balance  
- Transaction history  
- Backend sync


# 🏦 Accounts
- Multiple account types  
- Create account  
- Real-time updates  


# 💳 Card Controls
- View cards  
- Card details  
- Order new card  


# 💸 Payments
- Bill payments  
- Validation  
- Success / failure states  


# 📦 Bulk Payments
- Batch processing  
- Independent transaction states  
- Granular feedback  


# 🤝 P2P Transfer
- Send & request money  
- Identity validation  
- Real-time updates  


# 📷 QR Payments
- CameraX integration  
- QR parsing  
- Instant payments  


# ⚙️ Tech Stack

- Kotlin  
- Jetpack Compose  
- MVVM Architecture  
- Coroutines & Flow  
- REST APIs  
- Material 3  
- CameraX  


# 🔐 Security

- Input validation  
- Secure flows  
- Controlled state transitions  
- Backend verification  


# ⚡ Performance

- LazyColumn optimization  
- Efficient recomposition  
- Smooth UI updates  


# 🧠 Product Thinking

> “How can financial interactions feel simple, fast and trustworthy?”

# Focus:

- Clarity  
- Speed  
- Reliability  
- Minimal friction  



# 📦 Setup Guide

# 1. Clone

git clone (https://github.com/Avi6855/MonzoBank.git)


# 2. Open in Android Studio

- Sync Gradle  
- Install SDK  

# 3. Configure API

BASE_URL = "YOUR_API_URL"


# 4. Run App

- Select device  
- Click Run ▶️  

# 📂 Project Structure

app/
├── data/
├── domain/
├── ui/
├── viewmodel/
├── network/
└── utils/


# 📌 Future Work

- International Transfers 🌍  
- Notifications 🔔  
- Offline mode  
- Compose Multiplatform  


# 👨‍💻 About Me

**Avinash Patil**  
Android Engineer | Kotlin | Jetpack Compose  

🔗 LinkedIn: https://www.linkedin.com/in/avinash-patil-278011228/  
💻 GitHub: https://github.com/Avi6855?tab=repositories
 #  Email: avinashpatil6855@gmail.com

# 📩 Contact

Open for Android & Fintech opportunities 🚀


# ⭐ Support

⭐ Star the repo  
🍴 Fork  
💬 Feedback  
