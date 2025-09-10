<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>공정 목록</title>
	<link rel="stylesheet" href="../asset/05_process_list.css">
	<script src ="../asset/template_load.js" ></script>
	<script src ="../asset/05_process_list.js" ></script>
</head>
<body>
	<header></header>
	<div class="gnb"></div>
	<div class="titleBox">
		<span>공정 목록</span>
		<a href="">
			<div class="toMainpage">
				<img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표"
					style="transform: scaleX(-1);">
				메인 화면으로
			</div>
		</a>
	</div>

	<div class="wrap">
		<form id="processForm" action="process" method="get">
			<div class="lookup">
				<div class="lookup_left">
					담당 부서
					<select name="departLevel" onchange="this.form.submit()">
						<option value="">전체</option>
						<c:forEach var="departLevel" items="${departLevels}">
							<option value="${departLevel}" <c:if test="${selectedDepart eq departLevel}">selected</c:if>>
								<c:out value="${departLevel}" />
							</option>
						</c:forEach>
					</select>&nbsp;
					공정명
					<select name="procName">
						<option value="">전체</option>
						<!-- 담당 부서 선택 시 페이지가 새로 로드되므로, 여기서는 모든 공정 목록을 미리 보여주는 방식이 비효율적입니다. -->
						<!-- 따라서 부서 선택 후 조회 버튼을 누르는 방식으로 로직을 변경합니다. -->
						<c:if test="${not empty processes}">
							<c:forEach var="proc" items="${processes}">
								<option value="${proc.proc_name}">
									<c:out value="${proc.proc_name}" />
								</option>
							</c:forEach>
						</c:if>
					</select>
				</div>
				<div class="btnDiv">
					<!-- 조회 버튼 클릭 시 폼 제출 -->
					<input type="submit" value="조회" class="lookupBtn btn"></input>
					<input type="button" value="수정" class="updateProcessBtn btn"></input>
					<input type="button" value="신규" class="newProcessBtn btn"></input>
				</div>
			</div>
		</form>
		
		<div class="table-container" style="display: <c:if test="${not empty processes}">block</c:if>">
			<table class="proc_tab">
				<thead>
					<tr>
						<th>선택</th>
						<th>순번</th>
						<th>공정 ID</th>
						<th>공정명</th>
						<th>담당 부서</th>
						<th>담당자</th>
						<th>검사 여부</th>
					</tr>		
				</thead>
				<tbody>
					<!-- JSTL을 사용해 공정 목록 동적으로 출력 -->
					<c:if test="${empty processes}">
						<tr><td colspan="7">조회된 공정 정보가 없습니다.</td></tr>
					</c:if>
					<c:if test="${not empty processes}">
						<c:forEach var="proc" items="${processes}" varStatus="status">
							<tr>
								<td><input type="checkbox"></td>
								<td><c:out value="${status.count}" /></td>
								<td><c:out value="${proc.proc_id}" /></td>
								<td><c:out value="${proc.proc_name}" /></td>
								<td><c:out value="${proc.depart_level}" /></td>
								<td><c:out value="${proc.proc_info}" /></td> <%-- 담당자 정보가 없어서 proc_info로 대체함 --%>
								<td><c:out value="${proc.process_check eq 1 ? '검사' : '비검사'}" /></td>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>
