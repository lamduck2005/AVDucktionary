import Swal from 'sweetalert2'
import './SwalHelper.css' // Import file CSS custom

const showToast = (icon, title, timer = 3000) => {
  Swal.fire({
    toast: true,
    position: 'bottom-end',
    icon,
    title,
    showConfirmButton: false,
    timer,
    timerProgressBar: true,
    customClass: {
      popup: 'animated-toast',
    },
    didOpen: (toast) => {
      toast.style.animation =
        'slideIn 0.5s ease-out, fadeOut 0.5s ease-in-out ' + (timer - 500) + 'ms forwards'
    },
  })
}

export { showToast }
