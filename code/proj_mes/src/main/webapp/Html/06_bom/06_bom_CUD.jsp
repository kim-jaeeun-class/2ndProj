<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>BOM 등록/수정/삭제</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/06_bom_CUD.css">
	<script src ="${pageContext.request.contextPath}/Html/asset/06_bom_CUD.js" ></script>

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
		<span>BOM / 등록 / 수정 / 삭제</span>
		<a href="${pageContext.request.contextPath}/Html/02_main/mainpage.html">
			<div class="toMainpage">
				<img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표" style="transform: scaleX(-1);">
				메인 화면으로
			</div>
		</a>
	</div>

	<div class="wrap">
        <div class="lookup">
            <!-- 세션에 따라 수정 가능 or read only -->
			<div>품목 코드 &nbsp;&nbsp;<input type="text" placeholder="수정 가능 or read only"></div>
            <div>품목명 &nbsp;&nbsp;<input type="text" placeholder="수정 가능 or read only"></div>
			<div>작성자 &nbsp;&nbsp;<input type="text" placeholder="수정 가능 or read only"></div>
		</div>

		<div class="table-container">
			<table class="bom_tab">
				<thead>
					<tr>
						<th style="width: 30px;">선택</th>
						<th style="width: 33px;">No.</th>
						<th>완제</th>
						<th>자재</th>
						<th>소요량</th>
					</tr>		
				</thead>
				<tbody>

				</tbody>
			</table>
		</div>
		<div class="itembtnDiv">
			<input type="button" value="항목 추가" class="addItemBtn btn"></input>
			<input type="button" value="항목 삭제" class="delItemBtn btn"></input>
		</div>

		<div class="bottomBtn">
			<input type="button" value="삭제" class="deleteBtn btn"></input>
			<input type="button" value="수정" class="updateBtn btn"></input>
			<input type="button" value="등록" class="createBtn btn"></input>
		</div>
	</div>
</body>
</html>