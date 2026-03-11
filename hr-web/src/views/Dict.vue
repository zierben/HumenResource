<template>
  <div class="dict">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>字典类型</span>
          </template>
          <el-menu :default-active="currentType" @select="handleTypeSelect">
            <el-menu-item v-for="item in typeList" :key="item.dictType" :index="item.dictType">
              <span>{{ item.dictName }}</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      
      <el-col :span="18">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>字典数据 - {{ currentTypeName }}</span>
              <el-button type="primary" size="small" @click="openDataDialog" :disabled="!currentType">新增</el-button>
            </div>
          </template>
          <el-table :data="dataList" v-loading="loading">
            <el-table-column prop="dictLabel" label="标签" width="150" />
            <el-table-column prop="dictValue" label="值" width="150" />
            <el-table-column prop="sortOrder" label="排序" width="80" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" link @click="editData(row)">编辑</el-button>
                <el-button type="danger" link @click="deleteData(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog v-model="dataDialogVisible" :title="isEditData ? '编辑字典数据' : '新增字典数据'" width="400px">
      <el-form :model="dataForm" label-width="80px">
        <el-form-item label="标签" required>
          <el-input v-model="dataForm.dictLabel" placeholder="显示名称" />
        </el-form-item>
        <el-form-item label="值" required>
          <el-input v-model="dataForm.dictValue" placeholder="存储值" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dataForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dataForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dataForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveData">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/index'

const loading = ref(false)
const dataDialogVisible = ref(false)
const isEditData = ref(false)
const currentType = ref('')
const currentTypeName = ref('')
const typeList = ref([])
const dataList = ref([])

const dataForm = reactive({ id: null, dictType: '', dictLabel: '', dictValue: '', sortOrder: 0, status: 1, remark: '' })

const fetchTypes = async () => {
  try {
    const res = await request.get('/dict/types')
    if (res.code === 200) {
      typeList.value = res.data
      if (typeList.value.length > 0 && !currentType.value) {
        handleTypeSelect(typeList.value[0].dictType)
      }
    }
  } catch (e) {
    console.error(e)
  }
}

const handleTypeSelect = (type) => {
  currentType.value = type
  const found = typeList.value.find(t => t.dictType === type)
  currentTypeName.value = found ? found.dictName : ''
  fetchData()
}

const fetchData = async () => {
  if (!currentType.value) return
  loading.value = true
  try {
    const res = await request.get(`/dict/data/${currentType.value}`)
    if (res.code === 200) {
      dataList.value = res.data
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openDataDialog = () => {
  isEditData.value = false
  Object.assign(dataForm, { id: null, dictType: currentType.value, dictLabel: '', dictValue: '', sortOrder: 0, status: 1, remark: '' })
  dataDialogVisible.value = true
}

const editData = (row) => {
  isEditData.value = true
  Object.assign(dataForm, row)
  dataDialogVisible.value = true
}

const saveData = async () => {
  if (!dataForm.dictLabel || !dataForm.dictValue) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    const res = isEditData.value
      ? await request.put(`/dict/data/${dataForm.id}`, dataForm)
      : await request.post('/dict/data', dataForm)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      dataDialogVisible.value = false
      fetchData()
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const deleteData = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除"${row.dictLabel}"？`, '提示', { type: 'warning' })
    const res = await request.delete(`/dict/data/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchTypes()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
