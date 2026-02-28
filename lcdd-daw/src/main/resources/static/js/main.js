/**
* Template Name: iConstruction
* Template URL: https://bootstrapmade.com/iconstruction-bootstrap-construction-template/
* Updated: Jul 27 2025 with Bootstrap v5.3.7
* Author: BootstrapMade.com
* License: https://bootstrapmade.com/license/
*/

(function () {
  "use strict";

  /**
   * Apply .scrolled class to the body as the page is scrolled down
   */
  function toggleScrolled() {
    const selectBody = document.querySelector('body');
    const selectHeader = document.querySelector('#header');
    if (!selectHeader.classList.contains('scroll-up-sticky') && !selectHeader.classList.contains('sticky-top') && !selectHeader.classList.contains('fixed-top')) return;
    window.scrollY > 100 ? selectBody.classList.add('scrolled') : selectBody.classList.remove('scrolled');
  }

  document.addEventListener('scroll', toggleScrolled);
  window.addEventListener('load', toggleScrolled);

  /**
   * Mobile nav toggle
   */
  const mobileNavToggleBtn = document.querySelector('.mobile-nav-toggle');

  function mobileNavToogle() {
    document.querySelector('body').classList.toggle('mobile-nav-active');
    mobileNavToggleBtn.classList.toggle('bi-list');
    mobileNavToggleBtn.classList.toggle('bi-x');
  }
  if (mobileNavToggleBtn) {
    mobileNavToggleBtn.addEventListener('click', mobileNavToogle);
  }

  /**
   * Hide mobile nav on same-page/hash links
   */
  document.querySelectorAll('#navmenu a').forEach(navmenu => {
    navmenu.addEventListener('click', () => {
      if (document.querySelector('.mobile-nav-active')) {
        mobileNavToogle();
      }
    });

  });

  /**
   * Toggle mobile nav dropdowns
   */
  document.querySelectorAll('.navmenu .toggle-dropdown').forEach(navmenu => {
    navmenu.addEventListener('click', function (e) {
      e.preventDefault();
      this.parentNode.classList.toggle('active');
      this.parentNode.nextElementSibling.classList.toggle('dropdown-active');
      e.stopImmediatePropagation();
    });
  });

  /**
   * Preloader
   */
  const preloader = document.querySelector('#preloader');
  if (preloader) {
    window.addEventListener('load', () => {
      preloader.remove();
    });
  }

  /**
   * Scroll top button
   */
  let scrollTop = document.querySelector('.scroll-top');

  function toggleScrollTop() {
    if (scrollTop) {
      window.scrollY > 100 ? scrollTop.classList.add('active') : scrollTop.classList.remove('active');
    }
  }
  scrollTop.addEventListener('click', (e) => {
    e.preventDefault();
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  });

  window.addEventListener('load', toggleScrollTop);
  document.addEventListener('scroll', toggleScrollTop);

  /**
   * Animation on scroll function and init
   */
  function aosInit() {
    AOS.init({
      duration: 600,
      easing: 'ease-in-out',
      once: true,
      mirror: false
    });
  }
  window.addEventListener('load', aosInit);

  /**
   * Timer
   */
  const timers = document.querySelectorAll(".next-event-timer");
  let date = new Date("2026-10-10 10:10:10");
  timers.forEach(timer => {

    setInterval(function () {
      // Get the current date and time
      let now = new Date().getTime();

      // Calculate the distance between now and the countdown date
      let distance = date - now;

      // Calculate days, hours, minutes and seconds
      let days = Math.floor(distance / (1000 * 60 * 60 * 24));
      let hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
      let seconds = Math.floor((distance % (1000 * 60)) / 1000);

      // Display the result
      timer.querySelector(".days").innerHTML = days.
        toString().padStart(2, '0');
      timer.querySelector(".hours").innerHTML = hours.
        toString().padStart(2, '0');
      timer.querySelector(".minutes").innerHTML = minutes.
        toString().padStart(2, '0');
      timer.querySelector(".seconds").innerHTML = seconds.
        toString().padStart(2, '0');

      // If the countdown is over, display a message
      if (distance < 0) {
        clearInterval(x);
        document.getElementById("countdown").innerHTML = "EXPIRED";
      }
    }, 1000);
  })


  // --- Event form logic (Show/Hide link container) ---
  const radioSi = document.getElementById('req-si');
  const radioNo = document.getElementById('req-no');
  const linkContainer = document.getElementById('link-container');

  // Only execute this code if the elements exist on the current page
  if (radioSi && radioNo && linkContainer) {
    function toggleLinkVisibility() {
      if (radioSi.checked) {
        linkContainer.style.display = 'block';
      } else {
        linkContainer.style.display = 'none';

        // Clear the input value if changed to "No"
        const linkInput = document.getElementById('link');
        if (linkInput) {
          linkInput.value = '';
        }
      }
    }

    // Add event listeners to the radio buttons
    radioSi.addEventListener('change', toggleLinkVisibility);
    radioNo.addEventListener('change', toggleLinkVisibility);
  }

})();

