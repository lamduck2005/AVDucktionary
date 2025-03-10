<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-12 col-md-8 col-lg-6">
        <transition name="fade">
          <div class="card shadow p-4 animated-register">
            <h2 class="text-center mb-3">Đăng ký</h2>
            <form @submit.prevent="handleRegister">
              <div class="mb-3">
                <label class="form-label">Họ và tên</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="fullName"
                  placeholder="Nhập họ và tên"
                  required
                />
              </div>
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
              <button type="submit" class="btn btn-primary w-100">Đăng ký</button>
            </form>
            <div v-if="message" class="alert alert-success mt-3" role="alert">
              {{ message }}
            </div>
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
import { showToast } from '@/utils/SwalHelper'

const email = ref('')
const password = ref('')
const fullName = ref('')
const message = ref('')

const { registerUser } = useUser()

const handleRegister = async () => {
  try {
    await registerUser(email.value, password.value, fullName.value)
    showToast('success', 'Đăng ký thành công, chuyển hướng tới trang đăng nhập!', 5000)
    router.push('/login')
  } catch (err) {
    showToast('error', err, 5000)
  }
}
</script>

<style scoped>
.animated-register {
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
