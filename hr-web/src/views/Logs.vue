<template>
  <div class="logs">
    <div class="page-header">
      <div>
        <h2>操作日志</h2>
        <p class="sub-title">系统操作记录查询</p>
      </div>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="logList" style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="操作用户" width="100" />
        <el-table-column prop="module" label="模块" width="100">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.module }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="操作" width="80">
          <template #default="{ row }">
            <el-tag :type="getActionType(row.action)" size="small">{{ row.action }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetType" label="目标类型" width="100" />
        <el-table-column prop="targetId" label="目标ID" width="80" />
        <el-table-column prop="detail" label="详情" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="130" />
        <el-table-column prop="createTime" label="操作时间" width="160" />
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>
    
    <el-dialog v-model="showDetailDialog" title="日志详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作用户">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="模块">{{ currentLog.module }}</el-descriptions-item>
        <el-descriptions-item label="操作">
          <el-tag :type="getActionType(currentLog.action)" size="small">{{ currentLog.action }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="目标类型">{{ currentLog.targetType }}</el-descriptions-item>
        <el-descriptions-item label="目标ID">{{ currentLog.targetId }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">{{ currentLog.createTime }}</el-descriptions-item>
        <el-descriptions-item label="详情" :span="2">
          <div style="white-space: pre-wrap;">{{ currentLog.detail }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/index'

const loading = ref(false)
const showDetailDialog = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const logList = ref([])
const currentLog = ref({})

const getActionType = (action) => {
  const map = { 新增: 'success', 修改: 'warning', 删除: 'danger', 查询: 'info', 登录: 'primary', 登出: 'info' }
  return map[action] || 'info'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/logs', { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    if (res.code === 200) {
      logList.value = res.data.records
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
