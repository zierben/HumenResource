# HR外包人力管理系统 - Linux部署指南

## 一、环境要求

- 操作系统: CentOS 7+ / Ubuntu 18.04+
- Java: JDK 17+
- Node.js: 18+ (用于前端构建)
- MySQL: 8.0+
- 内存: 最低2GB
- 磁盘: 最低10GB

## 二、一键部署脚本

创建部署脚本 `deploy.sh`:

```bash
#!/bin/bash

# HR外包人力管理系统 - 一键部署脚本
# 使用方法: chmod +x deploy.sh && ./deploy.sh

set -e

echo "=========================================="
echo "  HR外包人力管理系统 - 一键部署"
echo "=========================================="

# 配置变量
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="root"
DB_PASS="your_password"
DB_NAME="hr_system"

SERVER_IP="your_server_ip"
DEPLOY_DIR="/opt/hr-system"

echo "[1/6] 安装依赖..."
yum install -y java-17-openjdk java-17-openjdk-devel mysql-server nginx

echo "[2/6] 创建部署目录..."
mkdir -p $DEPLOY_DIR/{backend,frontend,logs}

echo "[3/6] 上传文件..."
# 后端
scp hr-admin/target/hr-admin-1.0.0.jar root@$SERVER_IP:$DEPLOY_DIR/backend/
# 前端
scp -r hr-web/dist/* root@$SERVER_IP:$DEPLOY_DIR/frontend/
# SQL
scp hr-admin/src/main/resources/schema.sql root@$SERVER_IP:$DEPLOY_DIR/

echo "[4/6] 初始化数据库..."
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -e "CREATE DATABASE IF NOT EXISTS $DB_NAME;"
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME < $DEPLOY_DIR/schema.sql

echo "[5/6] 配置后端服务..."
cat > /etc/systemd/system/hr-backend.service << 'EOF'
[Unit]
Description=HR System Backend
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/hr-system/backend
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar hr-admin-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

echo "[6/6] 配置Nginx..."
cat > /etc/nginx/conf.d/hr-system.conf << 'EOF'
upstream backend {
    server 127.0.0.1:8080;
}

server {
    listen 80;
    server_name your_domain.com;

    location / {
        root /opt/hr-system/frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
EOF

echo "启动服务..."
systemctl daemon-reload
systemctl enable hr-backend
systemctl start hr-backend
systemctl restart nginx

echo "=========================================="
echo "  部署完成！"
echo "=========================================="
echo "访问地址: http://your_domain.com"
echo "默认账号: admin / admin123"
echo "=========================================="
```

## 三、手动部署步骤

### 1. 安装依赖

```bash
# CentOS/RHEL
yum install -y java-17-openjdk java-17-openjdk-devel mysql-server nginx

# Ubuntu/Debian
apt update && apt install -y openjdk-17-jdk mysql-server nginx
```

### 2. 准备数据库

```bash
# 启动MySQL
systemctl start mysqld
systemctl enable mysqld

# 创建数据库和用户
mysql -u root -p << 'EOF'
CREATE DATABASE IF NOT EXISTS hr_system DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'hr_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON hr_system.* TO 'hr_user'@'localhost';
FLUSH PRIVILEGES;
EOF

# 导入初始数据
mysql -u hr_user -p hr_system < schema.sql
```

### 3. 部署后端

```bash
# 创建目录
mkdir -p /opt/hr-system/backend /opt/hr-system/logs

# 上传jar包
scp hr-admin/target/hr-admin-1.0.0.jar /opt/hr-system/backend/

# 创建配置文件
cat > /opt/hr-system/backend/application-prod.yml << 'EOF'
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hr_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: hr_user
    password: your_password

jwt:
  secret: your-very-long-secret-key-at-least-256-bits
  expiration: 86400000

logging:
  file:
    path: /opt/hr-system/logs
EOF

# 创建systemd服务
cat > /etc/systemd/system/hr-backend.service << 'EOF'
[Unit]
Description=HR System Backend Service
After=network.target mysqld.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/hr-system/backend
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar hr-admin-1.0.0.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 启动服务
systemctl daemon-reload
systemctl enable hr-backend
systemctl start hr-backend
```

### 4. 部署前端

```bash
# 本地构建
cd hr-web
npm install
npm run build

# 上传到服务器
mkdir -p /opt/hr-system/frontend
scp -r dist/* /opt/hr-system/frontend/
```

### 5. 配置Nginx

```bash
cat > /etc/nginx/conf.d/hr-system.conf << 'EOF'
upstream backend {
    server 127.0.0.1:8080;
}

server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /opt/hr-system/frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # API代理
    location /api {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # 日志
    access_log /var/log/nginx/hr-access.log;
    error_log /var/log/nginx/hr-error.log;
}
EOF

# 测试配置
nginx -t

# 重启Nginx
systemctl restart nginx
```

## 四、Docker部署（推荐）

### docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: hr-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123456
      MYSQL_DATABASE: hr_system
      TZ: Asia/Shanghai
    volumes:
      - mysql_data:/var/lib/mysql
      - ./hr-admin/src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    ports:
      - "3306:3306"
    networks:
      - hr-network

  backend:
    build: ./hr-admin
    container_name: hr-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/hr_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root123456
      JWT_SECRET: your-very-long-secret-key-at-least-256-bits
    depends_on:
      - mysql
    ports:
      - "8080:8080"
    networks:
      - hr-network

  frontend:
    build: ./hr-web
    container_name: hr-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - hr-network

