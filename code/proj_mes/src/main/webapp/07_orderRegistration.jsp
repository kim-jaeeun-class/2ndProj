<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="./Html/asset/registration.css">
<script src ="./Html/asset/template_load.js"></script>
<script src="./Html/asset/07_order_resistration.js"></script>
</head>
<body>
	<header></header>
    <div class="gnb"></div>
    <div class="titleBox">
		<span>발주등록</span>
		<a href="">
			<div class="toMainpage">
				<img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표"
					style="transform: scaleX(-1);">
				메인 화면으로
			</div>
		</a>
	</div>
    <div class="wrap">
        <div class="main">
            <div class="box header_row">
                <div>발주 기본정보</div>
                <div class="state box hide">
                    <span>진행상태</span>
                    <div>승인대기</div>
                </div>
                <input type="hidden" id="order_state" value="0">
                <div class="action">
                    <button id="save" type="button">임시저장</button>
                    <button id="approve" type="submit">승인 요청</button>
                    <button id="recall" type="button">회수</button>
                </div>
            </div>
            <div class="box">
                <div class="item">
                    <div>발주 번호</div>
                    <input id="order_num" class="main_input" type="text">
                </div>
                <div class="item">
                    <div>발주 날짜</div>
                    <input class="main_input" type="date">
                </div>
                <div class="item">
                    <div>부서</div>
                    <input id="dept_input" class="main_input" type="text" placeholder="부서를 선택하세요">
                </div>
                <div class="item">
                    <div>담당자</div>
                    <input id="name_input" class="main_input" type="text" readonly>
                </div>
            </div>
            <div class="box">
                <div class="item">
                    <div>거래처</div>
                    <input id="client_input" class="main_input" type="text" placeholder="거래처를 선택하세요">
                </div>
                <div class="item">
                    <div>거래처 사업자번호</div>
                    <input id="business_input" class="main_input" type="text" readonly>
                </div>
                <div class="item">
                    <div>거래처 연락번호</div>
                    <input id="number_input" class="main_input" type="text" readonly>
                </div>
                <div class="item">
                    <div>납기일</div>
                    <input class="main_input" type="date">
                </div>
            </div>
            <div class="note box">
                <div class="item">
                    <div>비고</div>
                    <input class="note_input" type="text">
                </div>
            </div>
        </div>
        <div class="list">
            <div class="box header_list">
                <div>발주 상세(품목)</div>
                <div class="list_action">
                    <button type="button">삭제</button>
                    <button type="submit" class="item_add">추가</button>
                </div>
            </div>
            <div class="table_wrap_detail" tabindex="0">
                <table class="tables">
                    <thead>
                        <tr>
                            <th>
                                <input type="checkbox" id="checkAll">
                            </th>
                            <th>NO</th>
                            <th>품목</th>
                            <th>단가</th>
                            <th>수량</th>
                            <th>합계</th>
                        </tr>
                    </thead>
                    <tbody class="order_list">
                    
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="4">Total</td>
                            <td></td>
                            <td></td>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </div>
        <div class="bottom">
            <a href="/07_order_list.html"><button type="button">목록</button></a>
        </div>
    </div>
    <div id="dept_modal" class="modal hide">
        <div class="search">
            <input type="text" class="m_search" placeholder="검색내용을 입력하세요">
            <button class="m_button" type="button">검색</button>
        </div>
        <div class="table_box table_wrap" tabindex="0">
            <table class="tables">
                <thead>
                    <tr>
                        <th></th>
                        <th>부서번호</th>
                        <th>부서명</th>
                        <th>사원번호</th>
                        <th>이름</th>
                    </tr>
                </thead>
                <tbody class="list">
                    <tr>
                        <td>
                            <input type="radio" name="dept">
                        </td>
                        <td>DFF5555</td>
                        <td>생산5팀</td>
                        <td>FF4561</td>
                        <td>나사원</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="dept">
                        </td>
                        <td>DFF5555</td>
                        <td>생산5팀</td>
                        <td>FF4561</td>
                        <td>나사원</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="dept">
                        </td>
                        <td>DFF5555</td>
                        <td>생산5팀</td>
                        <td>FF4561</td>
                        <td>나사원</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="dept">
                        </td>
                        <td>DFF5555</td>
                        <td>생산5팀</td>
                        <td>FF4561</td>
                        <td>나사원</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="dept">
                        </td>
                        <td>DFF5555</td>
                        <td>생산5팀</td>
                        <td>FF4561</td>
                        <td>나사원</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="dept">
                        </td>
                        <td>DFF5665</td>
                        <td>생산2팀</td>
                        <td>FF4666</td>
                        <td>나대리</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="dept">
                        </td>
                        <td>DFF5665</td>
                        <td>생산2팀</td>
                        <td>FF4666</td>
                        <td>나대리</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="dept">
                        </td>
                        <td>DFF5665</td>
                        <td>생산2팀</td>
                        <td>FF4666</td>
                        <td>나대리</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="dept">
                        </td>
                        <td>DFF5665</td>
                        <td>생산2팀</td>
                        <td>FF4666</td>
                        <td>나대리</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="bottom">
            <button class="cancel m_button" type="button">취소</button>
            <button class="confirm m_button" type="button">확인</button>
        </div>
    </div>
    <div id="client_modal" class="modal hide">
        <div class="search">
            <input type="text" class="m_search" placeholder="검색내용을 입력하세요">
            <button class="m_button" type="button">검색</button>
        </div>
        <div class="table_wrap" tabindex="0">
            <table class="tables">
                <thead>
                    <tr>
                        <th class="row_1"></th>
                        <th class="row_2">거래처코드</th>
                        <th class="row_3">거래처명</th>
                        <th class="row_4">사업자번호</th>
                        <th class="row_5">연락처</th>
                        <th class="row_6">담당자</th>
                    </tr>
                </thead>
                <tbody class="list">
                    <tr>
                        <td>
                            <input type="radio" name="client">
                        </td>
                        <td>SDF4435</td>
                        <td>휴먼유리</td>
                        <td>12-345-67890</td>
                        <td>010-1234-5678</td>
                        <td>나사원</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="client">
                        </td>
                        <td>SDF4435</td>
                        <td>휴먼유리</td>
                        <td>12-345-67890</td>
                        <td>010-1234-5678</td>
                        <td>나사원</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="client">
                        </td>
                        <td>SDF4435</td>
                        <td>휴먼유리</td>
                        <td>12-345-67890</td>
                        <td>010-1234-5678</td>
                        <td>나사원</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="client">
                        </td>
                        <td>SDF4435</td>
                        <td>휴먼유리</td>
                        <td>12-345-67890</td>
                        <td>010-1234-5678</td>
                        <td>나사원</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="client">
                        </td>
                        <td>SDF4435</td>
                        <td>휴먼유리</td>
                        <td>12-345-67890</td>
                        <td>010-1234-5678</td>
                        <td>나대리</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="client">
                        </td>
                        <td>SDF4435</td>
                        <td>휴먼유리</td>
                        <td>12-345-67890</td>
                        <td>010-1234-5678</td>
                        <td>나대리</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="client">
                        </td>
                        <td>SDF4435</td>
                        <td>휴먼유리</td>
                        <td>12-345-67890</td>
                        <td>010-1234-5678</td>
                        <td>나대리</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="client">
                        </td>
                        <td>SDF4435</td>
                        <td>휴먼유리</td>
                        <td>12-345-67890</td>
                        <td>010-1234-5678</td>
                        <td>나대리</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="bottom">
            <button class="cancel m_button" type="button">취소</button>
            <button class="confirm m_button" type="button">확인</button>
        </div>
    </div>
    <div id="item_modal" class="modal hide">
        <div class="search">
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
                    <tr>
                        <td>
                            <input type="radio" name="item">
                        </td>
                        <td>SDF4435</td>
                        <td>휴먼유리</td>
                        <td>원재료</td>
                        <td>50,000</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="item">
                        </td>
                        <td>SDF4435</td>
                        <td>교육유리</td>
                        <td>원재료</td>
                        <td>50,000</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="item">
                        </td>
                        <td>SDF4435</td>
                        <td>아이폰17</td>
                        <td>완제품</td>
                        <td>1,000,000</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="item">
                        </td>
                        <td>SDF4435</td>
                        <td>아이폰17</td>
                        <td>완제품</td>
                        <td>1,000,000</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="item">
                        </td>
                        <td>SDF4435</td>
                        <td>아이폰17</td>
                        <td>완제품</td>
                        <td>1,000,000</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="item">
                        </td>
                        <td>SDF4435</td>
                        <td>아이폰17</td>
                        <td>완제품</td>
                        <td>1,000,000</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="item">
                        </td>
                        <td>SDF4435</td>
                        <td>아이폰17</td>
                        <td>완제품</td>
                        <td>1,000,000</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="item">
                        </td>
                        <td>SDF4435</td>
                        <td>아이폰17</td>
                        <td>완제품</td>
                        <td>1,000,000</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="radio" name="item">
                        </td>
                        <td>SDF4435</td>
                        <td>아이폰17</td>
                        <td>완제품</td>
                        <td>1,000,000</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="bottom_input">
            <span>수량 : </span>
            <input type="text" class="m_quantity" placeholder="수량을 입력하세요">
        </div>
        <div class="bottom">
            <button class="cancel m_button" type="button">취소</button>
            <button class="confirm m_button" type="button">추가</button>
        </div>
    </div>
</body>
</html>