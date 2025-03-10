<script setup>
import { onMounted, ref, computed, reactive } from 'vue'
import {
  fetchWords,
  suggestWords,
  fetchWordDetails,
  addFavorite,
  removeFavorite,
  getFavorites,
  addNote,
  getNotes,
  removeNotes,
  removeNote,
} from '@/services/api'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import * as bootstrap from 'bootstrap'
import { useUser } from '@/services/userService'
import router from '@/router'
import Swal from 'sweetalert2'
import { showToast } from '@/utils/SwalHelper'

const { user } = useUser()
const userId = user.value.id
const words = ref([])
const suggestions = ref([])
const selectedWord = ref(null)
const searchTerm = ref('')
const isLoading = ref(false) // ✅ Trạng thái loading

const currentPage = ref(0) // API trả về page bắt đầu từ 0
const totalPages = ref(1)
const pageSize = 30

const favoriteWords = ref([])

const notes = ref([])
const newNote = ref('')

//func
const detailWord = async (word) => {
  selectedWord.value = await fetchWordDetails(word.id, userId)
  loadNotes()
  const modal = new bootstrap.Modal(document.getElementById('wordModal'))
  modal.show()
}

const loadWords = async (page = 0, keyword = '') => {
  isLoading.value = true
  const response = await fetchWords(keyword, page, pageSize)
  words.value = response.words
  totalPages.value = response.totalPages
  currentPage.value = response.currentPage
  isLoading.value = false

  favoriteWords.value = (await getFavorites(userId)).map((fav) => fav.word.id)
}

const fetchSuggestions = async () => {
  if (searchTerm.value.length < 2) {
    suggestions.value = []
    return
  }
  isLoading.value = true
  suggestions.value = await suggestWords(searchTerm.value)
  isLoading.value = false
}

const selectWord = async (word) => {
  searchTerm.value = word.word
  suggestions.value = [] // Ẩn danh sách gợi ý
  detailWord(word)
}

const searchWord = async () => {
  if (searchTerm.value.length < 2) return
  await loadWords(0, searchTerm.value)
}

const clearSearch = () => {
  searchTerm.value = ''
  suggestions.value = []
  loadWords(currentPage.value)
}

const addToFavorites = async (wordId) => {
  await addFavorite(userId, wordId)
  favoriteWords.value.push(wordId)
  showToast('success', 'Đã thêm từ vào danh sách yêu thích')
}

const removeFromFavorites = async (wordId) => {
  await removeFavorite(userId, wordId)
  favoriteWords.value = favoriteWords.value.filter((id) => id !== wordId)
  showToast('success', 'Đã xoá từ khỏi danh sách yêu thích')
}

//phân trang
const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    loadWords(page)
  }
}

const displayedPages = computed(() => {
  const total = totalPages.value
  const current = currentPage.value
  const pagesArr = []

  if (total <= 10) {
    for (let i = 0; i < total; i++) {
      pagesArr.push({ type: 'page', number: i })
    }
  } else {
    pagesArr.push({ type: 'page', number: 0 })
    if (current > 2) {
      pagesArr.push({ type: 'gap' })
    }
    const start = Math.max(1, current - 1)
    const end = Math.min(total - 1, current + 2)
    for (let i = start; i < end; i++) {
      pagesArr.push({ type: 'page', number: i })
    }
    if (end < total - 1) {
      pagesArr.push({ type: 'gap' })
    }
    pagesArr.push({ type: 'page', number: total - 1 })
  }
  return pagesArr
})

//note
const loadNotes = async () => {
  notes.value = await getNotes(userId, selectedWord.value.id)
  console.log(notes.value)
}

const saveNote = async () => {
  try {
    let message = ''
    if (!newNote.value) throw new Error('Ghi chú không được để trống')
    if (newNote.value.length > 1000) throw new Error('Ghi chú không được quá 1000 ký tự')
    if (!favoriteWords.value.includes(selectedWord.value.id)) {
      await addToFavorites(selectedWord.value.id)
      message = '\n(tự động thêm từ vào danh sách yêu thích)'
    }
    await addNote(userId, selectedWord.value.id, newNote.value)
    notes.value.push({ content: newNote.value })
    newNote.value = ''
    // loadNotes()
    showToast('success', 'Đã thêm ghi chú ' + message)
  } catch (error) {
    showToast('error', error.message)
  }
}

const deleteNote = async (noteId) => {
  await removeNote(noteId)
  // loadNotes()
  notes.value = notes.value.filter((n) => n.id !== noteId) // Cập nhật danh sách ghi chú
}

const deleteNotes = async () => {
  Swal.fire({
    title: 'Xác nhận xoá',
    text: 'Bạn có chắc chắn muốn xoá tất cả ghi chú?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Xoá',
    cancelButtonText: 'Hủy',
  }).then(async (result) => {
    if (result.isConfirmed) {
      await removeNotes(userId, selectedWord.value.id)
      notes.value = []
      showToast('success', 'Đã xoá tất cả ghi chú')
    }
  })
}

onMounted(() => {
  loadWords()
})
</script>

