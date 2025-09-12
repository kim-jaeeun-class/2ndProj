<!-- ===================================================================== -->
<!-- stock_registration.jsp -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>재고 등록</title>
    <!-- 디자인/CSS/JS 경로와 파일명은 원본과 동일하게 유지 -->

<script src="<c:url value='/Html/asset/08_stock_registration.js' />"></script>
<link rel="stylesheet" href="<c:url value='/Html/asset/registration.css' />">
</head>
<body>
<header></header>
<div class="gnb"></div>
<div class="titleBox">
    <span>재고 등록</span>
    <a href="">
        <div class="toMainpage">
            <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표" style="transform: scaleX(-1);">
            메인 화면으로
        </div>
    </a>
</div>
<div class="wrap">
    <div class="main">
        <!-- 필요 시 서버 제출용 form (JS와 충돌하지 않도록 버튼 타입/클래스는 그대로 유지) -->
        <form id="regForm" method="post" action="${pageContext.request.contextPath}/08_stockList.jsp">
            <div class="action approve">
                <button type="button" class="stock_approve">승인 요청</button>
                <button type="button" class="submit_btn">등록</button>
            </div>
            <div class="box">
                <div class="item">
                    <div>등록날짜</div>
                    <input class="main_input" type="date" name="regDate" value="${stock.regDate}">
                </div>
                <div class="item">
                    <div>LOT번호</div>
                    <input class="main_input" type="text" name="lotNo" value="${stock.lotNo}">
                </div>
                <div class="item">
                    <div>위치</div>
                    <input class="main_input" type="text" name="location" value="${stock.location}">
                </div>
            </div>
            <div class="box">
                <div class="item">
                    <div>품목코드</div>
                    <input class="main_input item_input" type="text" name="itemCode" placeholder="품목을 선택하세요" value="${stock.itemCode}">
                </div>
                <div class="item">
                    <div>품목명/모델명</div>
                    <input class="main_input itemName_input" type="text" name="itemName" value="${stock.itemName}" readonly>
                </div>

            </div>
            <div class="box">
                <div class="item">
                    <div>수량</div>
                    <input class="main_input qty_input" type="text" name="qty" value="${stock.qty}">
                </div>
                <div class="item">
                    <div>단가</div>
                    <input class="main_input unitPrice_input" type="text" name="unitPrice" value="${stock.unitPrice}" readonly>
                </div>
                <div class="item">
                    <div>합계</div>
                    <input class="main_input total_input" type="text" name="totalAmount" value="${stock.totalAmount}" readonly>
                </div>
            </div>
            <div class="note box">
                <div class="item">
                    <div>비고</div>
                    <input class="note_input" type="text" name="note" value="${stock.note}">
                </div>
            </div>
        </form>
    </div>

<!--     <div class="list hide"> -->
<!--         <div class="header_list">재고 이동현황</div> -->
<!--         <div class="table_wrap_detail" tabindex="0"> -->
<!--             <table class="tables"> -->
<!--                 <thead> -->
<!--                 <tr> -->
<!--                     <th>LOT번호</th> -->
<!--                     <th>이전 위치</th> -->
<!--                     <th>현 위치</th> -->
<!--                     <th>이동사유</th> -->
<!--                 </tr> -->
<!--                 </thead> -->
<!--                 <tbody> -->
<%--                 <c:forEach var="m" items="${moveHistory}"> --%>
<!--                     <tr> -->
<%--                         <td>${m.lotNo}</td> --%>
<%--                         <td>${m.prevLocation}</td> --%>
<%--                         <td>${m.currLocation}</td> --%>
<%--                         <td>${m.reason}</td> --%>
<!--                     </tr> -->
<%--                 </c:forEach> --%>
<%--                 <c:if test="${empty moveHistory}"> --%>
<!--                     <tr> -->
<!--                         <td colspan="4">이동 이력이 없습니다.</td> -->
<!--                     </tr> -->
<%--                 </c:if> --%>
<!--                 </tbody> -->
<!--             </table> -->
<!--         </div> -->
<!--     </div> -->

    <div class="bottom">
        <a href="./08_stockList.jsp"><button type="button">목록</button></a>
    </div>

    <!-- 품목 선택 모달 -->
    <div id="item_modal" class="modal hide">
        <div class="modal_search">
            <input type="text" class="m_search" placeholder="검색내용을 입력하세요">
            <button class="m_button" type="button">검색</button>
        </div>
        <div class="table_wrap" tabindex="0">
            <table class="tables">
                <thead>
                <tr>
                    <th></th>
                    <th>품목코드</th>
                    <th>품목명</th>
                    <th>유형</th>
                    <th>단가</th>
                </tr>
                </thead>
                <tbody class="list">
                <c:forEach var="it" items="${items}">
                    <tr>
                        <td><input type="radio" name="item" value="${it.itemCode}"
                                   data-name="${it.itemName}" data-type="${it.type}"
                                   data-unit-price="${it.unitPrice}"></td>
                        <td>${it.itemCode}</td>
                        <td>${it.itemName}</td>
                        <td>${it.type}</td>
                        <td><fmt:formatNumber value="${it.unitPrice}" groupingUsed="true"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty items}">
                    <tr>
                        <td colspan="5">표시할 품목이 없습니다.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
        <div class="bottom">
            <button class="cancel m_button" type="button">취소</button>
            <button class="confirm m_button" type="button">추가</button>
        </div>
    </div>
</div>

<script>
// 기존 JS와의 호환을 위해 폼 제출이 필요할 때만 JS에서 submit 호출하도록 보조 핸들러(선택)
(function(){
  var btn = document.querySelector('.submit_btn');
  if(btn){
    btn.addEventListener('click', function(){
      var form = document.getElementById('regForm');
      if(form){ form.submit(); }
    });
  }
})();
</script>
</body>
</html>

<!-- 컨트롤러에서 기대하는 모델 속성 예시 (설명용 주석) -------------------------
list 페이지: request.setAttribute("stocks", List<StockDto>);
              request.setAttribute("totalQty", Long);
              request.setAttribute("totalAmount", Long);
registration 페이지: request.setAttribute("stock", StockDto or Map);
                    request.setAttribute("items", List<ItemDto>);
                    request.setAttribute("moveHistory", List<MoveDto>);
---------------------------------------------------------------------------- -->