(function () {
  'use strict'

  // Search for all forms we want to apply custom Bootstrap validation styles to
  var forms = document.querySelectorAll('.needs-validation')

  // boolean checkValidity() method of HTMLFormElement to prevent submission if there are invalid fields
  Array.prototype.slice.call(forms)
    .forEach(function (form) {
      form.addEventListener('submit', function (event) {
        if (!form.checkValidity()) {
          event.preventDefault()
          event.stopPropagation()
        }
        form.classList.add('was-validated')
      }, false)
    })
})()


// Scripts for event_form.html

/**
 * logic for event form:
 * 1. Show/hide link input based on "Requiere inscripción" radio buttons
 * 2. Validate image input (size and format) before form submission 
 */
document.addEventListener('DOMContentLoaded', function () {

  const radios = document.querySelectorAll('.radio-registro');
  const linkContainer = document.getElementById('link-container');
  const linkInput = document.getElementById('link');

  if (radios.length > 0 && linkContainer && linkInput) {
    const toggleLinkValidation = () => {
      const siRadio = document.getElementById('req-si');
      if (siRadio && siRadio.checked) {
        linkContainer.style.display = 'block';
        linkInput.setAttribute('required', 'true');
      } else {
        linkContainer.style.display = 'none';
        linkInput.removeAttribute('required');
        linkInput.classList.remove('is-invalid');
      }
    };

    radios.forEach(radio => radio.addEventListener('change', toggleLinkValidation));

    toggleLinkValidation();
  }

  // image validation
  const imageInput = document.getElementById('imageField');

  if (imageInput) {
    imageInput.addEventListener('change', function () {
      const file = this.files[0];
      const maxSize = 10 * 1024 * 1024; // 10MB
      const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];

      if (file) {
        // size validation
        if (file.size > maxSize) {
          alert('¡Archivo demasiado grande! El máximo permitido son 10MB.');
          this.value = '';
          return;
        }

        // format validation
        if (!allowedTypes.includes(file.type)) {
          alert('Formato no permitido. Por favor, sube JPG, PNG o WebP.');
          this.value = '';
          return;
        }
      }
    });
  }

});

document.addEventListener('DOMContentLoaded', function () {

  // Password confirmation logic for register.html
  const registerForm = document.getElementById('registerForm');

  // Only execute this code if we're on the register page (where the form exists)
  if (registerForm) {
    const pwd = document.getElementById('password');
    const confirmPwd = document.getElementById('confirm-password');

    // Check if passwords match and set custom validity message if they don't
    function validatePasswords() {
      if (pwd.value !== confirmPwd.value) {
        confirmPwd.setCustomValidity("Las contraseñas no coinciden");
      } else {
        confirmPwd.setCustomValidity("");
      }
    }

    // Checking on form submission
    registerForm.addEventListener('submit', validatePasswords);

    // Checking in real-time as the user types
    pwd.addEventListener('input', validatePasswords);
    confirmPwd.addEventListener('input', validatePasswords);
  }

});

