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
<title>발주목록</title>
<link rel="stylesheet" href="asset/list.css">
</head>
<body>
	<div class="top">
        <div class="date box">
            <div class="period"> 기간 </div>
            <div class="month"> 월별 </div>
        </div>
        <div>
            <input type="date">
            <span>~</span>
            <input type="date">
        </div>
        <div class="state box">
            <div> 진행상태 </div>
            <select>
                <option>전체</option>
                <option>임시저장</option>
                <option>승인</option>
                <option>승인 대기</option>
                <option>반려</option>
            </select>
        </div>
        <button type="submit"> 조회 </button>
    </div>
    <div class="center">
        <table border="1" class="table">
            <thead>
                <tr>
                    <th>
                        <input type="checkbox">
                    </th>
                    <th>NO</th>
                    <th>날짜</th>
                    <th>발주번호</th>
                    <th>부서</th>
                    <th>사원이름</th>
                    <th>총 수량</th>
                    <th>총 금액</th>
                    <th>진행상태</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <input type="checkbox">
                    </td>
                    <td>1</td>
                    <td>20250820</td>
                    <td>PSF156322254</td>
                    <td>생산2팀</td>
                    <td>홍길동</td>
                    <td>5</td>
                    <td>100,000</td>
                    <td>승인대기</td>
                    <td>
                        <button type="button">회수</button>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox">
                    </td>
                    <td>1</td>
                    <td>2025-08-20</td>
                    <td>PSF156322254</td>
                    <td>생산2팀</td>
                    <td>홍길동</td>
                    <td>5</td>
                    <td>100,000</td>
                    <td>승인대기</td>
                    <td>
                        <button type="button">회수</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="bottom">
        <button type="submit">삭제</button>
        <button type="submit">추가</button>
    </div>

</body>
</html>