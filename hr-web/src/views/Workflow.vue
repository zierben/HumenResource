<template>
  <div class="workflow-page">
    <div class="page-header">
      <div>
        <h2>工作流定义</h2>
        <p class="sub-title">配置审批流程</p>
      </div>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>添加流程
      </el-button>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="workflowList" v-loading="loading">
        <el-table-column prop="workflowCode" label="流程编码" width="150" />
        <el-table-column prop="workflowName" label="流程名称" width="200" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="warning" link @click="openNodesDialog(row)">节点配置</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑流程' : '添加流程'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="编码" required>
          <el-input v-model="form.workflowCode" :disabled="isEdit" placeholder="如：REQUIREMENT_APPROVAL" />
        </el-form-item>
        <el-form-item label="名称" required>
          <el-input v-model="form.workflowName" placeholder="如：需求审批流程" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
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
    
    <el-dialog v-model="showNodesDialog" title="节点配置" width="800px">
      <div style="margin-bottom: 15px">
        <el-button type="primary" size="small" @click="addNode">添加节点</el-button>
      </div>
      <el-table :data="nodeList" border>
        <el-table-column prop="nodeCode" label="节点编码" width="120">
          <template #default="{ row }">
            <el-input v-model="row.nodeCode" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="nodeName" label="节点名称" width="120">
          <template #default="{ row }">
            <el-input v-model="row.nodeName" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="nodeType" label="类型" width="120">
          <template #default="{ row }">
            <el-select v-model="row.nodeType" size="small">
              <el-option label="开始" value="START" />
              <el-option label="审批" value="APPROVAL" />
              <el-option label="抄送" value="CC" />
              <el-option label="结束" value="END" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="approverRole" label="审批角色" width="140">
          <template #default="{ row }">
            <el-select v-model="row.approverRole" size="small" :disabled="row.nodeType !== 'APPROVAL'">
              <el-option label="总经理(CEO)" value="CEO" />
              <el-option label="副总经理(VP)" value="VP" />
              <el-option label="部门长" value="DEPT_DIRECTOR" />
              <el-option label="项目经理" value="PM" />
              <el-option label="HR" value="HR" />
              <el-option label="财务" value="FINANCE" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80">
          <template #default="{ row }">
            <el-input-number v-model="row.sortOrder" size="small" :min="0" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ $index }">
            <el-button type="danger" link size="small" @click="removeNode($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="showNodesDialog = false">取消</el-button>
        <el-button type="primary" @click="saveNodes">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/index'

const loading = ref(false)
const showDialog = ref(false)
const showNodesDialog = ref(false)
const isEdit = ref(false)
const workflowList = ref([])
const nodeList = ref([])
const currentWorkflowId = ref(null)

const form = reactive({
  id: null,
  workflowCode: '',
  workflowName: '',
  description: '',
  status: 1
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/workflow/workflows')
    if (res.code === 200) {
      workflowList.value = res.data.records || res.data
    }
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, { id: null, workflowCode: '', workflowName: '', description: '', status: 1 })
}

const openAddDialog = () => {
  isEdit.value = false
  resetForm()
  showDialog.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!form.workflowCode || !form.workflowName) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    if (isEdit.value) {
      await request.put(`/workflow/workflows/${form.id}`, form)
    } else {
      await request.post('/workflow/workflows', form)
    }
    ElMessage.success('保存成功')
    showDialog.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该流程？', '提示', { type: 'warning' })
    await request.delete(`/workflow/workflows/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const openNodesDialog = async (row) => {
  currentWorkflowId.value = row.id
  try {
    const res = await request.get(`/workflow/nodes/workflow/${row.id}`)
    if (res.code === 200) {
    nodeList.value = res.data || []
  }
  } catch (e) {
    nodeList.value = []
  }
  showNodesDialog.value = true
}

const addNode = () => {
  nodeList.value.push({
    id: null,
    workflowId: currentWorkflowId.value,
    nodeCode: '',
    nodeName: '',
    nodeType: 'APPROVAL',
    approverRole: '',
    sortOrder: nodeList.value.length
  })
}

const removeNode = (index) => {
  nodeList.value.splice(index, 1)
}

const saveNodes = async () => {
  try {
    await request.post(`/workflow/nodes/batch`, nodeList.value)
    ElMessage.success('保存成功')
    showNodesDialog.value = false
  } catch (e) {
    ElMessage.error('保存失败')
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
}

.sub-title {
  margin: 5px 0 0;
  color: #909399;
  font-size: 14px;
}
</style>

