<script setup>
import { ref, onMounted } from 'vue'
import {
  getUserHistory,
  fetchWordDetails,
  getNotes,
  getFavorites,
  addFavorite,
  removeFavorite,
  addNote,
  removeNote,
  removeNotes,
  removeAllHistory,
  removeHistory,
} from '@/services/api'
import { useUser } from '@/services/userService'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import * as bootstrap from 'bootstrap'
import Swal from 'sweetalert2'
import { showToast } from '@/utils/SwalHelper'

const { user } = useUser()
const userId = user.value.id

const history = ref([])
const limit = ref(50)
const selectedWord = ref(null)
const favoriteWords = ref([])
const notes = ref([])
const newNote = ref('')
const isLoading = ref(false)

// Load lịch sử và favorites (dạng full object)
const loadHistory = async () => {
  history.value = await getUserHistory(userId, limit.value)
  favoriteWords.value = (await getFavorites(userId)).map((fav) => fav.word)
}

const deleteAllHistory = async () => {
  Swal.fire({
    title: 'Xác nhận xoá',
    text: 'Bạn có chắc chắn muốn xoá tất cả lịch sử?',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Xoá',
    cancelButtonText: 'Hủy',
  }).then(async (result) => {
    if (!result.isConfirmed) return
    Swal.fire({
      title: 'Chắc chắn xoá',
      text: 'Không thể khôi phục sau khi xoá, vẫn tiếp tục?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Xoá',
      cancelButtonText: 'Hủy',
    }).then(async (result) => {
      if (result.isConfirmed) {
        await removeAllHistory(userId)
        history.value = []
        showToast('success', 'Đã xoá tất cả lịch sử')
      }
    })
  })
}

const deleteHistory = async (wordId) => {
  await removeHistory(userId, wordId)
  history.value = history.value.filter((entry) => entry.word.id !== wordId)
  showToast('success', 'Đã xoá lịch sử')
}
// Xem chi tiết từ và hiển thị modal
const detailWord = async (wordId) => {
  selectedWord.value = await fetchWordDetails(wordId, userId)
  await loadNotes()
  const modal = new bootstrap.Modal(document.getElementById('wordModal'))
  modal.show()
}

// Favorite functions (sử dụng đối tượng từ như FavoriteWords.vue)
const addToFavorites = async (word) => {
  await addFavorite(userId, word.id)
  favoriteWords.value.push(word)
  showToast('success', 'Đã thêm từ vào danh sách yêu thích')
}

const removeFromFavorites = async (wordId) => {
  await removeFavorite(userId, wordId)
  favoriteWords.value = favoriteWords.value.filter((word) => word.id !== wordId)
  showToast('success', 'Đã xoá từ khỏi danh sách yêu thích')
}

// Note functions
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
    notes.value.push({ content: newNote.value })
    newNote.value = ''
    showToast('success', 'Đã thêm ghi chú ' + message)
  } catch (error) {
    showToast('error', error.message)
  }
}

const deleteNote = async (noteId) => {
  await removeNote(noteId)
  notes.value = notes.value.filter((n) => n.id !== noteId)
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

// Xem thêm lịch sử
const loadMore = async () => {
  limit.value += 50
  await loadHistory()
}

onMounted(() => {
  loadHistory()
})
</script>

<template>
  <div>
    <h2 class="mb-3">📜 Lịch sử từ đã xem</h2>

    <div class="text-end mt-3">
      <button class="btn btn-danger" @click="deleteAllHistory">Xóa tất cả lịch sử</button>
    </div>

    <div v-if="history.length > 0" class="table-responsive">
      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr>
            <th>ID</th>
            <th>Từ</th>
            <th>Phiên âm</th>
            <th>Ngày xem</th>
            <th>Chức năng</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="entry in history" :key="entry.id">
            <td>{{ entry.id }}</td>
            <td>{{ entry.word.word }}</td>
            <td>{{ entry.word.phonetic }}</td>
            <td>{{ new Date(entry.viewedAt).toLocaleString() }}</td>
            <td>
              <button class="btn btn-outline-info btn-sm" @click="detailWord(entry.word.id)">
                Xem lại
              </button>
              <button class="btn btn-outline-danger btn-sm" @click="deleteHistory(entry.word.id)">
                Xoá
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <p v-else class="text-muted">Bạn chưa xem từ nào.</p>

    <div class="d-flex justify-content-center">
      <button
        v-if="history.length >= limit"
        class="btn btn-outline-info mt-3 text-center"
        @click="loadMore"
      >
        Xem thêm
      </button>
    </div>

    <!-- Modal hiển thị chi tiết từ -->
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
                <ul>
                  <li v-for="meaning in type.meanings" :key="meaning.id">
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
                <button class="btn btn-sm btn-outline-danger" @click="deleteNote(note.id)">
                  ❌
                </button>
              </li>
            </ul>
            <input v-model="newNote" class="form-control mt-2" placeholder="Nhập ghi chú..." />
            <button class="btn btn-success mt-2" @click="saveNote">Lưu ghi chú</button>
            <button class="btn btn-danger mt-2" @click="deleteNotes">Xóa tất cả ghi chú</button>
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
  </div>
</template>

<style scoped>
.list-group {
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
}
</style>
