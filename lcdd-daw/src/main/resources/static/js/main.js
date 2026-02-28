/**
* Template Name: iConstruction
* Template URL: https://bootstrapmade.com/iconstruction-bootstrap-construction-template/
* Updated: Jul 27 2025 with Bootstrap v5.3.7
* Author: BootstrapMade.com
* License: https://bootstrapmade.com/license/
*/

(function() {
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
    navmenu.addEventListener('click', function(e) {
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
    
    setInterval(function() {
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
        imageInput.addEventListener('change', function() {
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

document.addEventListener('DOMContentLoaded', function() {
  
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