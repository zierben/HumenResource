#!/bin/bash

# HR外包人力管理系统 - Linux一键部署脚本
# 使用方法: chmod +x deploy.sh && ./deploy.sh

set -e

echo "=========================================="
echo "  HR外包人力管理系统 - 一键部署"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 打印函数
info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        error "$1 未安装，请先安装 $1"
    fi
}

# 检查并安装Docker
install_docker() {
    if command -v docker &> /dev/null; then
        info "Docker已安装"
        return
    fi
    
    info "正在安装Docker..."
    curl -fsSL https://get.docker.com | sh
    systemctl start docker
    systemctl enable docker
    info "Docker安装完成"
}

# 检查并安装Docker Compose
install_docker_compose() {
    if command -v docker-compose &> /dev/null; then
        info "Docker Compose已安装"
        return
    fi
    
    info "正在安装Docker Compose..."
    curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    info "Docker Compose安装完成"
}

# 主函数
main() {
    info "开始部署..."
    
    # 检查系统
    if [ "$(id -u)" != "0" ]; then
        error "请使用root用户运行此脚本"
    fi
    
    # 安装依赖
    install_docker
    install_docker_compose
    
    # 获取代码
    if [ ! -d "HumenResource" ]; then
        info "正在克隆代码..."
        git clone https://github.com/zierben/HumenResource.git
        cd HumenResource
    else
        info "代码目录已存在，跳过克隆"
        cd HumenResource
    fi
    
    # 构建并启动
    info "正在构建Docker镜像..."
    docker-compose build
    
    info "正在启动服务..."
    docker-compose up -d
    
    # 等待服务启动
    info "等待服务启动..."
    sleep 30
    
    # 检查服务状态
    if docker-compose ps | grep -q "Up"; then
        info "服务启动成功！"
    else
        error "服务启动失败，请查看日志: docker-compose logs"
    fi
    
    # 获取服务器IP
    SERVER_IP=$(curl -s ifconfig.me || hostname -I | awk '{print $1}')
    
    echo ""
    echo "=========================================="
    echo -e "${GREEN}  部署完成！${NC}"
    echo "=========================================="
    echo ""
    echo "访问地址: http://$SERVER_IP"
    echo "默认账号: admin / admin123"
    echo ""
    echo "常用命令:"
    echo "  查看日志: docker-compose logs -f"
    echo "  重启服务: docker-compose restart"
    echo "  停止服务: docker-compose down"
    echo ""
    echo "数据库信息:"
    echo "  地址: localhost:3306"
    echo "  用户: root"
    echo "  密码: root123456"
    echo "  数据库: hr_system"
    echo ""
    warn "生产环境请修改数据库密码和JWT密钥！"
    echo "=========================================="
}

main "$@"
