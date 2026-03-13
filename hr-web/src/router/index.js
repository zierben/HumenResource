import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true, title: '仪表盘' }
  },
  {
    path: '/requirements',
    name: 'Requirements',
    component: () => import('@/views/Requirements.vue'),
    meta: { requiresAuth: true, title: '需求管理' }
  },
  {
    path: '/projects',
    name: 'Projects',
    component: () => import('@/views/Projects.vue'),
    meta: { requiresAuth: true, title: '项目管理' }
  },
  {
    path: '/projects/:id',
    name: 'ProjectDetail',
    component: () => import('@/views/ProjectDetail.vue'),
    meta: { requiresAuth: true, title: '项目详情' }
  },
  {
    path: '/suppliers',
    name: 'Suppliers',
    component: () => import('@/views/Suppliers.vue'),
    meta: { requiresAuth: true, title: '供应商管理' }
  },
  {
    path: '/suppliers/:id',
    name: 'SupplierDetail',
    component: () => import('@/views/SupplierDetail.vue'),
    meta: { requiresAuth: true, title: '供应商详情' }
  },
  {
    path: '/personnel',
    name: 'Personnel',
    component: () => import('@/views/Personnel.vue'),
    meta: { requiresAuth: true, title: '人员管理' }
  },
  {
    path: '/personnel/:id',
    name: 'PersonnelDetail',
    component: () => import('@/views/PersonnelDetail.vue'),
    meta: { requiresAuth: true, title: '人员详情' }
  },
  {
    path: '/workhours',
    name: 'WorkHours',
    component: () => import('@/views/WorkHours.vue'),
    meta: { requiresAuth: true, title: '工时管理' }
  },
  {
    path: '/settlements',
    name: 'Settlements',
    component: () => import('@/views/Settlements.vue'),
    meta: { requiresAuth: true, title: '结算管理' }
  },
  {
    path: '/reports',
    name: 'Reports',
    component: () => import('@/views/Reports.vue'),
    meta: { requiresAuth: true, title: '报表中心' }
  },
  {
    path: '/contracts',
    name: 'Contracts',
    component: () => import('@/views/Contracts.vue'),
    meta: { requiresAuth: true, title: '合同管理' }
  },
  {
    path: '/candidates',
    name: 'Candidates',
    component: () => import('@/views/Candidates.vue'),
    meta: { requiresAuth: true, title: '候选人管理' }
  },
  {
    path: '/payments',
    name: 'Payments',
    component: () => import('@/views/Payments.vue'),
    meta: { requiresAuth: true, title: '付款管理' }
  },
  {
    path: '/evaluations',
    name: 'Evaluations',
    component: () => import('@/views/Evaluations.vue'),
    meta: { requiresAuth: true, title: '绩效考核' }
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('@/views/Messages.vue'),
    meta: { requiresAuth: true, title: '消息通知' }
  },
  {
    path: '/logs',
    name: 'Logs',
    component: () => import('@/views/Logs.vue'),
    meta: { requiresAuth: true, title: '操作日志', roles: ['ADMIN'] }
  },
  {
    path: '/workflow',
    name: 'Workflow',
    component: () => import('@/views/Workflow.vue'),
    meta: { requiresAuth: true, title: '工作流定义', roles: ['ADMIN'] }
  },
  {
    path: '/todo',
    name: 'Todo',
    component: () => import('@/views/Todo.vue'),
    meta: { requiresAuth: true, title: '我的待办' }
  },
  {
    path: '/done',
    name: 'Done',
    component: () => import('@/views/Done.vue'),
    meta: { requiresAuth: true, title: '我的已办' }
  },
  {
    path: '/org-chart',
    name: 'OrgChart',
    component: () => import('@/views/OrgChart.vue'),
    meta: { requiresAuth: true, title: '组织架构' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/Settings.vue'),
    meta: { requiresAuth: true, title: '系统设置', roles: ['ADMIN'] }
  },
  {
    path: '/settings/users',
    name: 'Users',
    component: () => import('@/views/Users.vue'),
    meta: { requiresAuth: true, title: '用户管理', roles: ['ADMIN'] }
  },
  {
    path: '/settings/sync',
    name: 'Sync',
    component: () => import('@/views/Sync.vue'),
    meta: { requiresAuth: true, title: '数据同步', roles: ['ADMIN'] }
  },
  {
    path: '/settings/dict',
    name: 'Dict',
    component: () => import('@/views/Dict.vue'),
    meta: { requiresAuth: true, title: '字典管理', roles: ['ADMIN'] }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')

  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
    return
  }

  if (to.meta.roles && userInfo) {
    if (!to.meta.roles.includes(userInfo.role)) {
      next('/dashboard')
      return
    }
  }

  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }

  next()
})

export default router
