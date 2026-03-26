# BudgetWise Backend

Spring Boot application for BudgetWise personal finance management system.

## Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Database**: MySQL 8.0
- **Security**: Spring Security with JWT
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0 or higher installed and running
- MySQL Workbench (optional, for database management)

## Database Setup

1. Ensure MySQL is running on `localhost:3306`
2. Create a MySQL user with credentials:
   - **Username**: `root`
   - **Password**: `root`
   - (Or update credentials in `application.properties`)

3. The application will automatically create the database `budgetwiseDb4` on first run (thanks to `spring.jpa.hibernate.ddl-auto=update`)

### Manual Database Creation (Optional)

```sql
CREATE DATABASE budgetwiseDb4;
USE budgetwiseDb4;
```

## Configuration

All configuration is in `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/budgetwiseDb4?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root

# Server
server.port=8080

# JWT
jwt.secret=your-256-bit-secret-key-here-change-this-in-production
jwt.expiration=86400000 # 24 hours
```

**⚠️ Important**: Change `jwt.secret` in production!

## Running the Application

### Using Maven

```bash
cd backend
mvn spring-boot:run
```

### Using Maven Wrapper (if available)

```bash
cd backend
./mvnw spring-boot:run  # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

### Building JAR

```bash
mvn clean package
java -jar target/budgetwise-0.0.1-SNAPSHOT.jar
```

## API Endpoints

Base URL: `http://localhost:8080/api`

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | Login user | No |

### Transactions

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/transactions` | Get all transactions | Yes |
| GET | `/transactions/{id}` | Get transaction by ID | Yes |
| GET | `/transactions/date-range` | Get transactions by date range | Yes |
| POST | `/transactions` | Create transaction | Yes |
| PUT | `/transactions/{id}` | Update transaction | Yes |
| DELETE | `/transactions/{id}` | Delete transaction | Yes |
| GET | `/transactions/total-income` | Get total income | Yes |
| GET | `/transactions/total-expenses` | Get total expenses | Yes |
| GET | `/transactions/expenses-by-category` | Get expense breakdown | Yes |
| GET | `/transactions/income-by-category` | Get income breakdown | Yes |

### Budgets

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/budgets` | Get all budgets | Yes |
| GET | `/budgets/{id}` | Get budget by ID | Yes |
| GET | `/budgets/by-period` | Get budgets by month/year | Yes |
| POST | `/budgets` | Create budget | Yes |
| PUT | `/budgets/{id}` | Update budget | Yes |
| DELETE | `/budgets/{id}` | Delete budget | Yes |

### Savings Goals

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/savings-goals` | Get all goals | Yes |
| GET | `/savings-goals/active` | Get active goals | Yes |
| GET | `/savings-goals/{id}` | Get goal by ID | Yes |
| POST | `/savings-goals` | Create goal | Yes |
| PUT | `/savings-goals/{id}` | Update goal | Yes |
| PUT | `/savings-goals/{id}/progress` | Update goal progress | Yes |
| DELETE | `/savings-goals/{id}` | Delete goal | Yes |

### Financial Summary

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/summary` | Get financial summary | Yes |

