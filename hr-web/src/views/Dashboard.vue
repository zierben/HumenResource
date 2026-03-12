<template>
  <div class="dashboard">
    <el-carousel height="200px" :interval="5000" arrow="hover" indicator-position="outside">
      <el-carousel-item v-for="(banner, index) in banners" :key="index">
        <div class="banner-item" :style="{ background: banner.gradient }">
          <div class="banner-content">
            <h2>{{ banner.title }}</h2>
            <p>{{ banner.desc }}</p>
            <el-button type="primary" round size="small" @click="handleBannerAction(banner.action)">
              {{ banner.btnText }}
            </el-button>
          </div>
          <div class="banner-illustration">
            <component :is="banner.icon" :size="100" style="opacity: 0.3" />
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="stats-section">
      <div class="stats-grid">
        <div v-for="(stat, index) in stats" :key="index" 
             class="stat-card" 
             :style="{ animationDelay: index * 0.1 + 's' }"
             @click="handleStatClick(stat)">
          <div class="stat-icon" :style="{ background: stat.bgColor }">
            <component :is="stat.icon" :size="28" :style="{ color: stat.color }" />
          </div>
          <div class="stat-info">
            <div class="stat-value">
              <span class="value">{{ stat.value }}</span>
              <span class="unit">{{ stat.unit }}</span>
            </div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <div class="stat-trend" :class="stat.trend > 0 ? 'up' : stat.trend < 0 ? 'down' : ''" v-if="stat.trend !== 0">
            <el-icon><component :is="stat.trend > 0 ? 'Top' : 'Bottom'" /></el-icon>
            <span>{{ Math.abs(stat.trend) }}%</span>
          </div>
        </div>
      </div>
    </div>

    <div class="quick-actions">
      <div class="section-title">
        <span>快捷入口</span>
      </div>
      <div class="actions-grid">
        <div v-for="action in quickActions" :key="action.path" 
             class="action-item"
             @click="router.push(action.path)">
          <div class="action-icon" :style="{ background: action.bgColor }">
            <component :is="action.icon" :size="24" :style="{ color: action.color }" />
          </div>
          <span class="action-label">{{ action.label }}</span>
          <span class="action-count" v-if="action.count">{{ action.count }}</span>
        </div>
      </div>
    </div>

    <el-row :gutter="20" class="charts-section">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>费用趋势</span>
              <div class="header-actions">
                <el-radio-group v-model="chartPeriod" size="small">
                  <el-radio-button label="month">月</el-radio-button>
                  <el-radio-button label="year">年</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div ref="budgetChartRef" style="height: 320px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>人员分布</span>
            </div>
          </template>
          <div ref="pieChartRef" style="height: 320px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" class="todo-section">
      <template #header>
        <div class="card-header">
          <span>
            <el-badge :value="pendingTasks.length" class="todo-badge">
              待处理任务
            </el-badge>
          </span>
          <el-button type="primary" link @click="router.push('/todo')">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <div class="task-list" v-if="pendingTasks.length > 0">
        <div v-for="task in pendingTasks" :key="task.id" class="task-item" @click="handleTask(task)">
          <div class="task-priority" :class="task.priority"></div>
          <div class="task-content">
            <div class="task-title">{{ task.title }}</div>
            <div class="task-meta">
              <el-tag :type="getTaskType(task.type)" size="small">{{ task.type }}</el-tag>
              <span class="task-deadline">
                <el-icon><Clock /></el-icon>
                {{ task.deadline }}
              </span>
            </div>
          </div>
          <div class="task-actions">
            <el-button type="primary" size="small" round>处理</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无待处理任务" :image-size="80" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import request from '@/api/index'

const router = useRouter()
const budgetChartRef = ref(null)
const pieChartRef = ref(null)
const chartPeriod = ref('month')
const budgetChart = shallowRef(null)
const pieChart = shallowRef(null)

const summary = ref({
  onDutyPersonnel: 0,
  totalSuppliers: 0,
  activeProjects: 0,
  totalCost: 0,
  personnelTrend: 0,
  costTrend: 0,
  pendingRequirements: 0,
  budgetExecutionRate: 0
})

const banners = ref([
  {
    title: '欢迎使用HR外包管理系统',
    desc: '一站式外包人力管理解决方案，提升管理效率',
    btnText: '开始使用',
    action: 'guide',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    icon: 'Odometer'
  }
])

const stats = ref([
  { label: '在岗人员', value: 0, unit: '人', trend: 0, icon: 'User', color: '#0ea5e9', bgColor: '#e0f2fe' },
  { label: '合作供应商', value: 0, unit: '家', trend: 0, icon: 'TrendCharts', color: '#22c55e', bgColor: '#dcfce7' },
  { label: '进行中项目', value: 0, unit: '个', trend: 0, icon: 'Document', color: '#f59e0b', bgColor: '#fef3c7' },
  { label: '累计结算', value: 0, unit: '万', trend: 0, icon: 'Money', color: '#a855f7', bgColor: '#f3e8ff' }
])

