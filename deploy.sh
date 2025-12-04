#!/bin/bash

# Manga Server 部署脚本
# 使用方式: ./deploy.sh [start|stop|restart|logs|status]

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Docker 是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        log_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
}

# 检查环境变量文件
check_env() {
    if [ ! -f .env ]; then
        log_warn ".env 文件不存在"
        if [ -f env.example ]; then
            log_info "从 env.example 复制配置..."
            cp env.example .env
            log_warn "请编辑 .env 文件并配置必要的环境变量"
            exit 1
        else
            log_error "env.example 文件不存在"
            exit 1
        fi
    fi
}

# 启动服务
start() {
    log_info "启动 Manga Server..."
    check_docker
    check_env
    
    docker-compose up -d
    
    log_info "等待服务启动..."
    sleep 10
    
    if docker-compose ps | grep -q "Up"; then
        log_info "服务启动成功！"
        log_info "访问 http://localhost:8080/swagger-ui.html 查看 API 文档"
    else
        log_error "服务启动失败，请查看日志"
        docker-compose logs --tail=50
        exit 1
    fi
}

# 停止服务
stop() {
    log_info "停止 Manga Server..."
    docker-compose down
    log_info "服务已停止"
}

# 重启服务
restart() {
    log_info "重启 Manga Server..."
    stop
    sleep 2
    start
}

# 查看日志
logs() {
    docker-compose logs -f --tail=100 app
}

# 查看状态
status() {
    log_info "服务状态："
    docker-compose ps
    
    echo ""
    log_info "健康检查："
    curl -s http://localhost:8080/actuator/health 2>/dev/null || echo "服务未响应"
}

# 构建镜像
build() {
    log_info "构建 Manga Server 镜像..."
    docker-compose build app
    log_info "构建完成"
}

# 更新服务
update() {
    log_info "更新 Manga Server..."
    
    # 拉取最新代码（如果是 Git 仓库）
    if [ -d .git ]; then
        log_info "拉取最新代码..."
        git pull
    fi
    
    # 重新构建
    build
    
    # 重启服务
    restart
}

# 备份数据
backup() {
    log_info "备份数据..."
    
    BACKUP_DIR="./backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$BACKUP_DIR"
    
    # 加载环境变量
    if [ -f .env ]; then
        export $(grep -v '^#' .env | xargs)
    fi
    
    # 备份 MySQL
    log_info "备份 MySQL..."
    docker-compose exec -T mysql mysqldump -u root -p"${DB_PASSWORD:-}" manga_db > "$BACKUP_DIR/mysql_backup.sql" 2>/dev/null || log_warn "MySQL 备份失败"
    
    # 备份 Redis
    log_info "备份 Redis..."
    docker-compose exec redis redis-cli -a "${REDIS_PASSWORD:-}" save 2>/dev/null || log_warn "Redis 备份失败"
    docker cp manga-redis:/data/dump.rdb "$BACKUP_DIR/redis_backup.rdb" 2>/dev/null || log_warn "Redis 备份文件复制失败"
    
    log_info "备份完成: $BACKUP_DIR"
}

# 清理旧容器和镜像
clean() {
    log_warn "清理未使用的 Docker 资源..."
    docker system prune -f
    log_info "清理完成"
}

# 显示帮助
help() {
    echo "Manga Server 部署脚本"
    echo ""
    echo "使用方式: ./deploy.sh [命令]"
    echo ""
    echo "命令:"
    echo "  start    - 启动服务"
    echo "  stop     - 停止服务"
    echo "  restart  - 重启服务"
    echo "  logs     - 查看日志"
    echo "  status   - 查看状态"
    echo "  build    - 构建镜像"
    echo "  update   - 更新服务（拉取代码、重新构建、重启）"
    echo "  backup   - 备份数据"
    echo "  clean    - 清理未使用的 Docker 资源"
    echo "  help     - 显示帮助"
    echo ""
}

# 主函数
main() {
    case "$1" in
        start)
            start
            ;;
        stop)
            stop
            ;;
        restart)
            restart
            ;;
        logs)
            logs
            ;;
        status)
            status
            ;;
        build)
            build
            ;;
        update)
            update
            ;;
        backup)
            backup
            ;;
        clean)
            clean
            ;;
        help|--help|-h|"")
            help
            ;;
        *)
            log_error "未知命令: $1"
            help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
