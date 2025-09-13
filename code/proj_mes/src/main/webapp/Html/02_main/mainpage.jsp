<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>메인 페이지</title>

  <!-- Inter 폰트 & Tailwind -->
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>

  <!-- Chart.js -->
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

  <style>
    body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
    canvas { max-height: 200px; }
    .header-bg { background-color: #002a40; }
    .nav-bg { background-color: #003751; }

    /* 템플릿의 내비가 .mainList를 쓴다는 가정의 보강 스타일 (클래스명이 다르면 템플릿 쪽에 맞춰 수정) */
    .mainList, #noticeContent, #boardContent { cursor: pointer; }
    .mainList li:hover { background-color: #3b82f6; }
    #noticeContent p:hover, #boardContent p:hover { background-color: #3b82f6; color: #fff; }
    #noticeContent p:hover span, #boardContent p:hover span { color: #fff; }
  </style>
</head>
<body class="bg-gray-100 text-gray-800">

  <!-- 템플릿 슬롯 -->
  <div id="header-slot"></div>
  <div id="nav-slot"></div>

  <!-- 본문 -->
  <div class="p-4 sm:p-8 max-w-7xl mx-auto">
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 space-y-2 sm:space-y-0">
      <h2 class="text-2xl sm:text-3xl font-bold text-gray-900">메인 페이지</h2>
      <a href="<c:url value='/mainpage'/>" class="flex items-center text-blue-600 hover:text-blue-800">
<!--         <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" class="w-3 transform -scale-x-100 mr-1" alt="메인 화면으로 가는 화살표"> -->
<!--         <span>메인 화면으로</span> -->
      </a>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 왼쪽 패널 -->
      <div class="lg:col-span-2 space-y-6">
        <!-- 생산 현황 -->
        <div class="bg-white p-6 rounded-xl shadow-xl space-y-4">
          <h3 class="text-xl font-semibold mb-2">생산 현황</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="p-4 rounded-lg shadow-inner border-t-4 border-blue-600">
              <span class="text-sm text-gray-500">총 생산량 ( 단위 : 개 )</span>
              <strong class="block text-2xl font-bold text-blue-700">15,251</strong>
            </div>
            <div class="p-4 rounded-lg shadow-inner border-t-4 border-blue-600">
              <span class="text-sm text-gray-500">목표 달성률 ( % )</span>
              <strong class="block text-2xl font-bold text-blue-700">89.6%</strong>
            </div>
            <div class="p-4 rounded-lg shadow-inner border-t-4 border-blue-600">
              <span class="text-sm text-gray-500">누적 불량 수 ( 단위 : 개 )</span>
              <strong class="block text-2xl font-bold text-red-600">291</strong>
            </div>
          </div>
        </div>

        <!-- 불량 현황 및 작업 지시 -->
        <div class="bg-white p-6 rounded-xl shadow-xl space-y-4">
          <h3 class="text-xl font-semibold mb-2">불량 현황 및 작업 지시</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <h4 class="text-lg font-medium mb-2">불량 유형</h4>
              <canvas id="defectChart" class="w-full h-48"></canvas>
            </div>
            <div>
              <h4 class="text-lg font-medium mb-2">작업 지시 진행률</h4>
              <canvas id="workOrderChart" class="w-full h-48"></canvas>
            </div>
          </div>
        </div>
      </div>

      <!-- 오른쪽 패널 -->
      <div class="lg:col-span-1 space-y-6">
        <!-- 재고 현황 -->
        <div class="bg-white p-6 rounded-xl shadow-xl flex-1">
          <h3 class="text-xl font-semibold mb-2">재고 현황</h3>
          <canvas id="inventoryChart" class="w-full h-40"></canvas>
        </div>

        <!-- 공지/게시판 -->
        <div class="bg-white p-6 rounded-xl shadow-md flex-1">
          <div class="flex border-b border-gray-200">
            <button id="noticeTab" class="py-2 px-4 text-sm font-medium focus:outline-none border-b-2 border-transparent text-gray-500 hover:border-blue-600 hover:text-blue-600 transition-colors duration-200">
              공지 사항
            </button>
            <button id="boardTab" class="py-2 px-4 text-sm font-medium focus:outline-none border-b-2 border-transparent text-gray-500 hover:border-blue-600 hover:text-blue-600 transition-colors duration-200">
              게시판
            </button>
          </div>

          <div id="noticeContent" class="tab-content space-y-2 mt-4">
            <p class="py-2 px-1 rounded-md"><span class="text-gray-500">[안내]</span> <span class="text-gray-800">2025년 하반기 생산 목표 공지</span></p>
            <p class="py-2 px-1 rounded-md"><span class="text-gray-500">[긴급]</span> <span class="text-gray-800">라인2 설비 점검 일정 변경</span></p>
            <p class="py-2 px-1 rounded-md"><span class="text-gray-500">[공유]</span> <span class="text-gray-800">안전 수칙 교육 일정 안내</span></p>
          </div>

          <div id="boardContent" class="tab-content hidden space-y-2 mt-4">
            <p class="py-2 px-1 rounded-md"><span class="text-gray-500">[공지]</span> <span class="text-gray-800">2025년 하반기 생산 목표 공지</span></p>
            <p class="py-2 px-1 rounded-md"><span class="text-gray-500">[공지]</span> <span class="text-gray-800">라인2 설비 점검 일정 변경</span></p>
            <p class="py-2 px-1 rounded-md"><span class="text-gray-500">[질문]</span> <span class="text-gray-800">MES 접속이 안되는데 어떻게...</span></p>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 템플릿 로더 설정 + 스크립트 -->
  <script>
    window.__TPL_CONFIG = {
      root: '<c:url value="/"/>',
      templateUrl: '<c:url value="/Html/00_template/template.html"/>'
    };
  </script>
  <script src="<c:url value='/Html/asset/template_load.js'/>" defer></script>

  <!-- 페이지 스크립트: 탭 + 차트 (헤더/내비는 템플릿 로더가 처리) -->
  <script>
    document.addEventListener('DOMContentLoaded', function () {
      /* 탭 전환 */
      const noticeTab = document.getElementById('noticeTab');
      const boardTab = document.getElementById('boardTab');
      const noticeContent = document.getElementById('noticeContent');
      const boardContent = document.getElementById('boardContent');

      function activateTab(tab, content) {
        noticeTab.classList.remove('border-blue-600', 'text-blue-600');
        boardTab.classList.remove('border-blue-600', 'text-blue-600');
        noticeContent.classList.add('hidden');
        boardContent.classList.add('hidden');
        tab.classList.add('border-blue-600', 'text-blue-600');
        content.classList.remove('hidden');
      }
      activateTab(noticeTab, noticeContent);
      noticeTab.addEventListener('click', () => activateTab(noticeTab, noticeContent));
      boardTab.addEventListener('click', () => activateTab(boardTab, boardContent));

      /* Chart.js */
      // 재고 현황
      const invCtx = document.getElementById('inventoryChart').getContext('2d');
      new Chart(invCtx, {
        type: 'pie',
        data: {
          labels: ['원자재', '상품', '불량'],
          datasets: [{ data: [350, 450, 200], backgroundColor: ['#3b82f6', '#22c55e', '#ef4444'], hoverOffset: 4 }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: { position: 'bottom', labels: { boxWidth: 20, padding: 20, font: { size: 12 } } },
            tooltip: { callbacks: { label: (t) => t.label + ': ' + t.raw + '개' } }
          }
        }
      });

      // 불량 유형
      const defCtx = document.getElementById('defectChart').getContext('2d');
      new Chart(defCtx, {
        type: 'doughnut',
        data: {
          labels: ['기스', '파손', '오염'],
          datasets: [{ data: [120, 80, 91], backgroundColor: ['#f97316', '#dc2626', '#2563eb'], hoverOffset: 4 }]
        },
        options: {
          responsive: true,
          plugins: { legend: { position: 'bottom', labels: { boxWidth: 20, padding: 20, font: { size: 12 } } } }
        }
      });

      // 작업 지시 진행률
      const woCtx = document.getElementById('workOrderChart').getContext('2d');
      new Chart(woCtx, {
        type: 'bar',
        data: {
          labels: ['진행 중', '대기', '완료'],
          datasets: [{ label: '작업 지시 수', data: [75, 10, 150], backgroundColor: ['#f59e0b', '#6b7280', '#10b981'] }]
        },
        options: { responsive: true, scales: { y: { beginAtZero: true } }, plugins: { legend: { display: false } } }
      });
    });
  </script>
</body>
</html>
