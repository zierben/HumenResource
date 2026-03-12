# HR外包人力管理系统 - Linux部署指南

## 一、环境要求

- Linux (CentOS 7+ / Ubuntu 18.04+)
- Docker 20.10+
- Docker Compose 2.0+
- 内存: 4GB+
- 磁盘: 20GB+

## 二、一键部署

```bash
# 1. 克隆代码
git clone https://github.com/zierben/HumenResource.git
cd HumenResource

# 2. 执行部署脚本
chmod +x deploy.sh
./deploy.sh

# 3. 访问系统
# 前端: http://服务器IP
# 后端: http://服务器IP:8080
```

## 三、手动部署

### 3.1 安装Docker

```bash
# CentOS
yum install -y docker docker-compose
systemctl start docker
systemctl enable docker

# Ubuntu
apt update
apt install -y docker.io docker-compose
systemctl start docker
systemctl enable docker
```

### 3.2 构建并启动

```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 查看日志
docker-compose logs -f
```

### 3.3 初始化数据

```bash
# 进入MySQL容器
docker exec -it hr-mysql mysql -uroot -p123456

# 执行初始化SQL
source /docker-entrypoint-initdb.d/schema.sql
```

## 四、配置说明

### 4.1 环境变量

创建 `.env` 文件:

```env
# MySQL配置
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=hr_system

# 后端配置
SERVER_PORT=8080
JWT_SECRET=your-jwt-secret-key

# 前端配置
VITE_API_BASE_URL=http://localhost:8080
```

### 4.2 端口说明

| 服务 | 端口 | 说明 |
|-----|------|------|
| Nginx | 80 | 前端访问 |
| Spring Boot | 8080 | 后端API |
| MySQL | 3306 | 数据库 |

### 4.3 目录结构

```
/opt/hr-system/
├── deploy.sh              # 部署脚本
├── docker-compose.yml     # Docker编排
├── hr-admin/              # 后端代码
├── hr-web/                # 前端代码
├── nginx/                 # Nginx配置
├── mysql/                 # MySQL数据
└── logs/                  # 日志目录
```

## 五、运维命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f hr-admin
docker-compose logs -f hr-web

# 进入容器
docker exec -it hr-admin bash
docker exec -it hr-mysql mysql -uroot -p123456

# 备份数据库
docker exec hr-mysql mysqldump -uroot -p123456 hr_system > backup_$(date +%Y%m%d).sql

# 恢复数据库
docker exec -i hr-mysql mysql -uroot -p123456 hr_system < backup_20240101.sql
```

## 六、更新部署

```bash
# 拉取最新代码
git pull

# 重新构建
docker-compose build

# 重启服务
docker-compose up -d
```

## 七、默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 总经理(GM) |
| hr001 | admin123 | HR专员 |

## 八、常见问题

### 8.1 端口被占用
```bash
# 查看端口占用
netstat -tlnp | grep :80
netstat -tlnp | grep :8080

# 修改docker-compose.yml中的端口映射
```

### 8.2 内存不足
```bash
# 调整JVM参数
# 在docker-compose.yml中添加:
environment:
  - JAVA_OPTS=-Xms512m -Xmx1024m
```

### 8.3 数据库连接失败
```bash
# 检查MySQL是否启动
docker-compose ps hr-mysql

# 查看MySQL日志
docker-compose logs hr-mysql
```
