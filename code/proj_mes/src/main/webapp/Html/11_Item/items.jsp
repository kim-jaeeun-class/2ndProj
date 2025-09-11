<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.example.item.Item" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>품목 관리</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
        .header-bg { background-color: #002a40; }
        .nav-bg { background-color: #003751; }
        .mainList, #noticeContent, #boardContent { cursor: pointer; }
        .mainList li:hover { background-color: #3b82f6; }
        .hidden-section { display: none; }
    </style>
</head>
<body class="bg-gray-100 text-gray-800">

<header class="header-bg text-white p-4 shadow-lg flex justify-between items-center z-50 relative">
    <div class="flex items-center space-x-2">
        <img src="https://i.postimg.cc/qMsq73hD/icon.png" class="w-10" alt="회사 로고">
        <h3 class="text-xl font-bold">J2P4</h3>
    </div>
    <div class="flex items-center space-x-4 sm:space-x-8">
        <div class="search cursor-pointer">
            <img src="https://i.postimg.cc/9QcMwQym/magnifier-white.png" class="w-6 sm:w-7" alt="검색용 아이콘">
        </div>
        <div class="logout text-sm sm:text-base cursor-pointer hover:underline">로그아웃</div>
        <div class="myIcon relative">
            <a href="javascript:void(0);" id="myIconBtn">
                <img src="https://i.postimg.cc/zfVqTbvr/user.png" class="w-8 sm:w-9 rounded-full bg-white" alt="마이페이지 아이콘">
            </a>
        </div>
    </div>
</header>

<nav class="nav-bg text-white py-2 shadow-inner z-40 relative">
    <ul class="mainList flex flex-wrap justify-center text-sm sm:text-base">
        <li class="relative px-2 sm:px-4 py-2 rounded-md">기준 관리</li>
        <li class="relative px-2 sm:px-4 py-2 rounded-md">공정 관리</li>
        <li class="relative px-2 sm:px-4 py-2 rounded-md">BOM 관리</li>
        <li class="relative px-2 sm:px-4 py-2 rounded-md">발주 관리</li>
        <li class="relative px-2 sm:px-4 py-2 rounded-md">재고 관리</li>
        <li class="relative px-2 sm:px-4 py-2 rounded-md">생산 관리</li>
        <li class="relative px-2 sm:px-4 py-2 rounded-md">품질 관리</li>
        <li class="relative px-2 sm:px-4 py-2 rounded-md">품목 관리</li>
    </ul>
</nav>

<%
    List<Item> items = (List<Item>) request.getAttribute("items");
    String selectedType = (String) request.getAttribute("selectedType");
    if (selectedType == null) selectedType = "전체";
    String message = request.getParameter("message");
%>

<div id="item-list-section" class="p-4 sm:p-8 max-w-7xl mx-auto">
    <h2 class="text-2xl sm:text-3xl font-bold text-gray-900 mb-6">품목 목록</h2>

    <!-- 메시지 -->
    <%
        if (message != null && !message.isEmpty()) {
    %>
        <div class="bg-blue-50 border border-blue-200 text-blue-800 px-4 py-3 rounded mb-4">
            <%= message %>
        </div>
    <%
        }
    %>

    <!-- 필터 영역 -->
    <div class="bg-white p-6 rounded-xl shadow-xl mb-6">
        <form id="filterForm" action="<%= request.getContextPath() %>/items" method="get" class="flex flex-col md:flex-row items-center space-y-4 md:space-y-0 md:space-x-4">
            <div class="flex-1 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 w-full">
                <div class="flex flex-col">
                    <label for="select-wo" class="text-sm font-medium text-gray-700 mb-1">품목 유형</label>
                    <select id="select-wo" name="select-wo" class="select border rounded-md px-3 py-2 text-gray-800 focus:ring-blue-500 focus:border-blue-500">
                        <option value="전체" <%= "전체".equals(selectedType) ? "selected" : "" %>>전체</option>
                        <option value="완제품" <%= "완제품".equals(selectedType) ? "selected" : "" %>>완제품</option>
                        <option value="소모품" <%= "소모품".equals(selectedType) ? "selected" : "" %>>소모품</option>
                        <option value="원자재" <%= "원자재".equals(selectedType) ? "selected" : "" %>>원자재</option>
                    </select>
                </div>
            </div>
            <div class="w-full md:w-auto">
                <button type="submit" id="searchBtn" class="bg-blue-600 text-white rounded-md px-6 py-2 hover:bg-blue-700 transition-colors duration-200 w-full md:w-auto">조회</button>
            </div>
        </form>
    </div>

    <!-- 테이블 영역 -->
    <div class="bg-white p-6 rounded-xl shadow-xl overflow-x-auto">
        <form id="listForm" action="<%= request.getContextPath() %>/items" method="post">
            <input type="hidden" name="action" value="delete"/>
            <div class="flex justify-end mb-4 space-x-2">
                <button type="submit" id="deleteBtn" class="bg-red-500 text-white rounded-md px-4 py-2 hover:bg-red-600 transition-colors duration-200"
                        onclick="return confirm('선택한 품목을 삭제하시겠습니까?');">삭제</button>
                <a href="item_form.jsp" class="bg-green-500 text-white rounded-md px-4 py-2 hover:bg-green-600 transition-colors duration-200">등록</a>
            </div>
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            <input type="checkbox" id="selectAllCheckbox" class="rounded text-blue-600 focus:ring-blue-500" onclick="toggleAll(this)">
                        </th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">품목 코드</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">품목명</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">단가</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">유형</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                <%
                    if (items != null) {
                        for (Item item : items) {
                %>
                    <tr class="hover:bg-gray-50">
                        <td class="px-6 py-4 whitespace-nowrap">
                            <input type="checkbox" name="selectedCode" value="<%= item.getItemCode() %>" class="item-checkbox rounded text-blue-600 focus:ring-blue-500">
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900"><%= item.getItemCode() %></td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900"><%= item.getItemName() %></td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900"><%= String.format("%,d", item.getItemPrice()) %></td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900"><%= item.getItemType() %></td>
                    </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>
        </form>
    </div>
</div>

<script>
function toggleAll(master) {
    document.querySelectorAll('.item-checkbox').forEach(cb => cb.checked = master.checked);
}
</script>
</body>
</html>
