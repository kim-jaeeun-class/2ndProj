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
  <title>재고 목록</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="<c:url value='/Html/asset/list.css'/>">
  <script defer src="<c:url value='/Html/asset/08_stock_list.js'/>"></script>
  <style>
    body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
    .header-bg { background-color: #002a40; }
    .nav-bg { background-color: #003751; }
    .mainList li:hover { background-color: #3b82f6; }
  </style>
</head>
<body>

<!--  여기 두 줄이 템플릿에서 끌어올 자리 -->
<div id="header-slot"></div>
<div id="nav-slot"></div>

<div class="titleBox">
  <span>재고 목록</span>

  <div class="wrap">
    <div class="box date_filter">
      <form id="filterForm"  method="get" action="<c:url value='/stockList'/>">
        <div class="box_filter">
          <div class="filter">
            <div>대분류</div>
            <select name="big" id="big" class="big">
              <option value="">전체</option>
              <option value="RM" ${selBig=='RM'?'selected':''}>원자재</option>
              <option value="FP" ${selBig=='FP'?'selected':''}>완제품</option>
              <option value="CM" ${selBig=='CM'?'selected':''}>소모품</option>
              <option value="HP" ${selBig=='HP'?'selected':''}>반제품</option>
            </select>
          </div>

          <div class="filter">
            <div>중분류</div>
            <select name="mid" id="mid" class="middle">
              <option value="">전체</option>
              <!-- big에 따라 컨트롤러가 채워준 목록 사용 -->
              <c:forEach var="m" items="${midList}">
                <option value="${m.code}" ${selMid==m.code?'selected':''}>${m.name}</option>
              </c:forEach>
            </select>
          </div>

          <div class="filter">
            <div>소분류</div>
            <select name="small" id="small" class="small">
              <option value="">전체</option>
              <c:forEach var="s" items="${smallList}">
                <option value="${s.code}" ${selSmall==s.code?'selected':''}>${s.name}</option>
              </c:forEach>
            </select>
          </div>
        </div>
    </div>
      <div class="box date_filter">
        <div class="date box">
          <div class="period">기간</div>
          <input type="date" id="start_date" name="startDate" value="${startDate}">
          <span>~</span>
          <input type="date" id="end_date" name="endDate" value="${endDate}">
        </div>
        <div>
          <button type="submit" id="filter_btn">조회</button>
        </div>
      </div>
      </form>
  </div>
  <div class="wrap_list">
    <!-- 액션 -->
    <div class="action">
      <form id="deleteForm" method="post" action="<c:url value='/stockDel'/>">
        <input type="hidden" name="stock_id" id="delete_ids">
        <button type="submit" class="delete" id="deleteBtn">삭제</button>

        <a class="add" href="<c:url value='/stockDetail'><c:param name='mode' value='add'/></c:url>">
          <button type="button" class="item_add">등록</button>
        </a>
      </form>
    </div>
    <div class="table-wrap" tabindex="0">
      <table class="tables">
        <thead>
          <tr>
            <th class="order_row_1"><input type="checkbox" id="check_all"></th>
            <th class="order_row_2">NO</th>
            <th class="order_row_3">등록일</th>
            <th class="order_row_4">재고ID</th>
            <th class="order_row_5">품목코드</th>
            <th class="order_row_6">품목명</th>
            <th class="order_row_7">수량</th>
            <th class="order_row_8">위치</th>
            <th class="order_row_9">구분</th>
            <th class="order_row_0">상태</th>
          </tr>
        </thead>

        <tbody class="tables_body">
          <c:if test="${empty stockList}">
            <tr><td colspan="10">조회된 재고가 없습니다.</td></tr>
          </c:if>

          <c:forEach var="s" items="${stockList}" varStatus="st">
            <tr>
              <td>
                <input type="checkbox" class="row_check" name="stock_id" value="${s.stock_id}">
              </td>
              <td>${st.index + 1}</td>
              <td><fmt:formatDate value="${s.stock_date}" pattern="yyyy-MM-dd"/></td>

              <td>
                <c:url var="detailUrl" value="/stockDetail">
                  <c:param name="stock_id" value="${s.stock_id}"/>
                  <c:param name="mode" value="view"/>
                </c:url>
                <a href="${detailUrl}">${s.stock_id}</a>
              </td>

              <td>${s.item_code}</td>
              <td>${s.item_name}</td>
              <td><fmt:formatNumber value="${s.stock_number}"/></td>
              <td>${s.stock_loc}</td>
              <td>${s.stock_div}</td>
              <td>${s.stock_stat}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>


  </div>
</div>

<!-- ▼▼ 드롭다운(사람 아이콘) 에러 해결 버전 스크립트 ▼▼ -->
<script>
  // 템플릿의 header/nav 로드 → DOM 삽입 완료 후에 드롭다운 바인딩
  (async function () {
    try {
      const url = '<%= ctx %>/Html/00_template/template.html';
      const resp = await fetch(url, { credentials: 'same-origin' });
      const text = await resp.text();
      const doc  = new DOMParser().parseFromString(text, 'text/html');

      const header = doc.querySelector('header.header-bg') || doc.querySelector('header');
      const nav    = doc.querySelector('nav.nav-bg')       || doc.querySelector('nav');

      const headerSlot = document.getElementById('header-slot');
      const navSlot    = document.getElementById('nav-slot');

      if (header && headerSlot) headerSlot.replaceWith(header);
      if (nav && navSlot)       navSlot.replaceWith(nav);

      // 헤더/네비가 실제 DOM에 들어간 후 바인딩
      bindUserMenu();
    } catch (e) {
      console.error('템플릿 로드 실패:', e);
    }
  })();

  function bindUserMenu() {
    const myIconBtn = document.getElementById('myIconBtn');
    const userMenu  = document.getElementById('userMenu');

    // 템플릿에 요소가 없으면 조용히 종료 (에러 방지)
    if (!myIconBtn || !userMenu) return;

    function closeUserMenu() {
      userMenu.classList.add('hidden');
      myIconBtn.setAttribute('aria-expanded', 'false');
    }

    myIconBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      userMenu.classList.toggle('hidden');
      myIconBtn.setAttribute(
        'aria-expanded',
        userMenu.classList.contains('hidden') ? 'false' : 'true'
      );
    });

    userMenu.addEventListener('click', (e) => e.stopPropagation());
    document.addEventListener('click', closeUserMenu);
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') closeUserMenu();
    });
  }
</script>
<!-- ▲▲ 드롭다운(사람 아이콘) 에러 해결 버전 스크립트 ▲▲ -->

</body>
</html>
