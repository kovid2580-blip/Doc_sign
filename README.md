# Doc Sign

Full-stack document signature app with a Spring Boot backend and a Vite frontend.

## Project Structure

```text
backend/doc_signature/   Spring Boot API
frontend/                Vite frontend
```

## Requirements

- Java 17
- Node.js 20 or newer
- PostgreSQL

## Backend Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE signature_db;
```

Run the backend from `backend/doc_signature`:

```powershell
cd backend\doc_signature
$env:DB_URL="jdbc:postgresql://localhost:5432/signature_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_postgres_password"
$env:JWT_SECRET="replace-with-a-long-secret"
$env:MAIL_USERNAME="your_email@example.com"
$env:MAIL_PASSWORD="your_mail_app_password"
.\mvnw.cmd spring-boot:run
```

The API runs on `http://localhost:8080`.

Mail variables are only needed when generating signing links that send email.

## Frontend Setup

Run the frontend from `frontend`:

```powershell
cd frontend
npm install
npm run dev
```

The app runs on `http://127.0.0.1:5173`.

## API Quick Reference

Register:

```http
POST /api/users/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

Login:

```http
POST /api/users/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

Use the returned token for protected document routes:

```http
Authorization: Bearer <token>
```

Upload a PDF:

```http
POST /api/docs/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file=<pdf file>
userId=<login userId>
```

List documents:

```http
GET /api/docs?userId=<login userId>
Authorization: Bearer <token>
```

Preview a document:

```http
GET /api/docs/{documentId}/preview
Authorization: Bearer <token>
```

Save a signature position:

```http
POST /api/signatures
Content-Type: application/json

{
  "documentId": 1,
  "userId": 1,
  "xCoordinate": 120,
  "yCoordinate": 240,
  "pageNumber": 1
}
```

Generate a signing link:

```http
POST /api/sign-links/generate?documentId=1&signerEmail=signer@example.com
```

## Verification

Backend:

```powershell
cd backend\doc_signature
.\mvnw.cmd test
```

Frontend:

```powershell
cd frontend
npm run build
```