document.addEventListener("DOMContentLoaded", function () {

  const ctxGames = document.getElementById("gamesFavChart");
  const ctxEvents = document.getElementById("eventsParticipantsChart");

  // fill data with placeholders if there are less than 10 items to ensure the chart always has 10 bars and doesn't look empty
  const fillPlaceholderData = (data, type) => {
    const minItems = 10;
    const filledData = [...data];

    for (let i = filledData.length; i < minItems; i++) {
      filledData.push({
        [type === 'game' ? 'gameName' : 'eventName']: "---",
        [type === 'game' ? 'favCount' : 'participantCount']: 0
      });
    }
    return filledData;
  };

  const createAdminChart = (canvas, data, label, color, type) => {
    // apply placeholder logic to ensure the chart always has 10 bars
    const processedData = fillPlaceholderData(data, type);

    new Chart(canvas, {
      type: "bar",
      data: {
        labels: processedData.map(d => d.gameName || d.eventName),
        datasets: [{
          label: label,
          data: processedData.map(d => d.favCount || d.participantCount),
          backgroundColor: color + "73", // 45% opacity
          borderColor: color,
          borderWidth: 1,
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            enabled: true,
            callbacks: {
              label: (context) => context.raw === 0 ? "Sin datos" : `${context.raw} ${label}`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            suggestedMax: 10,
            ticks: { precision: 0 }
          },
          x: { ticks: { maxRotation: 45, minRotation: 0 } }
        }
      }
    });
  };

  if (ctxGames) {
    fetch("/admin/api/top-favorite-games")
      .then(res => res.json())
      .then(data => createAdminChart(ctxGames, data, "Favoritos", "#a71b12", "game"))
      .catch(err => console.error("Error Juegos Chart:", err));
  }

  if (ctxEvents) {
    fetch("/admin/api/top-events-participants")
      .then(res => res.json())
      .then(data => createAdminChart(ctxEvents, data, "Participantes", "#28a745", "event"))
      .catch(err => console.error("Error Eventos Chart:", err));
  }
});

/**
 * AJAX to load more games
 */
let currentGamePage = 1;

function loadMoreGames() {
  const btn = document.getElementById('load-more-btn');
  const text = document.getElementById('btn-text');
  const spinner = document.getElementById('btn-spinner');

  const urlParams = new URLSearchParams(window.location.search);
  urlParams.set('page', currentGamePage);

  btn.disabled = true;
  text.textContent = "Cargando...";
  spinner.classList.remove('d-none');

  fetch(`/games?${urlParams.toString()}`)
    .then(response => response.text())
    .then(html => {
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, 'text/html');

      const newGames = doc.querySelectorAll('#games-container .game-item');
      const container = document.getElementById('games-container');

      if (newGames.length > 0) {
        newGames.forEach(game => container.appendChild(game));

        const hasNextPage = doc.getElementById('load-more-btn') !== null;
        if (!hasNextPage) {
          btn.style.display = 'none';
        } else {
          currentGamePage++;
          btn.disabled = false;
          text.textContent = "Cargar más juegos";
          spinner.classList.add('d-none');
        }
      } else {
        btn.style.display = 'none';
      }
    })
    .catch(error => {
      console.error('Error al cargar juegos:', error);
      btn.disabled = false;
      text.textContent = "Error. Reintentar";
      spinner.classList.add('d-none');
    });
}

/**
 * ==========================================
 * Editing user profile logic:
 * 1. When user clicks "Edit Profile", switch to edit mode
 * 2. In edit mode, show image upload and make inputs editable
 * 3. Show "Cancel" and "Save Changes" buttons in edit mode
 * 4. If user clicks "Cancel", revert all changes and switch back to view mode
 * ==========================================
 */
