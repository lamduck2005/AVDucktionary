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
  getNotedWords,
  removeAllNotes,
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
const notedWords = ref([])
const suggestions = ref([])
const selectedWord = ref(null)
const searchTerm = ref('')
const isLoading = ref(false) // ✅ Trạng thái loading

const currentPage = ref(0) // API trả về page bắt đầu từ 0

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

const loadWords = async () => {
  isLoading.value = true
  const response = await getNotedWords(userId)
  notedWords.value = response
  isLoading.value = false

  favoriteWords.value = (await getFavorites(userId)).map((fav) => fav.word)
}

const filteredWords = computed(() => {
  return notedWords.value.filter((word) =>
    word.word.toLowerCase().includes(searchTerm.value.toLowerCase()),
  )
})

const removeFromFavorites = async (wordId) => {
  await removeFavorite(userId, wordId)
  favoriteWords.value = favoriteWords.value.filter((word) => word.id !== wordId)
  showToast('success', 'Đã xoá từ khỏi danh sách yêu thích')
}

// New addToFavorites function (like in FavoriteWords.vue)
const addToFavorites = async (word) => {
  await addFavorite(userId, word.id)
  favoriteWords.value.push(word)
  showToast('success', 'Đã thêm từ vào danh sách yêu thích')
}

// note
const loadNotes = async () => {
  notes.value = await getNotes(userId, selectedWord.value.id)
  console.log(notes.value)
}

const saveNote = async () => {
  try {
    let message = ''
    if (!newNote.value) throw new Error('Ghi chú không được để trống')
    if (newNote.value.length > 1000) throw new Error('Ghi chú không được quá 1000 ký tự')
    if (!favoriteWords.value.some((fav) => fav.id === selectedWord.value.id)) {
      await addToFavorites(selectedWord.value)
      message = '\n(tự động thêm từ vào danh sách yêu thích)'
    }
    await addNote(userId, selectedWord.value.id, newNote.value)
    if (!notedWords.value.some((word) => word.id === selectedWord.value.id)) {
      notedWords.value.push(selectedWord.value)
    }
    loadNotes()
    notes.value.push({ content: newNote.value })
    newNote.value = ''
    showToast('success', 'Đã thêm ghi chú ' + message)
  } catch (error) {
    showToast('error', error.message)
  }
}

const deleteNote = async (noteId) => {
  await removeNote(noteId)
  notes.value = notes.value.filter((n) => n.id !== noteId) // Cập nhật danh sách ghi chú
}

const deleteNotes = async (wordId = selectedWord.value.id) => {
  Swal.fire({
    title: 'Xác nhận xoá',
    text: 'Bạn có chắc chắn muốn xoá tất cả ghi chú của từ này?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Xoá',
    cancelButtonText: 'Hủy',
  }).then(async (result) => {
    if (result.isConfirmed) {
      await removeNotes(userId, wordId)
      notedWords.value = notedWords.value.filter((word) => word.id !== wordId)
      notes.value = []
      showToast('success', 'Đã xoá tất cả ghi chú liên quan tới từ này')
    }
  })
}

const deleteAllNotes = () => {
  Swal.fire({
    title: 'Xác nhận xoá',
    text: 'Bạn có chắc chắn muốn xoá tất cả ghi chú?',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Xoá',
    cancelButtonText: 'Hủy',
  }).then(async (result) => {
    if (!result.isConfirmed) return
    Swal.fire({
      title: 'Chắc chắn xoá',
      text: 'Không thể khôi phục các ghi chú đã xoá, vẫn tiếp tục?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Xoá',
      cancelButtonText: 'Hủy',
    }).then(async (result) => {
      if (result.isConfirmed) {
        await removeAllNotes(userId)
        notedWords.value = []
        showToast('success', 'Đã xoá tất cả ghi chú')
      }
    })
  })
}

onMounted(() => {
  loadWords()
})
</script>

<template>
  <div>
    <h2 class="text-center">Danh sách từ đã ghi chú</h2>
    <!-- ✅ Ô tìm kiếm với nút "X" và nút tìm kiếm -->
    <div class="mb-3 position-relative">
      <div class="input-group">
        <input
          v-model="searchTerm"
          type="text"
          class="form-control"
          placeholder="Nhập từ để tìm..."
        />
        <button v-if="searchTerm" class="btn btn-outline-secondary" @click="searchTerm = ''">
          ✖
        </button>
      </div>
    </div>

    <div class="text-end mt-3">
      <button class="btn btn-danger" @click="deleteAllNotes">Xóa tất cả ghi chú</button>
    </div>

    <!-- ✅ Hiển thị hiệu ứng loading -->
    <div v-if="isLoading" class="text-center my-3">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải...</span>
      </div>
    </div>

    <!-- ✅ Bảng danh sách từ -->
    <div class="table-responsive" v-if="filteredWords.length > 0">
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
          <tr v-for="word in filteredWords" :key="word.id">
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
              <button @click="deleteNotes(word.id)" class="btn btn-outline-danger btn-sm">
                Xoá ghi chú
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <p v-if="filteredWords.length === 0 && !isLoading" class="text-muted text-center">
    Không có từ yêu thích nào.
  </p>

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
            <li v-for="type in selectedWord?.wordTypes || []" :key="type.id">
              {{ type.type }}
              <ul>
                <li v-for="meaning in type.meanings || []" :key="meaning.id">
                  {{ meaning.meaning }}
                </li>
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
          <button class="btn btn-danger mt-2" @click="deleteNotes(selectedWord?.id)">
            Xóa tất cả ghi chú
          </button>
        </div>
        <div class="modal-footer">
          <button
            v-if="!favoriteWords.some((fav) => fav.id === selectedWord?.id)"
            @click="addToFavorites(selectedWord)"
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
