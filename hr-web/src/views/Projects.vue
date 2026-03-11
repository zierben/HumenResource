<template>
  <div class="projects">
    <div class="page-header">
      <div>
        <h2>项目管理</h2>
        <p class="sub-title">项目基础数据维护</p>
      </div>
      <div>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>添加项目
        </el-button>
      </div>
    </div>
    
    <el-card shadow="hover">
      <el-table :data="projectList" style="width: 100%" v-loading="loading">
        <el-table-column prop="projectCode" label="项目编号" width="130" />
        <el-table-column prop="projectName" label="项目名称" width="180">
          <template #default="{ row }">
            <el-link type="primary" @click="goDetail(row)">{{ row.projectName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="所属部门" width="120" />
        <el-table-column prop="manager" label="项目经理" width="100" />
        <el-table-column prop="partyA" label="甲方" width="120" />
        <el-table-column prop="partyB" label="乙方" width="120" />
        <el-table-column prop="budget" label="预算" width="120">
          <template #default="{ row }">¥{{ row.budget?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
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
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑项目' : '添加项目'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="项目编号" required>
          <el-input v-model="form.projectCode" placeholder="如：PRJ2024001" />
        </el-form-item>
        <el-form-item label="项目名称" required>
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-input v-model="form.department" placeholder="如：研发部" />
        </el-form-item>
        <el-form-item label="项目经理">
          <el-input v-model="form.manager" placeholder="请输入项目经理姓名" />
        </el-form-item>
        <el-form-item label="甲方">
          <el-select v-model="form.partyA" placeholder="请选择或输入甲方" style="width: 100%" filterable allow-create default-first-option>
            <el-option v-for="item in partyAOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="乙方">
          <el-select v-model="form.partyB" placeholder="请选择或输入乙方" style="width: 100%" filterable allow-create default-first-option>
            <el-option v-for="item in partyBOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目预算">
          <el-input-number v-model="form.budget" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="项目状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">进行中</el-radio>
            <el-radio :value="0">已结束</el-radio>
          </el-radio-group>
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
const pageSize = ref(10)
const total = ref(0)

const projectList = ref([])
const partyAOptions = ref([])
const partyBOptions = ref([])

const goDetail = (row) => {
  router.push(`/projects/${row.id}`)
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/projects', { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    if (res.code === 200) {
      projectList.value = res.data.records
      total.value = res.data.total
      const partyASet = new Set()
      const partyBSet = new Set()
      projectList.value.forEach(p => {
        if (p.partyA) partyASet.add(p.partyA)
        if (p.partyB) partyBSet.add(p.partyB)
      })
      partyAOptions.value = [...partyASet]
      partyBOptions.value = [...partyBSet]
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const form = reactive({
  id: null,
  projectCode: '',
  projectName: '',
  department: '',
  manager: '',
  partyA: '',
  partyB: '',
  budget: 0,
  status: 1
})

const resetForm = () => {
  Object.assign(form, {
    id: null,
    projectCode: '',
    projectName: '',
    department: '',
    manager: '',
    partyA: '',
    partyB: '',
    budget: 0,
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
  if (!form.projectCode || !form.projectName) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    let res
    if (isEdit.value) {
      res = await request.put(`/projects/${form.id}`, form)
    } else {
      res = await request.post('/projects', form)
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
    await ElMessageBox.confirm(`确认删除项目"${row.projectName}"？`, '提示', { type: 'warning' })
    const res = await request.delete(`/projects/${row.id}`)
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
