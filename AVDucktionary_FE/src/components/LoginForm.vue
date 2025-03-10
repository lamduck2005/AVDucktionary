<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-12 col-md-8 col-lg-6">
        <transition name="fade">
          <div class="card shadow p-4 animated-login">
            <h2 class="text-center mb-4">Đăng nhập</h2>
            <form @submit.prevent="handleLogin">
              <div class="mb-3">
                <label class="form-label">Email</label>
                <input
                  type="email"
                  class="form-control"
                  v-model="email"
                  placeholder="Nhập email"
                  required
                />
              </div>
              <div class="mb-3">
                <label class="form-label">Mật khẩu</label>
                <input
                  type="password"
                  class="form-control"
                  v-model="password"
                  placeholder="Nhập mật khẩu"
                  required
                />
              </div>
              <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
            </form>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import router from '@/router'
import { useUser } from '@/services/userService'
import Swal from 'sweetalert2'
import { showToast } from '@/utils/SwalHelper'
const email = ref('')
const password = ref('')
const { loginUser } = useUser()

const handleLogin = async () => {
  try {
    await loginUser(email.value, password.value)
    // showToast('success', 'Đăng nhập thành công, chuyển hướng tới trang chủ!', 5000)
    Swal.fire({
      icon: 'success',
      title: 'Đăng nhập thành công, đang chuyển hướng tới trang chủ!',
      showConfirmButton: true,
      timer: 2000,
      confirmButtonText: 'Chuyển hướng ngay',
      timerProgressBar: true,
    }).then(() => {
      router.push('/').then(() => {
        window.location.reload()
      })
    })
  } catch (error) {
    showToast('error', error || 'Đăng nhập thất bại, vui lòng kiểm tra lại thông tin đăng nhập!')
  }
}
</script>

<style scoped>
.animated-login {
  animation: slideIn 0.8s ease-out;
}

@keyframes slideIn {
  from {
    transform: translateY(-20%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
