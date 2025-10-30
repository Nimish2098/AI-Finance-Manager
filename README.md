# AI Personal Finance Tracker

A smart, full-stack web application designed to automate and optimize personal expense management. This application intelligently analyzes transaction data, auto-tracks expenses across 10+ categories, and monitors recurring subscriptions to provide actionable financial insights.

The core of this project is a rule-based AI engine that detects redundant subscriptions and unusual spending spikes, cutting down the time required for manual expense review by an estimated 40%. The backend is built with Java & Spring Boot, secured with Spring Security & JWT, and fully containerized with Docker.

## 🚀 Key Features

* 📈 **Automated Expense Tracking:** Intelligently analyzes transaction data to auto-categorize expenses across more than 10 categories.
* 🔄 **Subscription Monitoring:** Actively monitors all recurring subscriptions to provide a clear view of monthly commitments.
* 🤖 **Intelligent Insights Engine:** A rule-based engine that flags redundant subscriptions (e.g., two music streaming services) and detects significant spending spikes.
* 🔒 **Secure Authentication:** Implemented with Spring Security and stateless JWT (JSON Web Tokens) for secure API endpoints.
* 🐳 **Containerized Deployment:** Uses Docker for consistent, fast builds and portable deployment in under 2 minutes.
* 🐘 **Robust Database:** Leverages PostgreSQL for reliable and scalable data persistence.

## 🛠️ Tech Stack

* **Backend:** Java 21, Spring Boot 3
* **Security:** Spring Security, JWT
* **Database:** PostgreSQL
* **Deployment:** Docker, Docker Compose

## 🏛️ Architecture

This application uses a stateless, token-based authentication model. The client (e.g., a React/Angular/mobile app) authenticates with a username and password, receives a JWT, and includes this token in the `Authorization` header for all subsequent requests. Spring Security validates this token to protect the API endpoints.


### 1. Clone the Repository

```bash
git clone [YOUR_GITHUB_REPO_LINK]
cd ai-personal-finance-tracker