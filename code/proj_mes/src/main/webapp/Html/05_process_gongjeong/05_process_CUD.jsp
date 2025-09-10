<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
		<form id="lookupForm" action="process" method="get">
			<div class="lookup">
				<div class="lookLeft">
					<div>
						품목 코드
						<select id="itemCodeSelectCUD" name="itemCode">
						    <option value="" selected>선택</option>
						    <c:forEach var="itemCode" items="${itemCodes}">
						        <option value="${itemCode}" <c:if test="${process.item_code eq itemCode}">selected</c:if>>
						            <c:out value="${itemCode}" />
						        </option>
						    </c:forEach>
						</select>
					</div>
					<div>
						부서
						<select id="departSearchSelect" name="departLevel">
							<option value="">전체</option>
							<c:forEach var="departLevel" items="${departLevels}">
								<option value="${departLevel}" <c:if test="${selectedDepart eq departLevel}">selected</c:if>>
									<c:out value="${departLevel}" />
								</option>
							</c:forEach>
						</select>
					</div>
					<div>
						공정
						<select id="procSearchSelect" name="procName">
							<option value="">전체</option>
							<c:forEach var="procName" items="${procNames}">
								<option value="${procName}" <c:if test="${selectedProcName eq procName}">selected</c:if>>
									<c:out value="${procName}" />
								</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="loolRight">
					<input type="button" value="조회" class="lookupBtn btn"></input>
					<input type="button" value="새 공정 등록" class="newBtn btn"></input>
				</div>
			</div>
		</form>
		
		<form id="processCUDForm" action="process" method="post" enctype="multipart/form-data">
			<input type="hidden" name="action" id="action">
			<input type="hidden" name="procId" id="procId_hidden">
			<span>공정 정보 입력</span><br>
			<div class="processInfo">
				
				<div class="table-container">
					<table class="inputTb">
						<tr>
							<td>품목 코드</td>
							<td>
								<select id="itemCodeSelectCUD" name="itemCode" onchange="this.form.submit()">
									<option value="" selected>선택</option>
									<c:forEach var="itemCode" items="${itemCodes}">
										<option value="${itemCode}" <c:if test="${process.item_code eq itemCode}">selected</c:if>>
											<c:out value="${itemCode}" />
										</option>
									</c:forEach>
								</select>
								<input type="text" id="newItemCode" placeholder="새 품목코드 입력 (선택)" style="display:none;">
							</td>
						</tr>
						<tr>
							<td>공정 순서</td>
							<td><input type="text" id="procId" value="${process.proc_id}" <c:if test="${mode eq 'update'}">readonly</c:if>></td>
						</tr>
						<tr>
							<td>소속 부서</td>
							<td>
								<select id="departSelect" name="departLevel">
									<option value="" selected>선택</option>
									<c:forEach var="departLevel" items="${departLevels}">
										<option value="${departLevel}" <c:if test="${process.depart_level eq departLevel}">selected</c:if>>
											<c:out value="${departLevel}" />
										</option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<td>공정</td>
							<td><input type="text" id="procName" name="procName" value="${process.proc_name}"></td>
						</tr>
						<tr>
							<td>공정 설명</td>
							<td>
								<textarea id="procInfo" name="procInfo" rows="5" cols="40">${process.proc_info}</textarea>
							</td>
						</tr>
					</table>
				</div>
				
				<div class="imgDiv">
				    공정이미지
				    <div>
				        <input type="file" name="procImage" id="procImageInput">
				        <c:if test="${not empty process.proc_img}">
				            <img src="${pageContext.request.contextPath}/${process.proc_img}" alt="공정 이미지" id="procImage">
				            <button type="button" id="deleteImageBtn">이미지 삭제</button>
				        </c:if>
				        <c:if test="${empty process.proc_img}">
				            <img src="" alt="공정 이미지" id="procImage" style="display:none;">
				        </c:if>
				        <input type="hidden" name="deleteImage" id="deleteImageHidden" value="false">
				    </div>
				</div>
			</div>

			<div class="bottomBtn">
				<input type="button" value="삭제" class="deleteBtn btn"></input>
				<input type="button" value="수정" class="updateBtn btn"></input>
				<input type="button" value="등록" class="createBtn btn"></input>
			</div>
		</form>
	</div>
</body>
</html>