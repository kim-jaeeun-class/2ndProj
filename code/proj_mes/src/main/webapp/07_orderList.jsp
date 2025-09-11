<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>발주목록</title>

  <!-- 컨텍스트 경로 안전한 정적 파일 경로 -->
  <link rel="stylesheet" href="<c:url value='/Html/asset/list.css'/>">
<%--   <script src="<c:url value='/Html/asset/template_load.js'/>"></script> --%>
  <script src="<c:url value='/Html/asset/07_order_list.js'/>"></script>

</head>
<body>
<%-- <header></header>
<div class="gnb"></div>

<div class="titleBox">
  <span>발주 목록</span>
  <a href="<c:url value='/'/>">
    <div class="toMainpage">
      <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13" alt="메인 화면으로 가는 화살표" style="transform:scaleX(-1);" />
      메인 화면으로
    </div>
  </a>
</div> --%>

<div class="wrap">
  <div class="main">
    <!-- 필터 영역 -->
    <form class="box order_filter" method="get" action="<c:url value='/orderList'/>">
      <div class="order_date box">
        <div class="order_period">기간</div>
        <input type="date" id="start_date" name="startDate" value="${param.startDate}">
        <span>~</span>
        <input type="date" id="end_date" name="endDate" value="${param.endDate}">
      </div>

      <div class="box state">
        <div>진행상태</div>
		  <select id="state_select" name="state">
		    <option value=""  <c:if test="${empty param.state}">selected</c:if>>전체</option>
		    <option value="0" <c:if test="${param.state=='0'}">selected</c:if>>임시저장</option>
		    <option value="1" <c:if test="${param.state=='1'}">selected</c:if>>승인</option>
		    <option value="2" <c:if test="${param.state=='2'}">selected</c:if>>승인 대기</option>
		    <option value="3" <c:if test="${param.state=='3'}">selected</c:if>>반려</option>
		  </select>
      </div>

      <div>
        <button type="submit" id="filter_btn">조회</button>
      </div>
    </form>

    <!-- 목록 테이블 -->
    <div class="table-wrap" tabindex="0">
      <table class="tables">
        <thead>
        <tr>
          <th class="order_row_1">
            <input type="checkbox" id="check_all">
          </th>
          <th class="order_row_2">NO</th>
          <th class="order_row_3">날짜</th>
          <th class="order_row_4">발주번호</th>
          <th class="order_row_5">부서</th>
          <th class="order_row_6">담당자</th>
          <th class="order_row_7">총 수량</th>
          <th class="order_row_8">총 금액</th>
          <th class="order_row_9">진행상태</th>
          <th class="order_row_0"></th>
        </tr>
        </thead>
        <tbody class="tables_body">
        <!-- 데이터가 없을 때 -->
        <c:if test="${empty orderList}">
          <tr>
            <td colspan="10">조회된 발주가 없습니다.</td>
          </tr>
        </c:if>

        <!-- 데이터가 있을 때 -->
        <c:forEach var="o" items="${orderList}" varStatus="st">
          <tr>
            <!-- 행 선택 체크박스 -->
            <td>
              <input type="checkbox" class="row_check" name="order_key" value="${o.order_key}">
            </td>

            <!-- NO (1부터) -->
            <td>${st.index + 1}</td>

            <!-- 날짜 -->
            <td><fmt:formatDate value="${o.order_date}" pattern="yyyy-MM-dd"/></td>

            <!-- 발주번호 -->
			<c:url var="detailUrl" value="/orderDetail">
			  <c:param name="key"  value="${o.order_key}"/>
			  <c:param name="mode" value="view"/>
			</c:url>
			debug: key=${o.order_key}
			<td><a href="${detailUrl}">${o.order_number}</a></td>

			<!-- 부서(역할) -->
			<td>${o.depart_level}</td>
			
			<!-- 담당자 -->
			<td>${o.worker_name}</td>
			
			<!-- 총 수량 -->
			<td><fmt:formatNumber value="${o.totalQty}"/></td>
			
			<!-- 총 금액 -->
			<td><fmt:formatNumber value="${o.totalAmt}" pattern="#,##0"/></td>

            <!-- 진행상태 -->
            <td class="row_state">
              <c:choose>
                <c:when test="${o.order_state == 0}">임시저장</c:when>
			    <c:when test="${o.order_state == 1}">승인</c:when>
			    <c:when test="${o.order_state == 2}">승인대기</c:when>
			    <c:when test="${o.order_state == 3}">반려</c:when>
			    <c:otherwise>알수없음</c:otherwise>
              </c:choose>
            </td>

            <!-- 액션: 예시로 '회수' 버튼을 승인대기/임시저장일 때만 노출 -->
            <td>
              <c:if test="${o.order_state == 2 || o.order_state == 0}">
                <form method="post" action="<c:url value='/order/recall'/>" style="margin:0;">
                  <input type="hidden" name="key" value="${o.order_key}">
                  <button type="submit" class="recall">회수</button>
                </form>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>

    <!-- 하단 액션 -->
    <div class="action">
      <form id="delete" method="post" action="orderDel" >
        <input type="hidden" name="order_key" id="delete_key">
        <button type="submit" class="delete" id="deleteBtn">삭제</button>
      </form>

      <a href="<c:url value='/orderAdd'/>">
        <button type="button" class="add">추가</button>
      </a>
    </div>
  </div>
</div>

<script>
  // 전체 선택
  (function() {
    const checkAll = document.getElementById('check_all');
    const body = document.querySelector('.tables_body');

    if (checkAll && body) {
      checkAll.addEventListener('change', () => {
        body.querySelectorAll('.row_check').forEach(cb => cb.checked = checkAll.checked);
      });
    }

    // 일괄 삭제 예시 (선택된 key들을 hidden에 합쳐 담음)
    const bulkBtn  = document.getElementById('bulkDeleteBtn');
    const keyInput = document.getElementById('delete_keys');
    const form     = document.getElementById('bulkDeleteForm');

    if (bulkBtn && keyInput && form) {
      bulkBtn.addEventListener('click', () => {
        const keys = Array.from(document.querySelectorAll('.row_check:checked'))
          .map(cb => cb.value)
          .filter(Boolean);

        if (keys.length === 0) {
          alert('삭제할 항목을 선택하세요.');
          return;
        }
        keyInput.value = keys.join(',');
        form.submit();
      });
    }
  })();
</script>

</body>
</html>