const quickActions = ref([
  { label: '需求申请', icon: 'Document', color: '#3b82f6', bgColor: '#eff6ff', path: '/requirements', count: null },
  { label: '人员入场', icon: 'UserFilled', color: '#22c55e', bgColor: '#f0fdf4', path: '/personnel', count: null },
  { label: '工时填报', icon: 'Clock', color: '#f59e0b', bgColor: '#fffbeb', path: '/workhours', count: null },
  { label: '结算确认', icon: 'Money', color: '#a855f7', bgColor: '#faf5ff', path: '/settlements', count: 0 },
  { label: '合同管理', icon: 'Tickets', color: '#ec4899', bgColor: '#fdf2f8', path: '/contracts', count: null },
  { label: '绩效考核', icon: 'Star', color: '#14b8a6', bgColor: '#f0fdfa', path: '/evaluations', count: null },
  { label: '消息通知', icon: 'Bell', color: '#f97316', bgColor: '#fff7ed', path: '/messages', count: 0 },
  { label: '系统设置', icon: 'Setting', color: '#6b7280', bgColor: '#f9fafb', path: '/settings', count: null }
])

const pendingTasks = ref([])

const handleBannerAction = (action) => {
  const actionMap = {
    guide: () => router.push('/dashboard'),
    budget: () => router.push('/settlements'),
    todo: () => router.push('/todo'),
    security: () => router.push('/settings')
  }
  actionMap[action]?.()
}

const handleStatClick = (stat) => {
  const pathMap = {
    '在岗人员': '/personnel',
    '合作供应商': '/suppliers',
    '进行中项目': '/projects',
    '累计结算': '/settlements'
  }
  router.push(pathMap[stat.label] || '/dashboard')
}

const handleTask = (task) => {
  router.push('/todo')
}

const getTaskType = (type) => {
  const map = { '需求审批': 'primary', '入场审核': 'warning', '结算确认': 'success', '离场确认': 'info' }
  return map[type] || ''
}

const updateBanners = () => {
  const dynamicBanners = []
  
  if (summary.value.pendingRequirements > 0) {
    dynamicBanners.push({
      title: `${summary.value.pendingRequirements}个需求待审批`,
      desc: '您有需求申请等待审批处理',
      btnText: '立即处理',
      action: 'todo',
      gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      icon: 'Document'
    })
  }
  
  if (summary.value.budgetExecutionRate > 0) {
    dynamicBanners.push({
      title: `本月预算执行率 ${summary.value.budgetExecutionRate}%`,
      desc: summary.value.costTrend > 0 ? `费用较上月增长${Math.abs(summary.value.costTrend)}%` : '费用控制良好',
      btnText: '查看详情',
      action: 'budget',
      gradient: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
      icon: 'TrendCharts'
    })
  }
  
  dynamicBanners.push({
    title: '系统安全升级通知',
    desc: '支持双因子认证，保障账户安全',
    btnText: '立即设置',
    action: 'security',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    icon: 'Lock'
  })
  
  banners.value = [banners.value[0], ...dynamicBanners]
}

const fetchSummary = async () => {
  try {
    const res = await request.get('/reports/summary')
    if (res.code === 200) {
      const data = res.data
      summary.value = data
      
      stats.value[0].value = data.onDutyPersonnel || 0
      stats.value[0].trend = data.personnelTrend || 0
      stats.value[1].value = data.totalSuppliers || 0
      stats.value[2].value = data.activeProjects || 0
      stats.value[3].value = data.totalCost ? (data.totalCost / 10000).toFixed(1) : 0
      stats.value[3].trend = data.costTrend || 0
      
      updateBanners()
    }
  } catch (e) {
    console.error('获取汇总数据失败', e)
  }
}

const fetchMonthlyTrend = async () => {
  try {
    const year = new Date().getFullYear()
    const res = await request.get('/reports/monthly-trend', { params: { year } })
    if (res.code === 200) {
      const data = res.data
      updateBudgetChart(data.months, data.budget, data.actual)
    }
  } catch (e) {
    console.error('获取月度趋势失败', e)
  }
}

const fetchPersonnelBySupplier = async () => {
  try {
    const res = await request.get('/reports/personnel-by-supplier')
    if (res.code === 200) {
      updatePieChart(res.data)
    }
  } catch (e) {
    console.error('获取人员分布失败', e)
  }
}

