<template>
  <el-container class="app-container" v-if="isLoggedIn">
    <el-aside width="240px" class="sidebar">
      <div class="logo">
        <div class="logo-icon">HR</div>
        <div class="logo-text">外包人力管理</div>
      </div>
      <el-scrollbar class="menu-scrollbar">
        <el-menu :default-active="route.path" class="sidebar-menu" :unique-opened="true" router>
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          
          <el-sub-menu index="business">
            <template #title>
              <el-icon><Briefcase /></el-icon>
              <span>业务管理</span>
            </template>
            <el-menu-item index="/requirements">
              <el-icon><Document /></el-icon>
              <span>需求管理</span>
            </el-menu-item>
            <el-menu-item index="/projects">
              <el-icon><FolderOpened /></el-icon>
              <span>项目管理</span>
            </el-menu-item>
            <el-menu-item index="/suppliers">
              <el-icon><OfficeBuilding /></el-icon>
              <span>供应商管理</span>
            </el-menu-item>
            <el-menu-item index="/personnel">
              <el-icon><User /></el-icon>
              <span>人员管理</span>
            </el-menu-item>
            <el-menu-item index="/workhours">
              <el-icon><Clock /></el-icon>
              <span>工时管理</span>
            </el-menu-item>
            <el-menu-item index="/settlements">
              <el-icon><Money /></el-icon>
              <span>结算管理</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="finance">
            <template #title>
              <el-icon><Wallet /></el-icon>
              <span>财务管理</span>
            </template>
            <el-menu-item index="/contracts">
              <el-icon><Tickets /></el-icon>
              <span>合同管理</span>
            </el-menu-item>
            <el-menu-item index="/payments">
              <el-icon><CreditCard /></el-icon>
              <span>付款管理</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="hr">
            <template #title>
              <el-icon><Avatar /></el-icon>
              <span>人力运营</span>
            </template>
            <el-menu-item index="/candidates">
              <el-icon><UserFilled /></el-icon>
              <span>候选人管理</span>
            </el-menu-item>
            <el-menu-item index="/evaluations">
              <el-icon><Star /></el-icon>
              <span>绩效考核</span>
            </el-menu-item>
            <el-menu-item index="/reports">
              <el-icon><PieChart /></el-icon>
              <span>报表中心</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="workflow">
            <template #title>
              <el-icon><Connection /></el-icon>
              <span>工作流程</span>
            </template>
            <el-menu-item index="/todo">
              <el-icon><List /></el-icon>
              <span>我的待办</span>
            </el-menu-item>
            <el-menu-item index="/done">
              <el-icon><Finished /></el-icon>
              <span>我的已办</span>
            </el-menu-item>
            <el-menu-item index="/org-chart">
              <el-icon><Share /></el-icon>
              <span>组织架构</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="settings" v-if="isAdmin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/settings/users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/settings/dict">
              <el-icon><Collection /></el-icon>
              <span>字典管理</span>
            </el-menu-item>
            <el-menu-item index="/workflow">
              <el-icon><Connection /></el-icon>
              <span>工作流定义</span>
            </el-menu-item>
            <el-menu-item index="/logs">
              <el-icon><Document /></el-icon>
              <span>操作日志</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-menu-item index="/messages">
            <el-icon><Bell /></el-icon>
            <span>消息通知</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                {{ userInitial }}
              </el-avatar>
              <div class="user-detail">
                <span class="username">{{ userInfo?.realName || '用户' }}</span>
                <span class="user-role">{{ userInfo?.role === 'ADMIN' ? '管理员' : 'HR' }}</span>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
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
        <router-view />
      </el-main>
    </el-container>
  </el-container>
  <router-view v-else />
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAdmin = computed(() => userStore.isAdmin)
const userInfo = computed(() => userStore.userInfo)

const userInitial = computed(() => {
  return userInfo.value?.realName?.charAt(0) || 'U'
})

const currentTitle = computed(() => route.meta?.title || '首页')

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  background: #f0f2f5;
}

.sidebar {
  background: linear-gradient(180deg, #1a1f36 0%, #252d4a 100%);
  box-shadow: 2px 0 8px rgba(0,0,0,0.15);
  overflow: hidden;
}

.logo {
  height: 70px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  background: rgba(0,0,0,0.1);
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: bold;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  color: white;
  font-size: 16px;
  font-weight: 600;
}

.menu-scrollbar {
  height: calc(100vh - 70px);
}

.sidebar-menu {
  border-right: none;
  background: transparent;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  color: rgba(255,255,255,0.7);
  margin: 4px 8px;
  border-radius: 8px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255,255,255,0.1) !important;
  color: white;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%) !important;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.sidebar-menu :deep(.el-sub-menu__title) {
  height: 48px;
  line-height: 48px;
  color: rgba(255,255,255,0.85);
  margin: 4px 8px;
  border-radius: 8px;
}

.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255,255,255,0.1) !important;
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

.header {
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
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
  min-height: calc(100vh - 60px);
}
</style>
