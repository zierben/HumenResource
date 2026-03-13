<template>
  <el-container class="app-container" v-if="isLoggedIn">
    <el-aside :width="sidebarCollapsed ? '64px' : '240px'" class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="logo" @click="router.push('/dashboard')">
        <div class="logo-icon">HR</div>
        <div class="logo-text" v-show="!sidebarCollapsed">外包人力管理</div>
      </div>
      <el-scrollbar class="menu-scrollbar">
        <el-menu :default-active="route.path" class="sidebar-menu" :unique-opened="true" router :collapse="sidebarCollapsed">
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>
          
          <el-sub-menu index="business">
            <template #title>
              <el-icon><Briefcase /></el-icon>
              <span>业务管理</span>
            </template>
            <el-menu-item index="/requirements">
              <el-icon><Document /></el-icon>
              <template #title>需求管理</template>
            </el-menu-item>
            <el-menu-item index="/projects">
              <el-icon><FolderOpened /></el-icon>
              <template #title>项目管理</template>
            </el-menu-item>
            <el-menu-item index="/suppliers">
              <el-icon><OfficeBuilding /></el-icon>
              <template #title>供应商管理</template>
            </el-menu-item>
            <el-menu-item index="/personnel">
              <el-icon><User /></el-icon>
              <template #title>人员管理</template>
            </el-menu-item>
            <el-menu-item index="/workhours">
              <el-icon><Clock /></el-icon>
              <template #title>工时管理</template>
            </el-menu-item>
            <el-menu-item index="/settlements">
              <el-icon><Money /></el-icon>
              <template #title>结算管理</template>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="finance">
            <template #title>
              <el-icon><Wallet /></el-icon>
              <span>财务管理</span>
            </template>
            <el-menu-item index="/contracts">
              <el-icon><Tickets /></el-icon>
              <template #title>合同管理</template>
            </el-menu-item>
            <el-menu-item index="/payments">
              <el-icon><CreditCard /></el-icon>
              <template #title>付款管理</template>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="hr">
            <template #title>
              <el-icon><Avatar /></el-icon>
              <span>人力运营</span>
            </template>
            <el-menu-item index="/candidates">
              <el-icon><UserFilled /></el-icon>
              <template #title>候选人管理</template>
            </el-menu-item>
            <el-menu-item index="/evaluations">
              <el-icon><Star /></el-icon>
              <template #title>绩效考核</template>
            </el-menu-item>
            <el-menu-item index="/reports">
              <el-icon><PieChart /></el-icon>
              <template #title>报表中心</template>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="workflow">
            <template #title>
              <el-icon><Connection /></el-icon>
              <span>工作流程</span>
            </template>
            <el-menu-item index="/todo">
              <el-icon><List /></el-icon>
              <template #title>我的待办</template>
            </el-menu-item>
            <el-menu-item index="/done">
              <el-icon><Finished /></el-icon>
              <template #title>我的已办</template>
            </el-menu-item>
            <el-menu-item index="/org-chart">
              <el-icon><Share /></el-icon>
              <template #title>组织架构</template>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="settings" v-if="isAdmin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/settings/users">
              <el-icon><User /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>
            <el-menu-item index="/settings/dict">
              <el-icon><Collection /></el-icon>
              <template #title>字典管理</template>
            </el-menu-item>
            <el-menu-item index="/settings/sync">
              <el-icon><Refresh /></el-icon>
              <template #title>数据同步</template>
            </el-menu-item>
            <el-menu-item index="/workflow">
              <el-icon><Connection /></el-icon>
              <template #title>工作流定义</template>
            </el-menu-item>
            <el-menu-item index="/logs">
              <el-icon><Document /></el-icon>
              <template #title>操作日志</template>
            </el-menu-item>
          </el-sub-menu>
          
          <el-menu-item index="/messages">
            <el-icon><Bell /></el-icon>
            <template #title>消息通知</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
      
      <div class="sidebar-toggle" @click="sidebarCollapsed = !sidebarCollapsed">
        <el-icon :size="16">
          <component :is="sidebarCollapsed ? 'Expand' : 'Fold'" />
        </el-icon>
      </div>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-center">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索功能、人员、项目..." 
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="header-right">
          <el-tooltip content="全屏" placement="bottom">
            <div class="header-action" @click="toggleFullscreen">
              <el-icon><FullScreen /></el-icon>
            </div>
          </el-tooltip>
          
          <el-tooltip content="消息" placement="bottom">
            <el-badge :value="messageCount" :hidden="messageCount === 0" class="header-badge">
              <div class="header-action" @click="router.push('/messages')">
                <el-icon><Bell /></el-icon>
              </div>
            </el-badge>
          </el-tooltip>
          
          <el-tooltip content="设置" placement="bottom">
            <div class="header-action" @click="showSettings = true">
              <el-icon><Setting /></el-icon>
            </div>
          </el-tooltip>

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" class="user-avatar">
                {{ userInitial }}
              </el-avatar>
              <div class="user-detail">
                <span class="username">{{ userInfo?.realName || '用户' }}</span>
                <span class="user-role">{{ getRoleName(userInfo?.role) }}</span>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="showProfile = true">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="showSettings = true">
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <!-- 个人中心抽屉 -->
    <el-drawer v-model="showProfile" title="个人中心" size="400px">
      <div class="profile-drawer">
        <div class="profile-header">
          <el-avatar :size="80" class="large-avatar">
            {{ userInitial }}
          </el-avatar>
          <h3>{{ userInfo?.realName }}</h3>
          <p>{{ userInfo?.position || '暂无职位' }}</p>
          <el-tag :type="getRoleType(userInfo?.role)" size="small">
            {{ getRoleName(userInfo?.role) }}
          </el-tag>
        </div>
        <el-divider />
        <div class="profile-info">
          <div class="info-item">
            <el-icon><User /></el-icon>
            <span class="info-label">用户名</span>
            <span class="info-value">{{ userInfo?.username }}</span>
          </div>
          <div class="info-item">
            <el-icon><OfficeBuilding /></el-icon>
            <span class="info-label">部门</span>
            <span class="info-value">{{ userInfo?.department || '暂无' }}</span>
          </div>
          <div class="info-item">
            <el-icon><Avatar /></el-icon>
            <span class="info-label">职位</span>
            <span class="info-value">{{ userInfo?.position || '暂无' }}</span>
          </div>
          <div class="info-item">
            <el-icon><Clock /></el-icon>
            <span class="info-label">层级</span>
            <span class="info-value">L{{ userInfo?.level || '-' }}</span>
          </div>
        </div>
        <el-divider />
        <div class="profile-actions">
          <el-button type="primary" round>修改密码</el-button>
          <el-button round>编辑资料</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 系统设置抽屉 -->
    <el-drawer v-model="showSettings" title="系统设置" size="360px">
      <div class="settings-drawer">
        <div class="settings-section">
          <div class="section-label">主题配色</div>
          <div class="theme-grid">
            <div 
              v-for="theme in themeList" 
              :key="theme.key" 
              class="theme-item"
              :class="{ active: currentTheme === theme.key }"
              @click="setTheme(theme.key)"
            >
              <div class="theme-preview" :style="{ background: theme.sidebarBg }"></div>
              <span>{{ theme.name }}</span>
            </div>
          </div>
        </div>
        
        <el-divider />
        
        <div class="settings-section">
          <div class="section-label">界面设置</div>
          <div class="setting-item">
            <span>侧边栏收起</span>
            <el-switch v-model="sidebarCollapsed" />
          </div>
          <div class="setting-item">
            <span>显示面包屑</span>
            <el-switch v-model="showBreadcrumb" />
          </div>
          <div class="setting-item">
            <span>显示搜索框</span>
            <el-switch v-model="showSearch" />
          </div>
        </div>
      </div>
    </el-drawer>
  </el-container>
  <router-view v-else />
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useTheme } from '@/composables/useTheme'
import {
  Expand, Fold
} from '@element-plus/icons-vue'

