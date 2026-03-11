<template>
  <div class="requirements">
    <div class="page-header">
      <div>
        <h2>需求管理</h2>
        <p class="sub-title">外包人力需求申请与审批</p>
      </div>
      <div>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>新建需求
        </el-button>
      </div>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="requirementCode" label="需求编号" width="130" />
        <el-table-column prop="positionName" label="需求名称">
          <template #default="{ row }">
            <div>{{ row.positionName }}</div>
            <div class="sub-text">{{ row.projectName }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="demandCount" label="人数" width="70" />
        <el-table-column prop="dailyRate" label="人天单价" width="100">
          <template #default="{ row }">¥{{ row.dailyRate }}</template>
        </el-table-column>
        <el-table-column prop="totalBudget" label="总预算" width="120">
          <template #default="{ row }">¥{{ row.totalBudget ? row.totalBudget.toLocaleString() : '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workflowStatus" label="审批进度" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.workflowInstanceId" :type="getWorkflowStatusType(row.workflowStatus)">
              {{ getWorkflowStatusText(row.workflowStatus) }}
            </el-tag>
            <span v-else style="color: #909399">未提交</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" type="success" link @click="submitToWorkflow(row)">提交审批</el-button>
            <template v-if="row.status >= 1 && row.status <= 3">
              <el-button type="success" link @click="handleApprove(row, row.status + 1)">通过</el-button>
              <el-button type="danger" link @click="openRejectDialog(row)">驳回</el-button>
            </template>
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
    
    <el-dialog v-model="showDialog" title="新建外包需求" width="700px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属项目" required>
              <el-select v-model="form.projectId" placeholder="请选择项目" style="width: 100%">
                <el-option v-for="p in projects" :key="p.id" :label="p.projectName" :value="p.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位名称" required>
              <el-input v-model="form.positionName" placeholder="如：前端开发工程师" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职级">
              <el-select v-model="form.positionLevel" placeholder="请选择职级" style="width: 100%">
                <el-option label="P5" value="P5" />
                <el-option label="P6" value="P6" />
                <el-option label="P7" value="P7" />
                <el-option label="P8" value="P8" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="需求人数" required>
              <el-input-number v-model="form.demandCount" :min="1" :max="20" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="技能要求">
          <el-input v-model="form.skills" type="textarea" placeholder="如：Vue3、TypeScript、Element Plus" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="到岗时间">
              <el-date-picker v-model="form.expectedEntryDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务周期(月)">
              <el-input-number v-model="form.serviceMonths" :min="1" :max="24" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="人天单价" required>
              <el-input-number v-model="form.dailyRate" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="月度预算" required>
              <el-input-number v-model="form.monthlyBudget" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总预算" required>
              <el-input-number v-model="form.totalBudget" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="需求理由" required>
          <el-input v-model="form.demandReason" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="工作内容">
          <el-input v-model="form.workContent" type="textarea" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="showRejectDialog" title="驳回原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" rows="3" placeholder="请输入驳回原因" />
      <template #footer>
        <el-button @click="showRejectDialog = false">取消</el-button>
        <el-button type="danger" @click="handleReject">确认驳回</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="showDetailDialog" title="需求详情" width="700px">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="需求编号">{{ currentDetail.requirementCode }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentDetail.status)">{{ getStatusText(currentDetail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="岗位名称">{{ currentDetail.positionName }}</el-descriptions-item>
        <el-descriptions-item label="职级">{{ currentDetail.positionLevel }}</el-descriptions-item>
        <el-descriptions-item label="需求人数">{{ currentDetail.demandCount }}</el-descriptions-item>
        <el-descriptions-item label="服务周期">{{ currentDetail.serviceMonths }}个月</el-descriptions-item>
        <el-descriptions-item label="人天单价">¥{{ currentDetail.dailyRate }}</el-descriptions-item>
        <el-descriptions-item label="总预算">¥{{ currentDetail.totalBudget?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="技能要求" :span="2">{{ currentDetail.skills }}</el-descriptions-item>
        <el-descriptions-item label="需求理由" :span="2">{{ currentDetail.demandReason }}</el-descriptions-item>
        <el-descriptions-item label="工作内容" :span="2">{{ currentDetail.workContent }}</el-descriptions-item>
        <el-descriptions-item label="驳回原因" :span="2" v-if="currentDetail.rejectReason">
          <span style="color: #f56c6c">{{ currentDetail.rejectReason }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/index'

const loading = ref(false)
const showDialog = ref(false)
const showRejectDialog = ref(false)
const showDetailDialog = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const rejectReason = ref('')
const currentRejectId = ref(null)
const currentDetail = ref(null)

const tableData = ref([])
const projects = ref([])

const form = reactive({
  projectId: null,
  positionName: '',
  positionLevel: 'P6',
  skills: '',
  expectedEntryDate: '',
  serviceMonths: 6,
  dailyRate: 800,
  monthlyBudget: 20000,
  totalBudget: 0,
  demandCount: 1,
  demandReason: '',
  workContent: ''
})

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'warning', 3: 'warning', 4: 'success', 5: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '草稿', 1: '部门待审', 2: 'HR待审', 3: '财务待审', 4: '已通过', 5: '已驳回' }
  return map[status] || '未知'
}

const getWorkflowStatusType = (status) => {
  const map = { 'PENDING': 'warning', 'APPROVED': 'success', 'REJECTED': 'danger', 'CANCELLED': 'info' }
  return map[status] || 'info'
}

const getWorkflowStatusText = (status) => {
  const map = { 'PENDING': '审批中', 'APPROVED': '已通过', 'REJECTED': '已拒绝', 'CANCELLED': '已取消' }
  return map[status] || status
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/requirements', { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
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
    projectId: null,
    positionName: '',
    positionLevel: 'P6',
    skills: '',
    expectedEntryDate: '',
    serviceMonths: 6,
    dailyRate: 800,
    monthlyBudget: 20000,
    totalBudget: 0,
    demandCount: 1,
    demandReason: '',
    workContent: ''
  })
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!form.projectId || !form.positionName || !form.demandCount || !form.dailyRate) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    const res = await request.post('/requirements', form)
    if (res.code === 200) {
      ElMessage.success('提交成功')
      showDialog.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch (e) {
    ElMessage.error('提交失败')
  }
}

const handleApprove = async (row, newStatus) => {
  try {
    await ElMessageBox.confirm('确认通过该需求？', '提示', { type: 'warning' })
    const res = await request.post(`/requirements/${row.id}/approve?status=${newStatus}`)
    if (res.code === 200) {
      ElMessage.success('审批通过')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const openRejectDialog = (row) => {
  currentRejectId.value = row.id
  rejectReason.value = ''
  showRejectDialog.value = true
}

const handleReject = async () => {
  if (!rejectReason.value) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  try {
    const res = await request.post(`/requirements/${currentRejectId.value}/approve?status=5&rejectReason=${encodeURIComponent(rejectReason.value)}`)
    if (res.code === 200) {
      ElMessage.success('已驳回')
      showRejectDialog.value = false
      fetchData()
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const showDetail = (row) => {
  currentDetail.value = row
  showDetailDialog.value = true
}

onMounted(() => {
  fetchData()
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

.sub-text {
  font-size: 12px;
  color: #909399;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
