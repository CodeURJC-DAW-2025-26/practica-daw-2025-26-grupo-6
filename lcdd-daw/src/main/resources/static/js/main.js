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

  const form = document.getElementById('registerForm');
  if (form) {
    form.addEventListener('submit', async function(event) {
      const email = document.getElementById('email');
      const password = document.getElementById('password');
      const confirmPassword = document.getElementById('confirm-password');

      event.preventDefault();

      try {
        const response = await fetch(`/userExists?email=${encodeURIComponent(email.value)}`);
        const exists = await response.json();
        console.log(exists);

        if (exists) {
          // Show error
          email.classList.add('is-invalid');
          return;
        } else {
          // Clean error
          email.classList.remove('is-invalid');
        }
      } catch (error) {
        console.error("Error al verificar el usuario:", error);
        return;
      }
      
      if (password.value !== confirmPassword.value) {
          // Show error
          confirmPassword.classList.add('is-invalid');
          password.classList.add('is-invalid');
          return;
          
      } else {
          // Clean error
          confirmPassword.classList.remove('is-invalid');
          password.classList.remove('is-invalid');
      }

      form.submit();
    });
  }

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