import { ElMessage } from 'element-plus'
import request from '@/api/index'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { themes, currentTheme, themeList, setTheme } = useTheme()

const sidebarCollapsed = ref(false)
const showProfile = ref(false)
const showSettings = ref(false)
const searchKeyword = ref('')
const searchResults = ref([])
const showSearchResults = ref(false)
const messageCount = ref(0)
const showBreadcrumb = ref(true)
const showSearch = ref(true)

const fetchMessageCount = async () => {
  try {
    const userId = userInfo.value?.id
    if (userId) {
      const res = await request.get('/messages', { params: { pageNum: 1, pageSize: 1, userId } })
      if (res.code === 200) {
        messageCount.value = res.data.total || 0
      }
    }
  } catch (e) {
    console.error('获取消息数量失败', e)
  }
}

const typeIcons = {
  personnel: 'User',
  project: 'FolderOpened',
  supplier: 'OfficeBuilding',
  contract: 'Tickets'
}

const typeLabels = {
  personnel: '人员',
  project: '项目',
  supplier: '供应商',
  contract: '合同'
}

const handleSearchResultClick = (item) => {
  showSearchResults.value = false
  searchKeyword.value = ''
  router.push(item.path)
}

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAdmin = computed(() => userStore.isAdmin)
const userInfo = computed(() => userStore.userInfo)

const userInitial = computed(() => {
  return userInfo.value?.realName?.charAt(0) || 'U'
})

