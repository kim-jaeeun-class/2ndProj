<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% String ctx = request.getContextPath(); %>
<c:set var="cPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>BOM 목록</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/06_bom_list.css">
  <script src ="${pageContext.request.contextPath}/Html/asset/06_bom_list.js"></script>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <style>
    body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
    .header-bg { background-color: #002a40; }
    .nav-bg { background-color: #003751; }
    .mainList li:hover { background-color: #3b82f6; }
  </style>
</head>
<body class="bg-gray-100 text-gray-800">
  <div id="header-slot"></div>
  <div id="nav-slot"></div>

  <div class="titleBox">
    <span>BOM 목록</span>

    <div class="wrap">
      <form class="lookup" method="post" action="bom">
        <input type="hidden" name="action" value="search">
        <div class="lookup_left">
          <div class="lookup-title">품목(완제품) 코드</div>
          <select name="filter-item1">
<!--             <option value="all" selected>전체</option> -->
<!--             <option value="FPPHS23">FPPHS23</option> -->
<!--             <option value="FPPHA17">FPPHA17</option> -->
<!--             <option value="FPNIN3D">FPNIN3D</option> -->
<!--             <option value="FPNINLI">FPNINLI</option> -->
<!--             <option value="FPNVI05">FPNVI05</option> -->
<!--             <option value="FPNVI07">FPNVI07</option> -->
				 <option value="all"      ${param['filter-item1'] == 'all'      ? 'selected' : ''}>전체</option>
				 <option value="FPPHS23"  ${param['filter-item1'] == 'FPPHS23'  ? 'selected' : ''}>FPPHS23</option>
				 <option value="FPPHA17"  ${param['filter-item1'] == 'FPPHA17'  ? 'selected' : ''}>FPPHA17</option>
				 <option value="FPNIN3D"  ${param['filter-item1'] == 'FPNIN3D'  ? 'selected' : ''}>FPNIN3D</option>
				 <option value="FPNINLI"  ${param['filter-item1'] == 'FPNINLI'  ? 'selected' : ''}>FPNINLI</option>
				 <option value="FPNVI05"  ${param['filter-item1'] == 'FPNVI05'  ? 'selected' : ''}>FPNVI05</option>
				 <option value="FPNVI07"  ${param['filter-item1'] == 'FPNVI07'  ? 'selected' : ''}>FPNVI07</option>
          </select>
        </div>
        <div class="btnDiv">
          <input type="submit" value="조회" class="lookupBtn btn">
          <input type="button" value="신규" class="newBOMBtn btn open-btn">
        </div>
      </form>
    </div>

    <div class="wrap wrap-list">
      <!-- BOM 테이블 -->
      <form method="post" action="bom">
        <div class="table-container">
          <table class="bom_tab">
            <thead>
              <tr>
                <th>
                  <!-- 헤더 전체선택 체크박스 (id 추가하면 아래 JS에서 더 쉽게 제어 가능) -->
                  <input type="checkbox" id="bomCheckAll">
                </th>
                <th>BOM No.</th>
                <th>완제품</th>
                <th>자재</th>
                <th>소요량</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="bom" items="${list}">
                <tr>
                  <td>
                    <input type="checkbox" value="${bom.bomID}" name="chk" class="bom-row-check">
                  </td>
                  <td>${bom.bomID}</td>
                  <td>${bom.item_code_1}</td>
                  <td>${bom.item_code_2}</td>
                  <td>${bom.require_amount}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <div class="bottomBtn">
          <input type="hidden" name="action" value="delete">
          <input type="submit" value="삭제" name="delete" class="deleteBOMBtn btn">
        </div>
      </form>
    </div>
  </div>

  <!-- 등록용 패널 -->
  <div class="panel">
    <button class="close-btn">✕</button>
    <div class="slide-title">BOM 등록</div>
    <form action="bom" method="post">
      <input type="hidden" name="action" value="add">
      <input type="hidden" name="bomID">
      <div class="form-group">
        <label>완제품 품번</label>
        <input type="text" name="item-code-1">
      </div>
      <div class="form-group">
        <label>자재 품번</label>
        <input type="text" name="item-code-2">
      </div>
      <div class="form-group">
        <label>자재 소요량</label>
        <input type="number" name="require-amount">
      </div>
      <div class="form-actions">
        <input type="button" name="panel-cancel" class="button cancel reset" value="취소">
        <button type="submit" name="panel-save" class="button panel-save">저장</button>
      </div>
    </form>
  </div>

  <!-- BOM 상세 모달 -->
  <div id="bomDetailModal" class="modal hidden">
    <div class="modal-content">
      <span id="modalCloseBtn" class="close-btn">✕</span>
      <div class="modal-title">BOM 상세</div>
      <table class="modal-table">
        <tr><td>BOM No.</td><td id="bomID"></td></tr>
        <tr><td>완제품</td><td id="itemCode1"></td></tr>
        <tr><td>자재</td><td id="itemCode2"></td></tr>
        <tr><td>소요량</td><td id="requireAmount"></td></tr>
      </table>
      <div class="modal-actions">
        <form method="post" action="bom" style="display:inline-block;">
          <input type="hidden" name="action" value="delete">
          <input type="hidden" name="chk" id="modalDeleteID">
          <button type="submit">삭제</button>
        </form>
        <button id="modalEditBtn">수정</button>
      </div>
    </div>
  </div>

  <script>
    // 템플릿(header/nav) 비동기 로드 → 삽입 완료 후에 드롭다운 바인딩
    (async function () {
      try {
        const url = '<%= ctx %>/Html/00_template/template.html';
        const text = await (await fetch(url, { credentials: 'same-origin' })).text();
        const doc  = new DOMParser().parseFromString(text, 'text/html');

        const header = doc.querySelector('header.header-bg') || doc.querySelector('header');
        const nav    = doc.querySelector('nav.nav-bg')       || doc.querySelector('nav');

        const headerSlot = document.getElementById('header-slot');
        const navSlot    = document.getElementById('nav-slot');

        if (header && headerSlot) headerSlot.replaceWith(header);
        if (nav && navSlot)       navSlot.replaceWith(nav);

        // 헤더/네비가 DOM에 들어간 다음에만 바인딩
        bindUserMenu();
      } catch (e) {
        console.error('템플릿 로드 실패:', e);
      }
    })();

    // 사람 아이콘 드롭다운: 안전 바인딩(널가드 포함)
    function bindUserMenu() {
      const myIconBtn = document.getElementById('myIconBtn');
      const userMenu  = document.getElementById('userMenu');
      if (!myIconBtn || !userMenu) return; // 템플릿에 요소 없으면 조용히 패스

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
      document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeUserMenu(); });
    }

    // (선택) BOM 전체선택 체크박스 핸들링 — 현재 마크업에 맞춰 동작
    (function(){
      const checkAll = document.getElementById('bomCheckAll');
      if (!checkAll) return;
      checkAll.addEventListener('change', () => {
        document.querySelectorAll('.bom_tab tbody .bom-row-check')
          .forEach(cb => cb.checked = checkAll.checked);
      });
    })();

    // 기존 IIFE에서 #check_all, .tables_body, #deleteBtn 등 없는 요소를 참조하던 부분은 제거했습니다.
  </script>
</body>
</html>
