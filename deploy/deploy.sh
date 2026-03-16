#!/bin/bash

# HR系统部署脚本
# 用法: ./deploy.sh [prod|test]

ENV=$1
if [ -z "$ENV" ]; then
    echo "用法: ./deploy.sh [prod|test]"
    exit 1
fi

echo "========== 开始部署 HR系统 ($ENV) =========="

# 1. 停止服务
echo "[1/6] 停止服务..."
pm2 stop hr-admin || true

# 2. 备份数据库
echo "[2/6] 备份数据库..."
mysqldump -u$DB_USER -p$DB_PASSWORD $DB_NAME > backup/hr_system_$(date +%Y%m%d_%H%M%S).sql

# 3. 执行SQL更新
echo "[3/6] 执行数据库更新..."
mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME < sql/update.sql

# 4. 部署后端
echo "[4/6] 部署后端..."
cp hr-admin/target/hr-admin-*.jar /opt/hr-admin/
cd /opt/hr-admin
pm2 start hr-admin.json

# 5. 部署前端
echo "[5/6] 部署前端..."
rm -rf /var/www/hr-web
cp -r hr-web/dist /var/www/hr-web

# 6. 检查服务状态
echo "[6/6] 检查服务状态..."
pm2 status
curl -s http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d '{"username":"admin"}' | grep -q "code" && echo "后端服务正常" || echo "后端服务异常"

echo "========== 部署完成 =========="
