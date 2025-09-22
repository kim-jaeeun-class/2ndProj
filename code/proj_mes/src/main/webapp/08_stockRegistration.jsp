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
  <title>재고 등록/상세</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
	<script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="<c:url value='/Html/asset/registration.css'/>">
  <script defer src="<c:url value='/Html/asset/08_stock_registration.js'/>"></script>
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
<%-- 모드 결정 --%>
<c:set var="mode"   value="${empty mode ? (empty stock ? 'add' : 'view') : mode}"/>
<c:set var="isAdd"  value="${mode eq 'add'}"/>
<c:set var="isEdit" value="${mode eq 'edit'}"/>
<c:set var="isView" value="${mode eq 'view'}"/>
<c:set var="ro"     value="${not isAdd and not isEdit}"/>
	<span>재고 ${isAdd ? '등록' : '상세'}</span>

<%-- form action --%>
<c:choose>
  <c:when test="${isAdd}">
    <c:url var="formAction" value="/stockAdd"/>
  </c:when>
  <c:when test="${isEdit}">
    <c:url var="formAction" value="/stockUpdate"/>
  </c:when>
  <c:otherwise>
    <c:set var="formAction" value=""/>
  </c:otherwise>
</c:choose>

<%-- 링크 URL은 var로 미리 생성 --%>
<c:url var="editLink" value="/stockDetail">
  <c:param name="stock_id"  value="${stock.stock_id}"/>
  <c:param name="mode" value="edit"/>
</c:url>
<c:url var="viewLink" value="/stockDetail">
  <c:param name="stock_id"  value="${stock.stock_id}"/>
  <c:param name="mode" value="view"/>
</c:url>

<form id="mainForm" method="post" action="${formAction}">
  <c:if test="${isEdit}">
    <input type="hidden" name="stock_id" value="${stock.stock_id}">
  </c:if>

  <div class="wrap">
    <!-- 헤더 -->
      <div class="box header_row">
        

        <div class="action">
        
          <c:choose>
			  <c:when test="${isView}">
			    <a href="${editLink}">
			      <button type="button" class="primary" id="btnedit">수정</button>
			    </a>
			    <button type="submit" class="danger" id="btndelete" form="delForm">삭제</button>
			  </c:when>
			
			  <c:when test="${isEdit}">
			    <a href="${viewLink}">
			      <button type="button" id="editdel">취소</button>
			    </a>
			    <button type="submit" class="primary" id="btnsave">저장</button>
			  </c:when>
			
			  <c:when test="${isAdd}">
			    <a href="<c:url value='/stockList'/>"><button type="button" id="back">취소</button></a>
			    <button type="submit" class="primary" id="btnsave">등록</button>
			  </c:when>
		</c:choose>

        </div>
      </div>

      <!-- 기본 정보 -->
      <div class="box">
        <div class="item">
          <div>재고 ID</div>
          <input id="stock_id_input" name="stock_id" class="main_input" type="text"
                 value="${isAdd ? (empty param.stock_id ? '품목을 선택 하시면 자동입력됩니다.' : param.stock_id) : stock.stock_id}"
                 readonly>
        </div>

        <div class="item">
          <div>위치</div>
          <input name="stock_loc" class="main_input" type="number"
                 value="${stock.stock_loc}"
                 <c:if test="${ro}">readonly</c:if>>
        </div>
      </div>

      <!-- 품목 -->
      <div class="box">
        <div class="item">
          <div>품목코드</div>
          <input id="item_input" class="main_input item_input" type="text" name="item_code"
                 placeholder="품목을 선택하세요"
                 value="${stock.item_code}" readonly>
        </div>

        <div class="item">
          <div>품목명</div>
          <input id="item_name_input" class="main_input" type="text"
                 value="${stock.item_name}" readonly>
        </div>
      </div>

      <!-- 수량/단가 -->
      <div class="box">
        <div class="item">
          <div>수량</div>
          <input id="stock_number_input" name="stock_number" class="main_input" type="text"
                 value="${isAdd ? param.stock_number : stock.stock_number}"
                 <c:if test="${ro}">readonly</c:if>>
        </div>

        <div class="item">
          <div>단가</div>
          <input id="stock_price_input" name="item_price" class="main_input" type="text"
                 value="${stock.item_price}"readonly>
        </div>
      </div>
</form>
</div>
 <div class="bottom">
 	<a href="<c:url value='/stockList' />"><button type="button" class="back_btn">목록</button></a>
 </div>
    
<!-- 삭제 숨김 폼 -->
<form id="delForm" method="post" action="<c:url value='/stockDel'/>" style="display:none">
  <input type="hidden" name="stock_id" value="${stock.stock_id}">
</form>

<!-- ===== 품목 선택 모달 ===== -->
<div id="item_modal" class="modal hide" style="z-index:99999">
  <div class="search">
    <input type="text" class="m_search" placeholder="검색내용을 입력하세요">
    <button class="m_button" type="button" id="item_search_btn">검색</button>
  </div>
  <div class="table_wrap" tabindex="0">
    <table class="tables">
      <thead>
        <tr>
          <th></th>
          <th>품목코드</th>
          <th>품목명</th>
          <th>유형</th>
          <th>단가</th>
        </tr>
      </thead>
      <tbody class="list" id="item_modal_list">
        <c:forEach var="i" items="${itemList}">
          <tr>
            <td><input type="radio" name="pick_item"></td>
            <td class="col-code">${i.item_code}</td>
            <td class="col-name">${i.item_name}</td>
            <td class="col-type">${i.item_type}</td>
            <td class="col-price"><fmt:formatNumber value="${i.item_price}" pattern="#,##0"/></td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
  <div class="bottom_input">
    <span>수량 : </span>
    <input type="text" class="m_quantity" placeholder="수량을 입력하세요">
  </div>
  <div class="bottom">
    <button class="cancel m_button" type="button" id="item_modal_cancel">취소</button>
    <button class="confirm m_button" type="button" id="item_modal_confirm">확인</button>
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
	/* ---------- 사람 아이콘 드롭다운 ---------- */
    const myIconBtn = document.getElementById('myIconBtn');
    const userMenu  = document.getElementById('userMenu');

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
</script>
</body>
</html>
