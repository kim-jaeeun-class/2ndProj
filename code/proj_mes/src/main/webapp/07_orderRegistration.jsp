<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>발주 등록/상세</title>
<link rel="stylesheet" href="<c:url value='/Html/asset/registration.css'/>">
<script src="<c:url value='/Html/asset/07_order_registration.js'/>"></script>
</head>
<body>

<%-- 모드 결정 --%>
<c:set var="mode" value="${empty mode ? (empty order ? 'add' : 'view') : mode}"/>
<c:set var="isAdd"  value="${mode eq 'add'}"/>
<c:set var="isEdit" value="${mode eq 'edit'}"/>
<c:set var="isView" value="${mode eq 'view'}"/>
<c:set var="ro"     value="${not isAdd and not isEdit}"/>

<%-- form action --%>
<c:choose>
  <c:when test="${isAdd}">
    <c:url var="formAction" value="/orderAdd"/>
  </c:when>
  <c:when test="${isEdit}">
    <c:url var="formAction" value="/orderUpdate"/>
  </c:when>
  <c:otherwise>
    <c:set var="formAction" value=""/>
  </c:otherwise>
</c:choose>

<%-- 링크 URL은 var로 미리 생성 --%>
<c:url var="editLink" value="/orderDetail">
  <c:param name="key"  value="${order.order_key}"/>
  <c:param name="mode" value="edit"/>
</c:url>
<c:url var="viewLink" value="/orderDetail">
  <c:param name="key"  value="${order.order_key}"/>
  <c:param name="mode" value="view"/>
</c:url>

<%-- 날짜 포맷을 var로 뽑아서 value에 주입 --%>
<fmt:formatDate var="payDate" value="${order.order_pay}" pattern="yyyy-MM-dd"/>

