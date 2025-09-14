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
            <div class="wrap-title">작업 지시서</div>

            <div class="wrap-select">
                <!-- 날짜 필터: value 접근은 param['wo-filter-date'] 로 안전하게 -->
                <form class="date-filter" method="get" action="workorder">
                    <input type="hidden" name="action" value="search">
                    <div class="select-con">
                        <div class="select-title" name="wo-filter-title">지시일</div>
                        <input type="date" name="wo-filter-date" value="${param['wo-filter-date']}">
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
                            <c:forEach var="wo" items="${list}">
                                <tr class="data">
                                    <td><input type="checkbox" value="${wo.woNum}" name="chk"></td>
                                    <!-- 서버 주도: 클릭하면 servlet으로 요청(wo_num 전달) -> doGet에서 detailWO/detailBOM/detailPROC 세팅 -->
                                    <td>
                                        <a href="workorder?wo_num=${wo.woNum}&amp;action=view" class="wo-link">${wo.woNum}</a>
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

        <!-- 등록 / 수정 패널 (같은 패널을 재사용) -->
        <div class="panel" id="panel-add">
            <button class="close-btn">✕</button>
            <div class="slide-title">작업 지시서 등록</div>

            <form method="post" action="workorder" id="form-add" class="wrap-table">
                <!-- action 히든을 view/edit 상태에 따라 동적으로 출력 -->
                <c:choose>
                    <c:when test="${param.action == 'edit' || not empty editMode}">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="wo_num" value="${detailWO.woNum}">
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="action" value="add">
                    </c:otherwise>
                </c:choose>

                <input type="hidden" name="bom_id" id="hidden-bom-id" value="${detailWO.bom_id}">
                <input type="hidden" name="proc_id" id="hidden-proc-id" value="${detailWO.proc_id}">

                <div class="form-group">
                    <label>지시일</label>
                    <input type="date" name="wo_date" id="wo-date-input" value="${detailWO.woDate}">
                </div>
                <div class="form-group">
                    <label>납기일</label>
                    <input type="date" name="wo_duedate" value="${detailWO.woDuedate}">
                </div>
                <div class="form-group">
                    <label>담당자</label>
                    <input type="text" name="worker_id" placeholder="담당자명 입력" value="${detailWO.workerID}">
                </div>
                <div class="form-group">
                    <label>지시 수량</label>
                    <input type="number" name="wo_pq" min="1" placeholder="지시 수량 입력" value="${detailWO.woPQ}">
                </div>

                <!-- 품목 선택 영역 (radio). edit 상황이면 해당 라디오를 checked 처리 -->
                <div class="form-group">
                    <div class="wrap-table panel-table">
                        <div class="panel-table-wrap">
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
                                                     value="${item.item_code}"
                                                     data-bom-id="${item.bom_id}"
                                                     data-proc-id="${item.proc_id}"
                                                     <c:if test="${not empty detailWO and detailWO.item_code eq item.item_code}">checked</c:if>>
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

        <!-- 상세 패널 (서버에서 detailWO/detailBOM/detailPROC 세팅하면 이 영역이 렌더되고, 아래 스크립트가 열어줌) -->
        <div class="panel" id="panel-down">
            <button class="close-btn">✕</button>
            <div class="slide-title">작업 지시서 상세</div>

            <form class="wrap-table panel-table" method="get" action="workorder">
                <input type="hidden" name="action" value="">
                <input type="hidden" id="detail-wo-num" value="${detailWO.woNum}">
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
                            <!-- detailBOM은 List<WorkOrderDTO>로 전달되어야 함 -->
                            <c:if test="${not empty detailBOM}">
                                <c:forEach var="b" items="${detailBOM}">
                                    <tr>
                                        <td>${b.bom_id}</td>
                                        <td>${b.item_code}</td>
                                        <td>${b.bom_reqAm}</td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                            <c:if test="${empty detailBOM}">
                                <tr><td colspan="3">BOM 정보 없음</td></tr>
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
                                <c:forEach var="p" items="${detailPROC}">
                                    <tr>
                                        <td>${p.proc_id}</td>
                                        <td>${p.proc_name}</td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                            <c:if test="${empty detailPROC}">
                                <tr><td colspan="2">공정 정보 없음</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <div class="wrap-tableBtn">
                    <!-- 서버 주도: 수정은 action=edit 으로 same servlet에 요청 (doGet에서 editMode 처리 필요) -->
                    <a href="workorder?action=edit&amp;wo_num=${detailWO.woNum}" class="button edit-all-btn">수정</a>
                    <input type="button" class="button" id="edit-aq-btn" value="생산수량 입력">
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

        <!-- header/nav 템플릿 로드 (기존 로직 유지) -->
        <script>
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
        </script>

        <!-- 서버에서 viewMode=true 이면 상세패널 열기 -->
        <c:if test="${not empty viewMode || param.action == 'view'}">
            <script>
                document.addEventListener('DOMContentLoaded', function(){
                    var p = document.querySelector('#panel-down');
                    if (p) p.classList.add('open');
                });
            </script>
        </c:if>

        <!-- 서버에서 editMode (또는 ?action=edit) 이면 등록/수정 패널 열기 -->
        <c:if test="${not empty editMode || param.action == 'edit'}">
            <script>
                document.addEventListener('DOMContentLoaded', function(){
                    var p = document.querySelector('#panel-add');
                    if (p) p.classList.add('open');
                });
            </script>
        </c:if>

        <script src="<c:url value='/Html/asset/09_common.js'/>"></script>
    </body>
</html>
