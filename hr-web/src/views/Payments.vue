<template>
  <div class="payments">
    <div class="page-header">
      <div>
        <h2>付款管理</h2>
        <p class="sub-title">供应商付款记录管理</p>
      </div>
      <div>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>添加付款
        </el-button>
      </div>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="paymentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="paymentCode" label="付款编号" width="130" />
        <el-table-column prop="supplierId" label="供应商" width="150">
          <template #default="{ row }">{{ getSupplierName(row.supplierId) }}</template>
        </el-table-column>
        <el-table-column prop="settlementId" label="结算单号" width="130" />
        <el-table-column prop="amount" label="付款金额" width="120">
          <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="paymentDate" label="付款日期" width="110" />
        <el-table-column prop="paymentMethod" label="付款方式" width="100" />
        <el-table-column prop="voucherPath" label="凭证" width="100">
          <template #default="{ row }">
            <el-button v-if="row.voucherPath" type="primary" link @click="viewVoucher(row.voucherPath)">查看</el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑付款' : '添加付款'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="付款编号" required>
              <el-input v-model="form.paymentCode" placeholder="如：FK2024001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" required>
              <el-select v-model="form.supplierId" placeholder="请选择供应商" style="width: 100%" clearable filterable>
                <el-option v-for="s in suppliers" :key="s.id" :label="s.supplierName" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="结算单号">
              <el-input v-model="form.settlementId" placeholder="请输入结算单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款金额" required>
              <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="付款日期">
              <el-date-picker v-model="form.paymentDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款方式">
              <el-select v-model="form.paymentMethod" placeholder="请选择" style="width: 100%">
                <el-option label="银行转账" value="银行转账" />
                <el-option label="现金" value="现金" />
                <el-option label="支票" value="支票" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="凭证路径">
              <el-input v-model="form.voucherPath" placeholder="凭证文件路径" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="待付款" :value="0" />
                <el-option label="已付款" :value="1" />
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

const paymentList = ref([])
const suppliers = ref([])

const form = reactive({
  id: null,
  paymentCode: '',
  supplierId: null,
  settlementId: '',
  amount: 0,
  paymentDate: '',
  paymentMethod: '',
  voucherPath: '',
  status: 0,
  remark: ''
})

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待付款', 1: '已付款' }
  return map[status] || '未知'
}

const getSupplierName = (supplierId) => {
  const supplier = suppliers.value.find(s => s.id === supplierId)
  return supplier ? supplier.supplierName : '-'
}

const viewVoucher = (path) => {
  window.open(path, '_blank')
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/payments', { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    if (res.code === 200) {
      paymentList.value = res.data.records
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
    paymentCode: '',
    supplierId: null,
    settlementId: '',
    amount: 0,
    paymentDate: '',
    paymentMethod: '',
    voucherPath: '',
    status: 0,
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
  if (!form.paymentCode || !form.supplierId || form.amount === null) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    let res
    if (isEdit.value) {
      res = await request.put(`/payments/${form.id}`, form)
    } else {
      res = await request.post('/payments', form)
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
    await ElMessageBox.confirm(`确认删除付款记录"${row.paymentCode}"？`, '提示', { type: 'warning' })
    const res = await request.delete(`/payments/${row.id}`)
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
