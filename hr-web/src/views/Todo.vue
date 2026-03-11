<template>
  <div class="todo-page">
    <div class="page-header">
      <h2>我的待办</h2>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="pendingList" v-loading="loading">
        <el-table-column prop="title" label="标题" width="250" />
        <el-table-column prop="businessType" label="业务类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getBusinessTypeText(row.businessType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag type="warning">待处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发起时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">查看</el-button>
            <el-button type="success" link @click="openApproveDialog(row)">通过</el-button>
            <el-button type="danger" link @click="openRejectDialog(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="showDetailDialog" title="详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ currentInstance?.title }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ getBusinessTypeText(currentInstance?.businessType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag type="warning">待处理</el-tag>
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
    
    <el-dialog v-model="showActionDialog" :title="actionType === 'approve' ? '审批通过' : '审批拒绝'" width="400px">
      <el-form label-width="60px">
        <el-form-item label="意见">
          <el-input v-model="actionComment" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showActionDialog = false">取消</el-button>
        <el-button :type="actionType === 'approve' ? 'success' : 'danger'" @click="submitAction">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/index'

const loading = ref(false)
const pendingList = ref([])
const showDetailDialog = ref(false)
const showActionDialog = ref(false)
const currentInstance = ref(null)
const records = ref([])
const actionType = ref('approve')
const actionComment = ref('')
const currentInstanceId = ref(null)

const getBusinessTypeText = (type) => {
  const map = { REQUIREMENT: '需求申请', PAYMENT: '付款申请', SETTLEMENT: '结算确认' }
  return map[type] || type
}

const getActionText = (action) => {
  const map = { SUBMIT: '提交', APPROVE: '通过', REJECT: '拒绝', CANCEL: '取消' }
  return map[action] || action
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/workflow/instances/pending')
    if (res.code === 200) {
      pendingList.value = res.data || []
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

const openApproveDialog = (row) => {
  currentInstanceId.value = row.id
  actionType.value = 'approve'
  actionComment.value = ''
  showActionDialog.value = true
}

const openRejectDialog = (row) => {
  currentInstanceId.value = row.id
  actionType.value = 'reject'
  actionComment.value = ''
  showActionDialog.value = true
}

const submitAction = async () => {
  try {
    const url = actionType.value === 'approve' 
      ? `/workflow/instances/${currentInstanceId.value}/approve`
      : `/workflow/instances/${currentInstanceId.value}/reject`
    await request.post(url, { comment: actionComment.value })
    ElMessage.success('操作成功')
    showActionDialog.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('操作失败')
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
