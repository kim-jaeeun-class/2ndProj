<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>불량률 현황</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/10_defect_rate.css">

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
    
   	<div class="sidenwrap">
       <div class="side">
           <a href="/proj_mes/lotTracking"><div class="side-menu">LOT 추적 관리</div></a>
           <a href="/proj_mes/inspection"><div class="side-menu">품질 검사 입력</div></a>
           <a href="/proj_mes/defectRate"><div class="side-menu">불량률 현황</div></a>
       </div>
       
	<div class="wrap">
		<div class="wrap-title">불량률 현황</div>

		    <form method="get" action="${pageContext.request.contextPath}/defectRate">
		        <!-- 품목 검색 -->
		        품명:
		        <select name="itemCode">
		            <option value="">전체</option>
		            <c:forEach var="item" items="${itemList}">
		                <option value="${item.item_code}" 
		                    <c:if test="${selectedItemCode == item.item_code}">selected</c:if>>
		                    ${item.item_code} (${item.item_name})
		                </option>
		            </c:forEach>
		        </select>
		
		        <!-- 공정 검색 -->
		        공정별:
			<select name="procId">
			    <option value="">전체</option>
			    <c:forEach var="proc" items="${processList}">
			        <option value="${proc.proc_id}" 
			            <c:if test="${selectedProcId == proc.proc_id}">selected</c:if>>
			            ${proc.proc_name}
			        </option>
			    </c:forEach>
			</select>
		
		        <!-- 일자 검색 -->
		        일자:
		        <input type="date" name="date" value="${selectedDate}">
		
		        <button type="submit">검색</button>
		    </form>
		</div>

		<!-- 불량률 현황 테이블 -->
		<div class="table-container">
		    <table class="lot_tab">
		        <thead>
		            <tr>
		                <th>공정명</th>
		                <th>일자</th>
		                <th>품목 ID</th>   <!-- ✅ "품명" → "품목 ID" -->
		                <th>생산 수량</th>
		                <th>불량 수량</th>
		                <th>불량률</th>
		            </tr>       
		        </thead>
		        <tbody>
		            <c:set var="totalProduction" value="0" />
		            <c:set var="totalDefect" value="0" />
		
		            <c:forEach var="dr" items="${defectRates}">
		                <tr>
		                    <td>${dr.procName}</td>
		                    <td>${dr.inspectionDate}</td>
		                    <td>${dr.itemCode}</td>   <!-- ✅ itemName → itemCode -->
		                    <td>
		                        ${dr.productionQty}
		                        <c:set var="totalProduction" value="${totalProduction + dr.productionQty}" />
		                    </td>
		                    <td>
		                        ${dr.defectQty}
		                        <c:set var="totalDefect" value="${totalDefect + dr.defectQty}" />
		                    </td>
		                    <td>
		                        <fmt:formatNumber value="${dr.defectRate}" type="number" maxFractionDigits="2" />%
		                    </td>
		                </tr>
		            </c:forEach>

				    <c:if test="${empty defectRates}">
				        <tr>
				            <td colspan="6" style="text-align:center;">검색 결과가 없습니다.</td>
				        </tr>
				    </c:if>
				</tbody>

				<tfoot>
				    <tr style="font-weight:bold; background:#f3f4f6;">
				        <td colspan="3" style="text-align:right;">총 합계</td>
				        <td>${totalProduction}</td>
				        <td>${totalDefect}</td>
				        <td>
				            <c:choose>
				                <c:when test="${totalProduction > 0}">
				                    <fmt:formatNumber value="${(totalDefect * 100.0 / totalProduction)}" 
				                                      type="number" maxFractionDigits="2" />%
				                </c:when>
				                <c:otherwise>0.00%</c:otherwise>
				            </c:choose>
				        </td>
				    </tr>
				</tfoot>
			</table>
		</div>
	</div>	
</body>
</html>