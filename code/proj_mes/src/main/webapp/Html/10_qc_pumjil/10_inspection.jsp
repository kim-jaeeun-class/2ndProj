<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>품질 검사 입력</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/10_inspection.css">
	<script src="${pageContext.request.contextPath}/Html/asset/10_inspection.js"></script>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f3f4f6;
        }

        /* 차트 캔버스의 최대 높이 설정 */
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
		<span>품질 검사 입력</span>
		<a href="">
			<div class="toMainpage">
				<img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표"
					style="transform: scaleX(-1);">
				메인 화면으로
			</div>
		</a>
	</div>

	<div class="wrap">
        <div class="topBox">
		    <h2>불량 처리 입력</h2>
		    <table class="inputTb" border="1" cellspacing="0" cellpadding="5" style="width:100%; border-collapse:collapse;">
		        <tr>
		            <td>검사 이력 코드</td>
		            <td>검사 종류</td>
		            <td>LOT 번호 선택</td>
		            <td>현재 공정 선택</td>
		        </tr>
		        <tr>
		            <td><input type="text" placeholder="검사 이력 코드 자동으로 DB에"></td>
		            <td>
		                <select>
		                    <option value="1">전수</option>
		                    <option value="2">샘플</option>
		                    <option value="3">재검</option>
		                </select>
		            </td>
     		        <td>
		                <select>
		                    <option selected>Lot 번호 선택</option>
		                </select>
		            </td>
		            <td>
		                <select>
		                    <option selected>공정 선택</option>
		                </select>
		            </td>
		        </tr>
		        <tr>
		            <td>일자</td>
		            <td>검사 시작 시간</td>
		            <td>검사 종료 시간</td>
		            <td>작업자</td>
		        </tr>
		        <tr>
		            <td><input type="date"></td>
		            <td><input type="time"></td>
		            <td><input type="time"></td>
		            <td><input type="text"></td>
		        </tr>
		        <tr>
		            <td>불량 사유 입력</td>
		            <td>양품 개수</td>
		            <td>불량 개수</td>
		            <td>품질 상태</td>
		        </tr>
		        <tr>
		            <td><input type="text"></td>
		            <td><input type="number"></td>
		            <td><input type="number"></td>
		            <td>
		            	<select>
		            		<option>양품</option>
		            		<option>재검 대기</option>
		            		<option>폐기</option>
		            	</select>
		            <td>
		        </tr>
		    </table>
		    <input type="button" class="inputBtn btn" value="입력">
		</div>

        <div class="table-container">
    		<table class="insp_tab">
				<thead>
					<tr>
						<th><input type="checkbox" id="allCheck"></th> <!-- 전체 선택 기능 -->
						<th>검사 이력 코드</th>
						<th>검사 종류</th>
						<th>LOT 번호</th>
						<th>공정</th>
						<th>일자</th>
						<th>불량 수량</th>
						<th>불량명</th>
						<th>비고</th>
					</tr>		
				</thead>
				<tbody>
					<tr>
						<td>ex)</td>
						<td>전수/샘플/재검</td>
						<td>Lot 번호</td>
						<td>품명</td>
						<td>공정명</td>
						<td>양품 수량</td>
						<td>불량 수량</td>
						<td>불량명</td>
						<td>비고</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="bottomBtn">
            <input type="button" value="항목 삭제" class="deleteBtn btn"></input>
			<input type="button" value="저장" class="saveBtn btn"></input>
		</div>
	</div>
</body>
</html>