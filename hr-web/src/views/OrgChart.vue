<template>
  <div class="org-tree-page">
    <div class="page-header">
      <h2>组织架构</h2>
      <p class="sub-title">审批层级与权限说明</p>
    </div>

    <div class="org-layout">
      <div class="org-left">
        <el-card shadow="hover" class="tree-card">
          <template #header>
            <div class="card-title">
              <el-icon><OfficeBuilding /></el-icon>
              <span>组织架构树</span>
              <span class="total-count">共 {{ validUserCount }} 人</span>
            </div>
          </template>
          <div class="org-tree-container">
            <div v-if="treeData.length === 0" class="empty-tip">
              <el-empty description="暂无组织架构数据" />
            </div>
            <el-tree
              v-else
              ref="treeRef"
              :data="treeData"
              :props="defaultProps"
              node-key="id"
              default-expand-all
              :expand-on-click-node="false"
              :indent="20"
              highlight-current
            >
              <template #default="{ data }">
                <div class="tree-node" :class="'level-' + data.level">
                  <div class="node-avatar" :style="{ background: getLevelColor(data.level) }">
                    {{ data.realName?.charAt(0) || '?' }}
                  </div>
                  <div class="node-content">
                    <div class="node-name">{{ data.realName }}</div>
                    <div class="node-info">
                      <span class="node-dept">{{ data.department || '-' }}</span>
                      <span class="node-position">{{ data.position }}</span>
                    </div>
                  </div>
                  <el-tag v-if="data.level" :type="getLevelType(data.level)" size="small" class="level-tag">
                    L{{ data.level }}
                  </el-tag>
                </div>
              </template>
            </el-tree>
          </div>
        </el-card>
      </div>

      <div class="org-right">
        <el-card shadow="hover" class="stats-card">
          <template #header>
            <div class="card-title">
              <el-icon><UserFilled /></el-icon>
              <span>人员层级分布</span>
            </div>
          </template>
          <div class="level-stats">
            <div v-for="item in levelStats" :key="item.level" class="stat-item">
              <div class="stat-count" :style="{ color: getLevelColor(item.level) }">{{ item.count }}</div>
              <div class="stat-label">L{{ item.level }}</div>
              <div class="stat-position">{{ getLevelName(item.level) }}</div>
            </div>
          </div>
          <div class="dept-stats">
            <div class="dept-title">部门人数</div>
            <div class="dept-list">
              <div v-for="dept in deptStats" :key="dept.name" class="dept-item">
                <span class="dept-name">{{ dept.name }}</span>
                <el-progress 
                  :percentage="dept.percentage" 
                  :stroke-width="8" 
                  :color="dept.color"
                  :show-text="false"
                />
                <span class="dept-count">{{ dept.count }}人</span>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="permission-card">
          <template #header>
            <div class="card-title">
              <el-icon><Setting /></el-icon>
              <span>岗位权限对照表</span>
            </div>
          </template>
          <el-table :data="permissionData" size="small" stripe>
            <el-table-column prop="level" label="层级" width="50" align="center">
              <template #default="{ row }">
                <el-tag :type="getLevelType(row.level)" size="small">L{{ row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="position" label="岗位" width="90" />
            <el-table-column prop="permission" label="权限" width="80">
              <template #default="{ row }">
                <el-tag :type="row.permissionType" size="small">{{ row.permission }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="scope" label="审批范围" show-overflow-tooltip />
          </el-table>
          
          <div class="workflow-desc">
            <div class="workflow-title">审批流程示例</div>
            <div class="workflow-steps">
              <div class="workflow-step">
                <span class="step-badge" style="background: #67c23a">L4</span>
                <span>发起申请</span>
              </div>
              <el-icon class="step-arrow"><ArrowRight /></el-icon>
              <div class="workflow-step">
                <span class="step-badge" style="background: #409eff">L3</span>
                <span>部门审核</span>
              </div>
              <el-icon class="step-arrow"><ArrowRight /></el-icon>
              <div class="workflow-step">
                <span class="step-badge" style="background: #e6a23c">L2</span>
                <span>副总审批</span>
              </div>
              <el-icon class="step-arrow"><ArrowRight /></el-icon>
              <div class="workflow-step">
                <span class="step-badge" style="background: #f56c6c">L1</span>
                <span>总经理终审</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import request from '@/api/index'

const users = ref([])
const treeRef = ref(null)

const validUserCount = computed(() => {
  return users.value.filter(u => u.level && u.level > 0 && u.department).length
})

const treeData = computed(() => {
  const validUsers = users.value.filter(u => u.level && u.level > 0 && u.department)
  if (validUsers.length === 0) return []
  
  const buildTree = (userList, parentId) => {
    return userList
      .filter(u => u.managerId === parentId)
      .sort((a, b) => a.level - b.level)
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
  
  return rootUsers
    .sort((a, b) => a.level - b.level)
    .map(root => ({
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
    if (u.level && u.level > 0 && u.department) {
      stats[u.level] = (stats[u.level] || 0) + 1
    }
  })
  return Object.entries(stats).map(([level, count]) => ({ level: parseInt(level), count })).sort((a, b) => a.level - b.level)
})

const deptStats = computed(() => {
  const stats = {}
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
  let colorIndex = 0
  
  users.value.forEach(u => {
    if (u.department && u.level > 0) {
      stats[u.department] = (stats[u.department] || 0) + 1
    }
  })
  
  return Object.entries(stats)
    .map(([name, count]) => ({
      name,
      count,
      percentage: Math.round((count / validUserCount.value) * 100),
      color: colors[colorIndex++ % colors.length]
    }))
    .sort((a, b) => b.count - a.count)
})

const defaultProps = {
  label: 'realName',
  children: 'children'
}

const permissionData = [
  { level: 1, position: '总经理', permission: '终审', permissionType: 'danger', scope: '所有超预算、重大合同' },
  { level: 2, position: '副总经理', permission: '审批', permissionType: 'warning', scope: '分管部门需求、合同' },
  { level: 3, position: '部门长', permission: '审核', permissionType: 'primary', scope: '本部门需求、人员调配' },
  { level: 4, position: '项目经理/专员', permission: '发起', permissionType: 'success', scope: '项目执行、日常业务' }
]

const getLevelName = (level) => {
  const map = { 1: '总经理', 2: '副总经理', 3: '部门长', 4: '项目经理/专员' }
  return map[level] || ''
}

const getLevelType = (level) => {
  const map = { 1: 'danger', 2: 'warning', 3: 'primary', 4: 'success' }
  return map[level] || 'info'
}

const getLevelColor = (level) => {
  const map = {
    1: '#f56c6c',
    2: '#e6a23c',
    3: '#409eff',
    4: '#67c23a'
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
  margin-bottom: 16px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.sub-title {
  margin: 4px 0 0;
  color: #909399;
  font-size: 14px;
}

.org-layout {
  display: flex;
  gap: 16px;
  min-height: calc(100vh - 180px);
}

.org-left {
  width: 33.33%;
  flex-shrink: 0;
}

.org-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.tree-card {
  height: 100%;
}

.tree-card :deep(.el-card__body) {
  padding: 0;
  height: calc(100% - 50px);
}

.org-tree-container {
  padding: 12px;
  height: 100%;
  overflow-y: auto;
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
  gap: 10px;
  padding: 6px 10px;
  border-radius: 6px;
  transition: all 0.2s;
  min-height: 42px;
  flex-shrink: 0;
}

.tree-node:hover {
  background: #f5f7fa;
}

.tree-node.level-1 .node-avatar {
  width: 40px;
  height: 40px;
  font-size: 15px;
}

.tree-node.level-2 .node-avatar {
  width: 36px;
  height: 36px;
  font-size: 14px;
}

.tree-node.level-3 .node-avatar {
  width: 34px;
  height: 34px;
  font-size: 13px;
}

.tree-node.level-4 .node-avatar {
  width: 32px;
  height: 32px;
  font-size: 13px;
}

.node-avatar {
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
  flex-shrink: 0;
}

.node-content {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.node-name {
  font-weight: 600;
  font-size: 13px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.node-info {
  display: flex;
  gap: 6px;
  font-size: 11px;
  color: #909399;
  margin-top: 1px;
  white-space: nowrap;
  overflow: hidden;
}

.node-dept {
  max-width: 70px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.node-position {
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.level-tag {
  flex-shrink: 0;
  font-size: 11px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.total-count {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}

.stats-card {
  flex-shrink: 0;
}

.level-stats {
  display: flex;
  justify-content: space-around;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

.stat-item {
  text-align: center;
}

.stat-count {
  font-size: 24px;
  font-weight: bold;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.stat-position {
  font-size: 11px;
  color: #606266;
  margin-top: 2px;
}

.dept-stats {
  padding: 0 8px;
}

.dept-title {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12px;
}

.dept-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.dept-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dept-name {
  width: 80px;
  font-size: 12px;
  color: #606266;
  flex-shrink: 0;
}

.dept-item .el-progress {
  flex: 1;
}

.dept-count {
  width: 36px;
  font-size: 12px;
  color: #909399;
  text-align: right;
  flex-shrink: 0;
}

.permission-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.permission-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 12px 16px;
}

.permission-card .el-table {
  flex-shrink: 0;
}

.workflow-desc {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  flex-shrink: 0;
}

.workflow-title {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12px;
}

.workflow-steps {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}

.workflow-step {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #606266;
}

.step-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  color: white;
  font-size: 11px;
  font-weight: bold;
}

.step-arrow {
  color: #c0c4cc;
  font-size: 14px;
}

:deep(.el-tree-node__content) {
  height: auto !important;
  padding: 2px 0;
}

:deep(.el-tree-node__expand-icon) {
  padding: 6px;
}

:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
  background-color: #ecf5ff;
}
</style>
