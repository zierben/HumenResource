<template>
  <div class="enhanced-table">
    <!-- 工具栏 -->
    <div class="table-toolbar" v-if="showToolbar">
      <div class="toolbar-left">
        <slot name="toolbar-left">
          <el-input
            v-if="showSearch"
            v-model="searchText"
            :placeholder="searchPlaceholder"
            class="search-input"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-select
            v-if="filters && filters.length > 0"
            v-model="activeFilter"
            :placeholder="filterPlaceholder"
            class="filter-select"
            clearable
            @change="handleFilter"
          >
            <el-option
              v-for="filter in filters"
              :key="filter.value"
              :label="filter.label"
              :value="filter.value"
            />
          </el-select>

          <el-date-picker
            v-if="showDateFilter"
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            class="date-picker"
            @change="handleDateFilter"
          />
        </slot>
      </div>
      
      <div class="toolbar-right">
        <slot name="toolbar-right">
          <el-button-group class="action-group">
            <el-tooltip content="刷新" placement="top">
              <el-button @click="handleRefresh">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="列设置" placement="top">
              <el-button @click="showColumnSettings = true">
                <el-icon><Setting /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="导出" placement="top">
              <el-button @click="handleExport">
                <el-icon><Download /></el-icon>
              </el-button>
            </el-tooltip>
          </el-button-group>
        </slot>
      </div>
    </div>

    <!-- 批量操作栏 -->
    <transition name="slide-down">
      <div v-if="selectedRows.length > 0" class="batch-actions">
        <span class="selected-count">已选择 {{ selectedRows.length }} 项</span>
        <div class="batch-buttons">
          <slot name="batch-actions" :rows="selectedRows">
            <el-button size="small" @click="clearSelection">取消选择</el-button>
          </slot>
        </div>
      </div>
    </transition>

    <!-- 表格 -->
    <el-table
      ref="tableRef"
      :data="processedData"
      :loading="loading"
      :row-key="rowKey"
      :height="height"
      :max-height="maxHeight"
      :stripe="stripe"
      :border="border"
      :size="size"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      @row-click="handleRowClick"
      class="data-table"
      v-bind="$attrs"
    >
      <slot />
    </el-table>

    <!-- 分页 -->
    <div v-if="showPagination" class="table-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="pageSizes"
        :total="total"
        :layout="paginationLayout"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 列设置抽屉 -->
    <el-drawer v-model="showColumnSettings" title="列设置" size="300px">
      <div class="column-settings">
        <div class="setting-item" v-for="col in columns" :key="col.prop">
          <el-checkbox v-model="col.visible">{{ col.label }}</el-checkbox>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'

const props = defineProps({
  data: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  showToolbar: { type: Boolean, default: true },
  showSearch: { type: Boolean, default: true },
  searchPlaceholder: { type: String, default: '搜索...' },
  showDateFilter: { type: Boolean, default: false },
  filters: { type: Array, default: () => [] },
  filterPlaceholder: { type: String, default: '筛选' },
  showPagination: { type: Boolean, default: true },
  total: { type: Number, default: 0 },
  pageSize: { type: Number, default: 10 },
  pageSizes: { type: Array, default: () => [10, 20, 50, 100] },
  height: { type: [String, Number], default: undefined },
  maxHeight: { type: [String, Number], default: undefined },
  stripe: { type: Boolean, default: true },
  border: { type: Boolean, default: false },
  size: { type: String, default: 'default' },
  rowKey: { type: String, default: 'id' },
  searchFields: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:pageSize', 'update:currentPage', 'search', 'filter', 'sort', 'refresh', 'export', 'selection-change', 'row-click'])

const tableRef = ref(null)
const searchText = ref('')
const activeFilter = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(props.pageSize)
const selectedRows = ref([])
const showColumnSettings = ref(false)
const columns = ref([])
const paginationLayout = 'total, sizes, prev, pager, next, jumper'

const processedData = computed(() => {
  return props.data
})

const handleSearch = () => {
  emit('search', searchText.value)
}

const handleFilter = () => {
  emit('filter', activeFilter.value)
}

const handleDateFilter = () => {
  emit('filter', { dateRange: dateRange.value })
}

const handleRefresh = () => {
  emit('refresh')
}

const handleExport = () => {
  emit('export')
}

const handleSortChange = ({ prop, order }) => {
  emit('sort', { prop, order })
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
  emit('selection-change', rows)
}

const handleRowClick = (row) => {
  emit('row-click', row)
}

const handleSizeChange = (size) => {
  emit('update:pageSize', size)
}

const handleCurrentChange = (page) => {
  emit('update:currentPage', page)
}

const clearSelection = () => {
  tableRef.value?.clearSelection()
}

defineExpose({
  clearSelection,
  tableRef
})
</script>

<style scoped>
.enhanced-table {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fafbfc;
  border-bottom: 1px solid #f0f2f5;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 240px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.filter-select {
  width: 140px;
}

.date-picker {
  width: 260px;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-group :deep(.el-button) {
  padding: 8px 12px;
}

.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #ecf5ff;
  border-bottom: 1px solid #b3d8ff;
}

.selected-count {
  font-size: 14px;
  color: #409eff;
  font-weight: 500;
}

.batch-buttons {
  display: flex;
  gap: 8px;
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-100%);
}

.data-table {
  --el-table-header-bg-color: #fafbfc;
}

.data-table :deep(.el-table__header th) {
  font-weight: 600;
  color: #1f2937;
}

.data-table :deep(.el-table__row) {
  transition: background-color 0.2s ease;
}

.data-table :deep(.el-table__row:hover > td) {
  background-color: #f9fafb !important;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  padding: 16px;
  border-top: 1px solid #f0f2f5;
}

.column-settings {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.setting-item {
  padding: 8px 0;
  border-bottom: 1px solid #f0f2f5;
}
</style>
