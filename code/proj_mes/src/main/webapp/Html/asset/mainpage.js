  // 임시 데이터 (LocalStorage와 연결해도 됨)
  const notices = JSON.parse(localStorage.getItem("notices") || "[]");

  const listDiv = document.getElementById("notice-list");

  if (notices.length === 0) {
    listDiv.innerHTML = "<p>등록된 글이 없습니다.</p>";
  } else {
    listDiv.innerHTML = notices
      .slice()  // 원본 배열 보존
      .reverse() // 최신 글 위로
      .map((n, i) => `
        <div class="notice-card">
          <a href="view.html?id=${notices.length - 1 - i}">${n.title}</a>
          <div class="notice-date">${n.date}</div>
        </div>
      `).join("");
  }