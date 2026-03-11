<template>
  <div class="candidates">
    <div class="page-header">
      <div>
        <h2>候选人管理</h2>
        <p class="sub-title">候选人面试与录用管理</p>
      </div>
      <div>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>添加候选人
        </el-button>
      </div>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="candidateList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="positionName" label="应聘职位" width="120" />
        <el-table-column prop="expectedRate" label="期望薪资" width="100">
          <template #default="{ row }">¥{{ row.expectedRate?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="supplierId" label="供应商" width="120">
          <template #default="{ row }">{{ getSupplierName(row.supplierId) }}</template>
        </el-table-column>
        <el-table-column prop="interviewDate" label="面试日期" width="110" />
        <el-table-column prop="interviewResult" label="面试结果" width="100">
          <template #default="{ row }">
            <el-tag :type="getInterviewResultType(row.interviewResult)" size="small">
              {{ getInterviewResultText(row.interviewResult) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
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
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑候选人' : '添加候选人'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" required>
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" required>
              <el-input v-model="form.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应聘职位" required>
              <el-input v-model="form.positionName" placeholder="请输入应聘职位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="期望薪资">
              <el-input-number v-model="form.expectedRate" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" style="width: 100%" clearable filterable>
                <el-option v-for="s in suppliers" :key="s.id" :label="s.supplierName" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="面试日期">
              <el-date-picker v-model="form.interviewDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面试结果">
              <el-select v-model="form.interviewResult" placeholder="请选择" style="width: 100%">
                <el-option label="待面试" value="PENDING" />
                <el-option label="通过" value="PASS" />
                <el-option label="拒绝" value="REJECT" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="待处理" :value="0" />
                <el-option label="面试中" :value="1" />
                <el-option label="已录用" :value="2" />
                <el-option label="已拒绝" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="面试备注">
          <el-input v-model="form.interviewRemark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
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
const isEdit = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const candidateList = ref([])
const suppliers = ref([])

const form = reactive({
  id: null,
  name: '',
  phone: '',
  email: '',
  positionName: '',
  expectedRate: 0,
  supplierId: null,
  interviewDate: '',
  interviewResult: 'PENDING',
  interviewRemark: '',
  status: 0
})

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待处理', 1: '面试中', 2: '已录用', 3: '已拒绝' }
  return map[status] || '未知'
}

const getInterviewResultType = (result) => {
  const map = { PENDING: 'info', PASS: 'success', REJECT: 'danger' }
  return map[result] || 'info'
}

const getInterviewResultText = (result) => {
  const map = { PENDING: '待面试', PASS: '通过', REJECT: '拒绝' }
  return map[result] || '未知'
}

const getSupplierName = (supplierId) => {
  const supplier = suppliers.value.find(s => s.id === supplierId)
  return supplier ? supplier.supplierName : '-'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/candidates', { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    if (res.code === 200) {
      candidateList.value = res.data.records
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchSuppliers = async () => {
  try {
    const res = await request.get('/suppliers', { params: { pageNum: 1, pageSize: 100 } })
    if (res.code === 200) {
      suppliers.value = res.data.records
    }
  } catch (e) {
    console.error(e)
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    phone: '',
    email: '',
    positionName: '',
    expectedRate: 0,
    supplierId: null,
    interviewDate: '',
    interviewResult: 'PENDING',
    interviewRemark: '',
    status: 0
  })
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
  if (!form.name || !form.phone || !form.positionName) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    let res
    if (isEdit.value) {
      res = await request.put(`/candidates/${form.id}`, form)
    } else {
      res = await request.post('/candidates', form)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      showDialog.value = false
      fetchData()
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除候选人"${row.name}"？`, '提示', { type: 'warning' })
    const res = await request.delete(`/candidates/${row.id}`)
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
  fetchSuppliers()
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
