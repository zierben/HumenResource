<template>
  <div class="workhours">
    <div class="page-header">
      <div>
        <h2>工时审核</h2>
        <p class="sub-title">外包人员工时填报与审核</p>
      </div>
      <div class="header-actions">
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button value="pending">待审核</el-radio-button>
          <el-radio-button value="all">全部</el-radio-button>
        </el-radio-group>
        <el-button type="success" @click="handleBatchApproveStandard" v-if="viewMode === 'pending'">
          批量通过(规范)
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
    
    <div class="workhours-grid" v-loading="loading">
      <div 
        v-for="item in workHoursList" 
        :key="item.id" 
        class="workhours-card"
        :class="getCardClass(item)"
      >
        <div class="card-header">
          <div class="person-info">
            <el-avatar :size="36" :style="{ background: getAvatarColor(item.personnelName) }">
              {{ item.personnelName?.charAt(0) || '?' }}
            </el-avatar>
            <div class="person-detail">
              <div class="person-name">{{ item.personnelName }}</div>
              <div class="person-project">{{ item.projectName || '未分配项目' }}</div>
            </div>
          </div>
          <el-tag :type="getStatusType(item.status)" size="small">
            {{ item.status === 'APPROVED' ? '已审核' : '待审核' }}
          </el-tag>
        </div>
        
        <div class="card-body">
          <div class="hours-display" :class="getHoursClass(item)">
            <span class="hours-value">{{ item.hours || 0 }}</span>
            <span class="hours-unit">小时</span>
          </div>
          <div class="work-date">
            <el-icon><Calendar /></el-icon>
            {{ item.workDate }}
          </div>
          <div class="remark" v-if="item.remark">
            {{ item.remark }}
          </div>
          <div class="warning-tags">
            <el-tag v-if="item.hours === 0" type="danger" size="small">缺岗</el-tag>
            <el-tag v-else-if="item.hours < 8" type="warning" size="small">不足8小时</el-tag>
            <el-tag v-if="isLeave(item)" type="info" size="small">请假</el-tag>
          </div>
        </div>
        
        <div class="card-footer" v-if="item.status === 'PENDING'">
          <!-- 8小时及以上：直接审核通过 -->
          <el-button type="success" size="small" @click="handleApprove(item)" v-if="item.hours >= 8">
            <el-icon><Check /></el-icon>
            审核通过
          </el-button>
          <!-- 0小时：确认为0或请假 -->
          <el-button type="info" size="small" @click="handleConfirmZero(item)" v-else-if="item.hours === 0">
            <el-icon><Check /></el-icon>
            确认为0
          </el-button>
          <!-- 不足8小时：可确认为8小时，或按实际 -->
          <el-dropdown v-else trigger="click" @command="(cmd) => handleInsufficientHours(item, cmd)">
            <el-button type="warning" size="small">
              <el-icon><Check /></el-icon>
              {{ item.hours }}小时
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :command="'full'">确认为8小时</el-dropdown-item>
                <el-dropdown-item :command="'actual'">按实际 {{ item.hours }} 小时</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <!-- 拒绝按钮 -->
          <el-button type="danger" size="small" plain @click="handleReject(item)">
            拒绝
          </el-button>
        </div>
        <div class="card-footer approved" v-else>
          <el-icon><Check /></el-icon>
          已审核
        </div>
      </div>
      
      <el-empty v-if="!loading && workHoursList.length === 0" description="暂无待审核工时" />
    </div>
    
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Check, ArrowDown } from '@element-plus/icons-vue'
import request from '@/api/index'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const viewMode = ref('pending')

const workHoursList = ref([])
const projects = ref([])
const suppliers = ref([])
const personnel = ref([])

const searchForm = reactive({
  supplierId: null,
  personnelId: null,
  projectId: null,
  dateRange: []
})

const filteredPersonnel = computed(() => {
  if (!searchForm.supplierId) return personnel.value
  return personnel.value.filter(p => p.supplierId === searchForm.supplierId)
})

const handleSupplierChange = () => {
  searchForm.personnelId = null
}

const isLeave = (item) => {
  const remark = item.remark || ''
  return remark.includes('请假') || remark.includes('leave') || remark.includes('病假') || remark.includes('事假')
}

const getCardClass = (item) => {
  if (item.status === 'APPROVED') return 'approved-card'
  if (item.hours === 0) return 'danger-card'
  if (item.hours < 8) return 'warning-card'
  if (isLeave(item)) return 'info-card'
  return 'normal-card'
}

const getHoursClass = (item) => {
  if (item.hours === 0) return 'hours-danger'
  if (item.hours < 8) return 'hours-warning'
  return 'hours-normal'
}

const getStatusType = (status) => {
  return status === 'APPROVED' ? 'success' : 'warning'
}