<%-- 메인 폼 --%>
<form id="orderForm" method="post" action="${formAction}">
  <!-- PK/업무번호 히든 -->
  <input type="hidden" name="order_key" value="${order.order_key}"/>
  <input type="hidden" name="order_number" value="${order.order_number}"/>
  <input type="hidden" id="order_state" name="order_state" value="${empty order.order_state ? 0 : order.order_state}">

  <div class="wrap">
    <!-- ===== 발주 기본 정보 ===== -->
    <div class="main">
      <div class="box header_row">
        <div>발주 ${isAdd ? '등록' : '상세'}</div>

        <div class="action">
			<c:choose>
				<c:when test="${isView}">
					<a href="${editLink}"><button type="button" class="primary">수정</button></a>
					<button type="submit" class="danger" form="delForm">삭제</button>
				</c:when>
				<c:when test="${isEdit}">
					<a href="${viewLink}"><button type="button">수정취소</button></a>
				</c:when>
			</c:choose>
			<%-- isAdd에서는 상단에 submit 두지 않고 하단 버튼만 사용 --%>

        </div>
      </div>

      <!-- 기본 정보 입력부 -->
      <div class="box">
        <div class="item">
          <div>부서</div>
          <input id="dept_input" class="main_input" type="text"
                 value="${order.depart_level}" placeholder="부서를 선택하세요" readonly>
          <!-- 서버에서 기대하는 name = dapart_ID2 (오탈자 포함 일치) -->
          <input type="hidden" id="dept_id" name="dapart_ID2" value="${order.dapart_ID2}">
        </div>

        <div class="item">
          <div>담당자</div>
          <input id="name_input" class="main_input" type="text"
                 value="${order.worker_name}" readonly>
          <input type="hidden" id="worker_id" name="worker_id" value="${order.worker_id}">
        </div>

        <div class="item">
          <div>납기일</div>
          <input name="order_pay" class="main_input" type="date"
                 value="${payDate}" <c:if test="${ro}">readonly</c:if> >
        </div>
      </div>

      <div class="box">
        <div class="item">
          <div>거래처</div>
          <input id="client_input" class="main_input" type="text"
                 placeholder="거래처를 선택하세요"
                 value="${order.client_name}" readonly>
          <input type="hidden" id="client_id" name="client_id" value="${order.client_id}">
        </div>
        <div class="item">
          <div>사업자번호</div>
          <input id="business_input" class="main_input" type="text"
                 value="${order.business_number}" readonly>
        </div>
        <div class="item">
          <div>연락처</div>
          <input id="number_input" class="main_input" type="text"
                 value="${order.client_phone}" readonly>
        </div>
      </div>

      <div class="note box">
        <div class="item">
          <div>비고</div>
          <input class="note_input" type="text" name="bigo"
                 value="${order.bigo}" <c:if test="${ro}">readonly</c:if> >
        </div>
      </div>
    </div>

    <!-- ===== 발주 상세(품목) ===== -->
    <div class="list">
      <div class="box header_list">
        <div>발주 상세(품목)</div>
        <div class="list_action">
          <c:if test="${not ro}">
            <button type="button" class="item_del">삭제</button>
            <button type="button" class="item_add">추가</button>
          </c:if>
        </div>
      </div>

      <div class="table_wrap_detail" tabindex="0">
        <table class="tables">
          <thead>
            <tr>
              <th><input type="checkbox" id="checkAll" <c:if test='${ro}'>disabled</c:if>></th>
              <th>NO</th>
              <th>품목</th>
              <th>단가</th>
              <th>수량</th>
              <th>합계</th>
            </tr>
          </thead>
			<tbody class="order_list">
				<c:forEach var="it" items="${orderItems}" varStatus="st">
					<tr>
					  <!-- 체크박스(보기 모드에선 비활성) -->
					  <td>
					    <input type="checkbox" class="row_check" <c:if test="${mode eq 'view'}">disabled</c:if> >
					  </td>
					
					  <!-- NO -->
					  <td>${st.index + 1}</td>
					
					  <!-- 품목(지금 DAO는 item_code/item_price/quantity만 전달) -->
					  <td class="col-code"><c:out value="${it.item_code}"/></td>
					
					  <!-- 단가(문자열일 수 있으므로 그대로 출력) -->
					  <td class="col-price"><c:out value="${it.item_price}"/></td>
					
					  <!-- 수량 -->
					  <td class="col-qty"><c:out value="${it.quantity}"/></td>
					
					  <!-- 합계(행별) — 아래 스크립트가 채움 -->
					  <td class="col-sum"></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
			  <tr>
			    <td colspan="4">Total</td>
			    <td class="total_qty"></td>
			    <td class="total_amt"></td>
			  </tr>
			</tfoot>
  		</table>
	</div>
</div>

    <!-- 하단 버튼 (실제 제출은 여기서만) -->
    <div class="bottom">
      <a href="<c:url value='/orderList'/>"><button type="button">목록</button></a>
      <c:if test="${isAdd}">
        <button type="submit" class="primary">등록</button>
      </c:if>
      <c:if test="${isEdit}">
        <button type="submit" class="primary">저장</button>
      </c:if>
    </div>
  </div>
</form>

<%-- 삭제 전용 분리 폼 (중첩 form 금지) --%>
<form id="delForm" method="post" action="<c:url value='/orderDel'/>">
  <input type="hidden" name="order_key" value="${order.order_key}">
</form>

<!-- ===== 부서 선택 모달 ===== -->
<div id="dept_modal" class="modal hide">
  <div class="search">
    <input type="text" class="m_search" placeholder="검색내용을 입력하세요">
    <button class="m_button" type="button">검색</button>
  </div>
  <div class="table_box table_wrap" tabindex="0">
    <table class="tables">
      <thead>
        <tr>
          <th></th><th>부서번호</th><th>부서명</th><th>사원번호</th><th>이름</th>
        </tr>
      </thead>
      <tbody class="list">
        <c:forEach var="d" items="${deptList}">
          <tr>
            <td><input type="radio" name="dept" <c:if test='${ro}'>disabled</c:if>></td>
            <td>${d.dapart_ID2}</td>
            <td>${d.depart_level}</td>
            <td>${d.worker_id}</td>
            <td>${d.worker_name}</td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
  <div class="bottom">
    <button class="cancel m_button" type="button">취소</button>
    <button class="confirm m_button" type="button" <c:if test='${ro}'>disabled</c:if>>확인</button>
  </div>
