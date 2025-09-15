<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Lot 추적 관리</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/10_lot_tracking.css">

    <!-- Tailwind (개발용 CDN, 운영 배포 시 PostCSS 방식으로 교체 필요) -->
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">

    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
        .header-bg { background-color: #002a40; }
        .nav-bg { background-color: #003751; }
        .mainList { cursor: pointer; }
        .mainList li:hover { background-color: #3b82f6; }
    </style>
</head>
<body class="bg-gray-100 text-gray-800">

    <!-- 헤더 -->
    <header class="header-bg text-white p-4 shadow-lg flex justify-between items-center">
        <div class="flex items-center space-x-2">
            <img src="https://i.postimg.cc/qMsq73hD/icon.png" class="w-10" alt="회사 로고">
            <h3 class="text-xl font-bold">J2P4</h3>
        </div>
        <div class="flex items-center space-x-4">
            <div class="logout cursor-pointer hover:underline">
                <form id="logoutForm" method="post" style="display:none"></form>
                <a href="#" id="logoutLink">로그아웃</a>
            </div>
        </div>
    </header>

    <nav class="nav-bg text-white py-2 shadow-inner z-40 relative">
        <ul class="mainList flex flex-wrap justify-center text-sm sm:text-base">
            <a href = "/proj_mes/StandardCtrl"><li class="relative px-2 sm:px-4 py-2 rounded-md">기준 관리</li></a>
            <a href = "/proj_mes/process"><li class="relative px-2 sm:px-4 py-2 rounded-md">공정 관리</li></a>
            <a href = "/proj_mes/bom"><li class="relative px-2 sm:px-4 py-2 rounded-md">BOM 관리</li></a>
            <a href = "/proj_mes/orderList"><li class="relative px-2 sm:px-4 py-2 rounded-md">발주 관리</li></a>
            <a href = "/proj_mes/stockList"><li class="relative px-2 sm:px-4 py-2 rounded-md">재고 관리</li></a>
            <a href = "/proj_mes/proplan"><li class="relative px-2 sm:px-4 py-2 rounded-md">생산 관리</li></a>
            <a href = "/proj_mes/items"><li class="relative px-2 sm:px-4 py-2 rounded-md">품목 관리</li></a>           
            <a href = "/proj_mes/inspection"><li class="relative px-2 sm:px-4 py-2 rounded-md">품질 관리</li></a>
        </ul>
    </nav>
    
    <div class="titleBox">
        <span>LOT 추적 관리</span>
        <a href="${pageContext.request.contextPath}/Html/02_main/mainpage.html">
            <div class="toMainpage">
                <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인으로" style="transform: scaleX(-1);">
                메인 화면으로
            </div>
        </a>
    </div>

    <div class="wrap">
        <!-- 검색 조건 -->
        <div class="lookup">
            <form method="get" action="${pageContext.request.contextPath}/lotTracking">
                품목 코드:
                <select name="itemCode">
                    <option value="">전체</option>
                    <c:forEach var="item" items="${itemList}">
                        <option value="${item.item_code}" 
                            <c:if test="${selectedItemCode == item.item_code}">selected</c:if>>
                            ${item.item_code}
                        </option>
                    </c:forEach>
                </select>

                일자:
                <input type="date" name="date" value="${selectedDate}">
                <button type="submit">검색</button>
            </form>
        </div>

        <!-- 메인 LOT 표 -->
        <div class="table-container">
            <table class="lot_tab">
                <thead>
                    <tr>
                        <th>Lot 번호</th>
                        <th>품목 코드</th>
                        <th>품명</th>
                        <th>수량</th>
                        <th>입고일</th>
                    </tr>        
                </thead>
                <tbody>
                    <c:forEach var="lot" items="${lotList}">
                        <tr>
                        	<td>
								<a href="#" class="lotLink"
								   data-lot="${lot.lotId}" 
								   data-item="${lot.itemName}" 
								   data-itemcode="${lot.itemCode}"  
								   data-date="${lot.startTime}">
								   ${lot.lotId}
								</a>
                            </td>
                            <td>${lot.itemCode}</td>
                            <td>${lot.itemName}</td>
                            <td>${lot.quantity}</td>
                            <td>${lot.startTime}</td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty lotList}">
                        <tr>
                            <td colspan="5" style="text-align:center;">검색 결과가 없습니다.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    
        <!-- LOT 상세 모달 -->
        <div id="lotModal" class="modal">
            <div class="modal-content">
                <span id="modalClose">&times;</span> <!-- ✅ ID 변경 -->
                <h2>LOT 상세 내역</h2>
        
                <!-- LOT 기본 스펙 -->
                <table class="modalInfo">
                    <tr><td>LOT 번호</td><td id="modalLotId"></td></tr>
                    <tr>
					    <td>품명</td>
					    <td><span id="modalItemCode"></span> (<span id="modalItem"></span>)</td>
					</tr>
                    <tr><td>가공 일자</td><td id="modalDate"></td></tr>
                </table>
        
                <div class="info2">
                    <!-- 공정 이력 -->
                    <h3>공정 이력</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>공정명</th>
                                <th>시작 시간</th>
                                <th>종료 시간</th>
                                <th>작업자</th>
                            </tr>
                        </thead>
                        <tbody id="procHistoryBody"></tbody>
                    </table>
        
                    <!-- 검사 결과 -->
                    <h3>검사 결과</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>공정명</th>
                                <th>검사 유형</th>
                                <th>불량 유형</th>
                                <th>개수</th>
                                <th>검사자</th>
                                <th>검사 시간</th>
                            </tr>
                        </thead>
                        <tbody id="inspectionResultBody"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/Html/asset/10_lot_tracking.js"></script>
</body>
</html>