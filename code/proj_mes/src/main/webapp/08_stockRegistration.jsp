<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>재고 등록/상세</title>
  <link rel="stylesheet" href="<c:url value='/Html/asset/registration.css'/>">
  <script defer src="<c:url value='/Html/asset/08_stock_registration.js'/>"></script>
</head>
<body>

<%-- 모드/readonly 플래그 --%>
<c:set var="mode"   value="${empty mode ? (empty stock ? 'add' : 'view') : mode}"/>
<c:set var="isAdd"  value="${mode eq 'add'}"/>
<c:set var="isEdit" value="${mode eq 'edit'}"/>
<c:set var="isView" value="${mode eq 'view'}"/>
<c:set var="ro"     value="${not isAdd and not isEdit}"/>

<%-- JS에서 모드 인지 --%>
<script>
  window.IS_ADD  = ${isAdd};
  window.IS_EDIT = ${isEdit};
  window.RO      = ${ro};
</script>

<%-- 폼 action --%>
<c:choose>
  <c:when test="${isAdd}">
    <c:url var="formAction" value="/stockAdd"/>
  </c:when>
  <c:otherwise>
    <c:url var="formAction" value="/stockUpdate"/>
  </c:otherwise>
</c:choose>

<form id="mainForm" method="post" action="${formAction}">
  <c:if test="${isEdit}">
    <input type="hidden" name="stock_id" value="${stock.stock_id}">
  </c:if>

  <div class="wrap">
    <!-- 헤더 -->
    <div class="main">
      <div class="box header_row">
        <div>재고 ${isAdd ? '등록' : '상세'}</div>

        <div class="action">
          <c:choose>
            <c:when test="${isAdd}">
              <button type="submit" class="primary">등록</button>
            </c:when>

            <c:when test="${isView}">
              <a href="<c:url value='/stockDetail'>
                         <c:param name='id' value='${stock.stock_id}'/>
                         <c:param name='mode' value='edit'/>
                       </c:url>">
                <button type="button" class="primary">수정</button>
              </a>
              <button type="button" class="danger" id="deleteBtn">삭제</button>
            </c:when>

            <c:when test="${isEdit}">
              <button type="submit" class="primary">저장</button>
              <a href="<c:url value='/stockDetail'>
                         <c:param name='id' value='${stock.stock_id}'/>
                         <c:param name='mode' value='view'/>
                       </c:url>">
                <button type="button">수정취소</button>
              </a>
            </c:when>
          </c:choose>
        </div>
      </div>

      <!-- 기본 정보 -->
      <div class="box">
        <div class="item">
          <div>재고 ID</div>
          <input id="stock_id_input" name="stock_id" class="main_input" type="text"
                 value="${isAdd ? (empty param.stock_id ? '선택 후 자동생성' : param.stock_id) : stock.stock_id}"
                 readonly>
        </div>

        <div class="item">
          <div>위치</div>
          <input name="stock_loc" class="main_input" type="number"
                 value="${isAdd ? param.stock_loc : stock.stock_loc}"
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
                 value="${isAdd ? param.item_price : stock.item_price}"
                 <c:if test="${ro}">readonly</c:if>>
        </div>
      </div>
    </div>

    <!-- 하단 버튼 -->
    <div class="bottom">
      <a href="<c:url value='/stockList'/>"><button type="button">목록</button></a>
      <c:if test="${isAdd}">
        <button type="submit" class="primary">등록</button>
      </c:if>
      <c:if test="${isEdit}">
        <button type="submit" class="primary">저장</button>
      </c:if>
    </div>

  </div>
</form>

<!-- 삭제 숨김 폼 -->
<form id="deleteForm" method="post" action="<c:url value='/stockDel'/>" style="display:none">
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

</body>
</html>
