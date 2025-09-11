<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>공지 사항 등록</title>
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
    .editor-toolbar{margin-bottom:10px;border:1px solid #ccc;background:#e9e9e9;padding:8px 10px;display:flex;gap:10px;flex-wrap:wrap;}
    .editor-toolbar button{background:none;border:none;cursor:pointer;font-size:14px;padding:5px 10px;border-radius:4px;}
    .post-content{border:1px solid #ccc;background:#f2f2f2;min-height:300px;margin-bottom:20px;padding:10px;font-size:15px;resize:vertical;width:100%;}
    .file-upload{margin-bottom:20px;}
    .file-upload label{display:block;margin-bottom:6px;font-size:14px;font-weight:bold;}
    .file-upload input[type=file]{width:100%;}
    .button-group{display:flex;justify-content:flex-end;gap:10px;}
    .btn{padding:8px 16px;background:#333;color:#fff;border:none;border-radius:4px;cursor:pointer;font-size:14px;}
    .btn:hover{background:#555;}
  </style>
</head>
<body>
  <%@ include file="header.jspf" %>
	버튼 링크들을 달아 놓을것
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
    <form method="post" action="${pageContext.request.contextPath}/notice/register" enctype="multipart/form-data">
      <div class="post-info">
        <div class="post-title">공지 등록</div>
        <div class="post-meta">작성자: <c:out value="${sessionScope.userName}" /> </div>
      </div>

      <div style="display:flex; gap:10px; margin-bottom:10px;">
        <select name="type" required>
          <option value="[안내]">[안내]</option>
          <option value="[공유]">[공유]</option>
          <option value="[긴급]">[긴급]</option>
          <option value="[경영]">[경영]</option>
          <option value="[기술]">[기술]</option>
        </select>
        <input type="text" name="title" placeholder="제목" style="flex:1;padding:8px;border:1px solid #ccc;border-radius:4px;" required>
        
        담당부서는 select
        <select name = "dept">
        	<option value = "">
        </select>
        
        
        
        <input type="text" name="dept" placeholder="담당부서" style="width:200px;padding:8px;border:1px solid #ccc;border-radius:4px;" required>
      </div>

      <textarea class="post-content" name="content" placeholder="본문을 입력하세요..." required></textarea>

      <div class="file-upload">
        <label for="file">첨부파일</label>
        <input type="file" id="file" name="attachments" multiple>
      </div>

      <div class="button-group">
        <button class="btn" type="submit">등록</button>
        <a class="btn" href="${pageContext.request.contextPath}/notice/list">취소</a>
      </div>
    </form>
  </div>
</body>
</html>
