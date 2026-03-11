<template>
  <div class="sync-page">
    <div class="page-header">
      <div>
        <h2>数据同步</h2>
        <p class="sub-title">外部系统数据同步管理</p>
      </div>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>禅道同步</span>
            </div>
          </template>
          <div class="sync-item">
            <div class="sync-info">
              <span>人员信息同步</span>
              <span class="sync-desc">从禅道同步人员账号信息</span>
            </div>
            <el-button type="primary" @click="handleSync('ZENTAO_USER')">同步</el-button>
          </div>
          <el-divider />
          <div class="sync-item">
            <div class="sync-info">
              <span>项目信息同步</span>
              <span class="sync-desc">从禅道同步项目信息</span>
            </div>
            <el-button type="primary" @click="handleSync('ZENTAO_PROJECT')">同步</el-button>
          </div>
          <el-divider />
          <div class="sync-item">
            <div class="sync-info">
              <span>工时数据同步</span>
              <span class="sync-desc">从禅道同步工时记录</span>
            </div>
            <el-button type="primary" @click="handleSync('ZENTAO_WORKHOURS')">同步</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>业财系统</span>
              <el-tag size="small" type="info">预留接口</el-tag>
            </div>
          </template>
          <div class="sync-item">
            <div class="sync-info">
              <span>项目推送</span>
              <span class="sync-desc">推送项目信息到业财系统</span>
            </div>
            <el-button disabled>待对接</el-button>
          </div>
          <el-divider />
          <div class="sync-item">
            <div class="sync-info">
              <span>费用推送</span>
              <span class="sync-desc">推送费用信息到业财系统</span>
            </div>
            <el-button disabled>待对接</el-button>
          </div>
          <el-divider />
          <div class="sync-item">
            <div class="sync-info">
              <span>预算推送</span>
              <span class="sync-desc">推送预算信息到业财系统</span>
            </div>
            <el-button disabled>待对接</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>同步日志</span>
          <el-button type="primary" link @click="fetchLogs">刷新</el-button>
        </div>
      </template>
      <el-table :data="logList" style="width: 100%">
        <el-table-column prop="syncType" label="同步类型" width="180">
          <template #default="{ row }">
            {{ getSyncTypeName(row.syncType) }}
          </template>
        </el-table-column>
        <el-table-column prop="syncDirection" label="方向" width="80">
          <template #default="{ row }">
            {{ row.syncDirection === 'IMPORT' ? '导入' : '导出' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="总数" width="80" />
        <el-table-column prop="successCount" label="成功数" width="80" />
        <el-table-column prop="errorMessage" label="错误信息" show-overflow-tooltip />
        <el-table-column prop="startedAt" label="开始时间" width="180" />
        <el-table-column prop="finishedAt" label="结束时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-if="row.status === 'FAILED'" @click="handleRetry(row)">重试</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'

const logList = ref([])

const getSyncTypeName = (type) => {
  const map = {
    'ZENTAO_USER': '禅道-人员同步',
    'ZENTAO_PROJECT': '禅道-项目同步',
    'ZENTAO_WORKHOURS': '禅道-工时同步'
  }
  return map[type] || type
}

const getStatusType = (status) => {
  const map = { 'SUCCESS': 'success', 'FAILED': 'danger', 'RUNNING': 'warning' }
  return map[status] || 'info'
}

const getStatusName = (status) => {
  const map = { 'SUCCESS': '成功', 'FAILED': '失败', 'RUNNING': '进行中' }
  return map[status] || status
}

const fetchLogs = async () => {
  const res = await request.get('/sync/logs')
  if (res.code === 200) {
    logList.value = res.data.records || res.data
  }
}

const handleSync = async (type) => {
  try {
    ElMessage.info('开始同步...')
    const res = await request.post('/sync/execute', { syncType: type })
    if (res.code === 200) {
      ElMessage.success('同步完成')
      fetchLogs()
    } else {
      ElMessage.error(res.message || '同步失败')
    }
  } catch (e) {
    ElMessage.error('同步失败')
  }
}

const handleRetry = async (row) => {
  await request.post(`/sync/retry/${row.id}`)
  ElMessage.success('已重新执行')
  fetchLogs()
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sync-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.sync-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sync-desc {
  font-size: 12px;
  color: #909399;
}
</style>
