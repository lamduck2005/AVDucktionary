<script setup>
import { ref } from 'vue'
import { useUser } from '@/services/userService'

const { user, updateProfile, updatePassword } = useUser()
const fullName = ref(user.value.fullName)
const avatarPreview = ref(
  user.value.avatarUrl ||
    'https://cdn-useast1.kapwing.com/static/templates/128x128-custom-discord-emote-maker-regular-d958dec0.webp',
)

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const message = ref('')
const error = ref('')

const handleUpdateProfile = async () => {
  try {
    await updateProfile(fullName.value, avatarPreview.value)
    message.value = 'Cập nhật thông tin thành công!'
    error.value = ''
  } catch (err) {
    error.value = err
    message.value = ''
  }
}

const handleChangePassword = async () => {
  if (newPassword.value !== confirmPassword.value) {
    error.value = 'Mật khẩu mới không khớp!'
    message.value = ''
    return
  }
  try {
    await updatePassword(oldPassword.value, newPassword.value)
    message.value = 'Đổi mật khẩu thành công!'
    error.value = ''
    oldPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  } catch (err) {
    error.value = err
    message.value = ''
  }
}
</script>
<template>
  <div class="container">
    <h2 class="text-center my-4">Thông tin tài khoản</h2>

    <div class="card p-4 shadow">
      <div class="text-center">
        <img :src="avatarPreview" class="rounded-circle" width="120" height="120" />
        <input type="file" class="form-control mt-2" @change="handleAvatarUpload" />
      </div>

      <form @submit.prevent="handleUpdateProfile">
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input type="email" class="form-control" v-model="user.email" disabled />
        </div>
        <div class="mb-3">
          <label class="form-label">Họ và tên</label>
          <input type="text" class="form-control" v-model="fullName" required />
        </div>
        <button type="submit" class="btn btn-primary w-100">Cập nhật thông tin</button>
      </form>

      <hr />

      <h3 class="text-center">Đổi mật khẩu</h3>
      <form @submit.prevent="handleChangePassword">
        <div class="mb-3">
          <label class="form-label">Mật khẩu cũ</label>
          <input type="password" class="form-control" v-model="oldPassword" required />
        </div>
        <div class="mb-3">
          <label class="form-label">Mật khẩu mới</label>
          <input type="password" class="form-control" v-model="newPassword" required />
        </div>
        <div class="mb-3">
          <label class="form-label">Xác nhận mật khẩu mới</label>
          <input type="password" class="form-control" v-model="confirmPassword" required />
        </div>
        <button type="submit" class="btn btn-danger w-100">Đổi mật khẩu</button>
      </form>

      <p v-if="message" class="text-success text-center mt-3">{{ message }}</p>
      <p v-if="error" class="text-danger text-center mt-3">{{ error }}</p>
    </div>
  </div>
</template>
