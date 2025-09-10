<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>J2P4 :: 생산 계획</title>
        <link rel="stylesheet" href="../asset/font.css">
        <link rel="stylesheet" href="../asset/09_common.css">
        <script src="/proj_mes/Html/asset/template_load.js"></script>

    </head>
    <body>
        <!-- 헤더 -->
        <header></header>

        <!-- GNB -->
        <div class="gnb"></div>

        <!-- 타이틀 -->
        <div class="titleBox">
            <span>생산 계획</span>
            <a href="">
                <div class="toMainpage">
                    <img
                        src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png"
                        width="13"
                        alt="메인 화면으로 가는 화살표"
                        style="transform:scaleX(-1);"/>
                    메인 화면으로
                </div>
            </a>
        </div>
        <div class="wrap">
            <div class="wrap-select">
                <form class="date-filter">
                    <div class="select-con">
                        <div class="select-title">작업 지시</div>
                        <div class="filter-date select-drop">
                            <input type="date">
                            <div>~</div>
                            <input type="date">
                        </div>                        
                    </div>
                    <div class="select-con">
                        <div class="select-title">정렬 기준</div>
                        <select name="align" class="select select-drop">
                            <option value="전달값1" selected="selected">생산실적번호별</option>
                            <option value="전달값2">일자별</option>
                        </select>
                    </div>
                    <div class="filter-submit">
                        <button type="button" class="button">조회</button>
                    </div>
                </form>
            </div>
            <!-- 테이블 영역 -->
            <form class="wrap-table">
                <div class = "table-view">
                    <table>
                        <thead>
                            <th></th>
                            <th>순번</th>
                            <th>생산계획번호</th>
                            <th>제품 번호</th>
                            <th>계획 수량</th>
                            <th>진행 상태</th>
                            <th>계획 기간</th>
                            <th>진행률</th>
                            <th>작업 공정</th>
                            <th>달성률</th>
                            <th>불량률</th>
                            <th>비고</th>
                        </thead>
                        <tbody>
                            <tr class = "data">
                                <td><input type="checkbox" value="전달값" name="변수명"></td>
                                <td>1</td>
                                <td>#생산계획번호</td>
                                <td>#제품번호</td>
                                <td>#계획수량</td>
                                <td>#진행상태</td>
                                <td>#계획기간</td>
                                <td>#진행률</td>
                                <td>#작업공정</td>
                                <td>#달성률</td>
                                <td>#불량률</td>
                                <td>#비고</td>
                            </tr>
                        </tbody>
                    </table>                   
                </div>
                <div class="wrap-tableBtn">
                    <input type="button" class="button delete-btn" value="삭제">
                    <input type="button" class="button open-btn" value="등록">
                </div>
            </form>
        </div>

        <!-- 사이드 패널 -->
        <div class="panel">
            <button class="close-btn">✕</button>
            <h2>생산 계획 등록</h2>
            <form>
                <div class="form-group">
                    <label>생산계획번호</label>
                    <input type="text" name = "pro-plan-no">
                </div>
                <div class="form-group">
                    <label>제품 번호</label>
                    <input type="text" name = "item-no">
                </div>
                <div class="form-group">
                    <label>계획 수량</label>
                    <input type="number" name="plan-amount">
                </div>
                <div class="form-group">
                    <label>계획 기간</label>
                    <div class="label-child">시작일</div>
                    <input type="date" name="plan-start">
                    <div class="label-child">종료일</div>
                    <input type="date" name="plan-end">
                </div>
                <div class="form-group">
                    <label>작업 공정</label>
                    <select name="work-seq" size="1">
                        <option value = "전달값1" selected>공정 1</option>
                        <option value = "전달값2">공정 2</option>
                        <option value = "전달값3">공정 3</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>진행 상태</label>
                    <div class = "radio-block">
                        <label><input type = "radio" name = "pro-perf-stat" value="1" selected>대기</label>
                        <label><input type = "radio" name = "pro-perf-stat" value="2">진행</label>
                        <label><input type = "radio" name = "pro-perf-stat" value="3">홀드</label>
                        <label><input type = "radio" name = "pro-perf-stat" value="4">완료</label>               
                    </div>
                </div>
                <div class="form-group">
                    <label>입고 형태</label>
                    <div class = "radio-block">
                        <label><input type = "radio" name = "in-form" value="1">정상</label>
                        <label><input type = "radio" name = "in-form" value="2">재가공</label>                        
                    </div>
                </div>
                <div class="form-group">
                    <label>출고 형태</label>
                    <div class = "radio-block">
                        <label><input type = "radio" name = "out-form" value="1">양산</label>
                        <label><input type = "radio" name = "out-form" value="2">개발</label>
                        <label><input type = "radio" name = "out-form" value="3">평가</label>
                        <label><input type = "radio" name = "out-form" value="4">폐기</label>                        
                    </div>
                </div>                
                <div class="form-group">
                    <label>비고</label>
                    <input type="text" name = "bigo">
                </div>
                <!-- 생각해보니 진행/불량률을 input로 넣으면 안 된다... -->
                <div class="form-actions">
                    <input type = "button" class="button cancel reset" value="취소">
                    <button type="submit" class="button panel-save">저장</button>
                </div>
            </form>
        </div>
        <!-- 생산 계획 모달 -->
        <div class="modal-overlay">
            <div class="modal">
                <button class="modal-close">✕</button>
                <h2>생산 계획 상세</h2>
                <form class="wrap-table panel-table">
                    <table class = "modal-table">
                        <tr>
                            <td class="modal-table-title">생산계획번호</td>
                            <td class="modal-table-con">#생산계획번호</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">제품번호</td>
                            <td class="modal-table-con">#품번</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">입고 형태</td>
                            <td class="modal-table-con">#입고 형태</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">출고 형태</td>
                            <td class="modal-table-con">#출고 형태</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">계획 수량</td>
                            <td class="modal-table-con">#수량</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">진행 상태</td>
                            <td class="modal-table-con">#상태(대기/진행중/완료)</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">계획 기간</td>
                            <td class="modal-table-con">#기간</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">진행률</td>
                            <td class="modal-table-con">#수치(%)</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">작업 공정</td>
                            <td class="modal-table-con">#공정명</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">달성률</td>
                            <td class="modal-table-con">#수치(%)</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">불량률</td>
                            <td class="modal-table-con">#수치(%)</td>
                        </tr>
                        <tr>
                            <td class="modal-table-title">비고</td>
                            <td class="modal-table-con">#비고(공란 가능)</td>
                        </tr>
                    </table>
                    <div class="wrap-tableBtn">
                        <input type="button" class="button" value="수정">
                        <input type="button" class="button delete" value="삭제">
                    </div>
                </form>
            </div>
        </div>

        <script src="../asset/09_common.js"></script>
    </body>
</html>