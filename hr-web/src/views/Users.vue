<template>
  <div class="users-page">
    <div class="page-header">
      <div>
        <h2>用户管理</h2>
        <p class="sub-title">系统用户账号管理</p>
      </div>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>添加用户
      </el-button>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="userList" style="width: 100%">
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleName(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleResetPassword(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑用户' : '添加用户'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名" required>
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" required v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="总经理" value="GM" />
            <el-option label="副总" value="VP" />
            <el-option label="部门长" value="DEPT_HEAD" />
            <el-option label="项目经理" value="PM" />
            <el-option label="HR" value="HR" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="resetDialogVisible" title="重置密码" width="400px">
      <el-form :model="resetForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input :value="resetForm.username" disabled />
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input v-model="resetForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitResetPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api'

const userList = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const resetDialogVisible = ref(false)

const form = reactive({
  id: null,
  username: '',
  realName: '',
  password: '',
  role: 'HR',
  status: 1
})

const resetForm = reactive({
  id: null,
  username: '',
  newPassword: ''
})

const roleMap = {
  ADMIN: { name: '管理员', type: 'danger' },
  GM: { name: '总经理', type: 'danger' },
  VP: { name: '副总', type: 'warning' },
  DEPT_HEAD: { name: '部门长', type: 'primary' },
  PM: { name: '项目经理', type: 'success' },
  HR: { name: 'HR', type: 'info' }
}

const getRoleName = (role) => roleMap[role]?.name || role
const getRoleType = (role) => roleMap[role]?.type || 'info'

const resetFormData = () => {
  Object.assign(form, {
    id: null,
    username: '',
    realName: '',
    password: '',
    role: 'HR',
    status: 1
  })
}

const fetchUsers = async () => {
  const res = await request.get('/users')
  if (res.code === 200) {
    userList.value = res.data.records || res.data
  }
}

const openAddDialog = () => {
  isEdit.value = false
  resetFormData()
  showDialog.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    role: row.role,
    status: row.status,
    password: ''
  })
  showDialog.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该用户？', '提示', { type: 'warning' })
    await request.delete(`/users/${row.id}`)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const handleResetPassword = (row) => {
  resetForm.id = row.id
  resetForm.username = row.username
  resetForm.newPassword = ''
  resetDialogVisible.value = true
}

const submitResetPassword = async () => {
  if (!resetForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  try {
    await request.post(`/users/${resetForm.id}/reset-password`, { newPassword: resetForm.newPassword })
    ElMessage.success('密码重置成功')
    resetDialogVisible.value = false
  } catch (e) {
    ElMessage.error('重置失败')
  }
}

const handleSubmit = async () => {
  if (!form.username || !form.realName) {
    ElMessage.warning('请填写必填项')
    return
  }
  if (!isEdit.value && !form.password) {
    ElMessage.warning('请填写密码')
    return
  }
  try {
    if (isEdit.value) {
      await request.put(`/users/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/users', form)
      ElMessage.success('添加成功')
    }
    showDialog.value = false
    fetchUsers()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
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
</style>
