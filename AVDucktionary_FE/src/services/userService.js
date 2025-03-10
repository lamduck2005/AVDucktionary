import { ref } from 'vue'
import { login, updateUserInfo, changePassword, register } from './api'

const user = ref(JSON.parse(localStorage.getItem('user')) || null)

export const useUser = () => {
  const isLoggedIn = ref(!!user.value)

  const registerUser = async (email, password, fullName) => {
    try {
      await register(email, password, fullName)
    } catch (error) {
      throw error.response?.data || 'Lỗi đăng ký!'
    }
  }

  const loginUser = async (email, password) => {
    try {
      const response = await login(email, password)
      user.value = response.data
      isLoggedIn.value = true
      localStorage.setItem('user', JSON.stringify(user.value))
    } catch (error) {
      throw error.response?.data || 'Lỗi đăng nhập!'
    }
  }

  const logout = () => {
    user.value = null
    isLoggedIn.value = false
    localStorage.removeItem('user')
  }

  const updateProfile = async (fullName, avatarUrl) => {
    try {
      await updateUserInfo(user.value.email, fullName, avatarUrl)
      user.value.fullName = fullName
      user.value.avatarUrl = avatarUrl
      localStorage.setItem('user', JSON.stringify(user.value))
    } catch (error) {
      throw error.response?.data || 'Lỗi cập nhật thông tin!'
    }
  }

  const updatePassword = async (oldPassword, newPassword) => {
    try {
      return await changePassword(user.value.email, oldPassword, newPassword)
    } catch (error) {
      throw error.response?.data || 'Lỗi đổi mật khẩu!'
    }
  }

  return { user, isLoggedIn, registerUser, loginUser, logout, updateProfile, updatePassword }
}
