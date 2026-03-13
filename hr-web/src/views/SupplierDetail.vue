<template>
  <div class="supplier-detail" v-loading="loading">
    <div class="page-header">
      <div>
        <el-button link @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>返回
        </el-button>
        <h2>{{ detail.supplierName }}</h2>
        <el-tag :type="detail.level === 'A' ? 'success' : detail.level === 'B' ? 'warning' : 'danger'" size="large">
          {{ detail.level }}级供应商
        </el-tag>
      </div>
      <div>
        <el-button type="primary" @click="showScoreDialog = true">
          <el-icon><Star /></el-icon>添加评分
        </el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">¥{{ formatAmount(detail.totalContractAmount) }}</div>
          <div class="stat-label">累计合同金额</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">¥{{ formatAmount(detail.totalSettledAmount) }}</div>
          <div class="stat-label">已结算金额</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ detail.activePersonnel || 0 }}</div>
          <div class="stat-label">在岗人员</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ detail.turnoverRate || 0 }}%</div>
          <div class="stat-label">人员离职率</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>人员素质评分</span>
              <el-tag v-if="avgScores.overallScore" type="success">
                综合: {{ avgScores.overallScore || 0 }}分
              </el-tag>
            </div>
          </template>
          <div class="score-list">
            <div class="score-item">
              <span>技术能力</span>
              <el-progress :percentage="avgScores.techAbility || 0" :stroke-width="12" />
            </div>
            <div class="score-item">
              <span>交付质量</span>
              <el-progress :percentage="avgScores.deliveryQuality || 0" :stroke-width="12" />
            </div>
            <div class="score-item">
              <span>响应速度</span>
              <el-progress :percentage="avgScores.responseSpeed || 0" :stroke-width="12" />
            </div>
            <div class="score-item">
              <span>沟通配合</span>
              <el-progress :percentage="avgScores.communication || 0" :stroke-width="12" />
            </div>
            <div class="score-item">
              <span>人员稳定性</span>
              <el-progress :percentage="avgScores.stability || 0" :stroke-width="12" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>评分趋势</template>
          <div ref="scoreChartRef" style="height: 250px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>服务项目</span>
              <el-tag>{{ (detail.projects || []).length }}个</el-tag>
            </div>
          </template>
          <el-table :data="detail.projects || []" size="small" @row-click="goToProject">
            <el-table-column prop="projectName" label="项目名称">
              <template #default="{ row }">
                <el-link type="primary">{{ row.projectName }}</el-link>
              </template>
            </el-table-column>
            <el-table-column prop="projectCode" label="项目编码" width="120" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                  {{ row.status === 1 ? '进行中' : '已完成' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>合同记录</span>
              <el-tag>{{ (detail.contracts || []).length }}份</el-tag>
            </div>
          </template>
          <el-table :data="detail.contracts || []" size="small">
            <el-table-column prop="contractName" label="合同名称" />
            <el-table-column prop="amount" label="金额" width="100">
              <template #default="{ row }">¥{{ formatAmount(row.amount) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">
                  {{ row.status === 1 ? '有效' : '过期' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>在岗人员</span>
              <el-tag>{{ (detail.personnelList || []).length }}人</el-tag>
            </div>
          </template>
          <el-table :data="detail.personnelList || []" size="small">
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column prop="positionName" label="岗位" />
            <el-table-column prop="entryDate" label="入场日期" width="110" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '在岗' : '离场' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>结算记录</span>
              <el-tag>{{ (detail.settlements || []).length }}笔</el-tag>
            </div>
          </template>
          <el-table :data="detail.settlements || []" size="small">
            <el-table-column label="结算月份" width="100">
              <template #default="{ row }">{{ row.year }}-{{ String(row.month).padStart(2, '0') }}</template>
            </el-table-column>
            <el-table-column prop="finalAmount" label="结算金额">
              <template #default="{ row }">¥{{ formatAmount(row.finalAmount) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 2 ? 'success' : 'warning'" size="small">
                  {{ row.status === 2 ? '已付款' : '待审批' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showScoreDialog" title="添加评分" width="500px">
      <el-form :model="scoreForm" label-width="100px">
        <el-form-item label="评分日期">
          <el-date-picker v-model="scoreForm.scoreDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="技术能力">
          <el-slider v-model="scoreForm.techAbility" :max="100" show-input />
        </el-form-item>
        <el-form-item label="交付质量">
          <el-slider v-model="scoreForm.deliveryQuality" :max="100" show-input />
        </el-form-item>
        <el-form-item label="响应速度">
          <el-slider v-model="scoreForm.responseSpeed" :max="100" show-input />
        </el-form-item>
        <el-form-item label="沟通配合">
          <el-slider v-model="scoreForm.communication" :max="100" show-input />
        </el-form-item>
        <el-form-item label="人员稳定性">
          <el-slider v-model="scoreForm.stability" :max="100" show-input />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="scoreForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showScoreDialog = false">取消</el-button>
        <el-button type="primary" @click="submitScore">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import request from '@/api/index'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const showScoreDialog = ref(false)
const scoreChartRef = ref(null)

const detail = ref({})
const avgScores = computed(() => detail.value.avgScores || {})

const scoreForm = reactive({
  scoreDate: new Date(),
  techAbility: 80,
  deliveryQuality: 80,
  responseSpeed: 80,
  communication: 80,
  stability: 80,
  remark: ''
})

const formatAmount = (amount) => {
  if (!amount) return '0'
  return Number(amount).toLocaleString()
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await request.get(`/suppliers/${route.params.id}/detail`)
    if (res.code === 200) {
      detail.value = res.data
      initScoreChart()
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const initScoreChart = () => {
  const chart = echarts.init(scoreChartRef.value)
  const monthlyScores = detail.value.monthlyScores || []
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { 
      type: 'category', 
      data: monthlyScores.map(s => s.month).reverse(),
      axisLabel: { rotate: 45 }
    },
    yAxis: { type: 'value', min: 0, max: 100 },
    series: [{
      type: 'line',
      data: monthlyScores.map(s => s.score).reverse(),
      smooth: true,
      itemStyle: { color: '#409eff' },
      areaStyle: { color: 'rgba(64, 158, 255, 0.2)' }
    }]
  })
}

const submitScore = async () => {
  try {
    const res = await request.post(`/suppliers/${route.params.id}/scores`, scoreForm)
    if (res.code === 200) {
      ElMessage.success('评分成功')
      showScoreDialog.value = false
      fetchDetail()
    }
  } catch (e) {
    ElMessage.error('评分失败')
  }
}

const goToProject = (row) => {
  router.push(`/projects/${row.id}`)
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.supplier-detail {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  display: inline-block;
  margin: 0 15px;
  font-size: 20px;
}

.stat-card {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  color: #909399;
  margin-top: 8px;
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.score-list {
  padding: 10px 0;
}

.score-item {
  margin-bottom: 15px;
}

.score-item span {
  display: block;
  margin-bottom: 5px;
  color: #606266;
  font-size: 14px;
}
</style>
