<template>
  <div class="evaluations">
    <div class="page-header">
      <div>
        <h2>绩效考核</h2>
        <p class="sub-title">人员绩效评估管理</p>
      </div>
      <div>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>添加考核
        </el-button>
      </div>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="evaluationList" style="width: 100%" v-loading="loading">
        <el-table-column prop="personnelId" label="人员" width="120">
          <template #default="{ row }">{{ getPersonnelName(row.personnelId) }}</template>
        </el-table-column>
        <el-table-column prop="projectId" label="项目" width="150">
          <template #default="{ row }">{{ getProjectName(row.projectId) }}</template>
        </el-table-column>
        <el-table-column prop="evaluator" label="评估人" width="100" />
        <el-table-column prop="evaluationMonth" label="考核月份" width="100" />
        <el-table-column prop="score" label="综合得分" width="100">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.score)" size="small">{{ row.score?.toFixed(1) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workQuality" label="工作质量" width="100">
          <template #default="{ row }">{{ row.workQuality?.toFixed(1) }}</template>
        </el-table-column>
        <el-table-column prop="communication" label="沟通能力" width="100">
          <template #default="{ row }">{{ row.communication?.toFixed(1) }}</template>
        </el-table-column>
        <el-table-column prop="punctuality" label="守时性" width="100">
          <template #default="{ row }">{{ row.punctuality?.toFixed(1) }}</template>
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
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑考核' : '添加考核'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="人员" required>
              <el-select v-model="form.personnelId" placeholder="请选择人员" style="width: 100%" clearable filterable>
                <el-option v-for="p in personnelList" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目" required>
              <el-select v-model="form.projectId" placeholder="请选择项目" style="width: 100%" clearable filterable>
                <el-option v-for="p in projects" :key="p.id" :label="p.projectName" :value="p.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="评估人">
              <el-input v-model="form.evaluator" placeholder="请输入评估人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考核月份">
              <el-date-picker v-model="form.evaluationMonth" type="month" value-format="YYYY-MM" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工作质量">
              <el-rate v-model="form.workQuality" :max="5" allow-half show-score />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="沟通能力">
              <el-rate v-model="form.communication" :max="5" allow-half show-score />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="守时性">
              <el-rate v-model="form.punctuality" :max="5" allow-half show-score />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="综合得分">
              <el-input-number v-model="form.score" :min="0" :max="5" :precision="1" :step="0.1" style="width: 100%" />
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

const evaluationList = ref([])
const personnelList = ref([])
const projects = ref([])

const form = reactive({
  id: null,
  personnelId: null,
  projectId: null,
  evaluator: '',
  evaluationMonth: '',
  score: 0,
  workQuality: 0,
  communication: 0,
  punctuality: 0,
  remark: ''
})

const getScoreType = (score) => {
  if (score >= 4.5) return 'success'
  if (score >= 3.5) return 'primary'
  if (score >= 2.5) return 'warning'
  return 'danger'
}

const getPersonnelName = (personnelId) => {
  const personnel = personnelList.value.find(p => p.id === personnelId)
  return personnel ? personnel.name : '-'
}

const getProjectName = (projectId) => {
  const project = projects.value.find(p => p.id === projectId)
  return project ? project.projectName : '-'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/evaluations', { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    if (res.code === 200) {
      evaluationList.value = res.data.records
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchPersonnel = async () => {
  try {
    const res = await request.get('/personnel', { params: { pageNum: 1, pageSize: 100 } })
    if (res.code === 200) {
      personnelList.value = res.data.records
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchProjects = async () => {
  try {
    const res = await request.get('/projects', { params: { pageNum: 1, pageSize: 100 } })
    if (res.code === 200) {
      projects.value = res.data.records
    }
  } catch (e) {
    console.error(e)
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    personnelId: null,
    projectId: null,
    evaluator: '',
    evaluationMonth: '',
    score: 0,
    workQuality: 0,
    communication: 0,
    punctuality: 0,
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
  if (!form.personnelId || !form.projectId) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    let res
    if (isEdit.value) {
      res = await request.put(`/evaluations/${form.id}`, form)
    } else {
      res = await request.post('/evaluations', form)
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
    await ElMessageBox.confirm('确认删除该考核记录？', '提示', { type: 'warning' })
    const res = await request.delete(`/evaluations/${row.id}`)
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
  fetchPersonnel()
  fetchProjects()
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
