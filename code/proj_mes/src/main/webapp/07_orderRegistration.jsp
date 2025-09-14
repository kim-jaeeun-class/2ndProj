<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>발주 등록/상세</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
<script src="https://cdn.tailwindcss.com"></script>
<style>
	body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
	.header-bg { background-color: #002a40; }
	.nav-bg { background-color: #003751; }
.mainList li:hover { background-color: #3b82f6; }
</style>
<link rel="stylesheet" href="<c:url value='/Html/asset/registration.css'/>">
<script src="<c:url value='/Html/asset/07_order_registration.js'/>"></script>
</head>
<body>
<!--  여기 두 줄이 템플릿에서 끌어올 자리 -->
<div id="header-slot"></div>
<div id="nav-slot"></div>

<div class="titleBox">
	
<%-- 모드 결정 --%>
<c:set var="mode" value="${empty mode ? (empty order ? 'add' : 'view') : mode}"/>
<c:set var="isAdd"  value="${mode eq 'add'}"/>
<c:set var="isEdit" value="${mode eq 'edit'}"/>
<c:set var="isView" value="${mode eq 'view'}"/>
<c:set var="ro"     value="${not isAdd and not isEdit}"/>
	<span>발주 ${isAdd ? '등록' : '상세'}</span>         

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
      <div class="box header_row">
        

        <div class="action">
        
			<c:choose>
				<c:when test="${isView}">
					<a href="${editLink}"><button type="button" class="primary" id= "btnedit">수정</button></a>
					<button type="submit" class="danger" id= "btndelete" form="delForm">삭제</button>
				</c:when>
				<c:when test="${isEdit}">
					<a href="${viewLink}"><button type="button" id="editdel">취소</button></a>
        			<button type="submit" class="primary" id="btnsave">저장</button>
				</c:when>
				<c:when test="${isAdd}">
				    <a href="<c:url value='/orderList'/>"><button type="button" id="back">취소</button></a>
		        	<button type="submit" class="primary" id="btnsave">등록</button>
		      	</c:when>
			</c:choose>

        </div>
      </div>

      <!-- 기본 정보 입력부 -->
      <div class="box">
        <div class="item">
          <div>부서</div>
          <input id="dept_input" class="main_input edit-only" type="text"
                 value="${order.depart_level}" placeholder="부서를 선택하세요" readonly>
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
					<tr data-price="${it.item_price}" data-qty="${it.quantity}">
					  <!-- 체크박스(보기 모드에선 비활성) -->
					  <td>
					    <input type="checkbox" class="row_check" <c:if test="${mode eq 'view'}">disabled</c:if> >
					  </td>
					
					  <!-- NO -->
					  <td>${st.index + 1}</td>
					
					  <!-- 품목 -->
					  <td class="col-code"><c:out value="${it.item_code}"/></td>
					
					  <!-- 단가 -->
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
</form>
</div>
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
          <th></th>
          <th>거래처코드</th>
          <th>거래처명</th>
          <th>사업자번호</th>
          <th>연락처</th>
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
    <button class="confirm m_button" type="button">확인</button>
  </div>
</div>

<!-- ===== 제출 직전 품목 hidden 생성(서버는 [] 없는 name으로 받음) ===== -->
<script>
	// 템플릿의 header/nav만 로드
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
	function _toNumber(s){ if(s==null) return 0; const n=parseFloat(String(s).replace(/[^0-9.]/g,'')); return isNaN(n)?0:n; }
	function _readCell(tr, sel, hiddenName, dataKey){
	  if (dataKey && tr.dataset && tr.dataset[dataKey]!=null) return _toNumber(tr.dataset[dataKey]);
	  const inp = tr.querySelector(`${sel} input[type="text"], ${sel} input[type="number"]`);
	  if (inp && inp.value) return _toNumber(inp.value);
	  if (hiddenName){
	    const hid = tr.querySelector(`input[name="${hiddenName}"],input[name="${hiddenName}[]"]`);
	    if (hid && hid.value) return _toNumber(hid.value);
	  }
	  const td = tr.querySelector(sel);
	  if (td && td.textContent) return _toNumber(td.textContent);
	  return 0;
	}
	function updateTotals(){
	  let totalQty=0, totalAmt=0;
	  document.querySelectorAll('.order_list tr').forEach(tr=>{
	    const qty   = _readCell(tr, '.col-qty', 'quantity', 'qty');
	    const price = _readCell(tr, '.col-price', 'item_price', 'price');
	    const sum = qty*price;
	    totalQty += qty; totalAmt += sum;
	    const cell = tr.querySelector('.col-sum'); if (cell) cell.textContent = new Intl.NumberFormat().format(sum);
	  });
	  const nf=new Intl.NumberFormat();
	  const tq=document.querySelector('.total_qty'); if(tq) tq.textContent = nf.format(totalQty);
	  const ta=document.querySelector('.total_amt'); if(ta) ta.textContent = nf.format(totalAmt);
	}
	document.addEventListener('DOMContentLoaded', updateTotals);
</script>

</body>
</html>
