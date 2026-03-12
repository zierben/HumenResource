import { ref } from 'vue'
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'
import { ElNotification } from 'element-plus'

const stompClient = ref(null)
const connected = ref(false)
const callbacks = []

export function useWebSocket() {
  const connect = (userId) => {
    if (connected.value) return
    
    const socket = new SockJS('http://localhost:8080/ws')
    stompClient.value = Stomp.over(socket)
    
    stompClient.value.connect({}, (frame) => {
      connected.value = true
      console.log('WebSocket Connected:', frame)
      
      stompClient.value.subscribe(`/user/${userId}/queue/messages`, (message) => {
        const data = JSON.parse(message.body)
        handleIncomingMessage(data)
      })
      
      stompClient.value.subscribe('/topic/messages', (message) => {
        const data = JSON.parse(message.body)
        handleIncomingMessage(data)
      })
    }, (error) => {
      console.error('WebSocket Error:', error)
      connected.value = false
    })
  }

  const disconnect = () => {
    if (stompClient.value && connected.value) {
      stompClient.value.disconnect(() => {
        connected.value = false
        console.log('WebSocket Disconnected')
      })
    }
  }

  const handleIncomingMessage = (data) => {
    callbacks.forEach(cb => cb(data))
    
    if (data.type === 'notification') {
      ElNotification({
        title: data.data?.title || '新消息',
        message: data.data?.content || '',
        type: 'info',
        duration: 3000
      })
    }
  }

  const onMessage = (callback) => {
    callbacks.push(callback)
  }

  const offMessage = (callback) => {
    const index = callbacks.indexOf(callback)
    if (index > -1) {
      callbacks.splice(index, 1)
    }
  }

  return {
    connect,
    disconnect,
    connected,
    onMessage,
    offMessage
  }
}