document.addEventListener('DOMContentLoaded', function () {
  const btnEdit = document.getElementById('btn-edit-profile');
  const btnCancel = document.getElementById('btn-cancel-edit');
  const editActions = document.getElementById('edit-actions');
  const imageUpload = document.getElementById('image-upload-wrapper');
  const inputs = document.querySelectorAll('.profile-input');

  const nicknameDisplay = document.getElementById('nickname-display');
  const nicknameInputGroup = document.getElementById('nickname-input-group');
  const nicknameInput = document.querySelector('input[name="nickname"]');
  const passwordGroups = document.querySelectorAll('.password-edit-group');
  const updateForm = document.getElementById('update-form');
  const pwd = document.getElementById('new-password');
  const confirmPwd = document.getElementById('confirm-password-edit');

  const imagePreview = document.getElementById('profile-img-preview');
  const fileInput = document.getElementById('image');

  // saving original image src to revert back if user cancels editing, only if the image preview element exists on the page (profile.html)
  let originalImageSrc = imagePreview ? imagePreview.src : '';
  const originalValues = {};

  function toggleEditMode(enable) {
    if (enable) {
      btnEdit.classList.add('d-none');
      editActions.classList.remove('d-none');
      imageUpload.classList.remove('d-none');

      if (nicknameDisplay) nicknameDisplay.classList.add('d-none');
      if (nicknameInputGroup) nicknameInputGroup.classList.remove('d-none');
      if (nicknameInput) originalValues['nickname'] = nicknameInput.value;

      passwordGroups.forEach(group => group.classList.remove('d-none'));

      inputs.forEach(input => {
        originalValues[input.name] = input.value;
        input.removeAttribute('readonly');
        input.classList.remove('form-control-plaintext');
      });
    } else {
      btnEdit.classList.remove('d-none');
      editActions.classList.add('d-none');
      imageUpload.classList.add('d-none');

      if (nicknameDisplay) nicknameDisplay.classList.remove('d-none');
      if (nicknameInputGroup) nicknameInputGroup.classList.add('d-none');
      if (nicknameInput) nicknameInput.value = originalValues['nickname'];

      passwordGroups.forEach(group => group.classList.add('d-none'));
      if (pwd) pwd.value = '';
      if (confirmPwd) confirmPwd.value = '';

      // restoring original image src if user cancels editing
      if (imagePreview) imagePreview.src = originalImageSrc;
      if (fileInput) fileInput.value = '';

      inputs.forEach(input => {
        input.value = originalValues[input.name];
        input.setAttribute('readonly', true);
        input.classList.add('form-control-plaintext');
      });
    }
  }

  function validatePasswords() {
    if (pwd.value !== confirmPwd.value) {
      confirmPwd.setCustomValidity("Las contraseñas no coinciden");
    } else {
      confirmPwd.setCustomValidity("");
    }
  }

  if (btnEdit) btnEdit.addEventListener('click', () => toggleEditMode(true));
  if (btnCancel) btnCancel.addEventListener('click', () => toggleEditMode(false));

  if (pwd && confirmPwd) {
    pwd.addEventListener('input', validatePasswords);
    confirmPwd.addEventListener('input', validatePasswords);
    if (updateForm) updateForm.addEventListener('submit', validatePasswords);
  }

  // --- img preview for profile editing ---
  if (imageUpload && imagePreview && fileInput) {
    fileInput.addEventListener('change', function () {
      const file = this.files[0];

      if (file) {
        if (file.type.startsWith('image/')) {
          const objectUrl = URL.createObjectURL(file);
          imagePreview.src = objectUrl;
        } else {
          alert('Por favor, selecciona un archivo de imagen válido (JPG, PNG...).');
          this.value = '';
        }
      }
    });
  }
});