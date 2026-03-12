<template>
  <div class="login-container">
    <div class="bg-animation">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
    </div>

    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-logo">
          <div class="logo-icon">HR</div>
        </div>
        <h1>HR外包人力管理系统</h1>
        <p>一站式外包人力资源数字化解决方案</p>
        <div class="brand-features">
          <div class="feature-item">
            <el-icon><User /></el-icon>
            <span>人员全生命周期管理</span>
          </div>
          <div class="feature-item">
            <el-icon><Document /></el-icon>
            <span>智能审批流程引擎</span>
          </div>
          <div class="feature-item">
            <el-icon><TrendCharts /></el-icon>
            <span>多维度数据报表</span>
          </div>
          <div class="feature-item">
            <el-icon><Connection /></el-icon>
            <span>第三方系统集成</span>
          </div>
        </div>
      </div>
    </div>

    <div class="login-form-wrapper">
      <div class="login-box">
        <div class="login-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号信息</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password @keyup.enter="handleLogin">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          </div>
          <el-form-item>
            <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="test-accounts" v-if="showTestAccounts">
          <div class="test-title">快速登录 (仅测试环境)</div>
          <div class="account-list">
            <div 
              v-for="account in testAccounts" 
              :key="account.username" 
              class="account-item"
              @click="quickLogin(account)"
            >
              <div class="account-avatar" :style="{ background: account.color }">
                {{ account.name.charAt(0) }}
              </div>
              <div class="account-info">
                <div class="account-name">{{ account.name }}</div>
                <div class="account-role">{{ account.roleName }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="copyright">
        <p>© 2024 HR Outsourcing Management System</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)
const showTestAccounts = ref(import.meta.env.DEV)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const testAccounts = [
  { username: 'admin', password: 'admin123', name: '系统管理员', roleName: '总经理 (GM)', color: '#f56c6c' },
  { username: 'ceo', password: 'admin123', name: '张伟华', roleName: 'CEO', color: '#f56c6c' },
  { username: 'vp_tech', password: 'admin123', name: '李明强', roleName: '副总-技术', color: '#e6a23c' },
  { username: 'vp_hr', password: 'admin123', name: '王晓红', roleName: '副总-人事', color: '#e6a23c' },
  { username: 'dept_dev', password: 'admin123', name: '陈志远', roleName: '部门长-研发', color: '#409eff' },
  { username: 'hr001', password: 'admin123', name: '孙丽娜', roleName: 'HR专员', color: '#909399' }
]

onMounted(() => {
  const savedUsername = localStorage.getItem('hr_remembered_username')
  if (savedUsername) {
    form.username = savedUsername
    rememberMe.value = true
  }
})

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (rememberMe.value) {
    localStorage.setItem('hr_remembered_username', form.username)
  } else {
    localStorage.removeItem('hr_remembered_username')
  }

  loading.value = true
  try {
    const result = await userStore.login(form.username, form.password)
    if (result.success) {
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } finally {
    loading.value = false
  }
}

const quickLogin = async (account) => {
  form.username = account.username
  form.password = account.password
  loading.value = true
  try {
    const result = await userStore.login(account.username, account.password)
    if (result.success) {
      ElMessage.success(`欢迎回来，${account.name}`)
      router.push('/')
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #1a1f36 0%, #252d4a 100%);
}

.bg-animation {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  animation: float 20s infinite ease-in-out;
}

.shape-1 {
  width: 600px;
  height: 600px;
  background: #667eea;
  top: -200px;
  left: -200px;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: #764ba2;
  bottom: -100px;
  right: -100px;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(50px, -50px) rotate(180deg); }
}

.login-brand {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  z-index: 1;
}

.brand-content {
  color: white;
  text-align: center;
}

.brand-logo { margin-bottom: 30px; }

.logo-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: bold;
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.4);
}

.brand-content h1 {
  font-size: 32px;
  font-weight: 600;
  margin: 0 0 12px;
}

.brand-content > p {
  font-size: 16px;
  opacity: 0.8;
  margin: 0 0 40px;
}

.brand-features {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  max-width: 400px;
  margin: 0 auto;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  font-size: 14px;
}

.feature-item .el-icon {
  font-size: 20px;
  color: #667eea;
}

.login-form-wrapper {
  width: 420px;
  background: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 1;
  overflow-y: auto;
}

.login-box {
  max-width: 320px;
  margin: 0 auto;
  width: 100%;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 24px;
  color: #1f2937;
  margin: 0 0 8px;
}

.login-header p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.login-form { margin-top: 20px; }

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.test-accounts {
  margin-top: 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.test-title {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 12px;
  text-align: center;
}

.account-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.account-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #e2e8f0;
}

.account-item:hover {
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
  transform: translateX(4px);
}

.account-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
}

.account-info {
  flex: 1;
  min-width: 0;
}

.account-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.account-role {
  font-size: 12px;
  color: #6b7280;
  margin-top: 2px;
}

.copyright {
  text-align: center;
  margin-top: 30px;
  font-size: 12px;
  color: #9ca3af;
}

@media (max-width: 1024px) {
  .login-brand { display: none; }
  .login-form-wrapper { width: 100%; max-width: 480px; margin: 0 auto; }
}

@media (max-width: 480px) {
  .login-form-wrapper { padding: 30px 20px; }
  .brand-features { grid-template-columns: 1fr; }
}
</style>
