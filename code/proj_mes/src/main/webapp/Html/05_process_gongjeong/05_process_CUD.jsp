<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>공정 등록/수정/삭제</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/05_process_CUD.css">
	<script src="${pageContext.request.contextPath}/Html/asset/05_process_CUD.js"></script>

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
		<span>공정 등록 / 수정 / 삭제</span>
		<a href="${pageContext.request.contextPath}/Html/02_main/mainpage.html">
			<div class="toMainpage">
				<img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표" style="transform: scaleX(-1);">
				메인 화면으로
			</div>
		</a>
	</div>

	<div class="wrap">		
	    <form id="processCUDForm" action="process" method="post" enctype="multipart/form-data">
	        <input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">
	        <input type="hidden" name="action" id="action">
	        <input type="hidden" name="procId" id="procId_hidden">
	
	        <div class="processInfo">
	            <div class="table-container">
	                <table class="inputTb">
						<tr>
							<td>품목 코드</td>
							<td>
							    <select id="itemCodeSelectCUD" name="itemCode" <c:if test="${mode eq 'update'}">disabled</c:if>>
								    <option value="">선택</option>
								    <c:forEach var="itemCode" items="${itemCodes}">
								        <option value="${itemCode}" <c:if test="${process.item_code eq itemCode}">selected</c:if>>
								            ${itemCode}
								        </option>
								    </c:forEach>
								</select>
								<c:if test="${mode eq 'update'}">
								    <input type="hidden" name="itemCode" value="${process.item_code}">
								</c:if>
							</td>
						</tr>
						<tr>
							<td>공정 코드</td>
							<td><input type="text" id="procId" name="procId" value="${process.proc_id}">
						</tr>
						<tr>
							<td>공정 순서</td>
							<td><input type="text" id="procSeq" name="procSeq" value="${process.proc_seq}"></td>
						</tr>
						<tr>
						    <td>소속 부서</td>
						    <td>
						        <select id="departSelect" name="departLevel">
								    <option value="">선택</option>
								    <c:forEach var="departLevel" items="${departLevels}"> <!-- getUniqueDepartLevels() -->
								        <option value="${departLevel}" <c:if test="${process.depart_level eq departLevel}">selected</c:if>>
								            ${departLevel}
								        </option>
								    </c:forEach>
								</select>
						    </td>
						</tr>
						<tr>
							<td>공정</td>
						    <td>                
						        <select id="procNameSelect" name="procName" data-initial-proc="${process.proc_name}">
							        <option value="">선택</option>
							        <c:forEach var="procName" items="${procNames}">
							            <option value="${procName}" <c:if test="${process.proc_name eq procName}">selected</c:if>>
							                ${procName}
							            </option>
							        </c:forEach>
							    </select>
						    </td>
						</tr>
						<tr>
							<td>공정 설명</td>
							<td>
								<textarea id="procInfo" name="procInfo" rows="5" cols="40">${process.proc_info}</textarea>
							</td>
						</tr>
					</table>
				</div>
				
				<div class="imgDiv">
	                공정이미지
	                <div>
	                    <input type="file" name="procImage" id="procImageInput">
	                    <c:if test="${not empty process.proc_img}">
	                        <img src="${pageContext.request.contextPath}/${process.proc_img}" alt="공정 이미지" id="procImage">
	                        <button type="button" id="deleteImageBtn">이미지 삭제</button>
	                    </c:if>
	                    <c:if test="${empty process.proc_img}">
	                        <img src="" alt="공정 이미지" id="procImage" style="display:none;">
	                    </c:if>
	                    <input type="hidden" name="deleteImage" id="deleteImageHidden" value="false">
	                </div>
	            </div>
        	</div>

	        <div class="bottomBtn">
	            <input type="button" value="삭제" class="deleteBtn btn"></input>
	            <input type="button" value="수정" class="updateBtn btn"></input>
	            <input type="button" value="등록" class="createBtn btn"></input>
	        </div>
		</form>
	</div>
</body>
</html>