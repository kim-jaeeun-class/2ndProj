<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<% String ctx = request.getContextPath();%>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>기준 목록</title>
  <link rel="stylesheet" href="<c:url value='/Html/asset/04_standard_list.css'/>" />
  <style>.main{ width:90%; margin:5%; }</style>
</head>
<body>
  <div id="header-slot"></div>
  <div id="nav-slot"></div>
  
<div class="titleBox">
        <span>기준 관리</span> 
        
  <div class="wrap">
      <!-- 상단 툴바 -->
      <div class="toolbar" role="region" aria-label="검색 및 등록">
      	<div>
	        <label for="std-category">분류</label>
	        <div class="select">
	          <select id="std-category">
	            <!-- ★ '선택하세요' 제거, '전체'를 첫 옵션으로 -->
	            <option>전체</option>
	            <option>공정</option>
	            <option>BOM</option>
	            <option>발주</option>
	            <option>재고</option>
	            <option>생산</option>
	            <option>품질</option>
	          </select>
	        </div>
      	</div>
        <div>
        <button class="btn filter_btn" type="button" id="std-search  ">조회</button>
        </div>
        <!-- <div class="spacer"></div> --><!-- 필요하신 건가요? -->
      </div>
 </div>
	<div class="wrap_list">
	<div class="action">
        <button class="btn item_add" type="button" id="std-create">등록</button>
    </div>  
      <!-- 표 -->
      <section aria-label="기준 목록 표">
        <div class="table-wrap" tabindex="0">
          <table class="tables" aria-label="기준 목록">
            <colgroup>
              <col style="width:120px" />
              <c:forEach var="i" begin="1" end="${fn:length(columns)}">
                <col />
              </c:forEach>
            </colgroup>

            <thead>
              <tr>
                <th scope="col">번호</th>
                <c:forEach var="col" items="${columns}">
                  <th scope="col">${col.title}</th>
                </c:forEach>
              </tr>
            </thead>

            <tbody id="std-body" class="tables_body">
              <c:choose>
                <c:when test="${empty data}">
                  <tr>
                    <td colspan="${fn:length(columns) + 1}" style="text-align:center;color:#888;">
                      선택한 분류에 해당하는 데이터가 없습니다.
                    </td>
                  </tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="row" items="${data}" varStatus="st">
                    <tr>
                      <td>${st.index + 1}</td>
                      <c:forEach var="col" items="${columns}">
                        <td>${row[col.key]}</td>
                      </c:forEach>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </div>

<script>
(function () {
  'use strict';

  const ROOT         = '<c:url value="/"/>';
  const SEARCH_URL   = '<c:url value="/StandardCtrl"/>';
  const TEMPLATE_URL = '<c:url value="/Html/00_template/template.html"/>';

  // === 현재 카테고리 셀렉트 초기화 ===
  (function setCurrentCategory() {
    var current = '<c:out value="${category}" />';
    var sel = document.getElementById('std-category');
    if (!sel) return;

    // 서버에서 category를 안 주는 경우도 대비: 기본 '전체'
    if (!current) current = '전체';

    for (var i = 0; i < sel.options.length; i++) {
      var opt = sel.options[i];
      if (opt.value === current || opt.text === current) {
        sel.selectedIndex = i;
        break;
      }
    }
  })();

  // === 조회 버튼 / Enter 키로 조회 ===
  (function wireSearch() {
    var sel = document.getElementById('std-category');
    var btn = document.getElementById('std-search');

    function goSearch() {
      var cat = (sel && sel.value ? sel.value : '').trim();
      // '전체' 기본이므로 파라미터 없이 들어오면 컨트롤러가 전체로 처리하지만,
      // 명시적으로 주는 편이 직관적임
      location.href = SEARCH_URL + '?category=' + encodeURIComponent(cat || '전체');
    }

    if (btn) btn.addEventListener('click', goSearch);
    if (sel) sel.addEventListener('keydown', function (e) {
      if (e.key === 'Enter') goSearch();
    });
  })();

  // === 등록하기 버튼 ===
  (function wireCreate() {
    var btn = document.getElementById('std-create');
    var sel = document.getElementById('std-category');

    var routeMap = {
      '발주': 'orderDetail',
      '공정': 'process',
      '재고': 'stock',
      'BOM' : 'bom',
      '생산': 'proplan',
      '품질': '품질'
    };

    function goCreate() {
      var cat = (sel && sel.value ? sel.value : '').trim();
      if (!cat || cat === '전체') {
        alert('전체에서는 등록할 수 없습니다. 특정 분류를 선택해 주세요.');
        return;
      }
      var page = routeMap[cat];
      if (!page) { alert('알 수 없는 분류입니다.'); return; }
      location.href = ROOT + encodeURIComponent(page);
    }

    if (btn) btn.addEventListener('click', goCreate);
  })();

  // === 템플릿 로드/삽입 ===
  (async function loadTemplateAndInject() {
    try {
      const res  = await fetch(TEMPLATE_URL, { credentials: 'same-origin' });
      const html = await res.text();
      const doc  = new DOMParser().parseFromString(html, 'text/html');

      const head = document.head;
      const existingHrefs = new Set(
        Array.from(head.querySelectorAll('link[rel="stylesheet"]'))
             .map(l => l.href).filter(Boolean)
      );

      doc.querySelectorAll('link[rel="stylesheet"]').forEach(linkEl => {
        const href = linkEl.getAttribute('href');
        if (!href) return;
        const abs = new URL(href, location.origin).href;
        if (!existingHrefs.has(abs)) {
          const link = document.createElement('link');
          link.rel = 'stylesheet';
          link.href = abs;
          head.appendChild(link);
          existingHrefs.add(abs);
        }
      });

      doc.querySelectorAll('style').forEach(styleEl => {
        head.appendChild(styleEl.cloneNode(true));
      });

      const header    = doc.querySelector('header.header-bg') || doc.querySelector('header');
      const nav       = doc.querySelector('nav.nav-bg')       || doc.querySelector('nav');
      const headerSlot= document.getElementById('header-slot');
      const navSlot   = document.getElementById('nav-slot');
      if (header && headerSlot) headerSlot.replaceWith(header);
      if (nav && navSlot)       navSlot.replaceWith(nav);

      initUserMenuHandlers();
    } catch (e) {
      console.error('템플릿 로드 실패:', e);
    }
  })();

  function initUserMenuHandlers() {
    const myIconBtn = document.getElementById('myIconBtn');
    const userMenu  = document.getElementById('userMenu');
    if (!myIconBtn || !userMenu) return;

    function closeUserMenu() {
      userMenu.classList.add('hidden');
      myIconBtn.setAttribute('aria-expanded', 'false');
    }
    function toggleMenu(e) {
      e && e.stopPropagation();
      userMenu.classList.toggle('hidden');
      myIconBtn.setAttribute('aria-expanded',
        userMenu.classList.contains('hidden') ? 'false' : 'true');
    }

    myIconBtn.addEventListener('click', toggleMenu);
    userMenu.addEventListener('click', function (e) { e.stopPropagation(); });
    document.addEventListener('click', closeUserMenu);
    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') closeUserMenu();
    });
  }
})();
</script>
</body>
</html>
