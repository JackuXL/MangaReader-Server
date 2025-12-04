# Manga Server

A generic manga website backend API service built with Spring Boot 3.2.

## Features

- 🔐 **Authentication**: JWT token-based authentication with user registration and login
- 📚 **Manga Management**: Full CRUD operations for manga, with batch import support
- 📖 **Chapter Management**: Chapter creation and retrieval
- ❤️ **Favorites**: User manga favorites functionality
- 🏷️ **Category Filtering**: Filter by tags, region, audience, and more
- 🔍 **Search**: Keyword search for manga
- 📢 **Announcements**: Configurable announcement system
- 🖼️ **Banners**: Configurable homepage banner carousel
- 🌐 **CDN Support**: Configurable CDN for image acceleration
- 📄 **API Documentation**: Auto-generated OpenAPI (Swagger) documentation

## Tech Stack

- **Framework**: Spring Boot 3.2
- **Security**: Spring Security + JWT
- **Database**: MySQL + Spring Data JPA
- **Cache**: Redis
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build**: Maven
- **Container**: Docker

## Quick Start

### Requirements

- JDK 17+
- Maven 3.9+
- MySQL 8.0+
- Redis 6.0+

### Local Development

1. Clone the project
```bash
git clone <your-repo-url>
cd manga-server
```

2. Set up the database

Create a MySQL database:
```sql
CREATE DATABASE manga_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Configure environment variables (or modify `application.yml`)
```bash
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DATABASE=manga_db
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=your_password
export REDIS_HOST=localhost
export REDIS_PORT=6379
export JWT_SECRET=your-secret-key-at-least-256-bits
```

4. Run the project
```bash
mvn spring-boot:run
```

5. Access API documentation
```
http://localhost:8080/swagger-ui.html
```

### Docker Deployment

```bash
# Build the image
docker build -t manga-server .

# Run the container
docker run -p 8080:8080 \
  -e MYSQL_HOST=your-mysql-host \
  -e MYSQL_USERNAME=root \
  -e MYSQL_PASSWORD=your_password \
  -e REDIS_HOST=your-redis-host \
  manga-server
```

Or use the deployment script:
```bash
./deploy.sh start
```

## API Overview

| Module | Endpoint | Description |
|--------|----------|-------------|
| Auth | `POST /api/auth/register` | User registration |
| Auth | `POST /api/auth/login` | User login |
| Manga | `GET /api/manga` | Get manga list |
| Manga | `GET /api/manga/{id}` | Get manga details |
| Manga | `GET /api/manga/search` | Search manga |
| Chapters | `GET /api/chapters/manga/{mangaId}` | Get chapter list |
| Favorites | `POST /api/favorites/{mangaId}` | Add to favorites |
| Admin | `POST /api/admin/manga/import` | Import manga (Admin) |

For complete API documentation, visit `/swagger-ui.html`

## Configuration

Main configuration options (set via environment variables):

| Variable | Description | Default |
|----------|-------------|---------|
| `MYSQL_HOST` | MySQL host | localhost |
| `MYSQL_PORT` | MySQL port | 3306 |
| `MYSQL_DATABASE` | Database name | manga_db |
| `MYSQL_USERNAME` | Database username | root |
| `MYSQL_PASSWORD` | Database password | - |
| `REDIS_HOST` | Redis host | localhost |
| `REDIS_PORT` | Redis port | 6379 |
| `JWT_SECRET` | JWT secret key | - |
| `JWT_EXPIRATION` | JWT expiration (ms) | 86400000 |
| `CDN_BASE_URL` | CDN base URL | - |
| `BANNER_JSON` | Banner config JSON | [] |
| `ANNOUNCEMENT_JSON` | Announcement config JSON | [] |

## Project Structure

```
src/main/java/com/manga/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # Database entities
├── exception/       # Exception handling
├── repository/      # Data access layer
├── security/        # Security configuration
└── service/         # Business logic layer
```

## License

MIT License

---

## ⚠️ Disclaimer

**IMPORTANT: Please read the following disclaimer carefully**

1. **Educational Purpose**: This project is intended for learning and research purposes only. It must not be used for any commercial purposes.

2. **Content Responsibility**: This project is a generic backend service framework and does not provide any manga content. Users are solely responsible for the content they host and must ensure compliance with local laws and regulations.

3. **Copyright Notice**: When using this project, please respect the copyright of original works. Any content distributed through this system remains the property of its original creators. Users must not upload or share any content that infringes upon copyrights.

4. **Legal Compliance**: Users must ensure that their use of this project complies with the laws and regulations of their jurisdiction. The developers assume no responsibility for any legal issues arising from the use of this project.

5. **No Warranty**: This software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose, and noninfringement.

6. **Limitation of Liability**: In no event shall the developers be liable for any direct, indirect, incidental, special, punitive, or consequential damages arising from the use of or inability to use this software.

**By using this project, you acknowledge that you have read and agree to this disclaimer.**

