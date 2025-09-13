(function () {
  'use strict';

  // --- Config ---
  var CFG = (window.__TPL_CONFIG || {});
  var ROOT = CFG.root || '/';
  var TEMPLATE_URL = CFG.templateUrl || (ROOT + 'Html/00_template/template.html');

  // --- Helpers ---
  function absUrl(href) {
    try { return new URL(href, location.origin).href; } catch (e) { return href; }
  }

  function injectHeadAssets(fromDoc) {
    var head = document.head;
    var existingHrefs = new Set(
      Array.from(head.querySelectorAll('link[rel="stylesheet"]'))
           .map(function (l) { return l.href; })
           .filter(Boolean)
    );

    // link rel=stylesheet
    fromDoc.querySelectorAll('link[rel="stylesheet"]').forEach(function (linkEl) {
      var href = linkEl.getAttribute('href');
      if (!href) return;
      var abs = absUrl(href);
      if (!existingHrefs.has(abs)) {
        var link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = abs;
        head.appendChild(link);
        existingHrefs.add(abs);
      }
    });

    // <style>
    fromDoc.querySelectorAll('style').forEach(function (styleEl) {
      head.appendChild(styleEl.cloneNode(true));
    });
  }

  function swapSlots(fromDoc) {
    var header = fromDoc.querySelector('header.header-bg') || fromDoc.querySelector('header');
    var nav    = fromDoc.querySelector('nav.nav-bg')       || fromDoc.querySelector('nav');
    var headerSlot = document.getElementById('header-slot');
    var navSlot    = document.getElementById('nav-slot');

    if (header && headerSlot) headerSlot.replaceWith(header);
    if (nav && navSlot)       navSlot.replaceWith(nav);
  }

  function initUserMenuHandlers() {
    var myIconBtn = document.getElementById('myIconBtn');
    var userMenu  = document.getElementById('userMenu');
    if (!myIconBtn || !userMenu) return;

    function closeUserMenu() {
      userMenu.classList.add('hidden');
      myIconBtn.setAttribute('aria-expanded', 'false');
    }
    function toggleMenu(e) {
      if (e) e.stopPropagation();
      userMenu.classList.toggle('hidden');
      myIconBtn.setAttribute('aria-expanded',
        userMenu.classList.contains('hidden') ? 'false' : 'true');
    }

    myIconBtn.addEventListener('click', toggleMenu);
    userMenu.addEventListener('click', function (e) { e.stopPropagation(); });
    document.addEventListener('click', closeUserMenu);
    document.addEventListener('keydown', function (e) { if (e.key === 'Escape') closeUserMenu(); });
  }

  function dispatchTemplateReady() {
    try {
      var evt = new CustomEvent('template:ready');
      document.dispatchEvent(evt);
    } catch (e) {
      // IE fallback 불필요한 환경이면 무시
    }
  }

  async function boot() {
    try {
      var res  = await fetch(TEMPLATE_URL, { credentials: 'same-origin' });
      var html = await res.text();
      var doc  = new DOMParser().parseFromString(html, 'text/html');

      injectHeadAssets(doc);   // 템플릿 CSS/STYLE을 <head>로
      swapSlots(doc);          // header/nav 삽입
      initUserMenuHandlers();  // 메뉴 버튼/ESC 등 핸들러

      dispatchTemplateReady(); // 페이지 후속 초기화용 이벤트
    } catch (e) {
      console.error('[templateLoader] 템플릿 로드 실패:', e);
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', boot);
  } else {
    boot();
  }
})();