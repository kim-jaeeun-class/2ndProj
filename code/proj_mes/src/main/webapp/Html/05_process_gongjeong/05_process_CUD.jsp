<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>공정 등록/수정/삭제</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Html/asset/05_process_CUD.css">
	<script src ="${pageContext.request.contextPath}/Html/asset/template_load.js" ></script>
	<script src="${pageContext.request.contextPath}/Html/asset/05_process_CUD.js"></script>
</head>
<body>
	<header></header>
	<div class="gnb"></div>
	<div class="titleBox">
		<span>공정 등록/수정/삭제</span>
		<a href="">
			<div class="toMainpage">
				<img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표"
					style="transform: scaleX(-1);">
				메인 화면으로
			</div>
		</a>
	</div>

	<div class="wrap">
		<div class="lookup">
			<div>
				품목 코드&nbsp;
				<select id="itemCodeSelect">
					<c:forEach var="departLevel" items="${departLevels}">
							<option value="${departLevel}">
								<c:out value="${departLevel}" />
							</option>
						</c:forEach>
					</select>
				공정 검색 &nbsp;
				<select id="procSearchSelect">
					<option value="" selected>전체</option>
					<c:forEach var="departLevel" items="${departLevels}">
						<option value="${departLevel}">
							<c:out value="${departLevel}" />
						</option>
					</c:forEach>
				</select>
			</div>
			<div>
				<input type="button" value="조회" class="lookupBtn btn"></input>
				<input type="button" value="새 공정 등록" class="newBtn btn"></input>
			</div>
		</div>
		
		<span>공정 정보 입력</span><br>
		<div class="processInfo">
			
			<div class="table-container">
				<table class="inputTb">
					<tr>
						<td>품목 코드</td>
						<td><input type="text" id=""></td>
					</tr>
					<tr>
						<td>공정 ID</td>
						<td><input type="text" id="procId"></td>
					</tr>
					<tr>
						<td>공정명</td>
						<td><input type="text" id="procName"></td>
					</tr>
					<tr>
						<td>담당 부서</td>
						<td>
							<select id="departSelect">
								<option value="" selected>선택</option>
								<!-- 서버에서 전달받은 departLevels 목록을 JSTL로 동적 생성 -->
								<c:forEach var="departLevel" items="${departLevels}">
									<option value="${departLevel}">
										<c:out value="${departLevel}" />
									</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td>담당자</td>
						<td><input type="text" id="procManager"></td>
					</tr>
				</table>
			</div>
			
			<div class="imgDiv">
				공정이미지
				<input type="file">
			</div>
		</div>

		<div class="bottomBtn">
			<input type="button" value="삭제" class="deleteBtn btn"></input>
			<input type="button" value="수정" class="updateBtn btn"></input>
			<input type="button" value="등록" class="createBtn btn"></input>
		</div>
	</div>

</body>
</html>