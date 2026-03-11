<template>
  <div class="project-detail">
    <div class="page-header">
      <el-button @click="$router.back()" circle>
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2>{{ project?.projectName || '项目详情' }}</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-tag :type="project?.status === 1 ? 'success' : 'info'">
                {{ project?.status === 1 ? '进行中' : '已结束' }}
              </el-tag>
            </div>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="项目编号">{{ project?.projectCode }}</el-descriptions-item>
            <el-descriptions-item label="所属部门">{{ project?.department }}</el-descriptions-item>
            <el-descriptions-item label="项目经理">{{ project?.manager }}</el-descriptions-item>
            <el-descriptions-item label="甲方">{{ project?.partyA }}</el-descriptions-item>
            <el-descriptions-item label="乙方">{{ project?.partyB }}</el-descriptions-item>
            <el-descriptions-item label="总预算">¥{{ project?.budget?.toLocaleString() }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" class="chart-card">
          <template #header>人员分布（按供应商）</template>
          <div ref="pieChartRef" style="height: 300px"></div>
        </el-card>

        <el-card shadow="hover" class="chart-card">
          <template #header>预算使用情况</template>
          <div ref="barChartRef" style="height: 250px"></div>
        </el-card>

        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>里程碑/付款节点</span>
              <el-button type="primary" size="small" @click="openMilestoneDialog">添加</el-button>
            </div>
          </template>
          <el-table :data="milestones" size="small">
            <el-table-column prop="milestoneName" label="名称" />
            <el-table-column prop="planDate" label="计划日期" width="120" />
            <el-table-column prop="actualDate" label="实际日期" width="120" />
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">
                  {{ row.status === 1 ? '已付款' : '待付款' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="editMilestone(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="budget-card">
          <template #header>预算概览</template>
          <div class="budget-info">
            <el-progress type="dashboard" :percentage="budgetInfo.usedPercent" :width="120" />
            <div class="budget-text">
              <p>总预算: <strong>¥{{ budgetInfo.totalBudget?.toLocaleString() }}</strong></p>
              <p>已结算: <span class="used">¥{{ budgetInfo.usedBudget?.toLocaleString() }}</span></p>
              <p>待结算: <span class="pending">¥{{ budgetInfo.pendingBudget?.toLocaleString() }}</span></p>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="risk-card" v-if="risks.length > 0">
          <template #header>风险预警</template>
          <div v-for="(risk, index) in risks" :key="index" class="risk-item">
            <el-alert :title="risk.message" :type="risk.level === 'danger' ? 'error' : 'warning'" show-icon />
          </div>
        </el-card>

        <el-card shadow="hover">
          <template #header>供应商人员</template>
          <div v-for="sp in supplierPersonnel" :key="sp.supplierId" class="supplier-item">
            <div class="supplier-header">
              <span class="supplier-name">{{ sp.supplierName }}</span>
              <el-tag size="small">{{ sp.activeCount }}/{{ sp.totalCount }}人在岗</el-tag>
            </div>
            <el-avatar-group>
              <el-avatar v-for="p in sp.personnel?.slice(0, 5)" :key="p.id" :size="32" style="background: #409eff">
                {{ p.name?.charAt(0) }}
              </el-avatar>
              <el-avatar v-if="sp.personnel?.length > 5" :size="32" style="background: #909399">
                +{{ sp.personnel.length - 5 }}
              </el-avatar>
            </el-avatar-group>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="milestoneDialogVisible" :title="isEditMilestone ? '编辑里程碑' : '添加里程碑'" width="400px">
      <el-form :model="milestoneForm" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="milestoneForm.milestoneName" />
        </el-form-item>
        <el-form-item label="计划日期">
          <el-date-picker v-model="milestoneForm.planDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实际日期">
          <el-date-picker v-model="milestoneForm.actualDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number v-model="milestoneForm.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="milestoneForm.status">
            <el-radio :value="0">待付款</el-radio>
            <el-radio :value="1">已付款</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="milestoneDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMilestone">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import request from '@/api/index'

const route = useRoute()
const projectId = route.params.id

const project = ref(null)
const supplierPersonnel = ref([])
const milestones = ref([])
const budgetInfo = ref({ totalBudget: 0, usedBudget: 0, pendingBudget: 0, usedPercent: 0 })
const risks = ref([])

const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null
let barChart = null

const milestoneDialogVisible = ref(false)
const isEditMilestone = ref(false)
const milestoneForm = reactive({ id: null, milestoneName: '', planDate: '', actualDate: '', amount: 0, status: 0 })

const fetchData = async () => {
  try {
    const res = await request.get(`/detail/project/${projectId}`)
    if (res.code === 200) {
      project.value = res.data.project
      supplierPersonnel.value = res.data.supplierPersonnel || []
      milestones.value = res.data.milestones || []
      budgetInfo.value = res.data.budgetInfo || {}
      risks.value = res.data.risks || []
      nextTick(() => {
        renderCharts()
      })
    }
  } catch (e) {
    console.error(e)
  }
}

const renderCharts = () => {
  renderPieChart()
  renderBarChart()
}

const renderPieChart = () => {
  if (!pieChartRef.value || supplierPersonnel.value.length === 0) return
  pieChart = echarts.init(pieChartRef.value)
  const data = supplierPersonnel.value.map(sp => ({
    name: sp.supplierName,
    value: sp.totalCount
  }))
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      labelLine: { show: false },
      data
    }]
  })
}

const renderBarChart = () => {
  if (!barChartRef.value) return
  barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['总预算', '已结算', '待结算'] },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      barWidth: '40%',
      data: [
        { value: budgetInfo.value.totalBudget, itemStyle: { color: '#409eff' } },
        { value: budgetInfo.value.usedBudget, itemStyle: { color: '#67c23a' } },
        { value: budgetInfo.value.pendingBudget, itemStyle: { color: '#e6a23c' } }
      ],
      label: { show: true, position: 'top', formatter: '¥{c}' }
    }]
  })
}

const openMilestoneDialog = () => {
  isEditMilestone.value = false
  Object.assign(milestoneForm, { id: null, milestoneName: '', planDate: '', actualDate: '', amount: 0, status: 0 })
  milestoneDialogVisible.value = true
}

const editMilestone = (row) => {
  isEditMilestone.value = true
  Object.assign(milestoneForm, row)
  milestoneDialogVisible.value = true
}

const saveMilestone = async () => {
  if (!milestoneForm.milestoneName) {
    ElMessage.warning('请输入里程碑名称')
    return
  }
  try {
    const res = isEditMilestone.value
      ? await request.put(`/detail/milestone/${milestoneForm.id}`, milestoneForm)
      : await request.post(`/detail/project/${projectId}/milestone`, milestoneForm)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      milestoneDialogVisible.value = false
      fetchData()
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.project-detail {
  padding: 0 10px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-card, .chart-card, .budget-card, .risk-card {
  margin-bottom: 20px;
}

.budget-info {
  display: flex;
  align-items: center;
  gap: 30px;
}

.budget-text {
  flex: 1;
}

.budget-text p {
  margin: 8px 0;
  color: #666;
}

.budget-text strong {
  color: #333;
}

.used {
  color: #67c23a;
  font-weight: bold;
}

.pending {
  color: #e6a23c;
  font-weight: bold;
}

.risk-item {
  margin-bottom: 10px;
}

.supplier-item {
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.supplier-item:last-child {
  border-bottom: none;
}

.supplier-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.supplier-name {
  font-weight: bold;
}
</style>
