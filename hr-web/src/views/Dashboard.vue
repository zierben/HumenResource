<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">外包人员总数</div>
              <div class="stat-value">156</div>
              <div class="stat-trend">
                <span class="trend-up">↑ 12%</span>
                <span class="trend-text">较上月</span>
              </div>
            </div>
            <div class="stat-icon" style="background: #e0f2fe">
              <el-icon :size="30" color="#0ea5e9"><User /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">本月预算执行率</div>
              <div class="stat-value">78%</div>
              <div class="stat-trend">
                <span class="trend-up">↑ 5%</span>
                <span class="trend-text">较上月</span>
              </div>
            </div>
            <div class="stat-icon" style="background: #dcfce7">
              <el-icon :size="30" color="#22c55e"><TrendCharts /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">待审批需求</div>
              <div class="stat-value">12</div>
              <div class="stat-trend">
                <span class="trend-down">↑ 3</span>
                <span class="trend-text">较昨日</span>
              </div>
            </div>
            <div class="stat-icon" style="background: #fef3c7">
              <el-icon :size="30" color="#f59e0b"><Document /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">本月结算金额</div>
              <div class="stat-value">¥1.28M</div>
              <div class="stat-trend">
                <span class="trend-up">↑ 8%</span>
                <span class="trend-text">较上月</span>
              </div>
            </div>
            <div class="stat-icon" style="background: #f3e8ff">
              <el-icon :size="30" color="#a855f7"><Money /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>外包人员分布</span>
            </div>
          </template>
          <div ref="personnelChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>预算执行趋势</span>
            </div>
          </template>
          <div ref="budgetChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>待处理任务</span>
          <el-button type="primary" link>查看全部</el-button>
        </div>
      </template>
      <el-table :data="pendingTasks" style="width: 100%">
        <el-table-column prop="title" label="任务名称" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeColor(row.type)">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default>
            <el-button type="primary" link>查看</el-button>
            <el-button type="success" link>审批</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const personnelChartRef = ref(null)
const budgetChartRef = ref(null)

const pendingTasks = ref([
  { title: '研发部前端工程师需求审批', type: '需求审批', deadline: '2023-07-25', status: '待审批' },
  { title: '李工入场资料审核', type: '入场审核', deadline: '2023-07-24', status: '待审核' },
  { title: '7月供应商账单确认', type: '结算确认', deadline: '2023-07-26', status: '待确认' },
  { title: '王工离职交接确认', type: '离场确认', deadline: '2023-07-23', status: '待确认' }
])

const getTypeColor = (type) => {
  const map = { '需求审批': 'primary', '入场审核': 'warning', '结算确认': 'success', '离场确认': 'info' }
  return map[type] || ''
}

const getStatusColor = (status) => {
  return status === '待审批' || status === '待审核' ? 'warning' : 'success'
}

onMounted(() => {
  const personnelChart = echarts.init(personnelChartRef.value)
  personnelChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 45, name: '研发部' },
        { value: 30, name: '测试部' },
        { value: 25, name: '设计部' },
        { value: 20, name: '运维部' },
        { value: 15, name: '产品部' }
      ]
    }]
  })
  
  const budgetChart = echarts.init(budgetChartRef.value)
  budgetChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
    },
    yAxis: { type: 'value', name: '万元' },
    series: [
      {
        name: '预算',
        type: 'bar',
        data: [120, 135, 128, 142, 155, 148, 160],
        itemStyle: { color: '#3b82f6' }
      },
      {
        name: '实际',
        type: 'bar',
        data: [110, 125, 118, 135, 145, 138, 150],
        itemStyle: { color: '#22c55e' }
      }
    ]
  })
})
</script>

<style scoped>
.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #1f2937;
}

.stat-trend {
  margin-top: 8px;
  font-size: 12px;
}

.trend-up {
  color: #22c55e;
  margin-right: 4px;
}

.trend-down {
  color: #ef4444;
  margin-right: 4px;
}

.trend-text {
  color: #9ca3af;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