const fetchPendingTasks = async () => {
  try {
    const res = await request.get('/workflow/instances/pending', { params: { pageNum: 1, pageSize: 5 } })
    if (res.code === 200) {
      const typeMap = { 'REQUIREMENT': '需求审批', 'ENTRY': '入场审核', 'SETTLEMENT': '结算确认', 'EXIT': '离场确认' }
      pendingTasks.value = (res.data.records || []).map(item => ({
        id: item.id,
        title: item.title,
        type: typeMap[item.businessType] || '其他',
        deadline: item.createTime ? item.createTime.substring(0, 10) : '',
        priority: 'medium',
        businessType: item.businessType,
        businessId: item.businessId
      }))
      const settlementAction = quickActions.value.find(a => a.path === '/settlements')
      if (settlementAction) settlementAction.count = pendingTasks.value.filter(t => t.businessType === 'SETTLEMENT').length
    }
  } catch (e) {
    console.error('获取待办任务失败', e)
  }
}

const initCharts = () => {
  budgetChart.value = echarts.init(budgetChartRef.value)
  pieChart.value = echarts.init(pieChartRef.value)
  
  budgetChart.value.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '12%', top: '10%', containLabel: true },
    xAxis: { type: 'category', data: [] },
    yAxis: { type: 'value', name: '万元' },
    series: [
      {
        name: '预算',
        type: 'bar',
        barWidth: '35%',
        data: [],
        itemStyle: { 
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      },
      {
        name: '实际',
        type: 'bar',
        barWidth: '35%',
        data: [],
        itemStyle: { 
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#22c55e' },
            { offset: 1, color: '#16a34a' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  })

  pieChart.value.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { bottom: 0, itemWidth: 10, itemHeight: 10 },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: []
    }]
  })

  window.addEventListener('resize', () => {
    budgetChart.value?.resize()
    pieChart.value?.resize()
  })
}

const colors = ['#667eea', '#22c55e', '#f59e0b', '#a855f7', '#ec4899', '#14b8a6', '#f97316', '#0ea5e9']

const updateBudgetChart = (months, budget, actual) => {
  if (budgetChart.value) {
    budgetChart.value.setOption({
      xAxis: { data: months },
      series: [
        { data: budget },
        { data: actual }
      ]
    })
  }
}

const updatePieChart = (data) => {
  if (pieChart.value) {
    const chartData = data.filter(d => d.value > 0).map((item, index) => ({
      value: item.value,
      name: item.name,
      itemStyle: { color: colors[index % colors.length] }
    }))
    pieChart.value.setOption({
      series: [{ data: chartData }]
    })
  }
}

watch(chartPeriod, async () => {
  await fetchMonthlyTrend()
})

onMounted(() => {
  initCharts()
  fetchSummary()
  fetchMonthlyTrend()
  fetchPersonnelBySupplier()
  fetchPendingTasks()
})
</script>

<style scoped>
.dashboard {
  padding-bottom: 20px;
}

.banner-item {
  height: 200px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 60px;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
}

.banner-content {
  color: white;
  z-index: 1;
}

.banner-content h2 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
}

.banner-content p {
  margin: 0 0 20px;
  opacity: 0.9;
  font-size: 14px;
}

.banner-illustration {
  opacity: 0.2;
  color: white;
}

:deep(.el-carousel__indicators--outside) {
  margin-top: 12px;
}

:deep(.el-carousel__indicator--horizontal .el-carousel__button) {
  width: 24px;
  height: 6px;
  border-radius: 3px;
}

.stats-section {
  margin-top: 20px;
  margin-bottom: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info { flex: 1; }

.stat-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.stat-value .value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
}

.stat-value .unit {
  font-size: 14px;
  color: #6b7280;
}

.stat-label {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 12px;
}

.stat-trend.up {
  color: #22c55e;
  background: #dcfce7;
}

.stat-trend.down {
  color: #ef4444;
  background: #fee2e2;
}

.quick-actions {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 600;
  color: #1f2937;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 8px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.action-item:hover {
  background: #f9fafb;
  transform: translateY(-2px);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
}

.action-item:hover .action-icon {
  transform: scale(1.1);
}

.action-label {
  font-size: 12px;
  color: #4b5563;
}

.action-count {
  position: absolute;
  top: 8px;
  right: 8px;
  min-width: 18px;
  height: 18px;
  background: #ef4444;
  color: white;
  font-size: 10px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
}

.charts-section {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.todo-section {
  border-radius: 12px;
}

.todo-badge :deep(.el-badge__content) {
  top: -2px;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.task-item:hover {
  background: #f3f4f6;
  transform: translateX(4px);
}

.task-priority {
  width: 4px;
  height: 40px;
  border-radius: 2px;
  flex-shrink: 0;
}

.task-priority.high { background: #ef4444; }
.task-priority.medium { background: #f59e0b; }
.task-priority.low { background: #22c55e; }

.task-content { flex: 1; }

.task-title {
  font-size: 14px;
  color: #1f2937;
  margin-bottom: 6px;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.task-deadline {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #9ca3af;
}

.task-actions { flex-shrink: 0; }

@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .actions-grid { grid-template-columns: repeat(4, 1fr); }
}

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: 1fr; }
  .actions-grid { grid-template-columns: repeat(4, 1fr); }
  .banner-item { padding: 0 20px; }
  .banner-content h2 { font-size: 18px; }
}
</style>
