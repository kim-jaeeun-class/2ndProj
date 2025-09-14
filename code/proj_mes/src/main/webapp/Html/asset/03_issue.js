  // ----- 공통: 모달 열고 닫기 (같은 함수명 유지) -----
  function popOpen() {
    $('.modal_wrap').show();
    $('.modal_bg').show();
  }
  function popClose() {
    $('.modal_wrap').hide();
    $('.modal_bg').hide();
  }

  // ----- 페이지 로직(간단 샘플) -----
  (function () {
    // 전체선택
    $('#check-all').on('change', function () {
      const checked = $(this).is(':checked');
      $('.row-check').prop('checked', checked);
      toggleBulkButtons();
    });

    // 개별선택
    $(document).on('change', '.row-check', function () {
      const all = $('.row-check').length;
      const checked = $('.row-check:checked').length;
      $('#check-all').prop('checked', all === checked);
      toggleBulkButtons();
    });

    // 선택 버튼 활성/비활성
    function toggleBulkButtons() {
      const has = $('.row-check:checked').length > 0;
      $('#btn-bulk-approve').prop('disabled', !has);
      $('#btn-bulk-delete').prop('disabled', !has);
    }

    // 삭제(개별)
    let pendingDeleteRow = null;
    $(document).on('click', '.btn-delete', function () {
      pendingDeleteRow = $(this).closest('tr');
      $('#modal-target').text($(this).data('name') || '선택한');
      popOpen();
    });

    // 삭제(일괄)
    $('#btn-bulk-delete').on('click', function () {
      $('#modal-target').text($('.row-check:checked').length + '개');
      pendingDeleteRow = 'bulk';
      popOpen();
    });

    // 모달 예
    $('#modal-yes').on('click', function () {
      if (pendingDeleteRow === 'bulk') {
        $('.row-check:checked').closest('tr').remove();
      } else if (pendingDeleteRow) {
        pendingDeleteRow.remove();
      }
      pendingDeleteRow = null;
      popClose();
      pushAudit('DELETE', '항목 삭제');
      toggleBulkButtons();
    });

    // 승인(개별)
    $(document).on('click', '.btn-approve', function () {
      const $row = $(this).closest('tr');
      $row.find('.status-link').text('resolved');
      pushAudit('RESOLVE', '완료 처리');
    });

    // 승인(일괄)
    $('#btn-bulk-approve').on('click', function () {
      $('.row-check:checked').each(function () {
        $(this).closest('tr').find('.status-link').text('resolved');
      });
      pushAudit('RESOLVE', '선택 항목 완료');
    });

    // 문제추가(샘플)
    $('#btn-add').on('click', function () {
      $('#issue-tbody').prepend(`
        <tr>
          <td><input type="checkbox" class="row-check" /></td>
          <td>신규 이슈</td>
          <td>세부내역을 입력하세요</td>
          <td><a href="#" class="status-link" data-status="open">open</a></td>
          <td>관리자</td>
          <td>${new Date().toISOString().slice(0,10).replaceAll('-','.')}</td>
          <td>
            <button class="btn btn-approve" type="button">승인</button>
            <button class="btn btn-delete" type="button" data-name="신규 이슈">삭제</button>
          </td>
        </tr>
      `);
      pushAudit('CREATE', '이슈 추가');
    });

    // 페이징(가운데, 활성 토글만)
    $('.page-num').on('click', function () {
      $('.page-num').removeClass('is-active');
      $(this).addClass('is-active');
      // 실제 데이터 페이징은 서버/스크립트에 맞춰 구현하세요.
    });

    // 상태 링크 클릭 (예: 상태 변경 메뉴 연결 자리)
    $(document).on('click', '.status-link', function (e) {
      e.preventDefault();
      // 필요 시 상태 변경 팝업/드롭다운 연결
    });

    // 감사 로그 기록
    function pushAudit(action, msg) {
      const t = new Date().toLocaleString();
      const prev = $('#audit').val();
      $('#audit').val(`[${t}] ${action} - ${msg}\n` + prev);
    }
  });