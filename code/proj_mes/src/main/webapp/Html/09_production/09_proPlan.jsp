<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% String ctx = request.getContextPath(); %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>J2P4 :: 생산 계획</title>
        <link rel="stylesheet" href="<c:url value='/Html/asset/font.css'/>">
        <link rel="stylesheet" href="<c:url value='/Html/asset/09_common.css'/>">
<!--         <script src="/proj_mes/Html/asset/template_load.js"></script> -->
                <script src ="../asset/template_load.js" ></script>
        
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
        .header-bg { background-color: #002a40; }
        .nav-bg { background-color: #003751; }
        .mainList li:hover { background-color: #3b82f6; }
    </style>
</head>
<body class="bg-gray-100 text-gray-800" page="pro-plan">
    <div id="header-slot"></div>
    <div id="nav-slot"></div>
        <div class="sidenwrap">
            <div class="side">
                <!-- a 링크 나중에 달기 -->
                <a href="proplan"><div class="side-menu">생산 계획</div></a>
                <a href="workorder"><div class="side-menu">작업 지시서</div></a>
            </div>
            <div class="wrap">
                <div class="wrap-title">
                    생산 계획
                </div>
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
            <div class="slide-title">작업 지시서 등록</div>
            <form action="proplan" method="post">
            	<input type="hidden" name="action" value="add">
                <input type="hidden" name="cpID" value="">
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
                        <input type="button" name="detail-modify" class="button edit" value="수정">
                        <input type="submit" name="detaile-delete" class="button delete" value="삭제">
                    </div>
                </form>
            </div>
        </div>
        <script>
            //템플릿의 header/nav만 로드
                (async function () {
                try {
                    const url = '<%= ctx %>/Html/00_template/template.html';
                    const text = await (await fetch(url, { credentials: 'same-origin' })).text();
                    const doc  = new DOMParser().parseFromString(text, 'text/html');
                    const header = doc.querySelector('header.header-bg') || doc.querySelector('header');
                    const nav    = doc.querySelector('nav.nav-bg')    || doc.querySelector('nav');
                    const headerSlot = document.getElementById('header-slot');
                    const navSlot    = document.getElementById('nav-slot');
                    if (header && headerSlot) headerSlot.replaceWith(header);
                    if (nav && navSlot)       navSlot.replaceWith(nav);
                } catch (e) {
                    console.error('템플릿 로드 실패:', e);
                }
                })();

                (function() {
                    // 전체 선택
                    const checkAll = document.getElementById('check_all');
                    const body = document.querySelector('.tables_body');
                    if (checkAll && body) {
                    checkAll.addEventListener('change', () => {
                        body.querySelectorAll('.row_check').forEach(cb => cb.checked = checkAll.checked);
                    });
                    }

                    // 삭제
                    const deleteBtn  = document.getElementById('deleteBtn');
                    const deleteKeys = document.getElementById('delete_keys');
                    const deleteForm = document.getElementById('deleteForm');

                    if (deleteBtn && deleteKeys && deleteForm) {
                    deleteBtn.addEventListener('click', () => {
                        const keys = Array.from(document.querySelectorAll('.row_check:checked'))
                        .map(cb => cb.value)
                        .filter(Boolean);

                        if (keys.length === 0) {
                        alert('삭제할 항목을 선택하세요.');
                        return;
                        }
                        deleteKeys.value = keys.join(',');
                        deleteForm.submit();
                    });
                    }
                    
                })();
        </script>
        <script src="<c:url value='/Html/asset/09_common.js'/>"></script>
    </body>
</html>