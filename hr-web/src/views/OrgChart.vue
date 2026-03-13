<template>
  <div class="organization-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover" class="tree-card">
          <template #header>
            <div class="card-header">
              <span>
                <el-icon><OfficeBuilding /></el-icon>
                组织架构
              </span>
              <el-button type="primary" size="small" @click="handleAddRoot">添加</el-button>
            </div>
          </template>
          <div class="tree-container" v-loading="loading">
            <div v-if="departmentTree.length === 0" class="empty-tip">
              <el-empty description="暂无组织架构" :image-size="80" />
            </div>
            <div v-else>
              <div v-for="dept in departmentTree" :key="dept.id" class="dept-node">
                <div class="dept-item level-1" :class="{ active: selectedDept?.id === dept.id }" @click="selectDept(dept)">
                  <div class="dept-info">
                    <el-icon class="dept-icon" style="color: #f56c6c"><User /></el-icon>
                    <span class="dept-name">{{ dept.deptName }}</span>
                    <el-tag v-if="dept.managerName" size="small" type="success" class="manager-tag">
                      {{ dept.managerName }}
                    </el-tag>
                  </div>
                  <div class="dept-actions">
                    <el-button text size="small" @click.stop="handleAdd(dept)" v-if="dept.level < 3">
                      <el-icon><Plus /></el-icon>
                    </el-button>
                    <el-button text size="small" @click.stop="handleEdit(dept)">
                      <el-icon><Edit /></el-icon>
                    </el-button>
                  </div>
                </div>
                <div v-if="dept.children && dept.children.length" class="dept-children">
                  <div v-for="child in dept.children" :key="child.id">
                    <div class="dept-item level-2" :class="{ active: selectedDept?.id === child.id }" @click="selectDept(child)">
                      <div class="dept-info">
                        <el-icon class="dept-icon" style="color: #e6a23c"><UserFilled /></el-icon>
                        <span class="dept-name">{{ child.deptName }}</span>
                        <el-tag v-if="child.managerName" size="small" type="success" class="manager-tag">
                          {{ child.managerName }}
                        </el-tag>
                      </div>
                      <div class="dept-count">{{ child.memberCount || 0 }}人</div>
                      <div class="dept-actions">
                        <el-button text size="small" @click.stop="handleAdd(child)" v-if="child.level < 3">
                          <el-icon><Plus /></el-icon>
                        </el-button>
                        <el-button text size="small" @click.stop="handleEdit(child)">
                          <el-icon><Edit /></el-icon>
                        </el-button>
                        <el-button text size="small" type="danger" @click.stop="handleDelete(child)">
                          <el-icon><Delete /></el-icon>
                        </el-button>
                      </div>
                    </div>
                    <div v-if="child.children && child.children.length" class="dept-children level-3">
                      <div v-for="grandChild in child.children" :key="grandChild.id"
                           class="dept-item level-3" :class="{ active: selectedDept?.id === grandChild.id }"
                           @click="selectDept(grandChild)">
                        <div class="dept-info">
                          <el-icon class="dept-icon" style="color: #409eff"><OfficeBuilding /></el-icon>
                          <span class="dept-name">{{ grandChild.deptName }}</span>
                          <el-tag v-if="grandChild.managerName" size="small" type="success" class="manager-tag">
                            {{ grandChild.managerName }}
                          </el-tag>
                        </div>
                        <div class="dept-count">{{ grandChild.memberCount || 0 }}人</div>
                        <div class="dept-actions">
                          <el-button text size="small" @click.stop="handleEdit(grandChild)">
                            <el-icon><Edit /></el-icon>
                          </el-button>
                          <el-button text size="small" type="danger" @click.stop="handleDelete(grandChild)">
                            <el-icon><Delete /></el-icon>
                          </el-button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="hover" v-if="selectedDept">
          <template #header>
            <div class="card-header">
              <span>{{ selectedDept.deptName }} - 部门详情</span>
              <div>
                <el-button type="primary" size="small" @click="handleSetManager">设置部门长</el-button>
                <el-button type="success" size="small" @click="handleAddMember">添加成员</el-button>
              </div>
            </div>
          </template>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="部门名称">{{ selectedDept.deptName }}</el-descriptions-item>
            <el-descriptions-item label="层级">
              <el-tag :type="getLevelType(selectedDept.level)" size="small">
                {{ getLevelName(selectedDept.level) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="部门长">{{ selectedDept.managerName || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="人员数量">{{ selectedDept.memberCount || 0 }} 人</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="selectedDept.status === 1 ? 'success' : 'danger'" size="small">
                {{ selectedDept.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
          
          <div class="members-section">
            <div class="section-title">
              <span>部门成员</span>
              <span class="member-count">共 {{ members.length }} 人</span>
            </div>
            <el-table :data="members" stripe size="small" v-if="members.length > 0">
              <el-table-column prop="realName" label="姓名" width="100" />
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="position" label="岗位" />
              <el-table-column prop="role" label="角色" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" size="small">
                    {{ row.role === 'ADMIN' ? '管理员' : 'HR' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="isManager" label="部门长" width="80" align="center">
                <template #default="{ row }">
                  <el-tag v-if="row.isManager === 1" type="success" size="small">是</el-tag>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160">
                <template #default="{ row }">
                  <el-button link type="primary" size="small" @click="handleSetAsManager(row)" v-if="row.isManager !== 1">
                    设为部门长
                  </el-button>
                  <el-button link type="danger" size="small" @click="handleRemoveMember(row)">
                    移出
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="暂无成员" :image-size="60" />
          </div>
        </el-card>
        <el-card shadow="hover" v-else>
          <el-empty description="请选择左侧部门查看详情" :image-size="100" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId" v-if="!form.id || form.level > 1">
          <el-select v-model="form.parentId" placeholder="请选择上级部门" clearable style="width: 100%">
            <el-option 
              v-for="dept in parentOptions" 
              :key="dept.id" 
              :label="dept.deptName + ' (' + getLevelName(dept.level) + ')'" 
              :value="dept.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="层级" prop="level">
          <el-select v-model="form.level" placeholder="请选择层级" style="width: 100%">
            <el-option label="L1 总经理" :value="1" />
            <el-option label="L2 副总" :value="2" />
            <el-option label="L3 部门" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="managerDialogVisible" title="设置部门长" width="500px" destroy-on-close>
      <el-select v-model="selectedManagerId" placeholder="请选择部门长" filterable style="width: 100%">
        <el-option v-for="user in allUsers" :key="user.id" :label="user.realName + ' (' + user.username + ')'" :value="user.id" />
      </el-select>
      <template #footer>
        <el-button @click="managerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitManager">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="memberDialogVisible" title="添加成员" width="500px" destroy-on-close>
      <el-select v-model="selectedUserIds" placeholder="请选择成员" multiple filterable style="width: 100%">
        <el-option v-for="user in availableUsers" :key="user.id" :label="user.realName + ' (' + user.username + ')'" :value="user.id" />
      </el-select>
      <template #footer>
        <el-button @click="memberDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMembers">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/index'

const loading = ref(false)
const departmentTree = ref([])
const departments = ref([])
const selectedDept = ref(null)
const members = ref([])
const allUsers = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  id: null,
  deptName: '',
  parentId: null,
  level: 1,
  sortOrder: 0,
  status: 1
})

const rules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择层级', trigger: 'change' }]
}

const managerDialogVisible = ref(false)
const selectedManagerId = ref(null)

const memberDialogVisible = ref(false)
const selectedUserIds = ref([])

const parentOptions = computed(() => {
  return departments.value.filter(d => d.id !== form.id && d.level < 3)
})

const availableUsers = computed(() => {
  const memberIds = members.value.map(m => m.id)
  return allUsers.value.filter(u => !memberIds.includes(u.id))
})

const fetchDepartments = async () => {
  loading.value = true
  try {
    const res = await request.get('/departments/tree')
    if (res.code === 200) {
      departmentTree.value = res.data || []
      departments.value = flattenTree(res.data || [])
    }
  } catch (e) {
    console.error('获取部门树失败', e)
  } finally {
    loading.value = false
  }
}

const fetchUsers = async () => {
  try {
    const res = await request.get('/users')
    if (res.code === 200) {
      allUsers.value = res.data || []
    }
  } catch (e) {
    console.error('获取用户列表失败', e)
  }
}

const fetchMembers = async (deptId) => {
  try {
    const res = await request.get(`/departments/${deptId}/members`)
    if (res.code === 200) {
      members.value = res.data || []
    }
  } catch (e) {
    console.error('获取成员列表失败', e)
    members.value = []
  }
}

const flattenTree = (tree, result = []) => {
  if (!tree) return result
  for (const node of tree) {
    result.push(node)
    if (node.children && node.children.length) {
      flattenTree(node.children, result)
    }
  }
  return result
}

const selectDept = (dept) => {
  selectedDept.value = dept
  fetchMembers(dept.id)
}

const getLevelName = (level) => {
  const names = { 1: '总经理', 2: '副总', 3: '部门' }
  return names[level] || '未知'
}

const getLevelType = (level) => {
  const types = { 1: 'danger', 2: 'warning', 3: 'primary' }
  return types[level] || 'info'
}

const resetForm = () => {
  form.id = null
  form.deptName = ''
  form.parentId = null
  form.level = 1
  form.sortOrder = 0
  form.status = 1
}

const handleAddRoot = () => {
  dialogTitle.value = '添加部门'
  resetForm()
  dialogVisible.value = true
}

const handleAdd = (parent) => {
  dialogTitle.value = '添加子部门'
  resetForm()
  form.parentId = parent.id
  form.level = Math.min(parent.level + 1, 3)
  dialogVisible.value = true
}

const handleEdit = (dept) => {
  dialogTitle.value = '编辑部门'
  Object.assign(form, {
    id: dept.id,
    deptName: dept.deptName,
    parentId: dept.parentId,
    level: dept.level,
    sortOrder: dept.sortOrder || 0,
    status: dept.status
  })
  dialogVisible.value = true
}

const handleDelete = async (dept) => {
  try {
    await ElMessageBox.confirm(`确定要删除"${dept.deptName}"吗？删除后不可恢复。`, '提示', { type: 'warning' })
    await request.delete(`/departments/${dept.id}`)
    ElMessage.success('删除成功')
    fetchDepartments()
    if (selectedDept.value?.id === dept.id) {
      selectedDept.value = null
      members.value = []
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (form.id) {
      await request.put(`/departments/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/departments', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchDepartments()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleSetManager = () => {
  selectedManagerId.value = selectedDept.value.managerId || null
  managerDialogVisible.value = true
}

const submitManager = async () => {
  if (!selectedManagerId.value) {
    ElMessage.warning('请选择部门长')
    return
  }
  try {
    await request.put(`/departments/${selectedDept.value.id}/manager?userId=${selectedManagerId.value}`)
    ElMessage.success('设置成功')
    managerDialogVisible.value = false
    fetchDepartments()
    fetchMembers(selectedDept.value.id)
  } catch (e) {
    ElMessage.error('设置失败')
  }
}

const handleSetAsManager = async (user) => {
  try {
    await ElMessageBox.confirm(`确定将"${user.realName}"设为部门长吗？`, '提示', { type: 'info' })
    await request.put(`/departments/${selectedDept.value.id}/manager?userId=${user.id}`)
    ElMessage.success('设置成功')
    fetchDepartments()
    fetchMembers(selectedDept.value.id)
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('设置失败')
    }
  }
}

const handleAddMember = () => {
  selectedUserIds.value = []
  memberDialogVisible.value = true
}

const submitMembers = async () => {
  if (selectedUserIds.value.length === 0) {
    ElMessage.warning('请选择成员')
    return
  }
  try {
    for (const userId of selectedUserIds.value) {
      await request.post(`/departments/${selectedDept.value.id}/members?userId=${userId}`)
    }
    ElMessage.success('添加成功')
    memberDialogVisible.value = false
    fetchMembers(selectedDept.value.id)
    fetchDepartments()
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

const handleRemoveMember = async (user) => {
  try {
    await ElMessageBox.confirm(`确定将"${user.realName}"移出该部门吗？`, '提示', { type: 'warning' })
    await request.delete(`/departments/${selectedDept.value.id}/members/${user.id}`)
    ElMessage.success('移出成功')
    fetchMembers(selectedDept.value.id)
    fetchDepartments()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('移出失败')
    }
  }
}

onMounted(() => {
  fetchDepartments()
  fetchUsers()
})
</script>

<style scoped>
.organization-page {
  padding: 0;
}

.tree-card {
  min-height: calc(100vh - 160px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.card-header span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tree-container {
  padding: 10px 0;
  max-height: calc(100vh - 240px);
  overflow-y: auto;
}

.empty-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.dept-node {
  margin-bottom: 4px;
}

.dept-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.dept-item:hover {
  background: #f5f7fa;
}

.dept-item.active {
  background: #ecf5ff;
  border-color: #409eff;
}

.dept-item.level-1 {
  background: #fef0f0;
  font-weight: 600;
}

.dept-item.level-1:hover,
.dept-item.level-1.active {
  background: #fde2e2;
}

.dept-item.level-2 {
  background: #fdf6ec;
}

.dept-item.level-2:hover,
.dept-item.level-2.active {
  background: #faecd8;
}

.dept-item.level-3 {
  background: #ecf5ff;
}

.dept-item.level-3:hover,
.dept-item.level-3.active {
  background: #d9ecff;
}

.dept-children {
  margin-left: 16px;
  margin-top: 4px;
}

.dept-children.level-3 {
  margin-left: 32px;
}

.dept-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.dept-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.dept-name {
  font-size: 14px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.manager-tag {
  flex-shrink: 0;
}

.dept-count {
  font-size: 12px;
  color: #909399;
  margin-right: 8px;
  flex-shrink: 0;
}

.dept-actions {
  display: flex;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.2s;
}

.dept-item:hover .dept-actions {
  opacity: 1;
}

.members-section {
  margin-top: 20px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.member-count {
  font-size: 13px;
  font-weight: normal;
  color: #909399;
}
</style>
