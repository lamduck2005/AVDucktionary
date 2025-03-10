<script setup>
import { useUser } from '@/services/userService'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'

const { user, isLoggedIn, logout } = useUser()
const router = useRouter()

console.log('User:', user.value)
const handleLogout = () => {
  logout()
  Swal.fire({
    icon: 'success',
    title: 'Đăng xuất thành công, đang chuyển hướng tới trang đang nhập!',
    showConfirmButton: true,
    timer: 2000,
    confirmButtonText: 'Chuyển hướng ngay',
    timerProgressBar: true,
  }).then(() => {
    router.push('/login').then(() => {
      window.location.reload()
    })
  })
}
</script>

<template>
  <header>
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
      <div class="container-fluid">
        <router-link class="navbar-brand" to="/">
          <img
            src="./assets/logo.png"
            alt="Logo"
            width="32"
            height="32"
            class="d-inline-block align-text-top"
          />
          AVDucktionary
        </router-link>
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav">
            <li class="nav-item">
              <router-link class="nav-link active" aria-current="page" to="/">Từ điển</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/favorite">Từ đã thích</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/note">Từ đã ghi chú</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/history">Lịch sử</router-link>
            </li>
          </ul>
        </div>

        <div v-if="isLoggedIn" class="d-flex align-items-center">
          <span class="me-3">Xin chào, {{ user?.fullName }}</span>
          <router-link class="btn btn-outline-primary btn-sm me-2" to="/profile"
            >Tài khoản</router-link
          >
          <button class="btn btn-outline-danger btn-sm" @click="handleLogout">Đăng xuất</button>
        </div>

        <div v-else>
          <router-link class="btn btn-primary me-2" to="/login">Đăng nhập</router-link>
          <router-link class="btn btn-secondary" to="/register">Đăng ký</router-link>
        </div>
      </div>
    </nav>
  </header>

  <main class="container mt-4">
    <router-view></router-view>
    <!-- Đây là nơi hiển thị component tương ứng với route -->
  </main>

  <footer>
    <div class="container-fluid bg-body-tertiary text-center">
      <p class="text-muted">© 2025 AVDict. All rights reserved.</p>
    </div>
  </footer>
</template>
