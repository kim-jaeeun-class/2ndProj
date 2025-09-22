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
        <title>J2P4 :: 작업 지시서</title>
        <link rel="stylesheet" href="<c:url value='/Html/asset/font.css'/>">
        <link rel="stylesheet" href="<c:url value='/Html/asset/09_common.css'/>">
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
	<body class="bg-gray-100 text-gray-800" page="wo">
    <div id="header-slot"></div>
    <div id="nav-slot"></div>
    <div class="sidenwrap">
        <div class="side">
            <a href="proplan">
                <div class="side-menu">생산 계획</div>
            </a>
            <a href="workorder">
                <div class="side-menu">작업 지시서</div>
            </a>
        </div>
        <div class="wrap">
            <div class="wrap-title">
                작업 지시서
            </div>
            <div class="wrap-select">
                <form class="date-filter" method="get" action="workorder">
                    <input type="hidden" name="action" value="search">
                    <div class="select-con">
                        <div class="select-title" name="wo-filter-title">지시일</div>
                        <input type="date" name="wo-filter-date" value="${param.wo-filter-date}">
                    </div>
                    <div class="filter-submit">
                        <button type="submit" class="button">조회</button>
                    </div>
                </form>
            </div>
            <!-- 테이블 - 메인 -->
            <form class="wrap-table" method="post" action="workorder">
                <input type="hidden" name="action" value="delete">
                <div class="table-view">
                    <table>
                        <thead>
                            <th><input type="checkbox" class="select-all"></th>
                            <th>작업지시번호</th>
                            <th>지시일</th>
                            <th>담당자명</th>
                            <th>납기일자</th>
                            <th>품목명</th>
                            <th>지시수량</th>
                            <th>생산수량</th>
                        </thead>
                        <tbody>
                            <!-- DTO에 있는 거 wo라고 이름 지어서 꺼내는 것!!! -->
                            <!-- 근데 지금 client 테이블 조인을... 내일 얘기하자 -->
                            <c:forEach var="wo" items="${list}">
                                <tr class = "data">
                                    <!-- 구분용으로 date, num 다 넣음 -->
                                    <td><input type="checkbox" value="${wo.woNum}" name="chk"></td>
                                    <td>
                                        <a href="workorder?action=view&wo_num=${wo.woNum}">${wo.woNum}</a>
                                    </td>
                                    <td>${wo.woDate}</td>
                                    <td>${wo.workerID}</td>
                                    <td>${wo.woDuedate}</td>
                                    <td>${wo.item_code}</td>
                                    <td>${wo.woPQ}</td>
                                    <td>${wo.woAQ}</td>
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
        <div class="panel" id="panel-add">
            <button class="close-btn">✕</button>
            <div class="slide-title">작업 지시서 등록</div>
            <form method = "post" action = "workorder" id="form-add" class="wrap-table">
                <input type="hidden" name="action" id="action-input" value="add">
                <input type="hidden" name="bom_id" id="hidden-bom-id">
                <input type="hidden" name="proc_id" id="hidden-proc-id">
                <input type="hidden" name="wo_num_hidden" id="wo-num-hidden">
                <div class="form-group">
                    <label>지시일</label>
                    <input type="date" name="wo_date" id="wo-date-input">
                </div>
                <div class="form-group">
                    <label>납기일</label>
                    <input type="date" name="wo_duedate">
                </div>
                <div class="form-group">
                    <label>담당자</label>
                    <input type="text" name = "worker_id" placeholder="담당자명 입력">
                </div>
                <div class="form-group">
                    <label>지시 수량</label>
                    <input type="number" name="wo_pq" min="1" placeholder="지시 수량 입력">
                </div>
                <!-- 품목 선택 영역 -->
                <div class ="form-group">
                    <div class = "wrap-table panel-table">
                        <div class = "panel-table-wrap">
                            <table>
                                <thead>
                                	<th>선택</th>
                                    <th>품목코드</th>
                                    <th>품목명</th>
                                </thead>
                                <tbody>
                                	<c:forEach var="item" items="${itemAll}">
	                                    <tr>
	                                        <td>
	                                        	<input type="radio" name="item_code"
                                                value="${item.item_code}" data-bom-id="${item.bom_id}" 
                                                data-proc-id="${item.proc_id}">
	                                        </td>
	                                        <td>${item.item_code}</td>
	                                        <td>${item.item_name}</td>
	                                    </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <button type="submit" class="button panel-save">저장</button>
                </div>
            </form>
        </div>
        <!-- 작업 지시서 상세로 가는중~~~ -->
        <div class="panel" id="panel-down">
            <button class="close-btn">✕</button>
            <div class="slide-title">작업 지시서 상세</div>
                <form class="wrap-table panel-table" method = "get" action="workorder">
                <input type="hidden" name="action" name = "action" value="">
                    <table class = "modal-table">
                    </table>
                    <!-- 여기에 있는 item-table은 클릭하면 자동으로 id에 맞는 조건만 나오도록 필터링 된
                        BOM, 공정 목록(내지는 상세) 페이지로 이동 -->
                    <div class="wrap-tableBtn">
                        <table class="item-table bom">
	                        <thead>
	                            <tr>
	                                <th style="width: 30%">BOM ID</th>
	                                <th>사용 용도</th>
	                                <th style="width: 15%;">소요량</th>
	                            </tr>
	                        </thead>
	                        <tbody>
                                <c:if test="${not empty detailBOM}">
                                    <tr>
                                        <td>${detailBOM.bom_id}</td>
                                        <td>${detailBOM.item_code}</td>
                                        <td>${detailBOM.bom_reqAm}</td>
                                    </tr>
                                </c:if>
	                        </tbody>
	                    </table>
                    </div>
                    <div class="wrap-tableBtn">
	                    <table class="item-table proc">
	                        <thead>
	                            <tr>
	                                <th style="width: 30%">공정 ID</th>
	                                <th>공정명</th>
	                            </tr>
	                        </thead>
	                        <tbody>
                                <c:if test="${not empty detailPROC}">
                                    <tr>
                                        <td>${detailPROC.proc_id}</td>
                                        <td>${detailPROC.proc_name}</td>
                                    </tr>
                                </c:if>
	                        </tbody>
	                    </table>
                    </div>
                    <div class="wrap-tableBtn">
                    	<input type="button" class="button" value="생산수량 입력" id="edit-aq-btn">
                        <input type="button" class="button edit-all-btn" value="수정" data-wo-num="${detailWO.woNum}">
                    </div>
                    <div class="form-group">
                        <label>생산 수량</label>
                        <p class="production-quantity-display">${detailWO.woAQ}</p>
                        <div class="edit-aq-area" style="display: none;">
                            <input type="number" id="edit-aq-input" value="${detailWO.woAQ}">
                            <button type="button" class="button complete-aq-btn">완료</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <script>
            //템플릿의 header/nav만 로드
///////////////////////////////   추가 드롭다운 초기화 함수 (템플릿 삽입 후에만 바인딩)/////////////////////////////////
                function initUserMenu() { 
                    const myIconBtn = document.getElementById('myIconBtn');
                    const userMenu  = document.getElementById('userMenu');
                    if (!myIconBtn || !userMenu) return; // 🔧 ADDED: 널 가드

                    function closeUserMenu() {
                        userMenu.classList.add('hidden');
                        myIconBtn.setAttribute('aria-expanded', 'false');
                    }

                    myIconBtn.addEventListener('click', (e) => {
                        e.stopPropagation();
                        userMenu.classList.toggle('hidden');
                        myIconBtn.setAttribute('aria-expanded',
                            userMenu.classList.contains('hidden') ? 'false' : 'true');
                    });

                    userMenu.addEventListener('click', (e) => e.stopPropagation());
                    document.addEventListener('click', closeUserMenu);
                    document.addEventListener('keydown', (e) => {
                        if (e.key === 'Escape') closeUserMenu();
                    });
                } // 🔧 ADDED (end)

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
                } finally { 
                    initUserMenu(); // 🔧 ADDED: 템플릿이 DOM에 붙은 뒤 드롭다운 바인딩
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