const roleMap = {
  ADMIN: { name: '管理员', type: 'danger' },
  GM: { name: '总经理', type: 'danger' },
  VP: { name: '副总', type: 'warning' },
  DEPT_HEAD: { name: '部门长', type: 'primary' },
  PM: { name: '项目经理', type: 'success' },
  HR: { name: 'HR', type: 'info' }
}

const getRoleName = (role) => roleMap[role]?.name || role
const getRoleType = (role) => roleMap[role]?.type || 'info'

const currentTitle = computed(() => route.meta?.title || '首页')

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const handleSearch = async () => {
  if (searchKeyword.value.trim()) {
    try {
      const res = await request.get('/search', { params: { keyword: searchKeyword.value.trim() } })
      if (res.code === 200 && res.data.length > 0) {
        searchResults.value = res.data
        showSearchResults.value = true
      } else {
        ElMessage.info('未找到相关结果')
      }
    } catch (e) {
      console.error(e)
    }
  }
}

watch(userInfo, (newVal) => {
  if (newVal?.id) {
    fetchMessageCount()
  }
}, { immediate: true })

onMounted(() => {
  if (userInfo.value?.id) {
    fetchMessageCount()
  }
})

</script>

<style>
:root {
  --primary-color: #667eea;
  --secondary-color: #764ba2;
  --primary-light: rgba(102, 126, 234, 0.1);
}
</style>

<style scoped>
.app-container {
  height: 100vh;
  background: #f0f2f5;
}

.sidebar {
  background: linear-gradient(180deg, #1a1f36 0%, #252d4a 100%);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  transition: width 0.3s ease;
  position: relative;
}

.sidebar.collapsed {
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.1);
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.logo:hover {
  background: rgba(0, 0, 0, 0.15);
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: white;
  font-weight: bold;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-text {
  color: white;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  transition: opacity 0.3s ease;
}

.menu-scrollbar {
  height: calc(100vh - 100px);
}

.sidebar-menu {
  border-right: none;
  background: transparent;
}

.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  height: 48px;
  line-height: 48px;
  color: rgba(255, 255, 255, 0.7);
  margin: 4px 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.1) !important;
  color: white;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, var(--primary-color) 0%, var(--secondary-color) 100%) !important;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.sidebar-menu :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  color: white;
}

.sidebar-menu :deep(.el-menu) {
  background: transparent;
}

.sidebar-menu :deep(.el-menu .el-menu-item) {
  padding-left: 52px !important;
}

.sidebar-menu :deep(.el-menu--collapse) .el-menu-item,
.sidebar-menu :deep(.el-menu--collapse) .el-sub-menu__title {
  margin: 4px;
}

.sidebar-toggle {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
}

.sidebar-toggle:hover {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.header {
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-center {
  flex: 1;
  max-width: 400px;
  margin: 0 40px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  background: #f5f7fa;
  box-shadow: none;
}

.search-input :deep(.el-input__wrapper:focus-within) {
  background: white;
  box-shadow: 0 0 0 1px var(--primary-color);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-action {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #606266;
  transition: all 0.3s ease;
}

.header-action:hover {
  background: #f5f7fa;
  color: var(--primary-color);
}

.header-badge :deep(.el-badge__content) {
  top: 6px;
  right: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 24px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-avatar {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  flex-shrink: 0;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.user-role {
  font-size: 12px;
  color: #909399;
}

.dropdown-icon {
  color: #909399;
  font-size: 12px;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  min-height: calc(100vh - 64px);
}

/* 页面过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 个人中心抽屉 */
.profile-drawer {
  padding: 20px;
}

.profile-header {
  text-align: center;
  padding: 20px 0;
}

.large-avatar {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  margin: 0 auto 16px;
}

.profile-header h3 {
  margin: 0 0 8px;
  color: #303133;
}

.profile-header p {
  margin: 0 0 12px;
  color: #909399;
  font-size: 14px;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.info-item .el-icon {
  color: var(--primary-color);
  font-size: 18px;
}

.info-label {
  color: #909399;
  font-size: 14px;
  width: 60px;
}

.info-value {
  color: #303133;
  font-size: 14px;
  flex: 1;
  text-align: right;
}

.profile-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  padding-top: 20px;
}

/* 设置抽屉 */
.settings-drawer {
  padding: 20px;
}

.settings-section {
  margin-bottom: 8px;
}

.section-label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 16px;
}

.theme-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.theme-item {
  padding: 12px;
  border-radius: 12px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.theme-item:hover {
  background: #f5f7fa;
}

.theme-item.active {
  border-color: var(--primary-color);
  background: var(--primary-light);
}

.theme-preview {
  width: 100%;
  height: 40px;
  border-radius: 8px;
  margin-bottom: 8px;
}

.theme-item span {
  font-size: 12px;
  color: #606266;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f2f5;
}

.setting-item span {
  font-size: 14px;
  color: #303133;
}
</style>
