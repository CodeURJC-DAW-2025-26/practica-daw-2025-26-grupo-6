/**
* Template Name: iConstruction
* Template URL: https://bootstrapmade.com/iconstruction-bootstrap-construction-template/
* Updated: Jul 27 2025 with Bootstrap v5.3.7
* Author: BootstrapMade.com
* License: https://bootstrapmade.com/license/
*/

(function () {
  "use strict";

  // --- GENERAL SCROLL Y NAV ---
  function toggleScrolled() {
    const selectBody = document.querySelector('body');
    const selectHeader = document.querySelector('#header');

    if (selectBody.classList.contains('scrolled')) {
      if (window.scrollY < 64) {
        selectBody.classList.remove('scrolled');
      }
    } else {
      if (window.scrollY > 100) {
        selectBody.classList.add('scrolled');
      }
    }
  }

  document.addEventListener('scroll', toggleScrolled);
  window.addEventListener('load', toggleScrolled);

  const mobileNavToggleBtn = document.querySelector('.mobile-nav-toggle');
  function mobileNavToogle() {
    document.querySelector('body').classList.toggle('mobile-nav-active');
    mobileNavToggleBtn.classList.toggle('bi-list');
    mobileNavToggleBtn.classList.toggle('bi-x');
  }
  if (mobileNavToggleBtn) {
    mobileNavToggleBtn.addEventListener('click', mobileNavToogle);
  }

  document.querySelectorAll('#navmenu a').forEach(navmenu => {
    navmenu.addEventListener('click', () => {
      if (document.querySelector('.mobile-nav-active')) mobileNavToogle();
    });
  });

  document.querySelectorAll('.navmenu .toggle-dropdown').forEach(navmenu => {
    navmenu.addEventListener('click', function (e) {
      e.preventDefault();
      this.parentNode.classList.toggle('active');
      this.parentNode.nextElementSibling.classList.toggle('dropdown-active');
      e.stopImmediatePropagation();
    });
  });

  const preloader = document.querySelector('#preloader');
  if (preloader) {
    window.addEventListener('load', () => preloader.remove());
  }

  let scrollTop = document.querySelector('.scroll-top');
  function toggleScrollTop() {
    if (scrollTop) {
      window.scrollY > 100 ? scrollTop.classList.add('active') : scrollTop.classList.remove('active');
    }
  }
  if (scrollTop) {
    scrollTop.addEventListener('click', (e) => {
      e.preventDefault();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }
  window.addEventListener('load', toggleScrollTop);
  document.addEventListener('scroll', toggleScrollTop);

  function aosInit() {
    if (typeof AOS !== 'undefined') {
      AOS.init({ duration: 600, easing: 'ease-in-out', once: true, mirror: false });
    }
  }
  window.addEventListener('load', aosInit);

  /**
   * Timer
   */
  const timers = document.querySelectorAll('.next-event-timer');

    timers.forEach(timer => {
    const startStr = timer.getAttribute('startDate');

    const getMadridDate = (dateStr) => {
      // 1. Create a formatter for the Madrid timezone
      const dtf = new Intl.DateTimeFormat('en-GB', {
        timeZone: 'Europe/Madrid',
        year: 'numeric', month: '2-digit', day: '2-digit',
        hour: '2-digit', minute: '2-digit', second: '2-digit',
        hour12: false
      });

      // 2. Parse the string as local time first
      const localDate = new Date(dateStr);
      
      // 3. Calculate the difference between "Now" and "Now in Madrid"
      // to find the offset, then apply it to your dateStr
      const parts = dtf.formatToParts(new Date());
      const map = new Map(parts.map(p => [p.type, p.value]));
      const isoMadrid = `${map.get('year')}-${map.get('month')}-${map.get('day')}T${map.get('hour')}:${map.get('minute')}:${map.get('second')}`;
      
      const diff = new Date(isoMadrid) - new Date();
      return new Date(localDate.getTime() - diff);
    };

    const startDate = getMadridDate(startStr);

    const updateInterval = setInterval(() => {
      const now = Date.now(); // Global UTC timestamp
      const distanceStart = startDate - now;

      if (distanceStart < 0) {
        clearInterval(updateInterval);
        timer.querySelector(".days").innerHTML = '00';
        timer.querySelector(".hours").innerHTML = '00';
        timer.querySelector(".minutes").innerHTML = '00';
        timer.querySelector(".seconds").innerHTML = '00';
        return;
      }

      const days = Math.floor(distanceStart / (1000 * 60 * 60 * 24));
      const hours = Math.floor((distanceStart % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutes = Math.floor((distanceStart % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((distanceStart % (1000 * 60)) / 1000);

      const daysEl = timer.querySelector(".days");
      const hoursEl = timer.querySelector(".hours");
      const minutesEl = timer.querySelector(".minutes");
      const secondsEl = timer.querySelector(".seconds");

    }, 1000);
  });

})();

// Bootstrap Validation
(function () {
  'use strict'
  var forms = document.querySelectorAll('.needs-validation')
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
})();


/**
 * ==========================================
 * EVENT FORM LOGIC
 * ==========================================
 */
document.addEventListener('DOMContentLoaded', function () {

  const radios = document.querySelectorAll('.radio-registro');
  const linkContainer = document.getElementById('link-container');
  const linkInput = document.getElementById('link');

  if (radios.length > 0 && linkContainer && linkInput) {
    const toggleLinkValidation = () => {
      const siRadio = document.getElementById('req-si');
      if (siRadio && siRadio.checked) {
        linkContainer.classList.remove('d-none');
        linkInput.setAttribute('required', 'true');
      } else {
        linkContainer.classList.add('d-none');
        linkInput.removeAttribute('required');
        linkInput.classList.remove('is-invalid');
      }
    };
    radios.forEach(radio => radio.addEventListener('change', toggleLinkValidation));
    toggleLinkValidation();
  }

  const dateInput = document.getElementById('eventDate');
  if (dateInput) {
    const today = new Date().toISOString().split('T')[0];
    dateInput.setAttribute('min', today);
  }

  const imageInput = document.getElementById('imageField');
  if (imageInput) {
    imageInput.addEventListener('change', function () {
      const file = this.files[0];
      const maxSize = 10 * 1024 * 1024; // 10MB
      const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];

      if (file) {
        if (file.size > maxSize) {
          alert('¡Archivo demasiado grande! El máximo permitido son 10MB.');
          this.value = '';
          return;
        }
        if (!allowedTypes.includes(file.type)) {
          alert('Formato no permitido. Por favor, sube JPG, PNG o WebP.');
          this.value = '';
          return;
        }
      }
    });
  }

  // EndDate validation
  const startDate = document.getElementById('startDate');
  const endDate = document.getElementById('endDate');

  function endDateValidation() {
    const startInput = document.getElementById('startDate');
    const endInput = document.getElementById('endDate');
    const startDate = new Date(startInput.value);
    const endDate = new Date(endInput.value);
    if (startDate > endDate) {
      endInput.setCustomValidity("La fecha de fin debe ser posterior a la de inicio.");
    } else {
      endInput.setCustomValidity("");
    }
  }
  startDate.addEventListener("change", endDateValidation);
  endDate.addEventListener("change", endDateValidation);
});

/**
 * ==========================================
 * USER REGISTRATION LOGIC
 * ==========================================
 */
document.addEventListener("DOMContentLoaded", function () {
  const registerForm = document.getElementById('registerForm');
  if (registerForm) {
    const pwd = document.getElementById('password');
    const confirmPwd = document.getElementById('confirm-password');

    function validatePasswords() {
      if (pwd.value !== confirmPwd.value) {
        confirmPwd.setCustomValidity("Las contraseñas no coinciden");
      } else {
        confirmPwd.setCustomValidity("");
      }
    }
    registerForm.addEventListener('submit', validatePasswords);
    pwd.addEventListener('input', validatePasswords);
    confirmPwd.addEventListener('input', validatePasswords);
  }
});

/**
 * ==========================================
 * ADMIN CHARTS LOGIC
 * ==========================================
 */
document.addEventListener("DOMContentLoaded", function () {
  const ctxGames = document.getElementById("gamesFavChart");
  const ctxEvents = document.getElementById("eventsParticipantsChart");

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
    if (typeof Chart === 'undefined') return;
    const processedData = fillPlaceholderData(data, type);

    new Chart(canvas, {
      type: "bar",
      data: {
        labels: processedData.map(d => d.gameName || d.eventName),
        datasets: [{
          label: label,
          data: processedData.map(d => d.favCount || d.participantCount),
          backgroundColor: color + "73",
          borderColor: color,
          borderWidth: 1,
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, suggestedMax: 10, ticks: { precision: 0 } },
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
 * ==========================================
 * EDIT PROFILE LOGIC
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
  const emailInput = document.querySelector('input[name="email"]');
  const passwordInput = document.getElementById('new-password');

  if (updateForm && emailInput && passwordInput) {
    updateForm.addEventListener('submit', function (event) {
      const emailChanged = emailInput.value !== originalValues['email'];
      const passwordChanged = passwordInput.value.trim() !== "";
      if (emailChanged || passwordChanged) {
        const confirmacion = confirm("Has cambiado tus credenciales de acceso. Deberás iniciar sesión de nuevo. ¿Continuar?");
        if (!confirmacion) event.preventDefault();
      }
    });
  }

  let originalImageSrc = imagePreview ? imagePreview.src : '';
  const originalValues = {};

  function toggleEditMode(enable) {
    if (enable) {
      if (btnEdit) btnEdit.classList.add('d-none');
      if (editActions) editActions.classList.remove('d-none');
      if (imageUpload) imageUpload.classList.remove('d-none');
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
      if (btnEdit) btnEdit.classList.remove('d-none');
      if (editActions) editActions.classList.add('d-none');
      if (imageUpload) imageUpload.classList.add('d-none');
      if (nicknameDisplay) nicknameDisplay.classList.remove('d-none');
      if (nicknameInputGroup) nicknameInputGroup.classList.add('d-none');
      if (nicknameInput) nicknameInput.value = originalValues['nickname'] || '';

      passwordGroups.forEach(group => group.classList.add('d-none'));
      if (pwd) pwd.value = '';
      if (confirmPwd) confirmPwd.value = '';
      if (imagePreview) imagePreview.src = originalImageSrc;
      if (fileInput) fileInput.value = '';

      inputs.forEach(input => {
        input.value = originalValues[input.name] || '';
        input.setAttribute('readonly', true);
        input.classList.add('form-control-plaintext');
      });
    }
  }

  function validateEditPasswords() {
    if (!pwd || !confirmPwd) return;
    if (pwd.value !== confirmPwd.value) {
      confirmPwd.setCustomValidity("Las contraseñas no coinciden");
    } else {
      confirmPwd.setCustomValidity("");
    }
  }

  if (btnEdit) btnEdit.addEventListener('click', () => toggleEditMode(true));
  if (btnCancel) btnCancel.addEventListener('click', () => toggleEditMode(false));

  if (pwd && confirmPwd) {
    pwd.addEventListener('input', validateEditPasswords);
    confirmPwd.addEventListener('input', validateEditPasswords);
    if (updateForm) updateForm.addEventListener('submit', validateEditPasswords);
  }

  if (imageUpload && imagePreview && fileInput) {
    fileInput.addEventListener('change', function () {
      const file = this.files[0];
      if (file && file.type.startsWith('image/')) {
        imagePreview.src = URL.createObjectURL(file);
      } else {
        alert('Por favor, selecciona una imagen válida.');
        this.value = '';
      }
    });
  }
});

/**
 * ==========================================
 * AJAX LOGIC (EVENTS, NEWS, GAMES)
 * ==========================================
 */

// --- Events AJAX ---
let currentEventsPage = 1;
function loadMoreEvents() {
  const btn = document.getElementById('load-more-events-btn');
  const text = document.getElementById('btn-events-text');
  const spinner = document.getElementById('btn-events-spinner');

  if (!btn) return;

  const name = btn.getAttribute('data-name') || "";
  const tag = btn.getAttribute('data-tag') || "";

  const urlParams = new URLSearchParams(window.location.search);
  urlParams.set('page', currentEventsPage);
  if (name) urlParams.set('name', name);
  if (tag) urlParams.set('tag', tag);

  btn.disabled = true;
  if (text) text.textContent = "Cargando...";
  if (spinner) spinner.classList.remove('d-none');

  fetch(`/events?${urlParams.toString()}`)
    .then(response => response.text())
    .then(html => {
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, 'text/html');

      const newEvents = doc.querySelectorAll('#events-container .event-item');
      const container = document.getElementById('events-container');

      if (newEvents.length > 0) {
        newEvents.forEach(event => container.appendChild(event));

        const hasNextPage = doc.getElementById('load-more-events-btn') !== null;
        if (!hasNextPage) {
          btn.style.display = 'none';
        } else {
          currentEventsPage++;
          btn.disabled = false;
          if (text) text.textContent = "Cargar más eventos";
          if (spinner) spinner.classList.add('d-none');
        }
      } else {
        btn.style.display = 'none';
      }
    })
    .catch(error => {
      console.error('Error cargando eventos:', error);
      btn.disabled = false;
      if (text) text.textContent = "Error. Reintentar";
      if (spinner) spinner.classList.add('d-none');
    });
}

// --- News AJAX ---
let currentNewsPage = 1;
function loadMoreNews() {
  const btn = document.getElementById('load-more-news-btn');
  const text = document.getElementById('btn-news-text');
  const spinner = document.getElementById('btn-news-spinner');

  if (!btn) return;

  const name = btn.getAttribute('data-name') || "";
  const tag = btn.getAttribute('data-tag') || "";

  btn.disabled = true;
  if (text) text.textContent = "Cargando...";
  if (spinner) spinner.classList.remove('d-none');

  fetch(`/news?page=${currentNewsPage}&name=${name}&tag=${tag}`)
    .then(response => response.text())
    .then(html => {
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, 'text/html');

      const newItems = doc.getElementById('news-container');
      const container = document.getElementById('news-container');

      if (newItems && container) {
        container.insertAdjacentHTML('beforeend', newItems.innerHTML);

        const hasNext = doc.getElementById('load-more-news-btn') !== null;
        if (!hasNext) {
          btn.style.display = 'none';
        } else {
          currentNewsPage++;
          btn.disabled = false;
          if (text) text.textContent = "Cargar más noticias";
          if (spinner) spinner.classList.add('d-none');
        }
      }
    })
    .catch(error => {
      console.error('Error cargando noticias:', error);
      btn.disabled = false;
      if (text) text.textContent = "Error. Reintentar";
      if (spinner) spinner.classList.add('d-none');
    });
}

// --- Games AJAX ---
let currentGamesPage = 1;
function loadMoreGames() {
  const btn = document.getElementById('load-more-games-btn');
  const text = document.getElementById('btn-games-text');
  const spinner = document.getElementById('btn-games-spinner');

  if (!btn) return;

  const urlParams = new URLSearchParams(window.location.search);
  urlParams.set('page', currentGamesPage);

  btn.disabled = true;
  if (text) text.textContent = "Cargando...";
  if (spinner) spinner.classList.remove('d-none');

  fetch(`/games?${urlParams.toString()}`)
    .then(response => response.text())
    .then(html => {
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, 'text/html');

      const newGames = doc.querySelectorAll('#games-container .game-item');
      const container = document.getElementById('games-container');

      if (newGames.length > 0) {
        newGames.forEach(game => container.appendChild(game));

        const hasNextPage = doc.getElementById('load-more-games-btn') !== null;
        if (!hasNextPage) {
          btn.style.display = 'none';
        } else {
          currentGamesPage++;
          btn.disabled = false;
          if (text) text.textContent = "Cargar más juegos";
          if (spinner) spinner.classList.add('d-none');
        }
      } else {
        btn.style.display = 'none';
      }
    })
    .catch(error => {
      console.error('Error al cargar juegos:', error);
      btn.disabled = false;
      if (text) text.textContent = "Error. Reintentar";
      if (spinner) spinner.classList.add('d-none');
    });
}

/**
 * ==========================================
 * CAROUSEL EVENT TIMER LOGIC
 * ==========================================
 */
document.addEventListener('DOMContentLoaded', function () {
  const timers = document.querySelectorAll('.countdown-timer');
  
  if (timers.length > 0) {
      setInterval(() => {
          timers.forEach(timer => {
              const targetDateStr = timer.getAttribute('data-date');
              if (!targetDateStr) return;
              
              const targetDate = new Date(targetDateStr + "T00:00:00").getTime();
              const now = new Date().getTime();
              const diff = targetDate - now;

              if (diff > 0) {
                  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
                  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
                  const seconds = Math.floor((diff % (1000 * 60)) / 1000);

                  timer.innerHTML = `<i class="bi bi-clock-history me-1"></i> Faltan: ${days}d ${hours}h ${minutes}m ${seconds}s`;
              } else {
                  timer.innerHTML = `<i class="bi bi-stars me-1"></i> ¡El evento es hoy!`;
                  timer.classList.replace('bg-danger', 'bg-success');
              }
          });
      }, 1000);
  }
});