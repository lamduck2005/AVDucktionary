import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL

export const fetchWords = async (keyword = '', page = 0, size = 30) => {
  const response = await axios.get(`${API_URL}/words/words`, {
    params: { keyword, page, size },
  })
  return response.data
}

export const suggestWords = async (keyword) => {
  if (!keyword) return [] // Nếu không có input, không gọi API
  const response = await axios.get(`${API_URL}/words/suggest`, { params: { keyword } })
  return response.data
}

export const fetchWordDetails = async (wordId, userId) => {
  const response = await axios.get(`${API_URL}/words/${wordId}`)
  if (userId) {
    await addHistory(userId, wordId)
  }
  return response.data
}

//user -----------------------------------------------------------------------------------------
export const register = async (email, password, fullName) => {
  return axios.post(`${API_URL}/auth/register`, { email, password, fullName })
}

export const login = async (email, password) => {
  return axios.post(`${API_URL}/auth/login`, { email, password })
}

export const fetchUserInfo = async (email) => {
  const response = await axios.get(`${API_URL}/auth/me?email=${email}`)
  return response.data
}

export const updateUserInfo = async (email, fullName, avatarUrl) => {
  return axios.put(`${API_URL}/users/update`, { email, fullName, avatarUrl })
}

export const changePassword = async (email, oldPassword, newPassword) => {
  return axios.put(`${API_URL}/users/change-password`, { email, oldPassword, newPassword })
}

//favorite -----------------------------------------------------------------------------------------
export const getFavorites = async (userId) => {
  const response = await axios.get(`${API_URL}/favorites/${userId}`)
  return response.data
}

export const addFavorite = async (userId, wordId) => {
  await axios.post(`${API_URL}/favorites/${userId}/${wordId}`)
}

export const removeFavorite = async (userId, wordId) => {
  await axios.delete(`${API_URL}/favorites/${userId}/${wordId}`)
}

export const removeAllFavorites = async (userId) => {
  await axios.delete(`${API_URL}/favorites/${userId}`)
}

//note -----------------------------------------------------------------------------------------
export const getNotedWords = async (userId) => {
  const response = await axios.get(`${API_URL}/notes/${userId}`)
  return response.data
}

export const getNotes = async (userId, wordId) => {
  const response = await axios.get(`${API_URL}/notes/${userId}/${wordId}`)
  return response.data
}

export const addNote = async (userId, wordId, content) => {
  await axios.post(`${API_URL}/notes/${userId}/${wordId}`, content, {
    headers: { 'Content-Type': 'text/plain' },
  })
}

export const removeNote = async (noteId) => {
  await axios.delete(`${API_URL}/notes/${noteId}`)
}

export const removeNotes = async (userId, wordId) => {
  await axios.delete(`${API_URL}/notes/${userId}/${wordId}`)
}

export const removeAllNotes = async (userId) => {
  await axios.delete(`${API_URL}/notes/user/${userId}`)
}

//note -----------------------------------------------------------------------------------------
export const getUserHistory = async (userId, limit = 50) => {
  const response = await axios.get(`${API_URL}/history/${userId}?limit=${limit}`)
  return response.data
}

export const addHistory = async (userId, wordId) => {
  await axios.post(`${API_URL}/history/${userId}/${wordId}`)
}

export const removeHistory = async (userId, wordId) => {
  await axios.delete(`${API_URL}/history/${userId}/${wordId}`)
}

export const removeAllHistory = async (userId) => {
  await axios.delete(`${API_URL}/history/${userId}`)
}
