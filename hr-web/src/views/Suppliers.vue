<template>
  <div class="suppliers">
    <div class="page-header">
      <div>
        <h2>供应商管理</h2>
        <p class="sub-title">外包供应商信息与考核管理</p>
      </div>
      <div>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>添加供应商
        </el-button>
      </div>
    </div>
    
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8" v-for="supplier in suppliers" :key="supplier.id">
        <el-card shadow="hover" class="supplier-card" @click="goToDetail(supplier)">
          <div class="supplier-header">
            <h3>{{ supplier.supplierName }}</h3>
            <el-tag :type="supplier.level === 'A' ? 'success' : supplier.level === 'B' ? 'warning' : 'danger'">
              {{ supplier.level }}级
            </el-tag>
          </div>
          <div class="supplier-info">
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>联系人：{{ supplier.contactPerson }}</span>
            </div>
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <span>电话：{{ supplier.contactPhone }}</span>
            </div>
            <div class="info-item">
              <el-icon><Message /></el-icon>
              <span>邮箱：{{ supplier.contactEmail || '-' }}</span>
            </div>
            <div class="info-item">
              <el-icon><UserFilled /></el-icon>
              <span>当前人数：{{ supplier.currentPersonCount || 0 }}</span>
            </div>
          </div>
          <div class="supplier-rate">
            <span>交付质量</span>
            <el-progress :percentage="supplier.deliveryRate || 0" :stroke-width="8" />
          </div>
          <div class="supplier-footer" @click.stop>
            <el-button type="primary" link @click="openEditDialog(supplier)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(supplier)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchData"
      />
    </div>
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑供应商' : '添加供应商'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="供应商名称" required>
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="联系人" required>
          <el-input v-model="form.contactPerson" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" required>
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系邮箱">
          <el-input v-model="form.contactEmail" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="供应商等级" required>
          <el-radio-group v-model="form.level">
            <el-radio label="A">A级</el-radio>
            <el-radio label="B">B级</el-radio>
            <el-radio label="C">C级</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="合作中" inactive-text="已停用" />
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/index'

const router = useRouter()
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const suppliers = ref([])

const form = reactive({
  id: null,
  supplierName: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  level: 'B',
  deliveryRate: 0,
  currentPersonCount: 0,
  status: 1
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/suppliers', { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    if (res.code === 200) {
      suppliers.value = res.data.records
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    supplierName: '',
    contactPerson: '',
    contactPhone: '',
    contactEmail: '',
    level: 'B',
    deliveryRate: 0,
    currentPersonCount: 0,
    status: 1
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
  if (!form.supplierName || !form.contactPerson || !form.contactPhone || !form.level) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    let res
    if (isEdit.value) {
      res = await request.put(`/suppliers/${form.id}`, form)
    } else {
      res = await request.post('/suppliers', form)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      showDialog.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除供应商"${row.supplierName}"？`, '提示', { type: 'warning' })
    const res = await request.delete(`/suppliers/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const goToDetail = (supplier) => {
  router.push(`/suppliers/${supplier.id}`)
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
  font-size: 20px;
}

.sub-title {
  margin: 5px 0 0;
  color: #909399;
  font-size: 14px;
}

.supplier-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.supplier-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.supplier-header h3 {
  margin: 0;
  font-size: 16px;
}

.supplier-info {
  padding: 15px 0;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.info-item .el-icon {
  color: #909399;
}

.supplier-rate {
  padding: 15px 0;
  border-top: 1px solid #ebeef5;
}

.supplier-rate span {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
  display: block;
}

.supplier-footer {
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
