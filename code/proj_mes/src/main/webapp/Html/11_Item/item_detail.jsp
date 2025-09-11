<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="Dto.ItemDTO" %>
<%
  ItemDTO item = (ItemDTO) request.getAttribute("item");
  String ctx = request.getContextPath();
  String message = request.getParameter("message");
  if (item == null) { response.sendRedirect(ctx + "/items"); return; }
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>품목 상세</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">
  <div class="max-w-3xl mx-auto mt-8 bg-white p-6 rounded-xl shadow">
    <h2 class="text-2xl font-bold mb-4">품목 상세</h2>

    <% if (message != null && !message.isEmpty()) { %>
      <div class="bg-blue-50 border border-blue-200 text-blue-800 px-4 py-3 rounded mb-4"><%= message %></div>
    <% } %>

    <form id="editForm" method="post" action="<%= ctx %>/items" class="space-y-4">
      <input type="hidden" name="action" value="update"/>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">품목 코드 (PK)</label>
        <input type="text" name="itemCode" value="<%= item.getItem_code() %>"
               class="w-full border rounded px-3 py-2 bg-gray-100" readonly />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">품목명</label>
        <input type="text" id="itemName" name="itemName" value="<%= item.getItem_name() %>"
               class="w-full border rounded px-3 py-2" readonly />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">단가</label>
        <input type="text" id="itemPrice" name="itemPrice" value="<%= item.getItem_price() %>"
               class="w-full border rounded px-3 py-2" readonly />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">품목 유형</label>
        <select id="itemType" name="itemType" class="w-full border rounded px-3 py-2" disabled>
          <option value="1" <%= item.getItem_type()==1?"selected":"" %>>완제품</option>
          <option value="2" <%= item.getItem_type()==2?"selected":"" %>>소모품</option>
          <option value="3" <%= item.getItem_type()==3?"selected":"" %>>원자재</option>
          <%-- <option value="4" <%= item.getItem_type()==4?"selected":"" %>>반제품</option> --%>
        </select>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">비고</label>
        <input type="text" id="itemBigo" name="itemBigo" value="<%= item.getItem_bigo() %>"
               class="w-full border rounded px-3 py-2" readonly />
      </div>

      <div class="flex gap-2 pt-2">
        <button id="editBtn"  type="button" class="bg-yellow-500 text-white px-4 py-2 rounded">수정</button>
        <button id="saveBtn"  type="submit" class="bg-blue-600 text-white px-4 py-2 rounded hidden">저장</button>
        <a href="<%= ctx %>/items" class="bg-gray-400 text-white px-4 py-2 rounded">목록</a>
      </div>
    </form>
  </div>

  <script>
    const editBtn = document.getElementById('editBtn');
    const saveBtn = document.getElementById('saveBtn');
    const nameEl  = document.getElementById('itemName');
    const priceEl = document.getElementById('itemPrice');
    const bigoEl  = document.getElementById('itemBigo');
    const typeEl  = document.getElementById('itemType');

    editBtn.addEventListener('click', () => {
      [nameEl, priceEl, bigoEl].forEach(el => el.readOnly = false);
      typeEl.disabled = false;
      editBtn.classList.add('hidden');
      saveBtn.classList.remove('hidden');
      nameEl.focus();
    });

    // 보기용 천단위 콤마(서버에서는 숫자만 저장)
    priceEl.addEventListener('input', (e)=>{
      const raw = e.target.value.replace(/[^0-9]/g,'');
      e.target.value = raw.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    });
  </script>
</body>
</html>
