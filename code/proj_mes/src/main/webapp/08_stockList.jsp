<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>재고 목록</title>
  <link rel="stylesheet" href="<c:url value='/Html/asset/list.css'/>">
  <script>
    // 체크박스 전체선택 & 일괄삭제용 키 합치기
    document.addEventListener('DOMContentLoaded', function(){
      const checkAll = document.getElementById('check_all');
      const body = document.querySelector('.tables_body');
      if (checkAll && body) {
        checkAll.addEventListener('change', ()=>{
          body.querySelectorAll('.row_check').forEach(cb => cb.checked = checkAll.checked);
        });
      }
      const delForm = document.getElementById('deleteForm');
      const delHidden = document.getElementById('delete_ids');
      const delBtn = document.getElementById('deleteBtn');
      if (delForm && delHidden && delBtn) {
        delBtn.addEventListener('click', function(e){
          e.preventDefault();
          const ids = Array.from(document.querySelectorAll('.row_check:checked'))
            .map(cb => cb.value).filter(Boolean);
          if (ids.length === 0) { alert('삭제할 항목을 선택하세요.'); return; }
          delHidden.value = ids.join(',');
          delForm.submit();
        });
      }
    });
  </script>
</head>
<body>

<div class="wrap">
  <div class="main">
    <div class="box header_row">
      <div>재고 목록</div>
      <div class="action"></div>
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
                  <c:param name="id" value="${s.stock_id}"/>
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

    <!-- 하단 액션 -->
    <div class="action">
      <form id="deleteForm" method="post" action="<c:url value='/stockDel'/>" style="display:inline;">
        <input type="hidden" name="stock_id" id="delete_ids">
        <button type="submit" class="delete" id="deleteBtn">삭제</button>
      </form>

      <a href="<c:url value='/stockAdd'/>">
        <button type="button" class="add">추가</button>
      </a>
    </div>
  </div>
</div>

</body>
</html>
