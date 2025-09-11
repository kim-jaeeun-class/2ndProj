<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
  String ctx = request.getContextPath();
  String message = request.getParameter("message");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>신규 품목 등록</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">
  <div class="p-4 sm:p-8 max-w-7xl mx-auto">
    <div class="max-w-4xl mx-auto bg-white rounded-xl shadow-xl p-8">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">신규 품목 등록</h2>

      <% if (message != null && !message.isEmpty()) { %>
        <div class="bg-blue-50 border border-blue-200 text-blue-800 px-4 py-3 rounded mb-4"><%= message %></div>
      <% } %>

      <form method="post" action="<%= ctx %>/items" class="space-y-6">
        <input type="hidden" name="action" value="create"/>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">품목 코드</label>
            <input type="text" name="itemCode" class="mt-1 block w-full px-4 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500" required>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">품목명</label>
            <input type="text" name="itemName" class="mt-1 block w-full px-4 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500" required>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">단가</label>
            <input type="text" id="price" name="itemPrice" class="mt-1 block w-full px-4 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500" required>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">품목 유형</label>
            <select name="itemType" class="mt-1 block w-full px-4 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500" required>
              <option value="">선택</option>
              <option value="1">완제품</option>
              <option value="2">소모품</option>
              <option value="3">원자재</option>
              <%-- <option value="4">반제품</option> --%>
            </select>
          </div>
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-1">비고</label>
            <input type="text" name="itemBigo" class="mt-1 block w-full px-4 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500">
          </div>
        </div>
        <div class="flex justify-end space-x-4 mt-6">
          <a href="<%= ctx %>/items" class="bg-gray-400 text-white font-semibold py-2 px-6 rounded-md hover:bg-gray-500 transition-colors">취소</a>
          <button type="submit" class="bg-blue-600 text-white font-semibold py-2 px-6 rounded-md hover:bg-blue-700 transition-colors">저장</button>
        </div>
      </form>
    </div>
  </div>

  <script>
    // 보기용 천단위 콤마
    const price = document.getElementById('price');
    price.addEventListener('input', (e)=>{
      const raw = e.target.value.replace(/[^0-9]/g,'');
      e.target.value = raw.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    });
  </script>
</body>
</html>
