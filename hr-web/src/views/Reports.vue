<template>
  <div class="reports">
    <div class="page-header">
      <div>
        <h2>报表中心</h2>
        <p class="sub-title">外包人力数据分析与报表</p>
      </div>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ summary.onDutyPersonnel || 0 }}</div>
          <div class="stat-label">在岗人员</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ summary.activeProjects || 0 }}</div>
          <div class="stat-label">进行中项目</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ summary.totalSuppliers || 0 }}</div>
          <div class="stat-label">合作供应商</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">¥{{ formatAmount(summary.totalCost) }}</div>
          <div class="stat-label">累计结算</div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>项目外包成本分布</span>
          </template>
          <div ref="costChartRef" style="height: 300px" v-loading="costLoading"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>供应商人员占比</span>
          </template>
          <div ref="supplierChartRef" style="height: 300px" v-loading="supplierLoading"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span>月度外包费用趋势</span>
          </template>
          <div ref="trendChartRef" style="height: 300px" v-loading="trendLoading"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import request from '@/api/index'

const router = useRouter()
const costChartRef = ref(null)
const supplierChartRef = ref(null)
const trendChartRef = ref(null)
const costLoading = ref(false)
const supplierLoading = ref(false)
const trendLoading = ref(false)

const summary = ref({})

const formatAmount = (amount) => {
  if (!amount) return '0'
  return amount.toLocaleString()
}

const fetchSummary = async () => {
  try {
    const res = await request.get('/reports/summary')
    if (res.code === 200) {
      summary.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
}

const initCostChart = async () => {
  costLoading.value = true
  try {
    const res = await request.get('/reports/cost-by-project')
    if (res.code === 200) {
      const chart = echarts.init(costChartRef.value)
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: ¥{c}' },
        legend: { bottom: '0%' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: res.data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
      chart.on('click', (params) => {
        if (params.data && params.data.projectId) {
          router.push(`/projects/${params.data.projectId}`)
        }
      })
    }
  } catch (e) {
    console.error(e)
  } finally {
    costLoading.value = false
  }
}

const initSupplierChart = async () => {
  supplierLoading.value = true
  try {
    const res = await request.get('/reports/personnel-by-supplier')
    if (res.code === 200) {
      const chart = echarts.init(supplierChartRef.value)
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}人' },
        legend: { bottom: '0%' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: res.data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
      chart.on('click', (params) => {
        if (params.data && params.data.supplierId) {
          router.push(`/suppliers/${params.data.supplierId}`)
        }
      })
    }
  } catch (e) {
    console.error(e)
  } finally {
    supplierLoading.value = false
  }
}

const initTrendChart = async () => {
  trendLoading.value = true
  try {
    const res = await request.get('/reports/monthly-trend')
    if (res.code === 200) {
      const chart = echarts.init(trendChartRef.value)
      chart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['预算', '实际费用'] },
        xAxis: { type: 'category', data: res.data.months },
        yAxis: { type: 'value', name: '万元' },
        series: [
          { name: '预算', type: 'line', data: res.data.budget },
          { name: '实际费用', type: 'line', data: res.data.actual }
        ]
      })
    }
  } catch (e) {
    console.error(e)
  } finally {
    trendLoading.value = false
  }
}

onMounted(() => {
  fetchSummary()
  initCostChart()
  initSupplierChart()
  initTrendChart()
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

.stat-card {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  color: #909399;
  margin-top: 8px;
}
</style>
