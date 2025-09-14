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
	<script src ="${pageContext.request.contextPath}/Html/asset/06_bom_list.js" ></script>
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
					<div class = "lookup-title">품목(완제품) 코드</div>
					<select name = "filter-item1">
						<!-- TODO 지금 하드코딩 해놨는데 DB에서 따오도록 설정 바꿔두기 -->
						<!-- 여기에서 name이든 뭐든 설정해야 하니까 아래 테이블처럼 설정해두기 -->
						<option value="all" selected>전체</option>
						<option value="FPPHS23">FPPHS23</option>
						<option value="FPPHA17">FPPHA17</option>
						<option value="FPNIN3D">FPNIN3D</option>
						<option value="FPNINLI">FPNINLI</option>
						<option value="FPNVI05">FPNVI05</option>
						<option value="FPNVI07">FPNVI07</option>
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
									<input type="checkbox" value="${bom.bomID}" name="chk">
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
										<input type="checkbox" value="${bom.bomID}" name="chk">
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
					<input type="submit" value="삭제" name="delete" class="deleteBOMBtn btn"></input>
				</div>
			</form>
		</div>
	</div>
	<!-- 등록용 -->
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
				<div class = "modal-title">BOM 상세</div>
				<table class = "modal-table">
					<tr>
						<td>BOM No.</td>
						<td id="bomID"></td>
					</tr>
					<tr>
						<td>완제품</td>
						<td id="itemCode1"></td>
					</tr>
					<tr>
						<td>자재</td>
						<td id="itemCode2"></td>
					</tr>
					<tr>
						<td>소요량</td>
						<td id="requireAmount"></td>
					</tr>
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

                (function() {
                    // 전체 선택
                    const checkAll = document.getElementById('check_all');
                    const body = document.querySelector('.tables_body');
                    if (checkAll && body) {
                    checkAll.addEventListener('change', () => {
                        body.querySelectorAll('.row_check').forEach(cb => cb.checked = checkAll.checked);
                    });
                    }

                    // 삭제
                    const deleteBtn  = document.getElementById('deleteBtn');
                    const deleteKeys = document.getElementById('delete_keys');
                    const deleteForm = document.getElementById('deleteForm');

                    if (deleteBtn && deleteKeys && deleteForm) {
                    deleteBtn.addEventListener('click', () => {
                        const keys = Array.from(document.querySelectorAll('.row_check:checked'))
                        .map(cb => cb.value)
                        .filter(Boolean);

                        if (keys.length === 0) {
                        alert('삭제할 항목을 선택하세요.');
                        return;
                        }
                        deleteKeys.value = keys.join(',');
                        deleteForm.submit();
                    });
                    }
                    
                })();
        </script>
</body>
</html>