### Export

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/export/csv` | Export data to CSV | Yes |

## Request/Response Examples

### Register User

**Request:**
```json
POST /api/auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "name": "John Doe",
  "email": "john@example.com"
}
```

### Login

**Request:**
```json
POST /api/auth/login
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "name": "John Doe",
  "email": "john@example.com"
}
```

### Create Transaction

**Request:**
```json
POST /api/transactions
Authorization: Bearer <token>
{
  "amount": 1500.00,
  "category": "Salary",
  "description": "Monthly salary",
  "date": "2024-01-15",
  "type": "INCOME"
}
```

**Response:**
```json
{
  "id": 1,
  "amount": 1500.00,
  "category": "Salary",
  "description": "Monthly salary",
  "date": "2024-01-15",
  "type": "INCOME"
}
```

### Create Budget

**Request:**
```json
POST /api/budgets
Authorization: Bearer <token>
{
  "category": "Groceries",
  "amount": 500.00,
  "month": 1,
  "year": 2024,
  "period": "MONTHLY"
}
```

**Response:**
```json
{
  "id": 1,
  "category": "Groceries",
  "amount": 500.00,
  "spent": 0.00,
  "month": 1,
  "year": 2024,
  "period": "MONTHLY"
}
```

### Get Financial Summary

**Request:**
```
GET /api/summary
Authorization: Bearer <token>
```

**Response:**
```json
{
  "totalIncome": 5000.00,
  "totalExpenses": 3500.00,
  "balance": 1500.00,
  "savingsRate": 30.0,
  "expensesByCategory": {
    "Groceries": 800.00,
    "Rent": 1500.00,
    "Utilities": 200.00,
    "Entertainment": 500.00,
    "Transportation": 500.00
  },
  "incomeByCategory": {
    "Salary": 4500.00,
    "Freelance": 500.00
  }
}
```

## Security

- All endpoints except `/api/auth/**` require JWT authentication
- Send JWT token in Authorization header: `Bearer <token>`
- Passwords are encrypted using BCrypt
- CORS is enabled for `localhost:3000` and `localhost:5173`

## Database Schema

### Users Table
- `id` (Primary Key)
- `name`
- `email` (Unique)
- `password` (Encrypted)
- `active`
- `roles`

### Transactions Table
- `id` (Primary Key)
- `user_id` (Foreign Key)
- `amount`
- `category`
- `description`
- `date`
- `type` (INCOME/EXPENSE)
- `created_at`

### Budgets Table
- `id` (Primary Key)
- `user_id` (Foreign Key)
- `category`
- `amount`
- `spent`
- `month`
- `year`
- `period` (MONTHLY/YEARLY)
- `created_at`

### Savings Goals Table
- `id` (Primary Key)
- `user_id` (Foreign Key)
- `name`
- `target_amount`
- `current_amount`
- `target_date`
- `achieved`
- `created_at`

### Forum Posts Table
- `id` (Primary Key)
- `user_id` (Foreign Key)
- `title`
- `content`
- `category`
- `likes`
- `views`
- `created_at`
- `updated_at`

## Testing

### Using cURL

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'

# Get Summary (replace <token> with actual JWT)
curl http://localhost:8080/api/summary \
  -H "Authorization: Bearer <token>"
```

### Using Postman

1. Import the API endpoints
2. Set up environment variable for base URL: `http://localhost:8080/api`
3. Register/Login to get JWT token
4. Add token to Authorization header for protected endpoints

## Troubleshooting

### Database Connection Error

- Ensure MySQL is running: `mysql -u root -p`
- Check username/password in `application.properties`
- Verify MySQL is on port 3306

### Port Already in Use

```bash
# Change port in application.properties
server.port=8081
```

### JWT Token Issues

- Ensure token is sent in header: `Authorization: Bearer <token>`
- Check token hasn't expired (24 hour expiration)
- Verify jwt.secret matches between requests

## Development

### Project Structure

```
backend/
├── src/main/java/com/budgetwise/
│   ├── BudgetWiseApplication.java    # Main application
│   ├── controller/                    # REST controllers
│   │   ├── AuthController.java
│   │   ├── TransactionController.java
│   │   ├── BudgetController.java
│   │   ├── SavingsGoalController.java
│   │   ├── FinancialSummaryController.java
│   │   └── ExportController.java
│   ├── service/                       # Business logic
│   │   ├── AuthService.java
│   │   ├── TransactionService.java
│   │   ├── BudgetService.java
│   │   ├── SavingsGoalService.java
│   │   ├── FinancialSummaryService.java
│   │   └── ExportService.java
│   ├── repository/                    # Data access
│   │   ├── UserRepository.java
│   │   ├── TransactionRepository.java
│   │   ├── BudgetRepository.java
│   │   ├── SavingsGoalRepository.java
│   │   └── ForumPostRepository.java
│   ├── model/                         # Entity classes
│   │   ├── User.java
│   │   ├── Transaction.java
│   │   ├── Budget.java
│   │   ├── SavingsGoal.java
│   │   └── ForumPost.java
│   ├── dto/                           # Data transfer objects
│   └── security/                      # Security config
│       ├── JwtUtil.java
│       ├── JwtAuthenticationFilter.java
│       ├── UserDetailsServiceImpl.java
│       └── SecurityConfig.java
└── src/main/resources/
    └── application.properties
```

## License

MIT
