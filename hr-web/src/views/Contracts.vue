<template>
  <div class="contracts">
    <div class="page-header">
      <div>
        <h2>合同管理</h2>
        <p class="sub-title">供应商合同与人员合同管理</p>
      </div>
      <div>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>添加合同
        </el-button>
      </div>
    </div>
    
    <el-alert 
      v-if="expiredContracts.length > 0" 
      :title="`${expiredContracts.length}个合同已过期`" 
      type="error" 
      show-icon 
      :closable="false"
      style="margin-bottom: 12px"
    >
      <template #default>
        <span v-for="(c, i) in expiredContracts.slice(0, 3)" :key="c.id">
          {{ c.contractName }}({{ c.endDate }}){{ i < Math.min(expiredContracts.length, 3) - 1 ? '、' : '' }}
        </span>
        <span v-if="expiredContracts.length > 3">等{{ expiredContracts.length }}个合同</span>
      </template>
    </el-alert>
    
    <el-alert 
      v-if="expiringContracts.length > 0" 
      :title="`${expiringContracts.length}个合同即将到期`" 
      type="warning" 
      show-icon 
      :closable="false"
      style="margin-bottom: 16px"
    >
      <template #default>
        <span v-for="(c, i) in expiringContracts.slice(0, 3)" :key="c.id">
          {{ c.contractName }}({{ c.endDate }}){{ i < Math.min(expiringContracts.length, 3) - 1 ? '、' : '' }}
        </span>
        <span v-if="expiringContracts.length > 3">等{{ expiringContracts.length }}个合同</span>
      </template>
    </el-alert>
    
    <el-card shadow="hover">
      <el-table :data="contractList" style="width: 100%" v-loading="loading">
        <el-table-column prop="contractCode" label="合同编号" width="130" />
        <el-table-column prop="contractName" label="合同名称" width="200" />
        <el-table-column prop="contractType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.contractType === 'SUPPLIER' ? 'primary' : 'success'" size="small">
              {{ row.contractType === 'SUPPLIER' ? '供应商合同' : '人员合同' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="supplierName" label="供应商" width="120">
          <template #default="{ row }">{{ getSupplierName(row.supplierId) }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="结束日期" width="120">
          <template #default="{ row }">
            <span :class="{ 'text-warning': isExpiringSoon(row.endDate), 'text-danger': isExpired(row.endDate) }">
              {{ row.endDate }}
              <el-tag v-if="isExpiringSoon(row.endDate)" type="warning" size="small" style="margin-left: 4px">即将到期</el-tag>
              <el-tag v-if="isExpired(row.endDate)" type="danger" size="small" style="margin-left: 4px">已过期</el-tag>
            </span>
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
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑合同' : '添加合同'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同编号" required>
              <el-input v-model="form.contractCode" placeholder="如：HT2024001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同名称" required>
              <el-input v-model="form.contractName" placeholder="请输入合同名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同类型" required>
              <el-select v-model="form.contractType" placeholder="请选择" style="width: 100%">
                <el-option label="供应商合同" value="SUPPLIER" />
                <el-option label="人员合同" value="PERSONNEL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同金额">
              <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" style="width: 100%" clearable filterable>
                <el-option v-for="s in suppliers" :key="s.id" :label="s.supplierName" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联人员">
              <el-select v-model="form.personnelId" placeholder="请选择人员" style="width: 100%" clearable filterable>
                <el-option v-for="p in personnelList" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="签订日期">
              <el-date-picker v-model="form.signDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="生效" :value="1" />
                <el-option label="到期" :value="2" />
                <el-option label="终止" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
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

const contractList = ref([])
const suppliers = ref([])
const personnelList = ref([])
const expiringContracts = ref([])
const expiredContracts = ref([])

const form = reactive({
  id: null,
  contractCode: '',
  contractName: '',
  contractType: 'SUPPLIER',
  supplierId: null,
  personnelId: null,
  projectId: null,
  startDate: '',
  endDate: '',
  amount: 0,
  status: 1,
  signDate: '',
  remark: ''
})

const getStatusType = (status) => {
  const map = { 1: 'success', 2: 'warning', 3: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 1: '生效', 2: '到期', 3: '终止' }
  return map[status] || '未知'
}

const getSupplierName = (supplierId) => {
  const supplier = suppliers.value.find(s => s.id === supplierId)
  return supplier ? supplier.supplierName : '-'
}

const isExpiringSoon = (date) => {
  if (!date) return false
  const days = (new Date(date) - new Date()) / (1000 * 60 * 60 * 24)
  return days > 0 && days <= 30
}

const isExpired = (date) => {
  if (!date) return false
  return new Date(date) < new Date()
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/contracts', { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    if (res.code === 200) {
      contractList.value = res.data.records
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchExpiring = async () => {
  try {
    const res = await request.get('/contracts/expiring')
    if (res.code === 200 && Array.isArray(res.data)) {
      expiringContracts.value = res.data
    } else {
      expiringContracts.value = []
    }
  } catch (e) {
    console.error(e)
    expiringContracts.value = []
  }
}

const fetchExpired = async () => {
  try {
    const res = await request.get('/contracts/expired')
    if (res.code === 200 && Array.isArray(res.data)) {
      expiredContracts.value = res.data
    } else {
      expiredContracts.value = []
    }
  } catch (e) {
    console.error(e)
    expiredContracts.value = []
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

const fetchPersonnel = async () => {
  try {
    const res = await request.get('/personnel', { params: { pageNum: 1, pageSize: 100 } })
    if (res.code === 200) {
      personnelList.value = res.data.records
    }
  } catch (e) {
    console.error(e)
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    contractCode: '',
    contractName: '',
    contractType: 'SUPPLIER',
    supplierId: null,
    personnelId: null,
    projectId: null,
    startDate: '',
    endDate: '',
    amount: 0,
    status: 1,
    signDate: '',
    remark: ''
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
  if (!form.contractCode || !form.contractName) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    let res
    if (isEdit.value) {
      res = await request.put(`/contracts/${form.id}`, form)
    } else {
      res = await request.post('/contracts', form)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      showDialog.value = false
      fetchData()
      fetchExpiring()
      fetchExpired()
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除合同"${row.contractName}"？`, '提示', { type: 'warning' })
    const res = await request.delete(`/contracts/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
      fetchExpiring()
      fetchExpired()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchData()
  fetchExpiring()
  fetchExpired()
  fetchSuppliers()
  fetchPersonnel()
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

.text-warning {
  color: #e6a23c;
  font-weight: 500;
}

.text-danger {
  color: #f56c6c;
  font-weight: 500;
}
</style>
