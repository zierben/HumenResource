<template>
  <div class="done-page">
    <div class="page-header">
      <h2>我的已办</h2>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="doneList" v-loading="loading">
        <el-table-column prop="title" label="标题" width="250" />
        <el-table-column prop="businessType" label="业务类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getBusinessTypeText(row.businessType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发起时间" width="180" />
        <el-table-column prop="updateTime" label="处理时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="showDetailDialog" title="详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ currentInstance?.title }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ getBusinessTypeText(currentInstance?.businessType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentInstance?.status)">{{ getStatusText(currentInstance?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发起时间">{{ currentInstance?.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-divider>审批记录</el-divider>
      <el-timeline>
        <el-timeline-item v-for="record in records" :key="record.id" :timestamp="record.createTime" placement="top">
          <el-card>
            <p><strong>{{ record.operatorName }}</strong> - {{ getActionText(record.action) }}</p>
            <p v-if="record.comment">意见: {{ record.comment }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/index'

const loading = ref(false)
const doneList = ref([])
const showDetailDialog = ref(false)
const currentInstance = ref(null)
const records = ref([])

const getBusinessTypeText = (type) => {
  const map = { REQUIREMENT: '需求申请', PAYMENT: '付款申请', SETTLEMENT: '结算确认' }
  return map[type] || type
}

const getStatusText = (status) => {
  const map = { PENDING: '进行中', APPROVED: '已通过', REJECTED: '已拒绝', CANCELLED: '已取消' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', CANCELLED: 'info' }
  return map[status] || 'info'
}

const getActionText = (action) => {
  const map = { SUBMIT: '提交', APPROVE: '通过', REJECT: '拒绝', CANCEL: '取消' }
  return map[action] || action
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/workflow/instances/done')
    if (res.code === 200) {
      doneList.value = res.data || []
    }
  } finally {
    loading.value = false
  }
}

const viewDetail = async (row) => {
  try {
    const res = await request.get(`/workflow/instances/${row.id}`)
    if (res.code === 200) {
      currentInstance.value = res.data.instance
      records.value = res.data.records || []
      showDetailDialog.value = true
    }
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}
</style>
