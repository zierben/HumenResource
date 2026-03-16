<template>
  <div class="personnel-detail">
    <div class="page-header">
      <el-button @click="$router.back()" circle>
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2>{{ personnel?.name || '人员详情' }}</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="100" style="background: #409eff; font-size: 36px">
              {{ personnel?.name?.charAt(0) }}
            </el-avatar>
            <h3>{{ personnel?.name }}</h3>
            <el-tag :type="statusType">{{ personnel?.status }}</el-tag>
            <div class="action-buttons" v-if="personnel?.status === '离场'">
              <el-button type="success" size="small" @click="handleReEntry">
                重新入场
              </el-button>
            </div>
          </div>
          <el-descriptions :column="1" border size="small" class="info-list">
            <el-descriptions-item label="工号">{{ personnel?.personnelCode }}</el-descriptions-item>
            <el-descriptions-item label="供应商">{{ supplierName }}</el-descriptions-item>
            <el-descriptions-item label="当前项目">{{ projectName }}</el-descriptions-item>
            <el-descriptions-item label="岗位">{{ personnel?.positionName }}</el-descriptions-item>
            <el-descriptions-item label="级别">{{ personnel?.positionLevel }}</el-descriptions-item>
            <el-descriptions-item label="日薪">¥{{ personnel?.dailyRate }}</el-descriptions-item>
            <el-descriptions-item label="入职日期">{{ personnel?.entryDate }}</el-descriptions-item>
            <el-descriptions-item label="合同起止">
              {{ personnel?.contractStartDate }} ~ {{ personnel?.contractEndDate }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" class="stats-card">
          <template #header>工作统计</template>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ statistics?.totalProjects || 0 }}</div>
              <div class="stat-label">参与项目</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ statistics?.totalWorkHours || 0 }}</div>
              <div class="stat-label">总工时</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ statistics?.avgScore || '-' }}</div>
              <div class="stat-label">平均评分</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card shadow="hover" class="section-card">
          <template #header>
            <div class="card-header">
              <span>资产清单</span>
              <el-button type="primary" size="small" @click="openAssetDialog">添加</el-button>
            </div>
          </template>
          <el-table :data="assets" size="small">
            <el-table-column prop="assetName" label="资产名称" />
            <el-table-column prop="assetCode" label="资产编号" width="120" />
            <el-table-column prop="receiveDate" label="领用日期" width="120" />
            <el-table-column prop="returnDate" label="归还日期" width="120" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                  {{ row.status === 1 ? '在用' : '已归还' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="editAsset(row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="deleteAsset(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card shadow="hover" class="section-card">
          <template #header>
            <div class="card-header">
              <span>工资变更记录</span>
              <el-button type="primary" size="small" @click="openSalaryDialog">添加</el-button>
            </div>
          </template>
          <div ref="salaryChartRef" style="height: 200px"></div>
          <el-timeline>
            <el-timeline-item v-for="s in salaryHistory" :key="s.effectiveDate" :timestamp="s.effectiveDate" placement="top">
              <el-card>
                <h4>¥{{ s.amount }} ({{ s.salaryType }})</h4>
                <p v-if="s.reason">{{ s.reason }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>

        <el-card shadow="hover" class="section-card">
          <template #header>项目经历</template>
          <el-empty description="暂无项目经历" v-if="!projectHistory.length" />
          <el-timeline v-else>
            <el-timeline-item v-for="p in projectHistory" :key="p.projectId" :timestamp="p.startDate" placement="top">
              <el-card>
                <h4>{{ p.projectName }}</h4>
                <p>{{ p.startDate }} ~ {{ p.endDate || '至今' }}</p>
                <p v-if="p.evaluation">评价: {{ p.evaluation }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>

        <el-card shadow="hover" class="section-card">
          <template #header>本月考勤</template>
          <div class="attendance-stats" v-if="attendanceData">
            <div class="stat-item normal">
              <span class="stat-value">{{ attendanceData.normalDays }}</span>
              <span class="stat-label">正常</span>
            </div>
            <div class="stat-item leave">
              <span class="stat-value">{{ attendanceData.leaveDays }}</span>
              <span class="stat-label">请假</span>
            </div>
            <div class="stat-item absent">
              <span class="stat-value">{{ attendanceData.absentDays }}</span>
              <span class="stat-label">旷工</span>
            </div>
          </div>
          <el-empty description="暂无考勤数据" v-if="!attendanceData" :image-size="60" />
          <el-collapse v-if="attendanceData && (attendanceData.leaveList?.length > 0 || attendanceData.absentList?.length > 0)">
            <el-collapse-item name="leave" v-if="attendanceData.leaveList?.length > 0">
              <template #title>
                <span class="collapse-title">请假记录 ({{ attendanceData.leaveList.length }})</span>
              </template>
              <div class="record-list">
                <div class="record-item" v-for="(item, idx) in attendanceData.leaveList" :key="'leave'+idx">
                  <span>{{ item.date }}</span>
                  <span class="remark">{{ item.remark || '请假' }}</span>
                </div>
              </div>
            </el-collapse-item>
            <el-collapse-item name="absent" v-if="attendanceData.absentList?.length > 0">
              <template #title>
                <span class="collapse-title">旷工记录 ({{ attendanceData.absentList.length }})</span>
              </template>
              <div class="record-list">
                <div class="record-item" v-for="(idx) in attendanceData.absentList" :key="'absent'+idx">
                  <span>{{ idx.date }}</span>
                  <span class="remark">{{ idx.remark || '旷工' }}</span>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="assetDialogVisible" :title="isEditAsset ? '编辑资产' : '添加资产'" width="400px">
      <el-form :model="assetForm" label-width="80px">
        <el-form-item label="资产名称" required>
          <el-select v-model="assetForm.assetName" placeholder="选择或输入" filterable allow-create style="width: 100%">
            <el-option label="笔记本电脑" value="笔记本电脑" />
            <el-option label="工卡" value="工卡" />
            <el-option label="工作服" value="工作服" />
            <el-option label="门禁卡" value="门禁卡" />
            <el-option label="显示器" value="显示器" />
            <el-option label="键盘鼠标" value="键盘鼠标" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产编号">
          <el-input v-model="assetForm.assetCode" />
        </el-form-item>
        <el-form-item label="领用日期">
          <el-date-picker v-model="assetForm.receiveDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="归还日期">
          <el-date-picker v-model="assetForm.returnDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="assetForm.status">
            <el-radio :value="1">在用</el-radio>
            <el-radio :value="2">已归还</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assetForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assetDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAsset">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="salaryDialogVisible" title="添加工资变更" width="400px">
      <el-form :model="salaryForm" label-width="80px">
        <el-form-item label="类型" required>
          <el-select v-model="salaryForm.salaryType" style="width: 100%">
            <el-option label="日薪" value="DAILY" />
            <el-option label="月薪" value="MONTHLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" required>
          <el-input-number v-model="salaryForm.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生效日期" required>
          <el-date-picker v-model="salaryForm.effectiveDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="变更原因">
          <el-select v-model="salaryForm.reason" placeholder="选择或输入" filterable allow-create style="width: 100%">
            <el-option label="入职定薪" value="入职定薪" />
            <el-option label="转正调薪" value="转正调薪" />
            <el-option label="年度调整" value="年度调整" />
            <el-option label="晋升调薪" value="晋升调薪" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="salaryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSalary">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import request from '@/api/index'

const route = useRoute()
const personnelId = route.params.id

const personnel = ref(null)
const supplierName = ref('')
const projectName = ref('')
const assets = ref([])
const salaryHistory = ref([])
const projectHistory = ref([])
const statistics = ref({ totalProjects: 0, totalWorkHours: 0, avgScore: null })
const attendanceData = ref(null)

const salaryChartRef = ref(null)
let salaryChart = null

const statusType = computed(() => {
  const s = personnel.value?.status
  if (s === '在岗') return 'success'
  if (s === '离场') return 'info'
  return 'warning'
})

const handleReEntry = async () => {
  try {
    await ElMessageBox.confirm('确定要让该人员重新入场吗？', '确认重新入场', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await request.post(`/personnel/${personnelId}/re-entry`)
    if (res.code === 200) {
      ElMessage.success('操作成功')
      fetchData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const assetDialogVisible = ref(false)
const isEditAsset = ref(false)
const assetForm = reactive({ id: null, assetName: '', assetCode: '', receiveDate: '', returnDate: '', status: 1, remark: '' })

const salaryDialogVisible = ref(false)
const salaryForm = reactive({ salaryType: 'DAILY', amount: 0, effectiveDate: '', reason: '' })

const fetchData = async () => {
  try {
    const res = await request.get(`/detail/personnel/${personnelId}`)
    if (res.code === 200) {
      personnel.value = res.data.personnel
      supplierName.value = res.data.supplierName
      projectName.value = res.data.projectName
      assets.value = res.data.assets || []
      salaryHistory.value = res.data.salaryHistory || []
      projectHistory.value = res.data.projectHistory || []
      statistics.value = res.data.statistics || {}
      nextTick(() => {
        renderSalaryChart()
      })
    }
    const attRes = await request.get(`/work-hours/personnel/${personnelId}/attendance`)
    if (attRes.code === 200) {
      attendanceData.value = attRes.data
    }
  } catch (e) {
    console.error(e)
  }
}

const renderSalaryChart = () => {
  if (!salaryChartRef.value || salaryHistory.value.length === 0) return
  salaryChart = echarts.init(salaryChartRef.value)
  const sorted = [...salaryHistory.value].sort((a, b) => a.effectiveDate.localeCompare(b.effectiveDate))
  salaryChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: sorted.map(s => s.effectiveDate) },
    yAxis: { type: 'value' },
    series: [{
      type: 'line',
      data: sorted.map(s => s.amount),
      smooth: true,
      itemStyle: { color: '#409eff' },
      areaStyle: { color: 'rgba(64,158,255,0.2)' }
    }]
  })
}

const openAssetDialog = () => {
  isEditAsset.value = false
  Object.assign(assetForm, { id: null, assetName: '', assetCode: '', receiveDate: '', returnDate: '', status: 1, remark: '' })
  assetDialogVisible.value = true
}

const editAsset = (row) => {
  isEditAsset.value = true
  Object.assign(assetForm, row)
  assetDialogVisible.value = true
}

const saveAsset = async () => {
  if (!assetForm.assetName) {
    ElMessage.warning('请输入资产名称')
    return
  }
  try {
    const res = isEditAsset.value
      ? await request.put(`/detail/asset/${assetForm.id}`, assetForm)
      : await request.post(`/detail/personnel/${personnelId}/asset`, assetForm)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      assetDialogVisible.value = false
      fetchData()
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const deleteAsset = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该资产记录？', '提示', { type: 'warning' })
    const res = await request.delete(`/detail/asset/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const openSalaryDialog = () => {
  Object.assign(salaryForm, { salaryType: 'DAILY', amount: 0, effectiveDate: '', reason: '' })
  salaryDialogVisible.value = true
}

const saveSalary = async () => {
  if (!salaryForm.amount || !salaryForm.effectiveDate) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    const res = await request.post(`/detail/personnel/${personnelId}/salary`, salaryForm)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      salaryDialogVisible.value = false
      fetchData()
    }
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.personnel-detail {
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

.profile-card {
  margin-bottom: 20px;
}

.avatar-section {
  text-align: center;
  padding: 20px 0;
}

.avatar-section h3 {
  margin: 15px 0 10px;
}

.action-buttons {
  margin-top: 15px;
}

.info-list {
  margin-top: 20px;
}

.stats-card {
  margin-bottom: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  text-align: center;
}

.stat-item {
  padding: 10px 0;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.section-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.attendance-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 16px;
}

.attendance-stats .stat-item {
  text-align: center;
  padding: 12px 16px;
  border-radius: 8px;
  min-width: 70px;
}

.attendance-stats .stat-item.normal {
  background: #f0f9eb;
}

.attendance-stats .stat-item.leave {
  background: #fef0f0;
}

.attendance-stats .stat-item.absent {
  background: #fdf6ec;
}

.attendance-stats .stat-value {
  display: block;
  font-size: 24px;
  font-weight: 600;
}

.attendance-stats .stat-item.normal .stat-value {
  color: #67c23a;
}

.attendance-stats .stat-item.leave .stat-value {
  color: #f56c6c;
}

.attendance-stats .stat-item.absent .stat-value {
  color: #e6a23c;
}

.attendance-stats .stat-label {
  font-size: 12px;
  color: #909399;
}

.collapse-title {
  font-size: 14px;
  color: #606266;
}

.record-list {
  padding: 8px 0;
}

.record-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
  font-size: 13px;
}

.record-item:last-child {
  border-bottom: none;
}

.record-item .remark {
  color: #909399;
}
</style>
