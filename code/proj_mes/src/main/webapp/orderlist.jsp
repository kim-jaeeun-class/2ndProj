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
<link rel="stylesheet" href="./Html/asset/list.css">
</head>
<body>
<header>
	<div class="corp">
		<img src="https://i.postimg.cc/qMsq73hD/icon.png" width="40px" alt="회사 로고">
		<h3>J2P4</h3>
	</div>
	<div class="search">
		<img src="https://i.postimg.cc/9QcMwQym/magnifier-white.png" width="30px" alt="검색용 아이콘">
	</div>
	<div class="logout">
		로그아웃
	</div>
	<div class="myIcon">
		<a href="javascript:void(0);" id="myIconBtn">
			<img src="https://i.postimg.cc/zfVqTbvr/user.png" width="35px" alt="마이페이지 아이콘"
				style="background-color: #ffffff; border-radius: 100%;">
		</a>
		<ul class="subList">
			<li><a href="">하위메뉴1</a></li>
			<li><a href="">하위메뉴2</a></li>
		</ul>
	</div>
</header>

<div class="gnb">
	<ul class="mainList">
		<li>기준 관리
			<ul class="subList">
				<li><a href="">기준 목록</a></li>
			</ul>
		</li>
		<li>공정 관리
			<ul class="subList">
				<li><a href="">공정 목록</a></li>
				<li><a href="">공정 등록/수정/삭제</a></li>
			</ul>
		</li>
		<li>BOM 관리
			<ul class="subList">
				<li><a href="">BOM 목록</a></li>
				<li><a href="">BOM 등록/수정/삭제</a></li>
			</ul>
		</li>
		<li>발주 관리
			<ul class="subList">
				<li><a href="">발주 목록</a></li>
			</ul>
		</li>
		<li>재고 관리
			<ul class="subList">
				<li><a href="">재고 목록</a></li>
				<li><a href="">재고 삭제</a></li>
			</ul>
		</li>
		<li>생산 관리
			<ul class="subList">
				<li><a href="">생산 계획</a></li>
				<li><a href="">생산 실적 현황</a></li>
			</ul>
		</li>
		<li>품질 관리
			<ul class="subList">
				<li><a href="">LOT 추적</a></li>
				<li><a href="">공정별 현황</a></li>
				<li><a href="">품질 검사 입력</a></li>
			</ul>
		</li>
	</ul>
</div>

<div class="titleBox">
	<span>마이 페이지</span>
	<a href="">
		<div class="toMainpage">
			<img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13px" alt="메인 화면으로 가는 화살표"
				style="transform: scaleX(-1);">
			메인 화면으로
		</div>
	</a>
</div>

<div class="wrap">
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
    <div class="bottom">
        <button type="submit">삭제</button>
        <button type="submit">추가</button>
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
</div>

</body>
</html>