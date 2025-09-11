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
        <title>J2P4 :: 작업 지시서</title>
        <link rel="stylesheet" href="<c:url value='/Html/asset/font.css'/>">
        <link rel="stylesheet" href="<c:url value='/Html/asset/09_common.css'/>">
        <script src ="../asset/template_load.js" ></script>
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
	<body class="bg-gray-100 text-gray-800" page="wo">

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
        <div class="titleBox">
            <span>작업 지시서</span>
            <a href="../02_main/mainpage.html">
                <div class="toMainpage">
                    <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표"
                        style="transform: scaleX(-1);">
                    메인 화면으로
                </div>
            </a>            
        </div>
        <div class="sidenwrap">
            <div class="side">
                <!-- a 링크 나중에 달기 -->
                <a href="proplan">
                    <div class="side-menu">생산 계획</div>
                </a>
                <a href="workorder">
                    <div class="side-menu">작업 지시서</div>
                </a>
            </div>
            <div class="wrap">
                <div class="wrap-select">
                    <form class="date-filter" action="workorder">
                    	<input type="hidden" name="action" value="search">
                        <div class="select-con">
                            <div class="select-title" name="wo-filter-title">조회일 입력</div>
							<input type="date" name="wo-filter-date">
                        </div>
                        <div class="filter-submit">
                            <button type="submit" class="button">조회</button>
                        </div>
                    </form>
                </div>
                <form class="wrap-table" method="get" action="workorder">
                	<input type="hidden" name="action" value="delete">
                    <div class="table-view">
                        <table>
                            <thead>
                                <th></th>
                                <th>작업지시번호</th>
                                <th>지시일</th>
                                <th>담당자명</th>
                                <th>납기일자</th>
                                <th>품목명</th>
                                <th>지시수량</th>
                                <th>생산수량</th>
                                <th>인쇄</th>
                            </thead>
                            <tbody>
                                <!-- DTO에 있는 거 wo라고 이름 지어서 꺼내는 것!!! -->
                                <!-- 근데 지금 client 테이블 조인을... 내일 얘기하자 -->
                                <c:forEach var="wo" items="${list}">
                                    <tr class = "data">
                                        <!-- 구분용으로 date, num 다 넣음 -->
                                        <td><input type="checkbox" value="${wo.woNum}" name="chk"></td>
                                        <td>${wo.woNum}</td>
                                        <td>${wo.woDate}</td>
                                        <td>${wo.workerID}</td>
                                        <td>${wo.woDuedate}</td>
                                        <td>${wo.item_name}</td>
                                        <td>${wo.woPQ}</td>
                                        <td>${wo.woAQ}</td>
                                        <td>
                                            <button type="button">인쇄</button>
                                        </td>
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

        <!-- TODO : 사이드 패널 - 여기부터 jsp 변환 작업 진행해야 함 -->
        <div class="panel">
            <button class="close-btn">✕</button>
            <h2>작업 지시서 등록</h2>
            <form method = "post" action = "workorder">
            	<input type="hidden" name="action" value="add">
                <div class="form-group">
                    <label>작업지시NO.</label>
                    <input type="number" name="order-no" min="1" max="5" placeholder="작업지시 번호 입력">
                </div>
                <div class="form-group">
                    <label>지시일</label>
                    <input type="date" name="order-date">
                </div>
                <div class="form-group">
                    <label>납기일</label>
                    <input type="date" name="duedate">
                </div>
                <div class="form-group">
                    <label>담당자</label>
                    <input type="text" name = "person" placeholder="담당자명 입력">
                </div>
                <div class="form-group">
                    <label>지시 수량</label>
                    <input type="number" name="order-amount" min="1" placeholder="지시 수량 입력">
                </div>
                <div class ="form-group">
                    <div class = "wrap-table panel-table">
                        <div class = "wrap-tableBtn">
                            <input type="button" name = "item-add" class="button" value="품목 추가">
                            <input type="button" name = "item-delete" class="button delete" value="품목 삭제">                       
                        </div>
                        <div class = "panel-table-wrap">
                            <table>
                                <thead>
                                    <th>품목코드</th>
                                    <th>품목명</th>
                                    <th>수량</th>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                    </tr>
                                </tbody>
                            </table>  
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <button type="submit" name="add-apply" class="button panel-save">저장</button>
                </div>
            </form>
        </div>
        <!-- 작업 지시서 상세로 가는중~~~ -->
        <div class="panel" id="panel-down">
            <button class="close-btn">✕</button>
                <h2>작업 지시서 상세</h2>
                <form class="wrap-table panel-table" method = "get" action="workorder">
                	<input type="hidden" name="action" value="delete">
                    <table class = "modal-table">
                    		<!--  여기는 메인 테이블 보고 자동으로 뜨도록 -->
