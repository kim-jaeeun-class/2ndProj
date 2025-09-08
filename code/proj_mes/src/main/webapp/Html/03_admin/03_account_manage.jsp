<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>계정 관리 페이지</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/template.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/03_account_manage.css">
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body>
	<header>
		<div class="corp">
			<img src="https://i.postimg.cc/qMsq73hD/icon.png" width="40px" alt="회사 로고">
			<h3>J2P4</h3>
		</div>
		<div class="search">
			<img src="https://i.postimg.cc/9QcMwQym/magnifier-white.png" width="30px" alt="검색용 아이콘">
		</div>
		<div class="logout">로그아웃</div>
		<div class="myIcon">
			<a href="javascript:void(0);" id="myIconBtn">
				<img src="https://i.postimg.cc/zfVqTbvr/user.png" width="35px" alt="마이페이지 아이콘"
					style="background-color: #ffffff; border-radius: 100%;">
			</a>
			<ul class="subList">
				<li><a href="">하위메뉴1</a></li>
				<li><a href="">하위메뉴2</a></li>
			</ul>
		</div>
	</header>

	<div class="gnb">
		<ul class="mainList">
			<li>기준 관리
				<ul class="subList"><li><a href="">기준 목록</a></li></ul>
			</li>
			<li>공정 관리
				<ul class="subList">
					<li><a href="">공정 목록</a></li>
					<li><a href="">공정 등록/수정/삭제</a></li>
				</ul>
			</li>
			<li>BOM 관리
				<ul class="subList">
					<li><a href="">BOM 목록</a></li>
					<li><a href="">BOM 등록/수정/삭제</a></li>
				</ul>
			</li>
			<li>발주 관리
				<ul class="subList"><li><a href="">발주 목록</a></li></ul>
			</li>
			<li>재고 관리
				<ul class="subList">
					<li><a href="">재고 목록</a></li>
					<li><a href="">재고 삭제</a></li>
				</ul>
			</li>
			<li>생산 관리
				<ul class="subList">
					<li><a href="">생산 계획</a></li>
					<li><a href="">생산 실적 현황</a></li>
				</ul>
			</li>
			<li>품질 관리
				<ul class="subList">
					<li><a href="">LOT 추적</a></li>
					<li><a href="">공정별 현황</a></li>
					<li><a href="">품질 검사 입력</a></li>
				</ul>
			</li>
		</ul>
	</div>

	<div class="titleBox">
		<span>마이 페이지</span>
		<a href="">
			<div class="toMainpage">
				<img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인으로"
					style="transform: scaleX(-1);">
				메인 화면으로
			</div>
		</a>
	</div>

	<div class="wrap">
		<main class="main">
      <div class="tabs">
        <button class="tab active" role="tab" aria-selected="true">이슈</button>
        <button class="tab" role="tab" aria-selected="false">계정 관리</button>
      </div>

      <section class="card">
      	<div id="acc-title" class="card-header">계정목록</div>
      	<div class="card-body">

          <!-- 삭제/수정 제출 폼 -->
          <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/AccountManage/delete">
            <div id="hiddenIds"></div>
          </form>
          <form id="updateForm" method="post" action="${pageContext.request.contextPath}/AccountManage/update">
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
		    <!--계정생성 버튼 -->
		    <a class="btn" href="${pageContext.request.contextPath}/AccountCreate">계정생성</a>
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
				      <td>${acc.workerId}</td>
				      <td>${acc.workerName}</td>
				      <td class="colDept">${acc.dapartId2}</td>
				      <td class="colGrade">${acc.workerGrade}</td> 
				      <td class="colRole">${acc.workerCando}</td>
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
          <input id="editDept" type="text" placeholder="예: D001"
                 style="width:100%; padding:6px 8px; margin-top:4px;">
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

<script>
  function getCheckedIds(){ return $('.rowCheck:checked').map(function(){ return $(this).data('empno'); }).get(); }

  // 전체선택
  $(document).on('change','#selectAll', function(){ $('.rowCheck').prop('checked', this.checked); });

  // 검색(SSR 재요청)
  $('#btnSearch').on('click', function(){
    const q = $('#searchInput').val().trim();
    const base='${pageContext.request.contextPath}/AccountManage';
    location.href = q ? (base + '?q=' + encodeURIComponent(q)) : base;
  });
  $('#searchInput').on('keydown', e => { if (e.key === 'Enter') $('#btnSearch').click(); });
  $('#btnRefresh').on('click', () => location.href='${pageContext.request.contextPath}/AccountManage');

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
    const ids=getCheckedIds(); if(!ids.length){ $('#deleteModalBg, #deleteModal').hide(); return; }
    const $hidden = $('#deleteForm #hiddenIds').empty();
    ids.forEach(id => $hidden.append(`<input type="hidden" name="ids" value="${id}">`));
    $('#deleteModalBg, #deleteModal').hide();
    $('#deleteForm')[0].submit(); // POST /AccountManage/delete
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
    $('#updateForm')[0].submit(); // POST /AccountManage/update
  });

  // 감사 로그 헬퍼
  function appendAudit(line){
    const $audit = $('#audit');
    const ts = new Date().toISOString().replace('T',' ').substring(0,19);
    $audit.val(($audit.val() + `\n[${ts}] ` + line).trim());
    $audit.scrollTop($audit[0].scrollHeight);
  }
</script>
</body>
</html>
