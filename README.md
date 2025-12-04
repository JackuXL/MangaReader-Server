# Manga Server

一个通用的漫画网站后端 API 服务，基于 Spring Boot 3.2 构建。

## 功能特性

- 🔐 **用户认证**: JWT 令牌认证，支持用户注册和登录
- 📚 **漫画管理**: 完整的漫画 CRUD 操作，支持批量导入
- 📖 **章节管理**: 章节的创建和查询
- ❤️ **收藏功能**: 用户收藏漫画功能
- 🏷️ **分类筛选**: 按标签、地区、受众等多维度筛选
- 🔍 **搜索功能**: 关键词搜索漫画
- 📢 **公告系统**: 支持配置公告信息
- 🖼️ **轮播图**: 支持配置首页轮播图
- 🌐 **CDN 支持**: 可配置 CDN 加速图片访问
- 📄 **API 文档**: 自动生成 OpenAPI (Swagger) 文档

## 技术栈

- **框架**: Spring Boot 3.2
- **安全**: Spring Security + JWT
- **数据库**: MySQL + Spring Data JPA
- **缓存**: Redis
- **文档**: SpringDoc OpenAPI (Swagger)
- **构建**: Maven
- **容器**: Docker

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.9+
- MySQL 8.0+
- Redis 6.0+

### 本地开发

1. 克隆项目
```bash
git clone <your-repo-url>
cd manga-server
```

2. 配置数据库

创建 MySQL 数据库：
```sql
CREATE DATABASE manga_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 配置环境变量（或修改 `application.yml`）
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

4. 运行项目
```bash
mvn spring-boot:run
```

5. 访问 API 文档
```
http://localhost:8080/swagger-ui.html
```

### Docker 部署

```bash
# 构建镜像
docker build -t manga-server .

# 运行容器
docker run -p 8080:8080 \
  -e MYSQL_HOST=your-mysql-host \
  -e MYSQL_USERNAME=root \
  -e MYSQL_PASSWORD=your_password \
  -e REDIS_HOST=your-redis-host \
  manga-server
```

或使用部署脚本：
```bash
./deploy.sh start
```

## API 概览

| 模块 | 端点 | 描述 |
|------|------|------|
| 认证 | `POST /api/auth/register` | 用户注册 |
| 认证 | `POST /api/auth/login` | 用户登录 |
| 漫画 | `GET /api/manga` | 获取漫画列表 |
| 漫画 | `GET /api/manga/{id}` | 获取漫画详情 |
| 漫画 | `GET /api/manga/search` | 搜索漫画 |
| 章节 | `GET /api/chapters/manga/{mangaId}` | 获取章节列表 |
| 收藏 | `POST /api/favorites/{mangaId}` | 添加收藏 |
| 管理 | `POST /api/admin/manga/import` | 导入漫画 (管理员) |

完整 API 文档请访问 `/swagger-ui.html`

## 配置说明

主要配置项（通过环境变量设置）：

| 变量名 | 描述 | 默认值 |
|--------|------|--------|
| `MYSQL_HOST` | MySQL 主机 | localhost |
| `MYSQL_PORT` | MySQL 端口 | 3306 |
| `MYSQL_DATABASE` | 数据库名 | manga_db |
| `MYSQL_USERNAME` | 数据库用户名 | root |
| `MYSQL_PASSWORD` | 数据库密码 | - |
| `REDIS_HOST` | Redis 主机 | localhost |
| `REDIS_PORT` | Redis 端口 | 6379 |
| `JWT_SECRET` | JWT 密钥 | - |
| `JWT_EXPIRATION` | JWT 过期时间(ms) | 86400000 |
| `CDN_BASE_URL` | CDN 基础 URL | - |
| `BANNER_JSON` | 轮播图配置 JSON | [] |
| `ANNOUNCEMENT_JSON` | 公告配置 JSON | [] |

## 项目结构

```
src/main/java/com/manga/
├── config/          # 配置类
├── controller/      # REST 控制器
├── dto/             # 数据传输对象
├── entity/          # 数据库实体
├── exception/       # 异常处理
├── repository/      # 数据访问层
├── security/        # 安全配置
└── service/         # 业务逻辑层
```

## 许可证

MIT License

---

## ⚠️ 免责声明

**重要提示：请仔细阅读以下免责声明**

1. **学习用途**: 本项目仅供学习和研究目的使用，不得用于任何商业用途。

2. **内容责任**: 本项目是一个通用的后端服务框架，不提供任何漫画内容。使用者需要自行负责其托管的内容，并确保符合当地法律法规。

3. **版权声明**: 使用本项目时，请尊重原创作品的版权。任何通过本系统传播的内容，其版权归原作者所有。用户不得上传、分享任何侵犯版权的内容。

4. **法律合规**: 使用者需自行确保其使用行为符合所在地区的法律法规。开发者不对因使用本项目而产生的任何法律问题承担责任。

5. **无担保声明**: 本软件按"原样"提供，不提供任何明示或暗示的保证，包括但不限于适销性、特定用途适用性和非侵权性的保证。

6. **免责条款**: 在任何情况下，开发者均不对因使用本软件或无法使用本软件而产生的任何直接、间接、偶然、特殊、惩罚性或后果性损害承担责任。

**使用本项目即表示您已阅读并同意上述免责声明。**