<!--                         <tr> -->
<!--                             <td class="modal-table-title">작업지시번호</td> -->
<!--                             <td class="modal-table-con">#작업지시번호</td> -->
<!--                         </tr> -->
<!--                         <tr> -->
<!--                             <td class="modal-table-title">지시일</td> -->
<!--                             <td class="modal-table-con">#지시일</td> -->
<!--                         </tr> -->
<!--                         <tr> -->
<!--                             <td class="modal-table-title">담당자명</td> -->
<!--                             <td class="modal-table-con">#담당자명</td> -->
<!--                         </tr> -->
<!--                         <tr> -->
<!--                             <td class="modal-table-title">납기일</td> -->
<!--                             <td class="modal-table-con">#납기일</td> -->
<!--                         </tr> -->
<!--                         <tr> -->
<!--                             <td class="modal-table-title">품목명</td> -->
<!--                             <td class="modal-table-con">#품목명</td> -->
<!--                         </tr> -->
<!--                         <tr> -->
<!--                             <td class="modal-table-title">지시 수량</td> -->
<!--                             <td class="modal-table-con">#수량</td> -->
<!--                         </tr> -->
<!--                         <tr> -->
<!--                             <td class="modal-table-title">생산 수량</td> -->
<!--                             <td class="modal-table-con">#수량</td> -->
<!--                         </tr> -->
                    </table>
                    <!-- 여기에 있는 item-table은 클릭하면 자동으로 id에 맞는 조건만 나오도록 필터링 된
                    	BOM, 공정 목록(내지는 상세) 페이지로 이동 -->
                    <div class="wrap-tableBtn">
	                    <table class="item-table bom">
	                        <thead>
	                            <tr>
	                                <th style="width: 30%">BOM ID</th>
	                                <th>사용 용도</th>
	                                <th style="width: 15%;">단위</th>
	                                <th style="width: 15%;">소요량</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                            <tr>
	                                <td>#ID</td>
	                                <td>#용도</td>
	                                <td>#단위</td>
	                                <td>#소요량</td>
	                            </tr>
	                        </tbody>
	                    </table>
                    </div>
                    <div class="wrap-tableBtn">
	                    <table class="item-table proc">
	                        <thead>
	                            <tr>
	                                <th style="width: 30%">공정 ID</th>
	                                <th>공정명</th>
	                                <th style="width: 15%;">단위</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                            <tr>
	                                <td>#ID</td>
	                                <td>#용도</td>
	                                <td>#단위</td>
	                                <td>#소요량</td>
	                            </tr>
	                        </tbody>
	                    </table>
                    </div>
                    <div class="wrap-tableBtn">
                    	<input type="button" class="button" value="생산수량 입력">
                        <input type="button" class="button" value="수정">
                        <input type="button" class="button delete" value="삭제">
                    </div>
                </form>
            </div>
        </div>
        <!-- 품목 추가 모달 -->
        <div class="modal-overlay">
            <div class="modal">
                <button class="modal-close">✕</button>
                <h2>품목 추가</h2>
                <form class="modal-form">
                    <div class="form-group modal-search">
                        <input type="text">
                        <input type="button" value="검색">
                    </div>
                    <div class ="form-group">
                    	<input type="hidden" name="action" value="item-add">
                        <div class = "wrap-table panel-table" style="height: 400px;">
                            <table>
                                <thead>
                                    <th></th>
                                    <th>품목코드</th>
                                    <th>품목명</th>

                                </thead>
                                <tbody>
                                    <tr>
                                        <td><input type="radio" value="전달값" name="변수명"></td>
                                        <td>#품목코드</td>
                                        <td>#품목명</td>
                                    </tr>
                                </tbody>
                            </table>                
                        </div>
                    </div>
                    <div class ="form-group">
                        <div>수량 입력</div>
                        <input type="number">
                        <input type="button" value="수량 입력">
                    </div>
                    <div class="wrap-tableBtn">
                        <input type="button" class="button reset" value="선택 해제">
                        <button type="submit" class="button save">저장</button>
                    </div>
                </form>
            </div>
        </div>

        <script src="<c:url value='/Html/asset/09_common.js'/>"></script>
    </body>
</html>