<template>
  <div>
    <!-- ✅ Ô tìm kiếm với nút "X" và nút tìm kiếm -->
    <div class="mb-3 position-relative">
      <div class="input-group">
        <input
          v-model="searchTerm"
          @input="fetchSuggestions"
          type="text"
          class="form-control"
          placeholder="Nhập từ cần tìm..."
        />
        <button v-if="searchTerm" class="btn btn-outline-secondary" @click="clearSearch">✖</button>
        <button class="btn btn-primary" @click="searchWord">🔍</button>
      </div>
      <ul v-if="suggestions.length" class="list-group position-absolute w-100 shadow">
        <li
          v-for="word in suggestions"
          :key="word.id"
          class="list-group-item list-group-item-action"
          @click="selectWord(word)"
        >
          {{ word.word }}
        </li>
      </ul>
      <p
        v-if="!isLoading && searchTerm.length >= 2 && suggestions.length === 0"
        class="text-muted mt-2"
      >
        Không tìm thấy từ nào
      </p>
    </div>

    <!-- ✅ Hiển thị hiệu ứng loading -->
    <div v-if="isLoading" class="text-center my-3">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải...</span>
      </div>
    </div>

    <!-- ✅ Bảng danh sách từ -->
    <div class="table-responsive" v-if="words.length > 0">
      <table class="table table-striped table-hover table-bordered">
        <thead class="table-dark">
          <tr>
            <th>ID</th>
            <th>Từ</th>
            <th>Phiên âm</th>
            <th>Loại từ</th>
            <th>Chức năng</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="word in words" :key="word.id">
            <td>{{ word.id }}</td>
            <td>{{ word.word }}</td>
            <td>{{ word.phonetic }}</td>
            <td>
              <ul>
                <li v-for="type in word.wordTypes" :key="type.id">
                  {{ type.type }}
                </li>
              </ul>
            </td>
            <td>
              <button class="btn btn-outline-info btn-sm" @click="detailWord(word)">
                Chi tiết
              </button>
              <button
                v-if="!favoriteWords.includes(word.id)"
                @click="addToFavorites(word.id)"
                class="btn btn-outline-danger btn-sm"
              >
                ❤️ Thích
              </button>
              <button v-else @click="removeFromFavorites(word.id)" class="btn btn-danger btn-sm">
                ❌ Bỏ thích
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ✅ Phân trang -->
    <div>
      <nav aria-label="Page navigation example">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: currentPage === 0 }">
            <a class="page-link" @click.prevent="changePage(currentPage - 1)">Previous</a>
          </li>
          <li
            v-for="item in displayedPages"
            :key="item.type === 'page' ? item.number : 'gap-' + item.number"
            :class="{
              'page-item': true,
              active: item.type === 'page' && currentPage === item.number,
            }"
          >
            <a
              v-if="item.type === 'page'"
              class="page-link"
              @click.prevent="changePage(item.number)"
              style="cursor: pointer"
            >
              {{ item.number + 1 }}
            </a>
            <span v-else class="page-link">...</span>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
            <a class="page-link" @click.prevent="changePage(currentPage + 1)">Next</a>
          </li>
        </ul>
      </nav>
    </div>
  </div>

  <!-- ✅ Modal hiển thị chi tiết từ -->
  <div
    class="modal fade"
    id="wordModal"
    tabindex="-1"
    aria-labelledby="wordModalLabel"
    aria-hidden="true"
  >
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="wordModalLabel">{{ selectedWord?.word }}</h5>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body">
          <p><strong>ID:</strong> {{ selectedWord?.id }}</p>
          <p>
            <strong>Từ tiếng Anh:</strong>
            <span class="fw-bold text-primary">{{ selectedWord?.word }}</span>
          </p>
          <p><strong>Phiên âm:</strong> {{ selectedWord?.phonetic }}</p>
          <p><strong>Loại từ và nghĩa tiếng Việt:</strong></p>
          <ul>
            <li v-for="type in selectedWord?.wordTypes" :key="type.id">
              {{ type.type }}
              <ul v-for="meaning in type.meanings" :key="meaning.id">
                <li>{{ meaning.meaning }}</li>
              </ul>
            </li>
          </ul>
          <p>
            <strong>Ví dụ sử dụng:</strong>
            {{
              selectedWord?.exampleSentences
                ? selectedWord?.exampleSentences
                : 'Không có ví dụ nào!'
            }}
          </p>
          <p>
            <strong>Thành ngữ:</strong>
            {{ selectedWord?.idioms ? selectedWord?.idioms : 'Không có thành ngữ nào!' }}
          </p>
          <p><strong>Ghi chú của bạn:</strong></p>
          <ul class="list-group">
            <li
              v-for="note in notes"
              :key="note.id"
              class="list-group-item d-flex justify-content-between align-items-center"
            >
              <span class="text-break">{{ note.content }}</span>
              <button class="btn btn-sm btn-outline-danger" @click="deleteNote(note.id)">❌</button>
            </li>
          </ul>
          <input v-model="newNote" class="form-control mt-2" placeholder="Nhập ghi chú..." />
          <button class="btn btn-success mt-2" @click="saveNote">Lưu ghi chú</button>
          <button class="btn btn-danger mt-2" @click="deleteNotes">Xóa tất cả ghi chú</button>
        </div>
        <div class="modal-footer">
          <button
            v-if="!favoriteWords.includes(selectedWord?.id)"
            @click="addToFavorites(selectedWord?.id)"
            class="btn btn-outline-danger"
          >
            ❤️ Thích
          </button>
          <button v-else @click="removeFromFavorites(selectedWord?.id)" class="btn btn-danger">
            ❌ Bỏ thích
          </button>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.list-group {
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
}
.pagination .page-item.disabled .page-link {
  pointer-events: none;
  cursor: default;
}
</style>
