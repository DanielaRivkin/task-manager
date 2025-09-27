# 📋 Task Manager

A modern, full-stack task management application built with Spring Boot and vanilla JavaScript. 
## ✨ Features

- **📝 Task Management**: Create, update, delete, and mark tasks as complete
- **🔍 Advanced Filtering**: Search by title, description, assignee, category, status, and due dates
- **📊 Status Tracking**: Track task status (OPEN, IN_PROGRESS, DONE)
- **⚡ Priority Levels**: Set task priorities (LOW, MEDIUM, HIGH)
- **📅 Due Dates**: Set and track task deadlines
- **👥 Assignment**: Assign tasks to team members
- **🏷️ Categories**: Organize tasks by categories
- **📱 Responsive Design**: Works on desktop, tablet, and mobile devices
- **🎨 Modern UI**: Beautiful dark theme with smooth animations

## 🚀 Quick Start

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- A web browser (Chrome, Firefox, Safari, Edge)

### Running the Application

1. **Clone or download** the project to your local machine

2. **Navigate** to the project directory:
   ```bash
   cd task_manager
   ```

3. **Run the application**:
   ```bash
   # Windows
   ./mvnw.cmd spring-boot:run
   
   # macOS/Linux
   ./mvnw spring-boot:run
   ```

4. **Open your browser** and visit:
   ```
   http://localhost:8080
   ```

## 🛠️ Technology Stack

### Backend
- **Java 11+** - Programming language
- **Spring Boot 2.7+** - Application framework
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database (development)
- **Maven** - Build tool and dependency management

### Frontend
- **Vanilla JavaScript** - No frameworks, pure JS
- **HTML5** - Semantic markup
- **CSS3** - Modern styling with animations
- **Responsive Design** - Mobile-first approach

## 📁 Project Structure

```
task_manager/
├── src/
│   ├── main/
│   │   ├── java/com/example/task_manager/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST API controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── exception/      # Exception handling
│   │   │   ├── mapper/         # Object mappers
│   │   │   ├── repository/     # Data repositories
│   │   │   ├── service/        # Business logic
│   │   │   └── TaskManagerApplication.java
│   │   └── resources/
│   │       ├── static/         # Frontend files
│   │       │   ├── index.html  # Main HTML page
│   │       │   ├── styles.css  # CSS styling
│   │       │   └── app.js      # JavaScript functionality
│   │       ├── application.yml # Configuration
│   │       └── db/migration/   # Database migrations
│   └── test/                   # Unit and integration tests
├── pom.xml                     # Maven dependencies
└── README.md                   # This file
```

## 🎯 API Endpoints

### Tasks
- `GET /api/v1/tasks` - Get all tasks with filtering and pagination
- `POST /api/v1/tasks` - Create a new task
- `PATCH /api/v1/tasks/{id}` - Update an existing task
- `DELETE /api/v1/tasks/{id}` - Delete a task

### Query Parameters
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `sort` - Sort field (createdAt, dueDate, -priority)
- `status` - Filter by status (OPEN, IN_PROGRESS, DONE)
- `assignee` - Filter by assignee
- `category` - Filter by category
- `dueAfter` - Filter tasks due after date
- `dueBefore` - Filter tasks due before date

## 🎨 UI Features

### Modern Design
- **Dark Theme**: Easy on the eyes with professional look
- **Gradient Backgrounds**: Beautiful visual depth
- **Smooth Animations**: Hover effects and transitions
- **Card-based Layout**: Clean, organized interface

### Interactive Elements
- **Responsive Buttons**: Hover effects and smooth transitions
- **Enhanced Forms**: Better input styling and focus states
- **Status Pills**: Color-coded task status indicators
- **Priority Colors**: Visual priority level indicators
- **Toggle Switch**: Custom-styled compact view toggle

### Icons and Visual Cues
- ✨ New Task creation
- 🔍 Search and filtering
- 🗑️ Delete actions
- ✅ Mark as complete
- 📋 Compact view toggle
- 📊 Task statistics

## 🔧 Configuration

### Database Configuration
The application uses H2 in-memory database by default. To use a different database:

1. **Update `application.yml`**:
   ```yaml
   spring:
     datasource:
       url: jdbc:your-database-url
       username: your-username
       password: your-password
   ```

2. **Add database driver dependency** in `pom.xml`

### Port Configuration
To change the default port (8080), update `application.yml`:
```yaml
server:
  port: 8081
```

## 🧪 Testing

Run the test suite:
```bash
./mvnw.cmd test
```

The project includes:
- **Unit Tests**: Service layer testing
- **Integration Tests**: Controller and API testing
- **Slice Tests**: Focused component testing

## 📱 Browser Support

- ✅ Chrome 80+
- ✅ Firefox 75+
- ✅ Safari 13+
- ✅ Edge 80+

## 🚀 Deployment

### Development
```bash
./mvnw.cmd spring-boot:run
```

### Production Build
```bash
./mvnw.cmd clean package
java -jar target/task_manager-*.jar
```

### Docker (Optional)
```dockerfile
FROM openjdk:11-jre-slim
COPY target/task_manager-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request


## 🆘 Troubleshooting

### Common Issues

**Port 8080 already in use:**
```bash
# Kill process using port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Maven wrapper not executable (Linux/macOS):**
```bash
chmod +x mvnw
```

**Database connection issues:**
- Check `application.yml` configuration
- Ensure database server is running
- Verify credentials

## 📞 Support

If you encounter any issues or have questions:
1. Check the troubleshooting section above
2. Review the application logs
3. Create an issue in the repository

---

**Happy Task Managing!** 🎉✨
