<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>Lot 추적 관리</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/10_lot_tracking.css">
	<script src="${pageContext.request.contextPath}/Html/asset/10_lot_tracking.js"></script>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f3f4f6;
        }

        canvas {
            max-height: 200px;
        }

        .header-bg {
            background-color: #002a40;
        }

        .nav-bg {
            background-color: #003751;
        }

        .mainList, #noticeContent, #boardContent {
            cursor: pointer;
        }
        
        .mainList li:hover {
            background-color: #3b82f6;
        }
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
            <div class="logout text-sm sm:text-base cursor-pointer hover:underline">
                <form id="logoutForm" method="post" style="display:none"></form>
				<a href="#" id="logoutLink">로그아웃</a>
            </div>
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
    
	<div class="titleBox">
		<span>LOT 추적 관리</span>
		<a href="${pageContext.request.contextPath}/Html/02_main/mainpage.html">
			<div class="toMainpage">
				<img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표" style="transform: scaleX(-1);">
				메인 화면으로
			</div>
		</a>
	</div>

	<div class="wrap">
		<div class="lookup">
			<div class="lookup_left">
				품목 코드
				<select>
					<option selected>전체</option>
					<option>2</option>
					<option>3</option>
					<option>4</option>
				</select>
				일자 <input type="date">
				공정
				<select> <!-- 품명 검색 -->
					<option selected>전체</option>
					<option>1</option>
					<option>2</option>
					<option>3</option>
					<option>4</option>
				</select>
			</div>
		</div>

		<div class="table-container">
			<table class="lot_tab">
				<thead>
					<tr>
						<th>Lot 번호</th>
						<th>품명</th>
						<th>공정</th>
						<th>가공 일자</th>
						<th>수량</th>
						<th>품질 상태</th>
					</tr>		
				</thead>
				<tbody>
					<tr>
						<td><a href="#" id="lotBtn">Lot 번호1</a></td>
						<td>품명1</td>
						<td>공정1</td>
						<td>가공 일자</td>
						<td>수량</td>
						<td>양품/불량/가공중</td>
					</tr>
					<tr>
						<td>Lot 번호2</td>
						<td>품명2</td>
						<td>공정2</td>
						<td>가공 일자</td>
						<td>수량</td>
						<td>양품/불량/가공중</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- 모달 -->
		<div id="lotModal" class="modal">
			<div class="modal-content">
				<span class="close">&times;</span>
				<h2>Lot 상세 내역</h2>
				<table class="modalInfo">
					<tr>
						<td>LOT 번호</td>
						<td>LOT 번호1</td>
					</tr>
					<tr>
						<td>품명</td>
						<td>품명1</td>
					</tr>
					<tr>
						<td>현 공정</td>
						<td>공정1</td>
					</tr>
					<tr>
						<td>가공 일자</td>
						<td>가공 일자1</td>
					</tr>
				</table>
				<div class="info2">
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
						<tbody>
							<tr>
								<td>공정명1</td>
								<td>시작 시간</td>
								<td>종료 시간</td>
								<td>작업자1</td>
							</tr>
							<tr>
								<td>공정명2</td>
								<td>시작 시간</td>
								<td>종료 시간</td>
								<td>작업자2</td>
							</tr>
							<tr>
								<td>공정명2</td>
								<td>시작 시간</td>
								<td>종료 시간</td>
								<td>작업자3</td>
							</tr>
							<tr>
								<td>공정명4</td>
								<td>시작 시간</td>
								<td>종료 시간</td>
								<td>작업자4</td>
							</tr>
						</tbody>
					</table>
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
						<tbody>
							<tr>
								<td>공정명1</td>
								<td>재검/전수/샘플</td>
								<td>불량명1</td>
								<td>개수</td>
								<td>작업자1</td>
								<td>검사 종료 시간</td>
							</tr>
							<tr>
								<td>공정명2</td>
								<td>재검/전수/샘플</td>
								<td>불량명2</td>
								<td>개수</td>
								<td>작업자2</td>
								<td>검사 종료 시간</td>
							</tr>
							<tr>
								<td>공정명3</td>
								<td>재검/전수/샘플</td>
								<td>불량명3</td>
								<td>개수</td>
								<td>작업자3</td>
								<td>검사 종료 시간</td>
							</tr>
							<tr>
								<td>공정명4</td>
								<td>재검/전수/샘플</td>
								<td>불량명4</td>
								<td>개수</td>
								<td>작업자4</td>
								<td>검사 종료 시간</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</body>
</html>