const getAvatarColor = (name) => {
  const colors = ['#667eea', '#764ba2', '#00c9ff', '#92fe9d', '#f78ca0', '#f8b500']
  const index = (name?.charCodeAt(0) || 0) % colors.length
  return colors[index]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { 
      pageNum: currentPage.value, 
      pageSize: pageSize.value 
    }
    
    if (viewMode.value === 'pending') {
      params.status = 'PENDING'
    }
    
    if (searchForm.personnelId) params.personnelId = searchForm.personnelId
    if (searchForm.projectId) params.projectId = searchForm.projectId
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

const handleApprove = async (item) => {
  try {
    await request.post(`/work-hours/${item.id}/approve`)
    ElMessage.success('审核通过')
    fetchData()
  } catch (e) {
    console.error(e)
    ElMessage.error('操作失败')
  }
}

const handleConfirmZero = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确认 ${item.personnelName} 当日工时为0（缺勤/请假）？`, 
      '确认为0', 
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    await request.post(`/work-hours/${item.id}/approve`)
    ElMessage.success('已确认为0并审核')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
      ElMessage.error('操作失败')
    }
  }
}

const handleInsufficientHours = async (item, command) => {
  if (command === 'full') {
    // 确认为8小时
    try {
      await ElMessageBox.confirm(
        `将 ${item.personnelName} 的工时确认为8小时（当前：${item.hours}小时）？确认后按8小时结算。`, 
        '确认为全天', 
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
      await request.put(`/work-hours/${item.id}`, { hours: 8, remark: (item.remark || '') + ' [已确认为全天]' })
      await request.post(`/work-hours/${item.id}/approve`)
      ElMessage.success('已确认为全天并审核')
      fetchData()
    } catch (e) {
      if (e !== 'cancel') {
        console.error(e)
        ElMessage.error('操作失败')
      }
    }
  } else {
    // 按实际小时数
    try {
      await request.post(`/work-hours/${item.id}/approve`)
      ElMessage.success(`按实际 ${item.hours} 小时审核通过`)
      fetchData()
    } catch (e) {
      console.error(e)
      ElMessage.error('操作失败')
    }
  }
}

const handleReject = async (item) => {
  try {
    await ElMessageBox.prompt('请输入拒绝原因', '拒绝工时', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    })
    ElMessage.info('已拒绝')
    fetchData()
  } catch (e) {
    // user cancelled
  }
}

const handleBatchApproveStandard = async () => {
  // 只批量审核规范的：8小时或0小时的（不足8小时的需单独处理）
  const standardItems = workHoursList.value.filter(item => item.hours >= 8 || item.hours === 0)
  const insufficientItems = workHoursList.value.filter(item => item.hours > 0 && item.hours < 8)
  
  if (insufficientItems.length > 0) {
    ElMessage.warning(`有 ${insufficientItems.length} 条不足8小时的工时需单独处理，已自动跳过`)
  }
  
  if (standardItems.length === 0) {
    ElMessage.info('没有符合条件的规范工时')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确认通过 ${standardItems.length} 条规范工时？（8小时或0小时的）不足8小时的将跳过，需单独处理。`, 
      '批量通过', 
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const promises = standardItems.map(item => request.post(`/work-hours/${item.id}/approve`))
    await Promise.all(promises)
    ElMessage.success(`已通过 ${standardItems.length} 条规范工时`)
    fetchData()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
      ElMessage.error('批量操作失败')
    }
  }
}

const resetSearch = () => {
  searchForm.supplierId = null
  searchForm.personnelId = null
  searchForm.projectId = null
  searchForm.dateRange = []
  currentPage.value = 1
  fetchData()
}

watch(viewMode, () => {
  currentPage.value = 1
  fetchData()
})

onMounted(() => {
  fetchProjects()
  fetchSuppliers()
  fetchPersonnel()
  fetchData()
})
</script>

<style scoped>
.workhours {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.sub-title {
  margin: 5px 0 0;
  color: #909399;
  font-size: 13px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.workhours-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-top: 20px;
  min-height: 200px;
}

.workhours-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
}

.workhours-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.workhours-card.danger-card {
  border-color: #f56c6c;
  background: #fef0f0;
}

.workhours-card.warning-card {
  border-color: #e6a23c;
  background: #fdf6ec;
}

.workhours-card.info-card {
  border-color: #909399;
  background: #f4f4f5;
}

.workhours-card.approved-card {
  border-color: #67c23a;
  background: #f0f9eb;
  opacity: 0.8;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.person-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.person-detail {
  display: flex;
  flex-direction: column;
}

.person-name {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
}

.person-project {
  font-size: 12px;
  color: #909399;
}

.card-body {
  margin-bottom: 12px;
}

.hours-display {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 8px;
}

.hours-value {
  font-size: 32px;
  font-weight: 700;
}

.hours-normal .hours-value {
  color: #67c23a;
}

.hours-warning .hours-value {
  color: #e6a23c;
}

.hours-danger .hours-value {
  color: #f56c6c;
}

.hours-unit {
  font-size: 14px;
  color: #909399;
}

.work-date {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}

.remark {
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 6px 10px;
  border-radius: 4px;
  margin-bottom: 8px;
}

.warning-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.card-footer {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.card-footer.approved {
  justify-content: center;
  color: #67c23a;
  font-size: 13px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
