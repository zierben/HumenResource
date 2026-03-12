import { ref, watch } from 'vue'

const THEME_KEY = 'hr-theme'
const LAYOUT_KEY = 'hr-layout'

const themes = {
  default: {
    name: '默认蓝紫',
    primary: '#667eea',
    secondary: '#764ba2',
    sidebarBg: 'linear-gradient(180deg, #1a1f36 0%, #252d4a 100%)',
    menuActive: 'linear-gradient(90deg, #667eea 0%, #764ba2 100%)'
  },
  ocean: {
    name: '海洋蓝',
    primary: '#0ea5e9',
    secondary: '#0284c7',
    sidebarBg: 'linear-gradient(180deg, #0c4a6e 0%, #075985 100%)',
    menuActive: 'linear-gradient(90deg, #0ea5e9 0%, #0284c7 100%)'
  },
  forest: {
    name: '森林绿',
    primary: '#22c55e',
    secondary: '#16a34a',
    sidebarBg: 'linear-gradient(180deg, #14532d 0%, #166534 100%)',
    menuActive: 'linear-gradient(90deg, #22c55e 0%, #16a34a 100%)'
  },
  sunset: {
    name: '日落橙',
    primary: '#f97316',
    secondary: '#ea580c',
    sidebarBg: 'linear-gradient(180deg, #431407 0%, #7c2d12 100%)',
    menuActive: 'linear-gradient(90deg, #f97316 0%, #ea580c 100%)'
  },
  dark: {
    name: '深邃黑',
    primary: '#6366f1',
    secondary: '#4f46e5',
    sidebarBg: 'linear-gradient(180deg, #0f0f0f 0%, #1a1a1a 100%)',
    menuActive: 'linear-gradient(90deg, #6366f1 0%, #4f46e5 100%)'
  }
}

const currentTheme = ref(localStorage.getItem(THEME_KEY) || 'default')
const menuPosition = ref(localStorage.getItem(LAYOUT_KEY) || 'left')

watch(currentTheme, (val) => {
  localStorage.setItem(THEME_KEY, val)
  applyTheme(val)
}, { immediate: true })

watch(menuPosition, (val) => {
  localStorage.setItem(LAYOUT_KEY, val)
})

function applyTheme(themeName) {
  const theme = themes[themeName]
  if (!theme) return
  
  document.documentElement.style.setProperty('--primary-color', theme.primary)
  document.documentElement.style.setProperty('--secondary-color', theme.secondary)
  document.documentElement.style.setProperty('--primary-light', theme.primary + '20')
}

export function useTheme() {
  return {
    themes,
    currentTheme,
    menuPosition,
    themeList: Object.entries(themes).map(([key, val]) => ({ key, ...val })),
    setTheme: (name) => { currentTheme.value = name },
    setMenuPosition: (pos) => { menuPosition.value = pos }
  }
}
