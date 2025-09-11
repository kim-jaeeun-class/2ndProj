<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="cPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>J2P4 :: 생산 계획</title>
        <link rel="stylesheet" href="<c:url value='/Html/asset/font.css'/>">
        <link rel="stylesheet" href="<c:url value='/Html/asset/09_common.css'/>">
        <script src="/proj_mes/Html/asset/template_load.js"></script>
        
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- 동적 차트를 위한 Chart.js CDN -->
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
        
        /* 메인 내비게이션 목록의 호버 배경색을 파란색으로 변경하여 글씨가 잘 보이게 수정 */
        .mainList li:hover {
            background-color: #3b82f6;
        }
    </style>
</head>
<body class="bg-gray-100 text-gray-800">

    <!-- 헤더 섹션 -->
    <!-- Tailwind 클래스 대신 직접 정의한 header-bg 클래스를 적용 -->
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

    <!-- 메인 내비게이션 -->
    <!-- Tailwind 클래스 대신 직접 정의한 nav-bg 클래스 적용 -->
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
        <!-- 타이틀 -->
        <div class="titleBox">
            <span>생산 계획</span>
            <a href="">
                <div class="toMainpage">
                    <img
                        src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png"
                        width="13"
                        alt="메인 화면으로 가는 화살표"
                        style="transform:scaleX(-1);"/>
                    메인 화면으로
                </div>
            </a>
        </div>
        <div class="sidenwrap">
            <div class="side">
                <!-- a 링크 나중에 달기 -->
                <a href="proplan"><div class="side-menu">생산 계획</div></a>
                <a href="workorder"><div class="side-menu">작업 지시서</div></a>
            </div>
            <div class="wrap">
                <!-- 여기 왜 sts에서 조건 wrap css가 안 먹냐? -->
                <div class="wrap-select">
                    <form class="date-filter" action="proplan" method="post">
                    	<input type="hidden" name="action" value="search">
                        <div class="select-con">
                            <div class="select-title">계획 시작일</div>
                            <div class="filter-date select-drop">
                                <input type="date" name = "filter-startDate">
                                <div>~</div>
                                <input type="date" name = "filter-endDate">
                            </div>                        
                        </div>
                        <div class="select-con">
                            <div class="select-title">정렬</div>
                            <select name="align" class="select select-drop">
                                <option value="upDate" selected="selected">시작일(오름차순)</option>
                                <option value="downDate">시작일(내림차순)</option>
                            </select>
                        </div>
                        <div class="filter-submit">
                            <button type="submit" class="button" name="search">조회</button>
                        </div>
                    </form>
                </div>
                <!-- 테이블 영역 -->
                <form class="wrap-table" action="proplan" method="post">
                	<input type="hidden" name="action" value="delete">
                    <div class = "table-view">
                        <table>
                            <thead>
                                <th></th>
                                <th>생산계획번호</th>
                                <th>제품 번호</th>
                                <th>계획 수량</th>
                                <th>계획 기간</th>
                                <th>진행률</th>
                                <th>달성률</th>
                                <th>불량률</th>
                                <th>비고</th>
                            </thead>
                            <tbody>
<!--                                 <tr class = "data"> -->
<%--                                     <td><input type="checkbox" value="${pp.cpID}" name="chk"></td> --%>
<!--                                     <td>#생산계획번호</td> -->
<!--                                     <td>#제품번호</td> -->
<!--                                     <td>#계획수량</td> -->
<!--                                     <td>#계획기간</td> -->
<!--                                     <td>#진행률</td> -->
<!--                                     <td>#달성률</td> -->
<!--                                     <td>#불량률</td> -->
<!--                                     <td>#비고</td> -->
<!--                                 </tr> -->
                                <c:forEach var="pp" items="${list}">
	                                <tr class= "data">
	                                	<td><input type="checkbox" value="${pp.cpID}" name="chk"></td>
	                                	<td>${pp.cpID}</td>
	                                	<td>${pp.itemCode}</td>
	                                	<td>${pp.cpCount}</td>
	                                	<td>${pp.startDate} ~ ${pp.endDate}</td>
	                                	<td>${pp.cpRate}</td>
	                                	<td>${pp.successRate}</td>
	                                	<td>${pp.defectRate}</td>
	                                	<td>${pp.bigo}</td>
	                                </tr>
                                </c:forEach>
                            </tbody>
                        </table>                   
                    </div>
                    <div class="wrap-tableBtn">
                        <input type="submit" name="main-sel-delete" class="button delete-btn" value="삭제">
                        <input type="button" name="main-apply" class="button open-btn" value="등록">
                    </div>
                </form>
            </div>                
        </div>


        <!-- 사이드 패널 : 입력란 좀 안 비어보이게(버튼은 조금 내리고), 품번 서치 가능하게 -->
        <div class="panel">
            <button class="close-btn">✕</button>
            <h2>생산 계획 등록</h2>
            <form action="proplan" method="post">
             	<input type="hidden" name="action" value="add">
                <div class="form-group">
                    <label>제품 번호</label>
                    <input type="text" name = "item-no">
                </div>
                <div class="form-group">
                    <label>계획 수량</label>
                    <input type="number" name="plan-amount">
                </div>
                <div class="form-group">
                    <label>계획 기간</label>
                    <div class="label-child">시작일</div>
                    <input type="date" name="plan-start">
                    <div class="label-child">종료일</div>
                    <input type="date" name="plan-end">
                </div>             
                <div class="form-group">
                    <label>비고</label>
                    <input type="text" name = "bigo">
                </div>
                <!-- 생각해보니 진행/불량률을 input로 넣으면 안 된다... -->
                <div class="form-actions">
                    <input type = "button" name="panel-cancel" class="button cancel reset" value="취소">
                    <button type="submit" name="panel-save" class="button panel-save">저장</button>
                </div>
            </form>
        </div>
        <!-- 생산 계획 모달 -> 슬라이드로 바꿈 -->
        <div class="panel" id="panel-down">
            <button class="close-btn">✕</button>
                <h2>생산 계획 상세</h2>
                <form class="wrap-table panel-table" action="proplan" method="post">
                	<input type="hidden" name="action" value="delete">
                	<input type="hidden" name="cpID" id="detail-delete-id">
                    <table class = "modal-table">
                        <tr>
                            <td class="modal-table-title">생산계획번호</td>
                            <td class="modal-table-con">#생산계획번호</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">제품번호</td>
                            <td class="modal-table-con">#품번</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">계획 수량</td>
                            <td class="modal-table-con">#수량</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">계획 기간</td>
                            <td class="modal-table-con">#기간</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">진행률</td>
                            <td class="modal-table-con">#수치(%)</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">달성률</td>
                            <td class="modal-table-con">#수치(%)</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">불량률</td>
                            <td class="modal-table-con">#수치(%)</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">비고</td>
                            <td class="modal-table-con">#비고(공란 가능)</td>
                        </tr>
                    </table>
                    <table class="item-table">
                        <thead>
                            <tr>
                                <th style="width: 30%">작업지시번호</th>
                                <th>지시일</th>
                                <th style="width: 15%;">지시수량</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>#작업지시번호</td>
                                <td>#지시일</td>
                                <td>#수량</td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="wrap-tableBtn">
                        <input type="button" name="detail-modify" class="button" value="수정">
                        <input type="submit" name="detaile-delete" class="button delete" value="삭제">
                    </div>
                </form>
            </div>
        </div>

        <script src="<c:url value='/Html/asset/09_common.js'/>"></script>
    </body>
</html>