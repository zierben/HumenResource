import { ref } from 'vue'
import request from '@/api/index'
import { ElMessage } from 'element-plus'

export function usePagination(endpoint, defaultParams = {}) {
  const list = ref([])
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const searchParams = ref({ ...defaultParams })
  
  const fetchData = async () => {
    loading.value = true
    try {
      const params = {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        ...searchParams.value
      }
      const res = await request.get(endpoint, { params })
      if (res.code === 200) {
        list.value = res.data.records
        total.value = res.data.total
      }
    } catch (e) {
      console.error(e)
      ElMessage.error('获取数据失败')
    } finally {
      loading.value = false
    }
  }
  
  const handleSearch = () => {
    currentPage.value = 1
    fetchData()
  }
  
  const handleReset = (resetParams = {}) => {
    searchParams.value = { ...defaultParams, ...resetParams }
    currentPage.value = 1
    fetchData()
  }
  
  const handlePageChange = (page) => {
    currentPage.value = page
    fetchData()
  }
  
  const setSearchParam = (key, value) => {
    if (value === null || value === undefined || value === '') {
      delete searchParams.value[key]
    } else {
      searchParams.value[key] = value
    }
  }
  
  return {
    list,
    loading,
    total,
    currentPage,
    pageSize,
    searchParams,
    fetchData,
    handleSearch,
    handleReset,
    handlePageChange,
    setSearchParam
  }
}

export function useReferenceData() {
  const suppliers = ref([])
  const projects = ref([])
  
  const fetchSuppliers = async () => {
    try {
      const res = await request.get('/suppliers', { params: { pageNum: 1, pageSize: 100 } })
      if (res.code === 200) {
        suppliers.value = res.data.records
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
  
  const fetchAll = async () => {
    await Promise.all([fetchSuppliers(), fetchProjects()])
  }
  
  const getSupplierName = (supplierId) => {
    const supplier = suppliers.value.find(s => s.id === supplierId)
    return supplier ? supplier.supplierName : '-'
  }
  
  const getProjectName = (projectId) => {
    const project = projects.value.find(p => p.id === projectId)
    return project ? project.projectName : '-'
  }
  
  return {
    suppliers,
    projects,
    fetchSuppliers,
    fetchProjects,
    fetchAll,
    getSupplierName,
    getProjectName
  }
}

export function useFormDialog(defaultForm = {}) {
  const showDialog = ref(false)
  const isEdit = ref(false)
  const editId = ref(null)
  const form = ref({ ...defaultForm })
  
  const openAddDialog = () => {
    isEdit.value = false
    editId.value = null
    form.value = { ...defaultForm }
    showDialog.value = true
  }
  
  const openEditDialog = (row) => {
    isEdit.value = true
    editId.value = row.id
    form.value = { ...row }
    showDialog.value = true
  }
  
  const closeDialog = () => {
    showDialog.value = false
  }
  
  return {
    showDialog,
    isEdit,
    editId,
    form,
    openAddDialog,
    openEditDialog,
    closeDialog
  }
}
