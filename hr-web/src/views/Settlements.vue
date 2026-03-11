<template>
  <div class="settlements">
    <div class="page-header">
      <div>
        <h2>结算管理</h2>
        <p class="sub-title">外包费用结算与付款管理</p>
      </div>
      <div>
        <el-button type="primary" @click="openGenerateDialog">
          <el-icon><Plus /></el-icon>生成账单
        </el-button>
      </div>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="settlementList" style="width: 100%" v-loading="loading">
        <el-table-column prop="settlementCode" label="账单编号" width="150" />
        <el-table-column prop="supplierId" label="供应商" width="120">
          <template #default="{ row }">{{ getSupplierName(row.supplierId) }}</template>
        </el-table-column>
        <el-table-column prop="projectId" label="项目" width="120">
          <template #default="{ row }">{{ getProjectName(row.projectId) }}</template>
        </el-table-column>
        <el-table-column label="结算周期" width="120">
          <template #default="{ row }">{{ row.settlementYear }}年{{ row.settlementMonth }}月</template>
        </el-table-column>
        <el-table-column prop="validDays" label="有效天数" width="100" />
        <el-table-column prop="totalAmount" label="应付金额" width="120">
          <template #default="{ row }">¥{{ row.totalAmount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="finalAmount" label="实付金额" width="120">
          <template #default="{ row }">¥{{ row.finalAmount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showDetail(row)">详情</el-button>
            <el-button type="success" link @click="handleConfirm(row)" v-if="row.status >= 0 && row.status < 4">
              {{ getConfirmText(row.status) }}
            </el-button>
            <el-button type="warning" link @click="handlePay(row)" v-if="row.status === 4">付款</el-button>
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
    
    <el-dialog v-model="generateDialogVisible" title="生成月度账单" width="500px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="结算年份" required>
          <el-select v-model="generateForm.year" style="width: 100%">
            <el-option :label="new Date().getFullYear() + '年'" :value="new Date().getFullYear()" />
            <el-option :label="(new Date().getFullYear() - 1) + '年'" :value="new Date().getFullYear() - 1" />
          </el-select>
        </el-form-item>
        <el-form-item label="结算月份" required>
          <el-select v-model="generateForm.month" style="width: 100%">
            <el-option v-for="i in 12" :key="i" :label="i + '月'" :value="i" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleGenerate">生成账单</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="detailDialogVisible" title="账单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="账单编号">{{ currentDetail.settlementCode }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentDetail.status)">{{ getStatusText(currentDetail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="供应商">{{ getSupplierName(currentDetail.supplierId) }}</el-descriptions-item>
        <el-descriptions-item label="项目">{{ getProjectName(currentDetail.projectId) }}</el-descriptions-item>
        <el-descriptions-item label="结算周期">{{ currentDetail.settlementYear }}年{{ currentDetail.settlementMonth }}月</el-descriptions-item>
        <el-descriptions-item label="有效天数">{{ currentDetail.validDays }}天</el-descriptions-item>
        <el-descriptions-item label="应付金额">¥{{ currentDetail.totalAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ currentDetail.finalAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="扣款金额" :span="2">¥{{ currentDetail.deductionAmount?.toLocaleString() || '0' }}</el-descriptions-item>
        <el-descriptions-item label="确认备注" :span="2" v-if="currentDetail.confirmRemark">{{ currentDetail.confirmRemark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/index'

const loading = ref(false)
const generateDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentDetail = ref(null)

const settlementList = ref([])
const suppliers = ref([])
const projects = ref([])

const generateForm = reactive({
  year: new Date().getFullYear(),
  month: new Date().getMonth() + 1
})

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'primary', 3: '', 4: 'success', 5: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待确认', 1: 'HR已确认', 2: '项目经理已确认', 3: '财务已确认', 4: '供应商已确认', 5: '已付款' }
  return map[status] || '未知'
}

const getConfirmText = (status) => {
  const map = { 0: 'HR确认', 1: 'PM确认', 2: '财务确认', 3: '供应商确认' }
  return map[status] || '确认'
}

const getSupplierName = (supplierId) => {
  const supplier = suppliers.value.find(s => s.id === supplierId)
  return supplier ? supplier.supplierName : supplierId || '-'
}

const getProjectName = (projectId) => {
  const project = projects.value.find(p => p.id === projectId)
  return project ? project.projectName : projectId || '-'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/settlements', { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    if (res.code === 200) {
      settlementList.value = res.data.records
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

const openGenerateDialog = () => {
  generateForm.year = new Date().getFullYear()
  generateForm.month = new Date().getMonth() + 1
  generateDialogVisible.value = true
}

const handleGenerate = async () => {
  try {
    await ElMessageBox.confirm(`确认生成 ${generateForm.year}年${generateForm.month}月 的结算账单？`, '提示', { type: 'warning' })
    const res = await request.post(`/settlements/generate?year=${generateForm.year}&month=${generateForm.month}`)
    if (res.code === 200) {
      ElMessage.success('账单生成成功')
      generateDialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '生成失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleConfirm = async (row) => {
  const confirmText = getConfirmText(row.status)
  try {
    await ElMessageBox.confirm(`确认${confirmText}？`, '提示', { type: 'warning' })
    let url = ''
    switch (row.status) {
      case 0: url = `/settlements/${row.id}/hr-confirm`; break
      case 1: url = `/settlements/${row.id}/pm-confirm`; break
      case 2: url = `/settlements/${row.id}/finance-confirm`; break
      case 3: url = `/settlements/${row.id}/supplier-confirm`; break
    }
    const res = await request.post(url)
    if (res.code === 200) {
      ElMessage.success('确认成功')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const handlePay = async (row) => {
  try {
    await ElMessageBox.confirm(`确认付款 ¥${row.finalAmount?.toLocaleString()} ？`, '提示', { type: 'warning' })
    const res = await request.post(`/settlements/${row.id}/pay`)
    if (res.code === 200) {
      ElMessage.success('付款成功')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const showDetail = (row) => {
  currentDetail.value = row
  detailDialogVisible.value = true
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
