/* header-gnb-titlebox-loader.js */
(async function () {
  // 외부 HTML 경로를 네 파일 위치로 변경
  const TEMPLATE_URL = "/proj_mes/Html/00_template/template.html";

  try {
    const res = await fetch(TEMPLATE_URL, { credentials: "same-origin" });
    if (!res.ok) throw new Error(`HTTP ${res.status} ${res.statusText}`);
    const html = await res.text();

    const base = new URL(TEMPLATE_URL, location.href);
    const parser = new DOMParser();
    const doc = parser.parseFromString(html, "text/html");

    // --- 외부 <head>의 CSS/STYLE 반영(중복 방지) ---
    adoptHead(doc.head, base);

    // --- 가져올 섹션 매핑: [외부에서, 현재 페이지의] ---
    const mappings = [
      ["header", "header"],
      [".gnb", ".gnb"],
      

    ];

    for (const [fromSel, toSel] of mappings) {
      const source = doc.querySelector(fromSel);
      const target = document.querySelector(toSel);
      if (!source || !target) continue;

      // 내부만 복제해서 삽입
      const tpl = document.createElement("template");
      tpl.innerHTML = source.innerHTML;

      // 상대경로(src/href) → 외부 문서 기준 절대경로화
      absolutizeURLs(tpl.content, base);

      // 교체 삽입
      target.replaceChildren(tpl.content);

      // 섹션 내부 <script>가 있다면 실행되게 재생성
      executeScripts(target);
    }
  } catch (err) {
    console.error("Include 실패:", err);
  }

  // ---------- 유틸들 ----------
  function adoptHead(head, base) {
    if (!head) return;

    // 외부 문서의 <link rel="stylesheet"> 중복 방지하며 채택
    const existing = new Set(
      [...document.head.querySelectorAll('link[rel="stylesheet"][href]')].map(
        (l) => new URL(l.getAttribute("href"), location.href).href
      )
    );

    head.querySelectorAll('link[rel="stylesheet"][href]').forEach((link) => {
      let href = link.getAttribute("href");
      if (!href) return;
      href = new URL(href, base).href;
      if (existing.has(href)) return;
      const clone = document.createElement("link");
      clone.rel = "stylesheet";
      clone.href = href;
      document.head.appendChild(clone);
      existing.add(href);
    });

    // <style> 채택
    head.querySelectorAll("style").forEach((style) => {
      const s = document.createElement("style");
      s.textContent = style.textContent;
      document.head.appendChild(s);
    });
  }

  //이 아래로는 made in gpt 내용은... 내일 확인하고 싶은데
  //탬플릿에서 가져온 조각 내부의 상대경로들을 절대 경로로 바꿔서 깨지지 않게한다. 라고 합니다.
  function absolutizeURLs(root, base) {
    const isAbs = (v) =>
      /^(https?:)?\/\//.test(v) ||
      v.startsWith("data:") ||
      v.startsWith("mailto:") ||
      v.startsWith("tel:") ||
      v.startsWith("#");

    root.querySelectorAll("[src]").forEach((el) => {
      const v = el.getAttribute("src");
      if (v && !isAbs(v)) el.setAttribute("src", new URL(v, base).href);
    });
    root.querySelectorAll("a[href], link[href]").forEach((el) => {
      const v = el.getAttribute("href");
      if (v && !isAbs(v)) el.setAttribute("href", new URL(v, base).href);
    });
  }


    //이거는 탬플릿 안에 있는 js코드를 실행시키는 함수라고 하는데 필요없지만 뭔가 아까우니 주석처리  
  function executeScripts(scope) {
    scope.querySelectorAll("script").forEach((old) => {
      const s = document.createElement("script");
      for (const { name, value } of [...old.attributes]) s.setAttribute(name, value);
      s.textContent = old.textContent; // 인라인 스크립트
      old.replaceWith(s); // 교체하며 실행
    });
  }
})();
