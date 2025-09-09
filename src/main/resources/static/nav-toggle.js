// nav-toggle.js
document.addEventListener('DOMContentLoaded', () => {
  const toggleBtn = document.querySelector('.nav-toggle');
  const mobileMenu = document.querySelector('.mobile-menu');

  if (toggleBtn && mobileMenu) {
    toggleBtn.addEventListener('click', () => {
      const isActive = mobileMenu.classList.toggle('active');
      toggleBtn.setAttribute('aria-expanded', isActive);
    });
  }
});