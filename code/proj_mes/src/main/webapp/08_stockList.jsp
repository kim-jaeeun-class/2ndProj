<!-- stock_list.jsp -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>재고 목록</title>

  <link rel="stylesheet" href="<c:url value='/Html/asset/08_stock_list.js' />">
  <link rel="stylesheet" href="<c:url value='/Html/asset/list.css' />">
</head>
<body>
<header></header>
<div class="gnb"></div>
<div class="titleBox">
    <span>재고 목록</span>
    <a href="">
        <div class="toMainpage">
            <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표" style="transform: scaleX(-1);">
            메인 화면으로
        </div>
    </a>
</div>
<div class="wrap">
    <div class="main">
        <!-- 조회 필터: 필요 시 컨트롤러로 GET 요청을 보낼 수 있도록 form만 추가 (CSS/JS 영향 없음) -->
        <form method="get" action="${pageContext.request.contextPath}/Stock">
            <div class="box filter">
                <div class="box">
                    <div> 대분류 </div>
                    <select class="big" name="big">
                        <option ${param.big==null?'selected':''}>전체</option>
                        <option ${param.big=='유리'?'selected':''}>유리</option>
                        <option ${param.big=='화학 강화제'?'selected':''}>화학 강화제</option>
                        <option ${param.big=='코팅'?'selected':''}>코팅</option>
                        <option ${param.big=='완제품'?'selected':''}>완제품</option>
                        <option ${param.big=='반제품'?'selected':''}>반제품</option>
                        <option ${param.big=='소모품'?'selected':''}>소모품</option>
                    </select>
                </div>
                <div class="box">
                    <div> 중분류 </div>
                    <select class="middle" name="middle">
                        <option ${param.middle==null?'selected':''}>전체</option>
                        <option ${param.middle=='LCD'?'selected':''}>LCD</option>
                        <option ${param.middle=='OLED'?'selected':''}>OLED</option>
                        <option ${param.middle=='AMOLED'?'selected':''}>AMOLED</option>
                        <option ${param.middle=='질산칼륨'?'selected':''}>질산칼륨</option>
                        <option ${param.middle=='질산나트륨'?'selected':''}>질산나트륨</option>
                        <option ${param.middle=='불소중합체'?'selected':''}>불소중합체</option>
                        <option ${param.middle=='산화티타늄'?'selected':''}>산화티타늄</option>
                        <option ${param.middle=='산화지르코늄'?'selected':''}>산화지르코늄</option>
                        <option ${param.middle=='핸드폰'?'selected':''}>핸드폰</option>
                        <option ${param.middle=='닌텐도'?'selected':''}>닌텐도</option>
                        <option ${param.middle=='네비게이션'?'selected':''}>네비게이션</option>
                        <option ${param.middle=='스티로폼'?'selected':''}>스티로폼</option>
                        <option ${param.middle=='비닐'?'selected':''}>비닐</option>
                    </select>
                </div>
                <div class="box">
                    <div> 소분류 </div>
                    <select class="small" name="small">
                        <option ${param.small=='갤럭시S23'?'selected':''}>갤럭시S23</option>
                        <option ${param.small=='아이폰17'?'selected':''}>아이폰17</option>
                        <option ${param.small=='닌텐도3D'?'selected':''}>닌텐도3D</option>
                        <option ${param.small=='닌텐도Lite'?'selected':''}>닌텐도Lite</option>
                        <option ${param.small=='5인치'?'selected':''}>5인치</option>
                        <option ${param.small=='7인치'?'selected':''}>7인치</option>
                        <option ${param.small=='S'?'selected':''}>S</option>
                        <option ${param.small=='M'?'selected':''}>M</option>
                        <option ${param.small=='L'?'selected':''}>L</option>
                        <option ${param.small=='10M'?'selected':''}>10M</option>
                        <option ${param.small=='30M'?'selected':''}>30M</option>
                    </select>
                </div>

            </div>
            <div class="box date_filter">
                <div class="date box">
                    <div class="period"> 기간 </div>
                    <input type="date" id="start_date" name="start_date" value="${param.start_date}">
                    <span>~</span>
                    <input type="date" id="end_date" name="end_date" value="${param.end_date}">
                </div>
                <div>
                    <button type="submit"> 조회 </button>
                </div>
            </div>
        </form>
    </div>

    <div class="table-wrap" tabindex="0">
        <table class="tables">
            <thead>
            <tr>
                <th class="row_1">NO</th>
                <th class="row_2">등록날짜</th>
                <th class="row_6">LOT번호</th>
                <th class="row_3">품목코드</th>
                <th class="row_4">품목명/모델명</th>
                <th class="row_5">위치</th>
                <th class="row_7">구분</th>
                <th class="row_8">수량</th>
                <th class="row_9">단가</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="s" items="${stocks}" varStatus="st">
                <tr>
                    <td>${st.index + 1}</td>
                    <td>
    <c:remove var="fmtErr" />
    <c:catch var="fmtErr">
        <fmt:formatDate value="${s.regDate}" pattern="yyyyMMdd"/>
    </c:catch>
    <c:if test="${not empty fmtErr}"><c:out value="${s.regDate}"/></c:if>
</td>
                    <td>${s.lotNo}</td>
                    <td>${s.itemCode}</td>
                    <td>${s.itemName}</td>
                    <td>${s.location}</td>
                    <td>${s.type}</td>
                    <td><fmt:formatNumber value="${s.qty}" groupingUsed="true"/></td>
                    <td><fmt:formatNumber value="${s.unitPrice}" groupingUsed="true"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty stocks}">
                <tr>
                    <td colspan="9">데이터가 없습니다.</td>
                </tr>
            </c:if>
            </tbody>
            <tfoot>
            <tr>
                <td colspan="7">Total</td>
                <td><fmt:formatNumber value="${totalQty}" groupingUsed="true"/></td>
                <td><fmt:formatNumber value="${totalAmount}" groupingUsed="true"/></td>
            </tr>
            </tfoot>
        </table>
    </div>
    <div class="action">
        <a href="./08_stockRegistration.jsp"><button type="button">추가</button></a>
    </div>
</div>
</body>
</html>