volumes:
  mysql_data:

networks:
  hr-network:
```

### 后端Dockerfile (hr-admin/Dockerfile)

```dockerfile
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 前端Dockerfile (hr-web/Dockerfile)

```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 一键启动

```bash
docker-compose up -d
```

## 五、常用命令

```bash
# 查看后端日志
journalctl -u hr-backend -f

# 重启后端
systemctl restart hr-backend

# 查看后端状态
systemctl status hr-backend

# 查看Nginx日志
tail -f /var/log/nginx/hr-access.log

# 数据库备份
mysqldump -u hr_user -p hr_system > backup_$(date +%Y%m%d).sql

# 数据库恢复
mysql -u hr_user -p hr_system < backup_20240101.sql
```

## 六、安全配置

### 1. 防火墙

```bash
# 开放端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --reload
```

### 2. HTTPS配置 (使用Let's Encrypt)

```bash
# 安装certbot
yum install -y certbot python3-certbot-nginx

# 获取证书
certbot --nginx -d your-domain.com

# 自动续期
echo "0 0 1 * * root certbot renew --quiet" | crontab -
```

### 3. 修改JWT密钥

在生产环境中，务必修改JWT密钥：

```yaml
jwt:
  secret: 请使用至少256位的随机字符串
```

生成密钥命令:
```bash
openssl rand -base64 64
```

## 七、性能优化

### 1. JVM参数

```bash
# 修改systemd服务
ExecStart=/usr/bin/java -Xms1g -Xmx2g -XX:+UseG1GC -jar hr-admin-1.0.0.jar
```

### 2. MySQL优化

```sql
-- my.cnf
[mysqld]
innodb_buffer_pool_size = 1G
max_connections = 500
query_cache_size = 64M
```

### 3. Nginx缓存

```nginx
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
    expires 30d;
    add_header Cache-Control "public, immutable";
}
```

## 八、故障排查

```bash
# 后端无法启动
journalctl -u hr-backend -n 100

# 数据库连接失败
mysql -u hr_user -p -h localhost hr_system

# 端口占用
netstat -tlnp | grep -E '8080|80'

# 查看进程
ps aux | grep java
```

## 九、初始账号

- 管理员: admin / admin123
- HR专员: hr001 / admin123

**生产环境请务必修改密码！**

## 十、Git工作流程与CI/CD

### 1. Git分支策略

```
master (生产) ◄──────────────────────┐
    ↑                                │
    │ PR + 标签 v1.0.0              │
    │                                │
release/v1.0.0 (发布) ◄──┐          │
    ↑                      │          │
    │ PR (测试通过)        │          │
    │                      │          │
develop (开发) ◄───────────┘          │
    ↑                                │
    │ PR                            │
    │                                │
feature/xxx ◄───────────────────────┘
```

### 2. 分支说明

| 分支 | 用途 | 生命周期 |
|------|------|----------|
| `master` | 生产环境 | 永久 |
| `develop` | 开发环境 | 永久 |
| `release/v*.*.*` | 发布测试 | 临时 |
| `feature/*` | 新功能开发 | 临时 |

### 3. 开发流程

```bash
# 1. 从develop创建功能分支
git checkout -b feature/xxx develop

# 2. 开发完成后提交
git add .
git commit -m "feat: 添加新功能"

# 3. 推送到远程
git push origin feature/xxx

# 4. 创建PR合并到develop
# 在Git平台操作，CI自动运行测试

# 5. 测试通过后，创建release分支
git checkout -b release/v1.0.0 develop

# 6. 发布测试通过后，合并到master并打标签
git checkout master
git merge release/v1.0.0
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin master --tags
```

### 4. CI/CD自动化

已配置GitHub Actions (`.github/workflows/ci-cd.yml`)，自动完成：
- ✅ 代码编译
- ✅ 单元测试  
- ✅ 构建前端dist
- ✅ 部署到测试环境 (develop/release分支)
- ✅ 部署到生产环境 (master分支)

### 5. 部署密钥配置

在Git仓库Settings → Secrets中添加：

| 密钥 | 说明 |
|------|------|
| `DB_HOST` | 数据库地址 |
| `DB_PORT` | 数据库端口 |
| `DB_NAME` | 数据库名称 |
| `DB_USER` | 数据库用户名 |
| `DB_PASSWORD` | 数据库密码 |
| `JWT_SECRET` | JWT密钥(至少256位) |
| `TEST_SERVER_HOST` | 测试服务器IP |
| `TEST_SERVER_USER` | 测试服务器用户名 |
| `TEST_SERVER_KEY` | SSH私钥 |
| `PROD_SERVER_HOST` | 生产服务器IP |
| `PROD_SERVER_USER` | 生产服务器用户名 |
| `PROD_SERVER_KEY` | SSH私钥 |

### 6. 版本更新

每次发布需要：
1. 在 `sql/update.sql` 添加数据库变更
2. 更新版本号 (pom.xml / package.json)
3. 提交代码 → PR → 测试 → 合并 → 打标签
