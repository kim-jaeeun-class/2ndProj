<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>공지 사항</title>
  <style>
    :root { --border:#9aa0a6; --text:#222; --muted:#666; --button:#333; --field-border:#a4a5a7; }
    body { font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Noto Sans KR","Apple SD Gothic Neo","Malgun Gothic","Helvetica Neue",Arial,sans-serif; background:#f5f5f5; margin:0; }
    header{background:#495057;height:50px;border:1px solid;color:#fff;font-size:1rem;font-weight:bold;display:flex;align-items:center;gap:10px;}
    header .corp{display:flex;align-items:center;padding-left:10px;gap:10px;font-size:1.2rem;}
    header .search{margin-left:auto;margin-right:10px;}
    header .myIcon{position:relative;padding:5px 10px 5px 0;}
    header ul.subList{display:none;background:turquoise;position:absolute;top:100%;right:0;list-style:none;color:#333;font-weight:normal;flex-direction:column;z-index:9999;min-width:150px;margin:0;padding:0;}
    header .myIcon:hover .subList{display:flex;}
    .gnb{background:#fff;font-weight:bold;font-size:1.2rem;}
    .gnb ul{list-style:none;display:flex;justify-content:space-evenly;padding-left:0;margin:0;}
    .gnb ul.subList{background:#495057;color:#fff;border-radius:5%;position:absolute;left:0;display:none;font-weight:normal;font-size:medium;flex-direction:column;z-index:9999;min-width:130px;}
    .gnb ul.mainList>li{position:relative;color:#333;}
    .gnb ul.mainList>li:hover .subList{display:flex;margin-top:0;}
    .subList li{padding:2px 4px;margin:1px;}
    a,a:link,a:visited{color:inherit;text-decoration:none;}
    .titleBox{background:var(--field-border);height:25px;margin:0 auto;padding:10px;display:flex;justify-content:space-between;align-items:center;}
    .titleBox span{font-size:1.4em;font-weight:bold;margin-left:10px;color:#333;}
    .toMainpage{color:#333;margin-right:10px;display:flex;gap:6px;align-items:center;}
    .container{width:900px;margin:30px auto;background:#fff;padding:20px;box-shadow:0 0 10px rgba(0,0,0,.08);}
    .search-area-outside{display:flex;flex-direction:row;align-items:center;gap:10px;margin-bottom:10px;margin-left:auto;width:fit-content;white-space:nowrap;}
    .search-area-outside select,.search-area-outside input{padding:6px;border:1px solid #ddd;border-radius:4px;}
    #search-button{padding:6px 12px;background:#333;color:#fff;border:none;border-radius:4px;cursor:pointer;}
    #search-button:hover{background:#555;}
    table{width:100%;border-collapse:collapse;}
    thead{background:#e0e0e0;}
    th,td{padding:12px;text-align:center;border-bottom:1px solid #ddd;}
    td:nth-child(2){text-align:left;}
    .pagination{display:flex;justify-content:center;gap:8px;margin:20px 0;}
    .pagination a{padding:6px 10px;border:1px solid #ddd;border-radius:4px;}
    .pagination a.active{background:#333;color:#fff;border-color:#333;}
    .button-area{text-align:right;margin-top:10px;}
    .write-button{display:inline-block;padding:8px 16px;background:#333;color:#fff;border-radius:4px;}
    .write-button:hover{background:#555;}
  </style>
</head>
<body>
  <%@ include file="header.jspf" %>

  <div class="titleBox">
    <span>공지 사항</span>
    <a href="${pageContext.request.contextPath}/main">
      <div class="toMainpage">
        <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13" alt="메인 이동" style="transform:scaleX(-1);">
        메인 화면으로
      </div>
    </a>
  </div>

  <div class="container">
    <!-- 검색 -->
    <form class="search-area-outside" method="get" action="${pageContext.request.contextPath}/notice/list">
      <label for="field">검색조건</label>
      <select id="field" name="field">
        <option value="title" ${param.field == 'title' ? 'selected' : ''}>제목</option>
        <option value="content" ${param.field == 'content' ? 'selected' : ''}>내용</option>
      </select>
      <input type="text" id="q" name="q" value="${fn:escapeXml(param.q)}" placeholder="검색할 내용을 입력하세요">
      <button id="search-button" type="submit">검색</button>
    </form>

    <!-- 목록 -->
    <table>
      <thead>
        <tr>
          <th>유형</th>
          <th>제목</th>
          <th>담당부서</th>
          <th>등록일</th>
          <th>조회수</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${not empty notices}">
            <c:forEach var="n" items="${notices}">
              <tr>
                <td><c:out value="${n.type}" /></td>
                <td>
                  <a href="${pageContext.request.contextPath}/notice/view?id=${n.id}">
                    <c:out value="${n.title}" />
                  </a>
                </td>
                <td><c:out value="${n.dept}" /></td>
                <td><fmt:formatDate value="${n.createdAt}" pattern="yyyy-MM-dd" /></td>
                <td><c:out value="${n.views}" /></td>
              </tr>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <tr>
              <td colspan="5">등록된 공지가 없습니다.</td>
            </tr>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>

    <!-- 페이징 -->
    <c:if test="${totalPages > 1}">
      <div class="pagination">
        <c:forEach var="p" begin="1" end="${totalPages}">
          <a href="${pageContext.request.contextPath}/notice/list?page=${p}&size=${size}&field=${param.field}&q=${fn:escapeXml(param.q)}"
             class="${p == page ? 'active' : ''}">${p}</a>
        </c:forEach>
      </div>
    </c:if>

    <div class="button-area">
      <a href="${pageContext.request.contextPath}/notice/write" class="write-button">글쓰기</a>
    </div>
  </div>
</body>
</html>
