<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="cPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>기준 목록</title>

  <!-- 정적 리소스: 컨텍스트 경로 안전 처리 -->
  <link rel="stylesheet" href="<c:url value='/Html/asset/template.css' />" />
  <link rel="stylesheet" href="<c:url value='/Html/asset/04_standard_list.css' />" />
  <script src="<c:url value='/Html/asset/template_load.js' />"></script>
</head>

<body>
  <!-- 헤더 -->
  <header></header>

  <!-- GNB -->
  <div class="gnb"></div>

  <!-- 타이틀 -->
  <div class="titleBox">
    <span>기준 목록</span>
    <a href="${cPath}/">
      <div class="toMainpage">
        <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png"
             width="13" alt="메인 화면으로 가는 화살표" style="transform:scaleX(-1);" />
        메인 화면으로
      </div>
    </a>
  </div>

  <!-- 본문 -->
  <div class="wrap">
    <main class="main">
      <!-- 상단 툴바 -->
      <div class="toolbar" role="region" aria-label="검색 및 등록">
        <label for="std-category">분류</label>
        <div class="select">
          <select id="std-category" name="category">
            <option value="">선택하세요</option>
            <!-- 서버에서 categories(List<String>) 넘겨온 경우 -->
            <c:choose>
              <c:when test="${not empty categories}">
                <c:forEach var="cat" items="${categories}">
                  <option><c:out value="${cat}" /></option>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <option>공정</option>
                <option>BOM</option>
                <option>발주</option>
                <option>재고</option>
                <option>생산</option>
                <option>품질</option>
              </c:otherwise>
            </c:choose>
          </select>
        </div>
        <button class="btn" type="button" id="std-search">조회</button>
        <div class="spacer"></div>
        <button class="btn" type="button" id="std-create">등록하기</button>
      </div>

      <!-- 표 -->
      <section aria-label="기준 목록 표">
        <div class="table-wrap" tabindex="0">
          <table class="tables" aria-label="기준 목록">
            <colgroup>
              <col style="width:120px" />  <!-- 번호 -->
              <col style="width:220px" />  <!-- 코드명 -->
              <col style="width:260px" />  <!-- 이름 -->
              <col />                      <!-- 속성: 남는 폭 -->
            </colgroup>
            <thead>
              <tr>
                <th scope="col">번호</th>
                <th scope="col">코드명</th>
                <th scope="col">이름</th>
                <th scope="col">속성</th>
              </tr>
            </thead>
            <tbody id="std-body">
              <c:choose>
                <!-- 컨트롤러에서 model.addAttribute("stdList", list)로 전달된 경우 -->
                <c:when test="${not empty stdList}">
                  <c:forEach var="std" items="${stdList}" varStatus="s">
                    <tr>
                      <td><c:out value="${s.index + 1}" /></td>
                      <td><c:out value="${std.code}" /></td>
                      <td><c:out value="${std.name}" /></td>
                      <!-- attributes/props 등 필드명은 프로젝트 DTO에 맞게 조정 -->
                      <td><c:out value="${std.attributes}" /></td>
                    </tr>
                  </c:forEach>
                </c:when>
                <c:otherwise>
           
                  <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                  <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                  <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                  <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                  <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                  <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                  <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                  <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                  <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>
      </section>
    </main>
  </div>

  <!-- 필요한 경우 페이지 전용 스크립트를 여기 추가하세요 -->
  <script src="<c:url value='/Html/asset/04_standard_list.js' />"></script>
</body>
</html>
