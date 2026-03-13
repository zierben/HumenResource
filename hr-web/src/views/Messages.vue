<template>
  <div class="messages">
    <div class="page-header">
      <div>
        <h2>消息通知</h2>
        <p class="sub-title">系统消息与通知管理</p>
      </div>
      <div>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>发送消息
        </el-button>
      </div>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="messageList" style="width: 100%" v-loading="loading" :row-class-name="getRowClass">
        <el-table-column prop="title" label="标题" width="200">
          <template #default="{ row }">
            <span :class="{ 'unread-title': !row.isRead }">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="userName" label="接收人" width="120">
          <template #default="{ row }">
            {{ getUserName(row.userId) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeStyle(row.type)" size="small">{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'info' : 'danger'" size="small">
              {{ row.isRead ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发送时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewMessage(row)">查看</el-button>
            <el-button v-if="!row.isRead" type="success" link @click="markAsRead(row)">标为已读</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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
    
    <el-dialog v-model="showDialog" title="发送消息" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="接收人" required>
          <el-select v-model="form.userId" filterable placeholder="请选择接收人" style="width: 100%">
            <el-option 
              v-for="user in userList" 
              :key="user.id" 
              :label="`${user.realName} (${user.username})`" 
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="系统通知" value="SYSTEM" />
            <el-option label="任务提醒" value="TASK" />
            <el-option label="审批通知" value="APPROVAL" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入消息内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发送</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="showViewDialog" title="消息详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ currentMessage.title }}</el-descriptions-item>
        <el-descriptions-item label="接收人">{{ getUserName(currentMessage.userId) }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="getTypeStyle(currentMessage.type)" size="small">{{ getTypeText(currentMessage.type) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentMessage.isRead ? 'info' : 'danger'" size="small">
            {{ currentMessage.isRead ? '已读' : '未读' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ currentMessage.createTime }}</el-descriptions-item>
        <el-descriptions-item label="内容">
          <div style="white-space: pre-wrap;">{{ currentMessage.content }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showViewDialog = false">关闭</el-button>
        <el-button v-if="!currentMessage.isRead" type="primary" @click="markAsRead(currentMessage)">标为已读</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/index'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const currentUserId = computed(() => userStore.userInfo?.id)

const loading = ref(false)
const showDialog = ref(false)
const showViewDialog = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const messageList = ref([])
const userList = ref([])
const currentMessage = ref({})

const form = reactive({
  id: null,
  userId: null,
  title: '',
  content: '',
  type: 'SYSTEM',
  isRead: false
})

const getTypeStyle = (type) => {
  const map = { SYSTEM: 'primary', TASK: 'warning', APPROVAL: 'success', OTHER: 'info' }
  return map[type] || 'info'
}

const getTypeText = (type) => {
  const map = { SYSTEM: '系统通知', TASK: '任务提醒', APPROVAL: '审批通知', OTHER: '其他' }
  return map[type] || '未知'
}

const getRowClass = ({ row }) => {
  return row.isRead ? '' : 'unread-row'
}

const getUserName = (userId) => {
  const user = userList.value.find(u => u.id === userId)
  return user ? user.realName : `ID:${userId}`
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { pageNum: currentPage.value, pageSize: pageSize.value }
    if (currentUserId.value) {
      params.userId = currentUserId.value
    }
    const res = await request.get('/messages', { params })
    if (res.code === 200) {
      messageList.value = res.data.records
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchUsers = async () => {
  try {
    const res = await request.get('/users', { params: { pageNum: 1, pageSize: 100 } })
    if (res.code === 200) {
      userList.value = res.data.records || res.data
    }
  } catch (e) {
    console.error(e)
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    userId: null,
    title: '',
    content: '',
    type: 'SYSTEM',
    isRead: false
  })
}

const openAddDialog = () => {
  resetForm()
  showDialog.value = true
}

const viewMessage = (row) => {
  currentMessage.value = { ...row }
  showViewDialog.value = true
}

const markAsRead = async (row) => {
  try {
    const res = await request.put(`/messages/${row.id}/read`)
    if (res.code === 200) {
      row.isRead = true
      if (currentMessage.value.id === row.id) {
        currentMessage.value.isRead = true
      }
      ElMessage.success('已标记为已读')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleSubmit = async () => {
  if (!form.userId || !form.title || !form.content) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    const res = await request.post('/messages', form)
    if (res.code === 200) {
      ElMessage.success('发送成功')
      showDialog.value = false
      fetchData()
    }
  } catch (e) {
    ElMessage.error('发送失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除消息"${row.title}"？`, '提示', { type: 'warning' })
    const res = await request.delete(`/messages/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchData()
  fetchUsers()
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

.unread-title {
  font-weight: bold;
}

:deep(.unread-row) {
  background-color: #f0f9eb;
}
</style>
