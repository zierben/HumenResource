<template>
  <div class="personnel">
    <div class="page-header">
      <div>
        <h2>人员管理</h2>
        <p class="sub-title">外包人员信息与在岗管理</p>
      </div>
      <div>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>添加人员
        </el-button>
      </div>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="personnelList" style="width: 100%" v-loading="loading">
        <el-table-column prop="personnelCode" label="人员编号" width="130" />
        <el-table-column prop="name" label="姓名" width="100">
          <template #default="{ row }">
            <el-link type="primary" @click="goDetail(row)">{{ row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="positionName" label="岗位" width="140" />
        <el-table-column prop="supplierName" label="所属供应商" width="120">
          <template #default="{ row }">{{ getSupplierName(row.supplierId) }}</template>
        </el-table-column>
        <el-table-column prop="projectName" label="所属项目" width="120">
          <template #default="{ row }">{{ getProjectName(row.projectId) }}</template>
        </el-table-column>
        <el-table-column prop="dailyRate" label="人天单价" width="100">
          <template #default="{ row }">¥{{ row.dailyRate }}</template>
        </el-table-column>
        <el-table-column prop="entryDate" label="入场时间" width="120" />
        <el-table-column prop="contractEndDate" label="合同到期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEntry(row)" v-if="row.status === 'PENDING_ENTRY'">入场</el-button>
            <el-button type="warning" link @click="openTransferDialog(row)" v-if="row.status === 'ON_DUTY'">调配</el-button>
            <el-button type="danger" link @click="handleExit(row)" v-if="row.status === 'ON_DUTY'">离场</el-button>
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
    
    <el-dialog v-model="showDialog" title="添加外包人员" width="700px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" required>
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属供应商" required>
              <el-select v-model="form.supplierId" placeholder="请选择供应商" style="width: 100%">
                <el-option v-for="s in suppliers" :key="s.id" :label="s.supplierName" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属项目">
              <el-select v-model="form.projectId" placeholder="请选择项目" style="width: 100%" clearable>
                <el-option v-for="p in projects" :key="p.id" :label="p.projectName" :value="p.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="岗位名称" required>
              <el-input v-model="form.positionName" placeholder="如：前端开发工程师" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="人天单价" required>
              <el-input-number v-model="form.dailyRate" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入场时间">
              <el-date-picker v-model="form.entryDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同截止">
              <el-date-picker v-model="form.contractEndDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="showTransferDialog" title="调配人员" width="400px">
      <el-form label-width="100px">
        <el-form-item label="目标项目">
          <el-select v-model="transferProjectId" placeholder="请选择项目" style="width: 100%">
            <el-option v-for="p in projects" :key="p.id" :label="p.projectName" :value="p.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTransferDialog = false">取消</el-button>
        <el-button type="primary" @click="handleTransfer">确认调配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/index'

const router = useRouter()

const loading = ref(false)
const showDialog = ref(false)
const showTransferDialog = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const transferPersonnelId = ref(null)
const transferProjectId = ref(null)

const personnelList = ref([])
const suppliers = ref([])
const projects = ref([])

const goDetail = (row) => {
  router.push(`/personnel/${row.id}`)
}

const form = reactive({
  name: '',
  idCard: '',
  phone: '',
  email: '',
  supplierId: null,
  projectId: null,
  positionName: '',
  dailyRate: 800,
  entryDate: '',
  contractEndDate: ''
})

const getStatusType = (status) => {
  const map = { 'PENDING_ENTRY': 'info', 'ON_DUTY': 'success', 'LEAVE': 'warning', 'TRANSFER': 'primary', 'OFF_DUTY': 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 'PENDING_ENTRY': '待入场', 'ON_DUTY': '在岗', 'LEAVE': '请假', 'TRANSFER': '调配中', 'OFF_DUTY': '已离场' }
  return map[status] || '未知'
}

const getSupplierName = (supplierId) => {
  const supplier = suppliers.value.find(s => s.id === supplierId)
  return supplier ? supplier.supplierName : '-'
}

const getProjectName = (projectId) => {
  const project = projects.value.find(p => p.id === projectId)
  return project ? project.projectName : '-'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/personnel', { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    if (res.code === 200) {
      personnelList.value = res.data.records
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

const fetchProjects = async () => {
  try {
    const res = await request.get('/projects', { params: { pageNum: 1, pageSize: 100 } })
    if (res.code === 200) {
      projects.value = res.data.records
    }
  } catch (e) {
    console.error(e)
  }
}

const openAddDialog = () => {
  Object.assign(form, {
    name: '',
    idCard: '',
    phone: '',
    email: '',
    supplierId: null,
    projectId: null,
    positionName: '',
    dailyRate: 800,
    entryDate: '',
    contractEndDate: ''
  })
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!form.name || !form.supplierId || !form.positionName) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    const res = await request.post('/personnel', form)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      showDialog.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

const handleEntry = async (row) => {
  try {
    await ElMessageBox.confirm('确认该人员入场？', '提示', { type: 'warning' })
    const res = await request.post(`/personnel/${row.id}/entry`)
    if (res.code === 200) {
      ElMessage.success('入场成功')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleExit = async (row) => {
  try {
    await ElMessageBox.confirm('确认该人员离场？', '提示', { type: 'warning' })
    const res = await request.post(`/personnel/${row.id}/exit`)
    if (res.code === 200) {
      ElMessage.success('离场成功')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const openTransferDialog = (row) => {
  transferPersonnelId.value = row.id
  transferProjectId.value = null
  showTransferDialog.value = true
}

const handleTransfer = async () => {
  if (!transferProjectId.value) {
    ElMessage.warning('请选择目标项目')
    return
  }
  try {
    const res = await request.post(`/personnel/${transferPersonnelId.value}/transfer?targetProjectId=${transferProjectId.value}`)
    if (res.code === 200) {
      ElMessage.success('调配成功')
      showTransferDialog.value = false
      fetchData()
    }
  } catch (e) {
    ElMessage.error('调配失败')
  }
}

onMounted(() => {
  fetchData()
  fetchSuppliers()
  fetchProjects()
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
