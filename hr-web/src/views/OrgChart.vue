<template>
  <div class="org-tree-page">
    <div class="page-header">
      <h2>组织架构</h2>
      <p class="sub-title">审批层级与权限说明</p>
    </div>

    <el-card shadow="hover">
      <div class="org-tree-container">
        <div v-if="treeData.length === 0" class="empty-tip">
          <el-empty description="暂无组织架构数据" />
        </div>
        <el-tree
          v-else
          :data="treeData"
          :props="defaultProps"
          node-key="id"
          default-expand-all
          :expand-on-click-node="false"
        >
          <template #default="{ data }">
            <div class="tree-node" :class="'level-' + data.level">
              <div class="node-avatar" :style="{ background: getLevelColor(data.level) }">
                {{ data.realName?.charAt(0) || '?' }}
              </div>
              <div class="node-content">
                <div class="node-name">{{ data.realName }}</div>
                <div class="node-info">
                  <span class="node-dept">{{ data.department }}</span>
                  <span class="node-position">{{ data.position }}</span>
                </div>
              </div>
              <el-tag v-if="data.level" :type="getLevelType(data.level)" size="small" class="level-tag">
                {{ getLevelName(data.level) }}
              </el-tag>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-title">
              <el-icon><UserFilled /></el-icon>
              <span>人员层级分布</span>
            </div>
          </template>
          <div class="level-stats">
            <div v-for="item in levelStats" :key="item.level" class="stat-item">
              <div class="stat-count" :style="{ color: getLevelColor(item.level) }">{{ item.count }}</div>
              <div class="stat-label">{{ getLevelName(item.level) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-title">
              <el-icon><Setting /></el-icon>
              <span>权限对照表</span>
            </div>
          </template>
          <el-table :data="permissionData" size="small" stripe>
            <el-table-column prop="level" label="层级" width="60">
              <template #default="{ row }">
                <el-tag :type="getLevelType(row.level)" size="small">L{{ row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="position" label="职位" width="100" />
            <el-table-column prop="permission" label="权限类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.permissionType">{{ row.permission }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="scope" label="审批范围" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/api/index'

const users = ref([])

const treeData = computed(() => {
  const validUsers = users.value.filter(u => u.level && u.level > 0)
  if (validUsers.length === 0) return []
  
  const buildTree = (userList, parentId) => {
    return userList
      .filter(u => u.managerId === parentId)
      .map(u => ({
        id: u.id,
        realName: u.realName,
        department: u.department,
        position: u.position,
        level: u.level,
        managerId: u.managerId,
        children: buildTree(userList, u.id)
      }))
  }
  
  const rootUsers = validUsers.filter(u => !u.managerId || u.managerId === 0)
  
  if (rootUsers.length === 0) {
    return buildTree(validUsers, null)
  }
  
  return rootUsers.map(root => ({
    id: root.id,
    realName: root.realName,
    department: root.department,
    position: root.position,
    level: root.level,
    managerId: root.managerId,
    children: buildTree(validUsers, root.id)
  }))
})

const levelStats = computed(() => {
  const stats = {}
  users.value.forEach(u => {
    if (u.level && u.level > 0) {
      stats[u.level] = (stats[u.level] || 0) + 1
    }
  })
  return Object.entries(stats).map(([level, count]) => ({ level: parseInt(level), count })).sort((a, b) => a.level - b.level)
})

const defaultProps = {
  label: 'realName',
  children: 'children'
}

const permissionData = [
  { level: 1, position: '总经理', permission: '最终审批权', permissionType: 'danger', scope: '所有超预算、重大合同' },
  { level: 2, position: '副总经理', permission: '审批权', permissionType: 'warning', scope: '分管部门需求、合同' },
  { level: 3, position: '部门长', permission: '审核权', permissionType: 'primary', scope: '本部门需求、人员调配' },
  { level: 4, position: '项目经理', permission: '发起权', permissionType: 'success', scope: '项目需求申请' },
  { level: 5, position: 'HR专员', permission: '执行权', permissionType: 'info', scope: '人员信息维护' }
]

const getLevelName = (level) => {
  const map = { 1: '总经理', 2: '副总经理', 3: '部门长', 4: '项目经理', 5: 'HR专员' }
  return map[level] || ''
}

const getLevelType = (level) => {
  const map = { 1: 'danger', 2: 'warning', 3: 'primary', 4: 'success', 5: 'info' }
  return map[level] || 'info'
}

const getLevelColor = (level) => {
  const map = {
    1: '#f56c6c',
    2: '#e6a23c',
    3: '#409eff',
    4: '#67c23a',
    5: '#909399'
  }
  return map[level] || '#909399'
}

const fetchUsers = async () => {
  try {
    const res = await request.get('/users', { params: { pageNum: 1, pageSize: 100 } })
    if (res.code === 200) {
      users.value = res.data.records || res.data
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.org-tree-page {
  padding: 0 10px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.sub-title {
  margin: 5px 0 0;
  color: #909399;
  font-size: 14px;
}

.org-tree-container {
  padding: 20px;
  min-height: 400px;
}

.empty-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 15px;
  border-radius: 8px;
  transition: all 0.3s;
}

.tree-node:hover {
  background: #f5f7fa;
}

.tree-node.level-1 .node-avatar {
  width: 50px;
  height: 50px;
  font-size: 18px;
}

.tree-node.level-2 .node-avatar {
  width: 45px;
  height: 45px;
  font-size: 16px;
}

.tree-node.level-3 .node-avatar {
  width: 42px;
  height: 42px;
  font-size: 15px;
}

.tree-node.level-4 .node-avatar,
.tree-node.level-5 .node-avatar {
  width: 38px;
  height: 38px;
  font-size: 14px;
}

.node-avatar {
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.node-content {
  flex: 1;
}

.node-name {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
}

.node-info {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.level-tag {
  margin-left: auto;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.level-stats {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
}

.stat-item {
  text-align: center;
}

.stat-count {
  font-size: 28px;
  font-weight: bold;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
