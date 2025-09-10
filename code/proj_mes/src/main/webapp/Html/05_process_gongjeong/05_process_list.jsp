<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>공정 목록</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/05_process_list.css">
	<script src ="${pageContext.request.contextPath}/Html/asset/template_load.js" ></script>
	<script src ="${pageContext.request.contextPath}/Html/asset/05_process_list.js" ></script>
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
				<div>
					품목코드&nbsp;
					<select name="itemCode" onchange="this.form.submit()">
						<option value="">전체</option>
						<c:forEach var="itemCode" items="${itemCodes}">
							<option value="${itemCode}" <c:if test="${selectedItemCode eq itemCode}">selected</c:if>>
								<c:out value="${itemCode}" />
							</option>
						</c:forEach>
					</select>
				</div>
				<div>	
					부서&nbsp;
					<select name="departLevel" onchange="this.form.submit()">
						<option value="">전체</option>
						<c:forEach var="departLevel" items="${departLevels}">
				           	<option value="${departLevel}" <c:if test="${selectedDepart eq departLevel}">selected</c:if>>
				                <c:out value="${departLevel}" />
				           	</option>
				       	</c:forEach>
					</select>
				</div>
				<div>
					공정명&nbsp;
				    <select name="procName" onchange="this.form.submit()">
						<option value="">전체</option>
						<c:forEach var="procName" items="${procNames}">
				            <option value="${procName}" <c:if test="${selectedProcName eq procName}">selected</c:if>>
				                <c:out value="${procName}" />
				            </option>
				        </c:forEach>
					</select>
				</div>
			</div>
			<div class="btnDiv">
				<input type="button" value="수정" class="updateProcessBtn btn"></input>
				<input type="button" value="신규" class="newProcessBtn btn"></input>
			</div>
		</form>
		
		<div class="table-container" style="display: <c:if test="${not empty processes}">block</c:if>">
			<table class="proc_tab">
				<thead>
					<tr>
						<th>선택</th>
						<th>순번</th>
						<th>공정 ID</th>
						<th>품목 코드</th>
						<th>공정명</th>
						<th>부서</th>
						<th>공정 설명</th>
						<th>이미지</th>
					</tr>		
				</thead>
				<tbody>
					<c:if test="${empty processes}">
						<tr><td colspan="8">조회된 공정 정보가 없습니다.</td></tr>
					</c:if>
					<c:if test="${not empty processes}">
						<c:forEach var="proc" items="${processes}" varStatus="status">
							<tr>
								<td><input type="radio" name="selectedProcId" value="${proc.proc_id}"></td>
								<td><c:out value="${status.count}" /></td>
								<td class="procIdCell"><c:out value="${proc.proc_id}" /></td>
								<td><c:out value="${proc.item_code}" /></td>
								<td class="procNameCell"><c:out value="${proc.proc_name}" /></td>
								<td class="departLevelCell"><c:out value="${proc.depart_level}" /></td>
								<td class="procInfoCell"><c:out value="${proc.proc_info}" /></td>
								<td>
								    <c:if test="${not empty proc.proc_img}">
								        <img src="${pageContext.request.contextPath}/${proc.proc_img}" alt="공정 이미지" style="width: 50px; height: 50px;">
								    </c:if>
								</td>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>