</div>

<!-- ===== 거래처 선택 모달 ===== -->
<div id="client_modal" class="modal hide">
  <div class="search">
    <input type="text" class="m_search" placeholder="검색내용을 입력하세요">
    <button class="m_button" type="button">검색</button>
  </div>
  <div class="table_wrap" tabindex="0">
    <table class="tables">
      <thead>
        <tr>
          <th></th><th>거래처코드</th><th>거래처명</th><th>사업자번호</th><th>연락처</th>
        </tr>
      </thead>
      <tbody class="list">
        <c:forEach var="c" items="${clientList}">
          <tr>
            <td><input type="radio" name="client" value="${c.client_id}" <c:if test='${ro}'>disabled</c:if>></td>
            <td class="col-id">${c.client_id}</td>
            <td class="col-name">${c.client_name}</td>
            <td class="col-biz">${c.business_number}</td>
            <td class="col-phone">${c.client_phone}</td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
  <div class="bottom">
    <button class="cancel m_button" type="button">취소</button>
    <button class="confirm m_button" type="button" <c:if test='${ro}'>disabled</c:if>>확인</button>
  </div>
</div>

<!-- ===== 품목 선택 모달 ===== -->
<div id="item_modal" class="modal hide">
  <div class="search">
    <input type="text" class="m_search" placeholder="검색내용을 입력하세요">
    <button class="m_button" type="button">검색</button>
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
      <tbody class="list">
        <c:forEach var="i" items="${itemList}">
          <tr>
            <td><input type="radio" name="item"></td>
            <td>${i.item_code}</td>
            <td>${i.item_name}</td>
            <td>${i.item_type}</td>
            <td><fmt:formatNumber value="${i.item_price}" pattern="#,##0"/></td>
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
    <button class="cancel m_button" type="button">취소</button>
    <button class="confirm m_button" type="button">추가</button>
  </div>
</div>

<!-- ===== 제출 직전 품목 hidden 생성(서버는 [] 없는 name으로 받음) ===== -->
<script>
(function(){
  const form = document.getElementById('orderForm');
  if(!form) return;

  form.addEventListener('submit', function(e){
    // 기존 자동 생성분 제거
    form.querySelectorAll('.auto-hidden').forEach(n => n.remove());

    const rows = form.querySelectorAll('.order_list tr');
    let count = 0;

    rows.forEach((tr, idx) => {
      // 우선 data-attr → 클래스 → 셀 인덱스 순으로 안전하게 읽기
      const code  = (tr.dataset.code  || tr.querySelector('.col-code') ?.textContent || tr.cells[2]?.textContent || '').trim();
      const price = (tr.dataset.price || tr.querySelector('.col-price')?.textContent || tr.cells[3]?.textContent || '').trim();
      const qty   = (tr.dataset.qty   || tr.querySelector('.col-qty')  ?.textContent || tr.cells[4]?.textContent || '').trim();

      if(!code) return;

      [['item_code',code],['item_price',price],['quantity',qty]].forEach(([name,val])=>{
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = name;       // [] 없이 다중 같은 name → getParameterValues("item_code")
        input.value= val || '';
        input.className = 'auto-hidden';
        form.appendChild(input);
      });
      count++;
    });

    if ((${isAdd or isEdit} && count===0)) {
      e.preventDefault();
      alert('품목을 추가하세요.');
    }
  });
})();
</script>

</body>
</html>
