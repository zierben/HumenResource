import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')

  async function login(username, password) {
    const res = await request.post('/auth/login', { username, password })
    if (res.code === 200) {
      token.value = res.data.token
      userInfo.value = {
        username: res.data.username,
        realName: res.data.realName,
        role: res.data.role
      }
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      return { success: true }
    }
    return { success: false, message: res.message }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  async function fetchCurrentUser() {
    try {
      const res = await request.get('/auth/current')
      if (res.code === 200) {
        userInfo.value = {
          username: res.data.username,
          realName: res.data.realName,
          role: res.data.role
        }
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      }
    } catch (e) {
      logout()
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    logout,
    fetchCurrentUser
  }
})
