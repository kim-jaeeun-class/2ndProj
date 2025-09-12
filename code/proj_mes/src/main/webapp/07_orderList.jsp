<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>발주목록</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
    .header-bg { background-color: #002a40; }
    .nav-bg { background-color: #003751; }
    .mainList li:hover { background-color: #3b82f6; }
  </style>
  <link rel="stylesheet" href="<c:url value='/Html/asset/list.css'/>">
  <script src="<c:url value='/Html/asset/07_order_list.js'/>"></script>
</head>
<body>

<!--  여기 두 줄이 템플릿에서 끌어올 자리 -->
<div id="header-slot"></div>
<div id="nav-slot"></div>

<div class="titleBox">
        <span>발주 목록</span>         
	
<div class="wrap">
    <!-- 필터 -->
    <form class="box order_filter" method="get" action="<c:url value='/orderList'/>">
      <div class="order_date box">
        <div class="order_period">기간</div>
        <input type="date" id="start_date" name="startDate" value="${param.startDate}">
        <span>~</span>
        <input type="date" id="end_date" name="endDate" value="${param.endDate}">
      </div>

      <%-- <div class="box state">
        <div>진행상태</div>
        <select id="state_select" name="state">
          <option value=""  <c:if test="${empty param.state}">selected</c:if>>전체</option>
          <option value="0" <c:if test="${param.state=='0'}">selected</c:if>>임시저장</option>
          <option value="1" <c:if test="${param.state=='1'}">selected</c:if>>승인</option>
          <option value="2" <c:if test="${param.state=='2'}">selected</c:if>>승인 대기</option>
          <option value="3" <c:if test="${param.state=='3'}">selected</c:if>>반려</option>
        </select>
      </div> --%>

      <div>
        <button type="submit" id="filter_btn">조회</button>
      </div>
    </form>
  </div>
  <div class="wrap_list">
  	<!-- 액션 -->
    <div class="action">
      <!-- 삭제: 선택된 key들을 CSV로 hidden에 담아 POST -->
      <form id="deleteForm" method="post" action="<c:url value='/orderDel'/>">
        <input type="hidden" name="order_key" id="delete_keys">
        <button type="button" class="delete" id="deleteBtn">삭제</button>

      <!-- 추가: 등록 화면으로 이동(GET). /orderAdd(POST 전용)로 가지 않도록! -->
      <a class="add" href="<c:url value='/orderDetail'><c:param name='mode' value='add'/></c:url>">
      <button type="button" class="item_add">추가</button></a>
   	  
      </form>
    </div>
    <!-- 목록 -->
    <div class="table-wrap" tabindex="0">
      <table class="tables">
        <thead>
          <tr>
            <th class="order_row_1"><input type="checkbox" id="check_all"></th>
            <th class="order_row_2">NO</th>
            <th class="order_row_3">날짜</th>
            <th class="order_row_4">발주번호</th>
            <th class="order_row_5">부서</th>
            <th class="order_row_6">담당자</th>
            <th class="order_row_7">품목 수</th>
            <th class="order_row_8">총 금액</th>
            <!-- <th class="order_row_9">진행상태</th>
            <th class="order_row_0"></th> -->
          </tr>
        </thead>
        <tbody class="tables_body">
          <c:if test="${empty orderList}">
            <tr>
              <td colspan="10">조회된 발주가 없습니다.</td>
            </tr>
          </c:if>

          <c:forEach var="o" items="${orderList}" varStatus="st">
            <tr>
              <td><input type="checkbox" class="row_check" name="order_key" value="${o.order_key}"></td>
              <td>${st.index + 1}</td>
              <td><fmt:formatDate value="${o.order_date}" pattern="yyyy-MM-dd"/></td>

              <c:url var="detailUrl" value="/orderDetail">
                <c:param name="key"  value="${o.order_key}"/>
                <c:param name="mode" value="view"/>
              </c:url>
              <td><a href="${detailUrl}">${o.order_number}</a></td>

              <td>${o.depart_level}</td>
              <td>${o.worker_name}</td>
              <td><fmt:formatNumber value="${o.totalQty}"/></td>
              <td><fmt:formatNumber value="${o.totalAmt}" pattern="#,##0"/></td>

              <%-- <td class="row_state">
                <c:choose>
                  <c:when test="${o.order_state == 0}">임시저장</c:when>
                  <c:when test="${o.order_state == 1}">승인</c:when>
                  <c:when test="${o.order_state == 2}">승인대기</c:when>
                  <c:when test="${o.order_state == 3}">반려</c:when>
                  <c:otherwise>알수없음</c:otherwise>
                </c:choose>
              </td>

              <td>
                <c:if test="${o.order_state == 2 || o.order_state == 0}">
                  <form method="post" action="<c:url value='/order/recall'/>" style="margin:0;">
                    <input type="hidden" name="key" value="${o.order_key}">
                    <button type="submit" class="recall">회수</button>
                  </form>
                </c:if>
              </td> --%>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>

    
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

</body>
</html>
