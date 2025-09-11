<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>신규 품목 등록</title>
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
    String error = (String) request.getAttribute("error");
%>

<div class="p-4 sm:p-8 max-w-7xl mx-auto">
    <div class="max-w-4xl mx-auto bg-white rounded-xl shadow-xl p-8">
        <h2 class="text-2xl font-bold text-gray-800 mb-6">신규 품목 등록</h2>

        <% if (error != null) { %>
        <div class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded mb-4"><%= error %></div>
        <% } %>

        <form id="itemRegisterForm" class="space-y-6" method="post" action="<%= request.getContextPath() %>/items">
            <input type="hidden" name="action" value="create"/>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <label for="itemCode" class="block text-sm font-medium text-gray-700 mb-1">품목 코드</label>
                    <input type="text" id="itemCode" name="itemCode" class="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500">
                </div>
                <div>
                    <label for="itemName" class="block text-sm font-medium text-gray-700 mb-1">품목명</label>
                    <input type="text" id="itemName" name="itemName" class="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500">
                </div>
                <div>
                    <label for="itemPrice" class="block text-sm font-medium text-gray-700 mb-1">단가</label>
                    <input type="text" id="itemPrice" name="itemPrice" class="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500">
                </div>
                <div>
                    <label for="itemType" class="block text-sm font-medium text-gray-700 mb-1">품목 유형</label>
                    <select id="itemType" name="itemType" class="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500">
                        <option value="완제품">완제품</option>
                        <option value="소모품">소모품</option>
                        <option value="원자재">원자재</option>
                    </select>
                </div>
            </div>
            <div class="flex justify-end space-x-4 mt-6">
                <a href="items" class="bg-gray-400 text-white font-semibold py-2 px-6 rounded-md hover:bg-gray-500 transition-colors">취소</a>
                <button type="submit" class="bg-blue-600 text-white font-semibold py-2 px-6 rounded-md hover:bg-blue-700 transition-colors">저장</button>
            </div>
        </form>
    </div>
</div>

</body>
</html>
