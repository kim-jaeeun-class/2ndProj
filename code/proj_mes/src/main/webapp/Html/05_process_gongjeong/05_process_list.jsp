<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>공정 목록</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/05_process_list.css">
	<script src ="${pageContext.request.contextPath}/Html/asset/05_process_list.js" ></script>
	
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
		<span>공정 목록</span>
		<a href="${pageContext.request.contextPath}/Html/02_main/mainpage.html">
			<div class="toMainpage">
				<img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표" style="transform: scaleX(-1);">
				메인 화면으로
			</div>
		</a>
	</div>

	<div class="wrap">
		<form id="processForm" action="process" method="get">
			<div class="lookup">
				<div>
					품목코드&nbsp;
					<select name="itemCode" onchange="this.form.submit()">
						<option value="">전체</option>
						<c:forEach var="itemCode" items="${itemCodes}">
							<option value="${itemCode}" <c:if test="${selectedItemCode eq itemCode}">selected</c:if>>
								<c:out value="${itemCode}" />
							</option>
						</c:forEach>
					</select>
				</div>
				<div>	
					부서&nbsp;
					<select name="departLevel" onchange="this.form.submit()">
						<option value="">전체</option>
						<c:forEach var="departLevel" items="${departLevels}">
				           	<option value="${departLevel}" <c:if test="${selectedDepart eq departLevel}">selected</c:if>>
				                <c:out value="${departLevel}" />
				           	</option>
				       	</c:forEach>
					</select>
				</div>
				<div>
					공정명&nbsp;
				    <select name="procName" onchange="this.form.submit()">
						<option value="">전체</option>
						<c:forEach var="procName" items="${procNames}">
				            <option value="${procName}" <c:if test="${selectedProcName eq procName}">selected</c:if>>
				                <c:out value="${procName}" />
				            </option>
				        </c:forEach>
					</select>
				</div>
			</div>
			<div class="btnDiv">
				<input type="button" value="수정" class="updateProcessBtn btn"></input>
				<input type="button" value="신규" class="newProcessBtn btn"></input>
			</div>
		</form>
		
		<div class="table-container" style="display: <c:if test="${not empty processes}">block</c:if>">
			<table class="proc_tab">
				<thead>
					<tr>
						<th>선택</th>
						<th>순번</th>
						<th>품목 코드</th>
						<th>부서</th>
						<th>공정 순서</th>
						<th>공정명</th>
						<th>공정 설명</th>
						<th>이미지</th>
					</tr>		
				</thead>
				<tbody>
					<c:if test="${empty processes}">
						<tr><td colspan="8">조회된 공정 정보가 없습니다.</td></tr>
					</c:if>
					<c:if test="${not empty processes}">
						<c:forEach var="proc" items="${processes}" varStatus="status">
							<tr>
								<td><input type="radio" name="selectedProcId" value="${proc.proc_id}"></td>
								<td><c:out value="${status.count}" /></td>
								<td><c:out value="${proc.item_code}" /></td>
								<td class="departLevelCell"><c:out value="${proc.depart_level}" /></td>
								<td class="procSeqCell"><c:out value="${proc.proc_seq}" /></td>
								<td class="procNameCell"><c:out value="${proc.proc_name}" /></td>
								<td class="procInfoCell"><c:out value="${proc.proc_info}" /></td>
								<td>
								    <c:if test="${not empty proc.proc_img}">
								        <img src="${pageContext.request.contextPath}/${proc.proc_img}" alt="공정 이미지" style="width: 50px; height: 50px;">
								    </c:if>
								</td>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>