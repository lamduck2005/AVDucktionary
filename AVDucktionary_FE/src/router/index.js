import { createRouter, createWebHistory } from 'vue-router'

// Import các component đúng cách
import Dictionary from '@/components/Dictionary.vue'
import LoginForm from '@/components/LoginForm.vue'
import RegisterForm from '@/components/RegisterForm.vue'
import Profile from '@/components/Profile.vue'
import { showToast } from '@/utils/SwalHelper'
import Note from '@/components/Note.vue'
import History from '@/components/History.vue'
import Favorite from '@/components/Favorite.vue'

const routes = [
  { path: '/', component: Dictionary },
  { path: '/login', component: LoginForm },
  { path: '/register', component: RegisterForm },
  { path: '/profile', component: Profile },
  { path: '/favorite', component: Favorite },
  { path: '/note', component: Note },
  { path: '/history', component: History },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

//middleware
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('user') // Lấy user từ localStorage
  const publicPages = ['/login', '/register'] // Các trang KHÔNG cần đăng nhập

  if (!isLoggedIn && !publicPages.includes(to.path)) {
    showToast('warning', 'Vui lòng đăng nhập để sử dụng chức năng này!')
    next('/login') // Chưa đăng nhập → Chuyển về Login
  } else {
    next() // Cho phép vào trang
  }
})

export default router
