<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>공지 사항 - 게시글 보기</title>
  <style>
    :root { --border:#9aa0a6; --text:#222; --muted:#666; --button:#333; --field-border:#a4a5a7; }
    body { font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Noto Sans KR","Apple SD Gothic Neo","Malgun Gothic","Helvetica Neue",Arial,sans-serif; background:#f9f9f9; margin:0; }
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
    .container{max-width:800px;margin:20px auto;background:#fff;padding:20px;border:1px solid #ccc;}
    .post-info{border:1px solid #ccc;padding:15px;margin-bottom:10px;background:#f7f7f7;}
    .post-title{font-size:18px;font-weight:bold;margin-bottom:8px;}
    .post-meta{font-size:13px;color:#555;}
    .post-content{border:1px solid #ccc;padding:20px;background:#f2f2f2;min-height:300px;margin-bottom:30px;}
    .button-group{display:flex;justify-content:space-between;align-items:center;gap:10px;}
    .btn{padding:8px 16px;background:#333;color:#fff;border:none;border-radius:4px;cursor:pointer;font-size:14px;}
    .btn:hover{background:#555;}
    .nav-buttons{display:flex;gap:10px;}
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
    <!-- 상단 정보 -->
    <section class="post-info">
      <div class="post-title">
        <strong><c:out value="${notice.type}" /></strong>
        <c:out value="${notice.title}" />
      </div>
      <div class="post-meta">
        작성자: <c:out value="${notice.author}" /> |
        작성 시간: <fmt:formatDate value="${notice.createdAt}" pattern="yyyy-MM-dd HH:mm" />
      </div>
    </section>

    <!-- 본문 -->
    <section id="postContent" class="post-content">
      <!-- content는 HTML 포함 가능 -->
      <c:out value="${notice.content}" escapeXml="false" />
    </section>

    <!-- 하단 버튼/이동 -->
    <div class="button-group">
      <div class="nav-buttons">
        <c:if test="${not empty prevNotice}">
          <a class="btn" href="${pageContext.request.contextPath}/notice/view?id=${prevNotice.id}">이전글</a>
        </c:if>
        <c:if test="${not empty nextNotice}">
          <a class="btn" href="${pageContext.request.contextPath}/notice/view?id=${nextNotice.id}">다음글</a>
        </c:if>
      </div>
      <div class="nav-buttons">
        <a class="btn" href="${pageContext.request.contextPath}/notice/list">목록</a>
        <a class="btn" href="${pageContext.request.contextPath}/notice/write">글쓰기</a>
        <c:if test="${canEdit}">
          <a class="btn" href="${pageContext.request.contextPath}/notice/edit?id=${notice.id}">수정</a>
          <form style="display:inline" method="post" action="${pageContext.request.contextPath}/notice/delete">
            <input type="hidden" name="id" value="${notice.id}"/>
            <button class="btn" type="submit" onclick="return confirm('삭제하시겠습니까?');">삭제</button>
          </form>
        </c:if>
      </div>
    </div>
  </div>
</body>
</html>
