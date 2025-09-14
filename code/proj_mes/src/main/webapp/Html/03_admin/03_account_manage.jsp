<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<% String ctx = request.getContextPath(); %>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>계정 관리 페이지</title>
	
  <!-- 정적 리소스 -->
  <link rel="stylesheet" href="<c:url value='/Html/asset/03_account_manage.css'/>">
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
      <!-- Inter 폰트 & Tailwind -->
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>

  <!-- Chart.js -->
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <style>
	body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
	.header-bg { background-color: #002a40; }
	.nav-bg { background-color: #003751; }
.mainList li:hover { background-color: #3b82f6; }
</style>
</head>

<body>
  <!-- 템플릿 슬롯 -->
  <div id="header-slot"></div>
  <div id="nav-slot"></div>

  <div class="titleBox">
    <span>계정 목록</span>

          <!-- 삭제/수정 제출 폼 -->
          <form id="deleteForm" method="post" action="<c:url value='/AccountManage/delete'/>">
            <div id="hiddenIds"></div>
          </form>
          <form id="updateForm" method="post" action="<c:url value='/AccountManage/update'/>">
            <input type="hidden" name="workerId"    id="updWorkerId">
            <input type="hidden" name="dapartId2"   id="updDept">
            <input type="hidden" name="workerCando" id="updRole">
          </form>
  <div class="wrap">
	<div class="box">
       <!-- 액션 바 -->
       <div>
         <input id="searchInput" type="text" placeholder="이름/사번/부서 검색">
 	   </div>
 	   
       <div>
        <button id="btnSearch" class="btn filter_btn" type="button">검색</button>
        <!-- <button id="btnRefresh" class="btn reset_btn" type="button">새로고침</button> -->
       </div>
	</div>
  </div>
<div class="wrap_list">
            <div class="box">
              <!-- 계정생성 -->
              <button id="btnAskDelete" class="btn delete_btn" type="button">삭제</button>
              <a href="<c:url value='/AccountCreate'/>"><button class="btn add_btn" type="button">생성</button></a>
            </div>
          <!-- 테이블 -->
          <div class="table-wrap" tabindex="0">
            <table class="tables" id="accountTable">
              <thead>
                <tr>
                  <th><input type="checkbox" id="selectAll"></th>
                  <th scope="col">사번</th>
                  <th scope="col">이름</th>
                  <th scope="col">부서ID</th>
                  <th scope="col">직급</th>
                  <th scope="col">권한</th>
                  <th scope="col">액션</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="acc" items="${accounts}">
                  <tr data-empno="${acc.workerId}" data-name="${acc.workerName}">
                    <td>
                      <input type="checkbox" class="rowCheck" data-empno="${acc.workerId}" data-name="${acc.workerName}">
                    </td>
                    <td><c:out value="${acc.workerId}"/></td>
                    <td><c:out value="${acc.workerName}"/></td>
                    <td class="colDept"><c:out value="${acc.dapartId2}"/></td>
                    <td class="colGrade"><c:out value="${acc.workerGrade}"/></td>
                    <td class="colRole"><c:out value="${acc.workerCando}"/></td>
                    <td><button class="btn btnEdit" type="button">수정</button></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
      <!-- 감사 로그 -->
      <div class="list">
        <div id="audit-title" class="box header_list" >
	        <div>감사 로그(Audit)</div>
	          <textarea id="audit" class="audit-textarea" placeholder="시스템의 감사 로그가 여기에 표시됩니다."></textarea>
      	</div>
      </div>
  </div>
</div>
      <!-- 삭제 모달 -->
      <div class="modal_bg" id="deleteModalBg" style="display:none;"></div>
      <div class="modal_wrap" id="deleteModal" style="display:none;">
        선택한 계정을 삭제할까요?
        <div id="deleteModalText"></div>
        <div >
          <button class="modal_close" id="btnConfirmDelete" type="button">예, 삭제</button>
          <button class="modal_close" id="btnCancelDelete" type="button">아니요</button>
        </div>
      </div>

      <!-- 수정 모달 -->
      <div class="modal_bg" id="editModalBg" style="display:none;"></div>
      <div class="modal_wrap" id="editModal" style="display:none;">
        <div >계정 수정</div>
        <div>사번: <span id="editEmpNo" ></span></div>
        <div>이름: <span id="editName"></span></div>

        <label style="display:block;">
          부서ID
          <input id="editDept" type="text" placeholder="예: D001" >
        </label>
        <label style="display:block;">
          권한
          <select id="editRole" >
            <option value="USER">USER</option>
            <option value="MANAGER">MANAGER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </label>

        <div style="display:flex; gap:8px; justify-content:flex-end;">
          <button class="modal_close" id="btnEditSave" type="button">저장</button>
          <button class="modal_close" id="btnEditCancel" type="button">취소</button>
        </div>
      </div>


  <!-- 페이지 스크립트 (jQuery 그대로 유지) -->
  <script>
    function getCheckedIds(){ return $('.rowCheck:checked').map(function(){ return $(this).data('empno'); }).get(); }

    // 전체선택
    $(document).on('change','#selectAll', function(){ $('.rowCheck').prop('checked', this.checked); });

    // 검색(SSR 재요청)
    $('#btnSearch').on('click', function(){
      const q = $('#searchInput').val().trim();
      const base = '<c:url value="/AccountManage"/>';
      location.href = q ? (base + '?q=' + encodeURIComponent(q)) : base;
    });
    $('#searchInput').on('keydown', e => { if (e.key === 'Enter') $('#btnSearch').click(); });
    $('#btnRefresh').on('click', () => location.href = '<c:url value="/AccountManage"/>');

    // 삭제 모달
    $('#btnAskDelete').on('click', function(){
      const ids = getCheckedIds();
      if (!ids.length){ alert('삭제할 계정을 선택하세요.'); return; }
      const list = ids.slice(0,10).join(', ') + (ids.length>10 ? ` 외 ${ids.length-10}건` : ``);
      $('#deleteModalText').html(`다음 사번이 삭제됩니다: ${list}.<br>이 작업은 되돌릴 수 없습니다.`);
      $('#deleteModalBg, #deleteModal').show();
    });
    $('#btnCancelDelete, #deleteModalBg').on('click', () => $('#deleteModalBg, #deleteModal').hide());
    $('#btnConfirmDelete').on('click', function(){
      const ids = getCheckedIds();
      if(!ids.length){ $('#deleteModalBg, #deleteModal').hide(); return; }
      const $hidden = $('#deleteForm #hiddenIds').empty();
      ids.forEach(id => $hidden.append(`<input type="hidden" name="ids" value="${id}">`));
      $('#deleteModalBg, #deleteModal').hide();
      $('#deleteForm')[0].submit();
    });

    // 수정 모달
    $(document).on('click','.btn.btnEdit', function(){
      const $tr = $(this).closest('tr');
      $('#editEmpNo').text($tr.data('empno'));
      $('#editName').text($tr.data('name'));
      $('#editDept').val(($tr.find('.colDept').text()||'').trim());
      $('#editRole').val(($tr.find('.colRole').text()||'').trim() || 'USER');
      $('#editModalBg, #editModal').show();
    });
    $('#btnEditCancel, #editModalBg').on('click', () => $('#editModalBg, #editModal').hide());
    $('#btnEditSave').on('click', function(){
      const id   = $('#editEmpNo').text();
      const dept = $('#editDept').val().trim();
      const role = $('#editRole').val();
      if (!dept){ alert('부서ID를 입력하세요.'); return; }
      $('#updWorkerId').val(id);
      $('#updDept').val(dept);
      $('#updRole').val(role);
      $('#editModalBg, #editModal').hide();
      $('#updateForm')[0].submit();
    });

    // 감사 로그
    function appendAudit(line){
      const $audit = $('#audit');
      const ts = new Date().toISOString().replace('T',' ').substring(0,19);
      $audit.val(($audit.val() + `\n[${ts}] ` + line).trim());
      $audit.scrollTop($audit[0].scrollHeight);
    }
  </script>

  <!-- 템플릿 로더 설정 + 스크립트 -->
  <script>
    window.__TPL_CONFIG = {
      root: '<c:url value="/"/>',
      templateUrl: '<c:url value="/Html/00_template/template.html"/>'
    };
  </script>
  <script src="<c:url value='/Html/asset/template_load.js'/>" defer></script>
</body>
</html>
