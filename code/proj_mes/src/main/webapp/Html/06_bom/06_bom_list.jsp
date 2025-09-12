<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="cPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>BOM 목록</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/06_bom_list.css">
	<script src ="${pageContext.request.contextPath}/Html/asset/06_bom_list.js" ></script>
	
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
    
	<div class="titleBox"></div>

	<div class="wrap">
		<form class="lookup" method="post" action="bom">
		<input type="hidden" name="action" value="search">
			<div class="lookup_left">
				<div class = "lookup-title">품목(완제품) 코드</div>
				<select name = "filter-item1">
					<!-- TODO 지금 하드코딩 해놨는데 DB에서 따오도록 설정 바꿔두기 -->
					<!-- 여기에서 name이든 뭐든 설정해야 하니까 아래 테이블처럼 설정해두기 -->
					<option value="all" selected>전체</option>
					<option value="FPPHS23">FPPHS23</option>
					<option value="FPPHA17">FPPHA17</option>
					<option value="FPNIN3D">FPNIN3D</option>
					<option value="FPNINLI">FPNINLI</option>
					<option value="FPNVI05">FPNVI05</option>
					<option value="FPNVI07">FPNVI07</option>
				</select>
			</div>
			<div class="btnDiv">
				<input type="submit" value="조회" class="lookupBtn btn">
				<input type="button" value="신규" class="newBOMBtn btn open-btn">
			</div>
		</form>

		<div class="table-container">
    		<table class="bom_tab">
				<thead>
					<tr>
						<th>BOM No.</th>
						<th>완제품</th>
						<th>자재</th>
						<th>소요량</th>
					</tr>		
				</thead>
				<tbody>
					<c:forEach var="bom" items="${list}">
						<tr>
							<td>${bom.bomID}</td>
							<td>${bom.item_code_1}</td>
							<td>${bom.item_code_2}</td>
							<td>${bom.require_amount}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
        </div>
		<div class="bottomBtn">
			<input type="button" value="수정" class="updateBOMBtn btn"></input>
		</div>
	</div>
	<!-- 등록용 -->
    <div class="panel">
           <button class="close-btn">✕</button>
           <h2>BOM 등록</h2>
           <form action="bom" method="post">
	           <input type="hidden" name="action" value="add">
	           <input type="hidden" name="bomID" value="">
               <div class="form-group">
                   <label>완제품 품번</label>
                   <input type="text" name = "item-code-1">
               </div>
               <div class="form-group">
                   <label>자재 품번</label>
                   <input type="text" name="item-code-2">
               </div>
               <div class="form-group">
                   <label>자재 소요량</label>
                   <input type="number" name="require-amount">
               </div>             
               <div class="form-actions">
                   <input type = "button" name="panel-cancel" class="button cancel reset" value="취소">
                   <button type="submit" name="panel-save" class="button panel-save">저장</button>
               </div>
           </form>
       </div>
</body>
</html>