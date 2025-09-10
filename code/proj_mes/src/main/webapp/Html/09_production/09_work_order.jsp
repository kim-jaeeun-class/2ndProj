<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>J2P4 :: 작업 지시서</title>
        <link rel="stylesheet" href="../asset/font.css">
        <link rel="stylesheet" href="../asset/09_common.css">
        <script src ="../asset/template_load.js" ></script>
    </head>
    <body page="wo">
        <header></header>
        <div class="gnb"></div>
        <div class="titleBox">
            <span>작업 지시서</span>
            <a href="../02_main/mainpage.html">
                <div class="toMainpage">
                    <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표"
                        style="transform: scaleX(-1);">
                    메인 화면으로
                </div>
            </a>            
        </div>
        <div class="wrap">
            <div class="wrap-select">
                <form class="date-filter" action="workorder">
                    <div class="select-con">
                        <div class="select-title">정렬 기준</div>
                        <select name="stat" class="select select-drop">
                            <option value="전달값1" selected="selected">전체</option>
                            <option value="전달값2">미진행</option>
                            <option value="전달값3">진행</option>
                            <option value="전달값4">완료</option>
                        </select>
                    </div>
                    <div class="filter-submit">
                        <button type="button" class="button">조회</button>
                    </div>
                </form>
            </div>
            <form class="wrap-table">
                <div class="table-view">
                    <table>
                        <thead>
                            <th></th>
                            <th>작업지시번호</th>
                            <th>일자</th>
                            <th>거래처명</th>
                            <th>담당자명</th>
                            <th>납기일자</th>
                            <th>품목명</th>
                            <th>지시수량</th>
                            <th>생산수량</th>
                            <th>진행상태</th>
                            <th>인쇄</th>
                        </thead>
                        <tbody>
                        	<!-- DTO에 있는 거 wo라고 이름 지어서 꺼내는 것!!! -->
                        	<!-- 근데 지금 client 테이블 조인을... 내일 얘기하자 -->
	                        <c:forEach var="wo" items="${list}">
	                            <tr>
	                            	<!-- 구분용으로 date, num 다 넣음 -->
	                                <td><input type="checkbox" value="${wo.woDate}-${wo.woNum}" name="chk"></td>
	                                <td>${wo.woNum}</td>
	                                <td>${wo.woDate}</td>
	                                <td>${wo.clientName}</td>
	                                <td>${wo.workerID}</td>
	                                <td>${wo.woDuedate}</td>
	                                <td>${wo.itemName}</td>
	                                <td>${wo.woPQ}</td>
	                                <td>${wo.woAQ}</td>
	                                <td>${wo.woPS}</td>
	                                <td><button type="button">인쇄</button></td>
	                            </tr>
	                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="wrap-tableBtn">
                    <input type="button" class="button delete-btn" value="삭제">
                    <input type="button" class="button open-btn" value="등록">
                </div>
            </form>
        </div>

        <!-- TODO : 사이드 패널 - 여기부터 jsp 변환 작업 진행해야 함 -->
        <div class="panel">
            <button class="close-btn">✕</button>
            <h2>작업 지시서 등록</h2>
            <form>
                <div class="form-group">
                    <label>지시일</label>
                    <input type="date" name="order-date">
                </div>
                <div class="form-group">
                    <label>작업지시NO.</label>
                    <input type="number" name="order-no" min="1" max="5" placeholder="작업지시 번호 입력">
                </div>
                <div class="form-group">
                    <label>담당자</label>
                    <input type="text" name = "person" placeholder="담당자명 입력">
                </div>
                <div class="form-group">
                    <label>납기일</label>
                    <input type="date" name="give-date">
                </div>
                <div class="form-group">
                    <label>첨부</label>
                    <input type = "file" name = "변수명">
                </div>
                <div class ="form-group">
                    <div class = "wrap-table panel-table">
                        <div class = "wrap-tableBtn">
                            <input type="button" class="button" value="품목 추가">
                            <input type="button" class="button delete" value="품목 삭제">                       
                        </div>
                        <div class = "panel-table-wrap">
                            <table>
                                <thead>
                                    <th></th>
                                    <th>순번</th>
                                    <th>품목코드</th>
                                    <th>품목명</th>
                                    <th>수량</th>
                                    <th>비고</th>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><input type="checkbox" value="전달값" name="변수명"></td>
                                        <td>1</td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                    </tr>
                                </tbody>
                            </table>  
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <button type="submit" class="button panel-save">저장</button>
                </div>
            </form>
        </div>
        <!-- 품목 추가 모달 -->
        <div class="modal-overlay">
            <div class="modal">
                <button class="modal-close">✕</button>
                <h2>품목 추가</h2>
                <form class="modal-form">
                    <div class ="form-group">
                        <div class = "wrap-table panel-table" style="height: 400px;">
                            <table>
                                <thead>
                                    <th></th>
                                    <th>품목코드</th>
                                    <th>품목명</th>

                                </thead>
                                <tbody>
                                    <tr>
                                        <td><input type="checkbox" value="전달값" name="변수명"></td>
                                        <td>#품목코드</td>
                                        <td>#품목명</td>
                                    </tr>
                                </tbody>
                            </table>                
                        </div>
                    </div>
                    <div class="form-group modal-search">
                        <input type="text">
                        <input type="button" value="검색">
                    </div>
                    <div class="wrap-tableBtn">
                        <input type="button" class="button reset" value="선택 해제">
                        <button type="button" class="button save">저장</button>
                    </div>
                </form>
            </div>
        </div>

        <script src="../asset/09_common.js"></script>
    </body>
</html>