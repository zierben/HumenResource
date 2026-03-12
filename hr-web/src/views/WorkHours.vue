<template>
  <div class="workhours">
    <div class="page-header">
      <div>
        <h2>工时管理</h2>
        <p class="sub-title">外包人员工时填报与审核</p>
      </div>
      <div>
        <el-button type="success" @click="handleBatchApprove" :disabled="selectedIds.length === 0">
          批量审核({{ selectedIds.length }})
        </el-button>
      </div>
    </div>
    
    <el-card shadow="hover">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="人力公司">
          <el-select v-model="searchForm.supplierId" placeholder="请选择" style="width: 150px" clearable @change="handleSupplierChange">
            <el-option v-for="s in suppliers" :key="s.id" :label="s.supplierName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="人员">
          <el-select v-model="searchForm.personnelId" placeholder="请选择人员" style="width: 120px" clearable>
            <el-option v-for="p in filteredPersonnel" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目">
          <el-select v-model="searchForm.projectId" placeholder="请选择项目" style="width: 180px" clearable>
            <el-option v-for="p in projects" :key="p.id" :label="p.projectName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 100px" clearable>
            <el-option label="待审核" value="PENDING" />
            <el-option label="已审核" value="APPROVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 220px"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card shadow="hover" style="margin-top: 20px">
      <el-table :data="workHoursList" style="width: 100%" v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="personnelName" label="人员" width="100" />
        <el-table-column prop="workDate" label="工作日期" width="110" />
        <el-table-column prop="hours" label="工时" width="70">
          <template #default="{ row }">{{ row.hours }}h</template>
        </el-table-column>
        <el-table-column prop="projectName" label="项目" min-width="160">
          <template #default="{ row }">{{ row.projectName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="任务内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'APPROVED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'APPROVED' ? '已审核' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="success" link @click="handleApprove(row)" v-if="row.status === 'PENDING'">审核</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/index'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedIds = ref([])

const workHoursList = ref([])
const projects = ref([])
const suppliers = ref([])
const personnel = ref([])

const searchForm = reactive({
  supplierId: null,
  personnelId: null,
  projectId: null,
  status: '',
  dateRange: []
})

const filteredPersonnel = computed(() => {
  if (!searchForm.supplierId) return personnel.value
  return personnel.value.filter(p => p.supplierId === searchForm.supplierId)
})

const handleSupplierChange = () => {
  searchForm.personnelId = null
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { pageNum: currentPage.value, pageSize: pageSize.value }
    if (searchForm.personnelId) params.personnelId = searchForm.personnelId
    if (searchForm.projectId) params.projectId = searchForm.projectId
    if (searchForm.status) params.status = searchForm.status
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    const res = await request.get('/work-hours', { params })
    if (res.code === 200) {
      const records = res.data.records || []
      records.forEach(item => {
        const person = personnel.value.find(p => p.id === item.personnelId)
        item.personnelName = person ? person.name : `ID:${item.personnelId}`
        const proj = projects.value.find(p => p.id === item.projectId)
        item.projectName = proj ? proj.projectName : ''
      })
      workHoursList.value = records
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
      projects.value = res.data.records || []
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchSuppliers = async () => {
  try {
    const res = await request.get('/suppliers', { params: { pageNum: 1, pageSize: 100 } })
    if (res.code === 200) {
      suppliers.value = res.data.records || []
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchPersonnel = async () => {
  try {
    const res = await request.get('/personnel', { params: { pageNum: 1, pageSize: 500 } })
    if (res.code === 200) {
      personnel.value = res.data.records || []
    }
  } catch (e) {
    console.error(e)
  }
}

const resetSearch = () => {
  searchForm.supplierId = null
  searchForm.personnelId = null
  searchForm.projectId = null
  searchForm.status = ''
  searchForm.dateRange = []
  fetchData()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm('确认审核通过？', '提示', { type: 'warning' })
    const res = await request.post(`/work-hours/${row.id}/approve`)
    if (res.code === 200) {
      ElMessage.success('审核成功')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleBatchApprove = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要审核的记录')
    return
  }
  try {
    await ElMessageBox.confirm(`确认批量审核 ${selectedIds.value.length} 条记录？`, '提示', { type: 'warning' })
    const promises = selectedIds.value.map(id => request.post(`/work-hours/${id}/approve`))
    await Promise.all(promises)
    ElMessage.success('批量审核成功')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchData()
  fetchProjects()
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

.search-form {
  margin-bottom: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
