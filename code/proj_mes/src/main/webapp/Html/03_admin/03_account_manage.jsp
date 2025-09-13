<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>계정 관리 페이지</title>

  <!-- 정적 리소스 -->
  <link rel="stylesheet" href="<c:url value='/Html/asset/template.css'/>">
  <link rel="stylesheet" href="<c:url value='/Html/asset/03_account_manage.css'/>">
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
      <!-- Inter 폰트 & Tailwind -->
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>

  <!-- Chart.js -->
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>

<body>
  <!-- 템플릿 슬롯 -->
  <div id="header-slot"></div>
  <div id="nav-slot"></div>

  <div class="titleBox">
    <span>마이 페이지</span>
  </div>

  <div class="wrap">
    <main class="main">


      <section class="card">
        <div id="acc-title" class="card-header">계정목록</div>
        <div class="card-body">
          <!-- 삭제/수정 제출 폼 -->
          <form id="deleteForm" method="post" action="<c:url value='/AccountManage/delete'/>">
            <div id="hiddenIds"></div>
          </form>
          <form id="updateForm" method="post" action="<c:url value='/AccountManage/update'/>">
            <input type="hidden" name="workerId"    id="updWorkerId">
            <input type="hidden" name="dapartId2"   id="updDept">
            <input type="hidden" name="workerCando" id="updRole">
          </form>

          <!-- 액션 바 -->
          <div class="actions" style="display:flex; gap:8px; align-items:center; margin-bottom:8px;">
            <input id="searchInput" type="text" placeholder="이름/사번/부서 검색" style="padding:6px 8px;">
            <button id="btnSearch" class="btn" type="button">검색</button>
            <button id="btnRefresh" class="btn" type="button">새로고침</button>

            <div style="margin-left:auto; display:flex; gap:8px;">
              <!-- 계정생성 -->
              <a href="<c:url value='/AccountCreate'/>"><button class="btn" type="button">계정생성</button></a>
              <button id="btnAskDelete" class="btn" type="button">선택 삭제</button>
            </div>
          </div>

          <!-- 테이블 -->
          <div class="table-wrap" tabindex="0">
            <table class="tables" id="accountTable">
              <thead>
                <tr>
                  <th style="width:44px; text-align:center;"><input type="checkbox" id="selectAll"></th>
                  <th scope="col">사번</th>
                  <th scope="col">이름</th>
                  <th scope="col">부서ID</th>
                  <th scope="col">직급</th>
                  <th scope="col">권한</th>
                  <th scope="col" style="width:90px;">액션</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="acc" items="${accounts}">
                  <tr data-empno="${acc.workerId}" data-name="${acc.workerName}">
                    <td style="text-align:center;">
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
        </div>
      </section>

      <!-- 삭제 모달 -->
      <div class="modal_bg" id="deleteModalBg" style="display:none;"></div>
      <div class="modal_wrap" id="deleteModal" style="display:none;">
        선택한 계정을 삭제할까요?
        <div id="deleteModalText" style="margin:8px 0 12px;"></div>
        <div style="display:flex; gap:8px; justify-content:flex-end;">
          <button class="modal_close" id="btnConfirmDelete" type="button">예, 삭제</button>
          <button class="modal_close" id="btnCancelDelete" type="button">아니요</button>
        </div>
      </div>

      <!-- 수정 모달 -->
      <div class="modal_bg" id="editModalBg" style="display:none;"></div>
      <div class="modal_wrap" id="editModal" style="display:none;">
        <div style="font-weight:700; margin-bottom:8px;">계정 수정</div>
        <div style="margin-bottom:6px;">사번: <span id="editEmpNo" style="font-weight:600;"></span></div>
        <div style="margin-bottom:12px;">이름: <span id="editName"></span></div>

        <label style="display:block; margin-bottom:8px;">
          부서ID
          <input id="editDept" type="text" placeholder="예: D001" style="width:90%; padding:6px 8px; margin:0 auto;">
        </label>
        <label style="display:block; margin-bottom:12px;">
          권한
          <select id="editRole" style="width:100%; padding:6px 8px; margin-top:4px;">
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

      <!-- 감사 로그 -->
      <section class="card">
        <div id="audit-title" class="card-header audit-header" style="text-align:center;">감사 로그(Audit)</div>
        <div class="card-body">
          <textarea id="audit" class="audit-textarea" placeholder="시스템의 감사 로그가 여기에 표시됩니다."></textarea>
        </div>
      </section>
    </main>
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
