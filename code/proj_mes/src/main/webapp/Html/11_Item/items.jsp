<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="java.util.List, Dto.ItemDTO" %>

<%
  @SuppressWarnings("unchecked")
  List<ItemDTO> items = (List<ItemDTO>) request.getAttribute("items");
  String selectedType = (String) request.getAttribute("selectedType");
  if (selectedType == null) selectedType = "전체";
  String message = request.getParameter("message");
  String ctx = request.getContextPath();

  boolean selAll = "전체".equals(selectedType) || "0".equals(selectedType);
  boolean sel1   = "완제품".equals(selectedType) || "1".equals(selectedType);
  boolean sel2   = "소모품".equals(selectedType) || "2".equals(selectedType);
  boolean sel3   = "원자재".equals(selectedType) || "3".equals(selectedType);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>품목 관리</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
    .header-bg { background-color: #002a40; }
    .nav-bg { background-color: #003751; }
    .mainList li:hover { background-color: #3b82f6; }
  </style>
</head>
<body class="bg-gray-100 text-gray-800">

<!-- 템플릿 삽입 위치 -->
<div id="header-slot"></div>
<div id="nav-slot"></div>

<div class="p-4 sm:p-8 max-w-7xl mx-auto">
  <h2 class="text-2xl sm:text-3xl font-bold text-gray-900 mb-6">품목 목록</h2>

  <% if (message != null && !message.isEmpty()) { %>
    <div class="bg-blue-50 border border-blue-200 text-blue-800 px-4 py-3 rounded mb-4"><%= message %></div>
  <% } %>

  <!-- 필터 -->
  <div class="bg-white p-6 rounded-xl shadow-xl mb-6">
    <form id="filterForm" action="<%= ctx %>/items" method="get"
          class="flex flex-col md:flex-row items-center space-y-4 md:space-y-0 md:space-x-4">
      <div class="flex-1 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 w-full">
        <div class="flex flex-col">
          <label for="select-wo" class="text-sm font-medium text-gray-700 mb-1">품목 유형</label>
          <select id="select-wo" name="select-wo"
                  class="select border rounded-md px-3 py-2 text-gray-800 focus:ring-blue-500 focus:border-blue-500">
            <option value="0" <%= selAll ? "selected" : "" %>>전체</option>
            <option value="1" <%= sel1   ? "selected" : "" %>>완제품</option>
            <option value="2" <%= sel2   ? "selected" : "" %>>소모품</option>
            <option value="3" <%= sel3   ? "selected" : "" %>>원자재</option>
          </select>
        </div>
      </div>
      <div class="w-full md:w-auto">
        <button type="submit" id="searchBtn"
                class="bg-blue-600 text-white rounded-md px-6 py-2 hover:bg-blue-700 transition-colors duration-200 w-full md:w-auto">조회</button>
      </div>
    </form>
  </div>

  <!-- 테이블 -->
  <div class="bg-white p-6 rounded-xl shadow-xl overflow-x-auto">
    <form id="listForm" action="<%= ctx %>/items" method="post">
      <input type="hidden" name="action" value="delete"/>
      <div class="flex justify-end mb-4 space-x-2">
        <button type="submit"
                class="bg-red-500 text-white rounded-md px-4 py-2 hover:bg-red-600 transition-colors duration-200"
                onclick="return confirm('선택한 품목을 삭제하시겠습니까?');">삭제</button>
        <a href="<%= ctx %>/items/new"
           class="bg-green-500 text-white rounded-md px-4 py-2 hover:bg-green-600 transition-colors duration-200">등록</a>
      </div>

      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              <input type="checkbox" id="selectAllCheckbox" class="rounded text-blue-600 focus:ring-blue-500">
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">품목 코드</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">품목명</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">단가</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">유형</th>
          </tr>
        </thead>
        <tbody id="itemTableBody" class="bg-white divide-y divide-gray-200">
        <%
          if (items != null && !items.isEmpty()) {
            for (ItemDTO item : items) {
              String code     = item.getItem_code();
              String name     = item.getItem_name();
              String priceStr = item.getItem_price();
              String priceOut = priceStr;
              try {
                long p = Long.parseLong(priceStr == null ? "0" : priceStr.replaceAll("[^0-9]", ""));
                priceOut = String.format("%,d", p);
              } catch (Exception ignore) {}

              String typeLabel;
              switch (item.getItem_type()) {
                case 1: typeLabel = "완제품"; break;
                case 2: typeLabel = "소모품"; break;
                case 3: typeLabel = "원자재"; break;
                default: typeLabel = String.valueOf(item.getItem_type());
              }
        %>
          <tr class="hover:bg-gray-50 cursor-pointer" data-code="<%= code %>">
            <td class="px-6 py-4 whitespace-nowrap">
              <input type="checkbox" name="selectedCode" value="<%= code %>"
                     class="item-checkbox rounded text-blue-600 focus:ring-blue-500">
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900"><%= code %></td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900"><%= name %></td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900"><%= priceOut %></td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900"><%= typeLabel %></td>
          </tr>
        <%
            }
          } else {
        %>
          <tr><td colspan="5" class="px-6 py-6 text-center text-sm text-gray-500">데이터가 없습니다.</td></tr>
        <%
          }
        %>
        </tbody>
      </table>
    </form>
  </div>
</div>

<script>
  // 전체 선택 체크박스
  (function(){
    const all = document.getElementById('selectAllCheckbox');
    if (all) {
      all.addEventListener('change', (e) => {
        document.querySelectorAll('.item-checkbox').forEach(cb => cb.checked = e.target.checked);
      });
    }
  })();

  // 더블클릭 → 상세 이동
  (function(){
    const ctx = '<%= ctx %>';
    document.querySelectorAll('#itemTableBody tr[data-code]').forEach(tr => {
      tr.addEventListener('dblclick', (e) => {
        if (e.target.closest('input, button, a, label')) return;
        const code = tr.getAttribute('data-code');
        location.href = ctx + '/items?view=detail&code=' + encodeURIComponent(code);
      });
    });
  })();

  // 템플릿(header/nav) 비동기 로드 후, 사용자 메뉴 바인딩
  (async function () {
    try {
      const url = '<%= ctx %>/Html/00_template/template.html';
      const res = await fetch(url, { credentials: 'same-origin' });
      const text = await res.text();
      const doc  = new DOMParser().parseFromString(text, 'text/html');

      const header = doc.querySelector('header.header-bg') || doc.querySelector('header');
      const nav    = doc.querySelector('nav.nav-bg')       || doc.querySelector('nav');

      const headerSlot = document.getElementById('header-slot');
      const navSlot    = document.getElementById('nav-slot');

      if (header && headerSlot) headerSlot.replaceWith(header);
      if (nav && navSlot)       navSlot.replaceWith(nav);

      // 헤더/네비가 DOM에 들어간 뒤에만 바인딩
      bindUserMenu();
    } catch (e) {
      console.error('템플릿 로드 실패:', e);
    }
  })();

  // 사람 아이콘 드롭다운 바인딩
  function bindUserMenu() {
    const myIconBtn = document.getElementById('myIconBtn');
    const userMenu  = document.getElementById('userMenu');

    // 템플릿에 요소가 없으면 조용히 종료
    if (!myIconBtn || !userMenu) return;

    function closeUserMenu() {
      userMenu.classList.add('hidden');
      myIconBtn.setAttribute('aria-expanded', 'false');
    }

    myIconBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      userMenu.classList.toggle('hidden');
      myIconBtn.setAttribute(
        'aria-expanded',
        userMenu.classList.contains('hidden') ? 'false' : 'true'
      );
    });

    userMenu.addEventListener('click', (e) => e.stopPropagation());
    document.addEventListener('click', closeUserMenu);
    document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeUserMenu(); });
  }
</script>
</body>
</html>
