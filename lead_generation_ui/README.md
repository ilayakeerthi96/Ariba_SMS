# Employee Onboarding (on-prem)

This repository contains a minimal on-premises Employee Onboarding application: an Express backend (with SQLite) and an Angular frontend.

Quick start (Windows PowerShell):

1. Server

```powershell
cd server
npm install
npm run migrate
npm start
```

2. Client

```powershell
cd client
npm install
npm start
```

The server listens on http://localhost:3000 and exposes APIs for invite generation and form submissions. Uploaded files and the SQLite database are stored locally under `uploads/` and `server/data.sqlite` respectively.

Notes:
- This scaffold is a minimal starting point — it focuses on local-only storage (uploads + SQLite).
- For production on-prem deployments, run the server behind a reverse proxy and secure static file access.

