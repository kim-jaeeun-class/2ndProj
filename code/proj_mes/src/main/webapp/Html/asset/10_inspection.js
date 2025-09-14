document.addEventListener('DOMContentLoaded', init);

function init() {
    const basePath = window.basePath || "";
    console.log("JS basePath inside init:", basePath);

    const inspTabBody = document.querySelector('.insp_tab tbody');
    const lotSelect = document.getElementById("lot_id");
    const procSelect = document.getElementById("proc_id");
    const today = new Date().toISOString().split('T')[0];
    document.getElementById("inspection_date").value = today;

    const allCheck = document.getElementById("allCheck");
    const inputBtn = document.querySelector('.inputBtn');
    const saveBtn = document.querySelector('.saveBtn');
    const deleteBtn = document.querySelector('.deleteBtn');

    function getCheckboxes() {
        return document.querySelectorAll('.insp_tab tbody input[type="checkbox"]');
    }

    // LOT 번호 선택 이벤트
    lotSelect.addEventListener("change", function () {
        const selectedLotId = this.value;
        if (!selectedLotId) {
            inspTabBody.innerHTML = `<td colspan="11" class="text-center py-4">LOT 번호를 선택해주세요.</td>`;
            allCheck.checked = false;
            return;
        }

        // 공정 목록 가져오기
        fetch(`${basePath}/inspection?action=getProcessesByLotId&lotId=${selectedLotId}`)
            .then(res => {
                if (!res.ok) throw new Error("HTTP " + res.status);
                return res.json();
            })
            .then(processData => {
                procSelect.innerHTML = '<option value="">공정 선택</option>';
                processData.forEach(p => {
                    const option = document.createElement("option");
                    option.value = p.proc_id;
                    option.textContent = `${p.proc_id} / ${p.proc_name}`;
                    procSelect.appendChild(option);
                });
            })
            .catch(err => console.error("Error fetching processes:", err));

        // 검사 결과 가져오기
        fetch(`${basePath}/inspection?action=getResultsByLot&lotId=${selectedLotId}`)
            .then(res => {
                if (!res.ok) throw new Error("HTTP " + res.status);
                return res.json();
            })
            .then(data => {
                inspTabBody.innerHTML = "";
                if (data.length === 0) {
                    inspTabBody.innerHTML = `<td colspan="11" class="text-center py-4">해당 LOT에 대한 검사 결과가 없습니다.</td>`;
                } else {
                    data.forEach(r => {
                        const row = document.createElement("tr");
                        const dateStr = `${r.inspection_date || ""} 시작:${r.start_time || "-"} 종료:${r.end_time || "-"}`;
                        row.innerHTML = `
                            <td><input type="checkbox" name="itemCheck"></td>
                            <td>${r.ir_id}</td>
                            <td data-value="${r.ir_type}">${r.ir_type === 1 ? "전수" : r.ir_type === 2 ? "샘플" : r.ir_type === 3 ? "재검" : ""}</td>
                            <td>${r.lot_id}</td>
                            <td data-value="${r.proc_id}">${r.proc_name || ""}</td>
                            <td data-date="${r.inspection_date || ""}" data-start="${r.start_time || ""}" data-end="${r.end_time || ""}">
                                ${dateStr}
                            </td>
                            <td data-gd="${r.gd_quantity || 0}" data-bd="${r.bd_quantity || 0}">
                                양품 수량: ${r.gd_quantity || 0} / 불량 수량: ${r.bd_quantity || 0}
                            </td>
                            <td>${r.bd_reason || ""}</td>
                            <td>${r.remark || ""}</td>
                            <td data-value="${r.quality_state}">${r.quality_state === 1 ? "양품" : r.quality_state === 2 ? "재검 대기" : r.quality_state === 3 ? "폐기" : ""}</td>
                            <td data-value="${r.worker_id}">${r.worker_name || ""}</td>
                        `;
                        inspTabBody.appendChild(row);
                    });
                }
                allCheck.checked = false;
            })
            .catch(err => console.error("Error fetching inspection results:", err));
    });

    // 👉 입력 버튼: 폼 데이터를 표에 추가
    inputBtn.addEventListener("click", function (e) {
        e.preventDefault();

        const irId = document.getElementById("ir_id").value.trim();
        const irType = document.querySelector('select[name="ir_type"]').value;
        const lotId = document.getElementById("lot_id").value.trim();
        const procId = document.getElementById("proc_id").value.trim();
        const inspectionDate = document.getElementById("inspection_date").value;
        const startTime = document.getElementById("start_time").value;
        const endTime = document.getElementById("end_time").value;
        const workerId = document.getElementById("worker_id").value;
        const bdReason = document.querySelector('input[name="bd_reason"]').value || "";
        const gdQuantity = document.querySelector('input[name="gd_quantity"]').value || "0";
        const bdQuantity = document.querySelector('input[name="bd_quantity"]').value || "0";
        const qualityState = document.querySelector('select[name="quality_state"]').value;
        const remark = document.getElementById("remark").value || "";

        if (!irId || !lotId) {
            showCustomMessage("IR 코드와 LOT 번호는 필수입니다.");
            return;
        }

        const procOpt = document.querySelector(`#proc_id option[value="${procId}"]`);
        const workerOpt = document.querySelector(`#worker_id option[value="${workerId}"]`);

        const procName = procOpt ? procOpt.textContent : "공정 미선택";
        const workerLabel = workerOpt ? workerOpt.textContent : "작업자 미선택";

        // 📌 날짜와 시간 dataset에 저장
        const dateDisplay = `${inspectionDate} 시작:${startTime || "-"} 종료:${endTime || "-"}`;

        const newRow = document.createElement("tr");
        newRow.innerHTML = `
            <td><input type="checkbox" name="itemCheck"></td>
            <td>${irId}</td>
            <td data-value="${irType}">${irType === "1" ? "전수" : irType === "2" ? "샘플" : "재검"}</td>
            <td>${lotId}</td>
            <td data-value="${procId}">${procName}</td>
            <td data-date="${inspectionDate}" data-start="${startTime}" data-end="${endTime}">
                ${dateDisplay}
            </td>
            <td data-gd="${gdQuantity}" data-bd="${bdQuantity}">
                양품 수량: ${gdQuantity} / 불량 수량: ${bdQuantity}
            </td>
            <td>${bdReason}</td>
            <td>${remark}</td>
            <td data-value="${qualityState}">
                ${qualityState === "1" ? "양품" : qualityState === "2" ? "재검 대기" : "폐기"}
            </td>
            <td data-value="${workerId}">${workerLabel}</td>
        `;

        if (inspTabBody.querySelector('td[colspan="11"]')) {
            inspTabBody.innerHTML = "";
        }
        inspTabBody.appendChild(newRow);

        // 입력 필드 초기화
        document.querySelector('select[name="ir_type"]').value = "1";
        document.getElementById("lot_id").value = "";
        document.getElementById("proc_id").value = "";
        document.getElementById("start_time").value = "";
        document.getElementById("end_time").value = "";
        document.getElementById("worker_id").value = "";
        document.querySelector('input[name="bd_reason"]').value = "";
        document.querySelector('input[name="gd_quantity"]').value = "0";
        document.querySelector('input[name="bd_quantity"]').value = "0";
        document.querySelector('select[name="quality_state"]').value = "1";
        document.getElementById("remark").value = "";

        showCustomMessage("데이터가 표에 추가되었습니다.");
    });

    // 저장 버튼
    saveBtn.addEventListener("click", function () {
        const rows = Array.from(inspTabBody.querySelectorAll("tr"));
        if (rows.length === 1 && rows[0].textContent.includes("LOT 번호를 선택해주세요")) {
            showCustomMessage("저장할 데이터가 없습니다.");
            return;
        }

        const dataToSave = rows.map(row => {
            const cells = row.querySelectorAll("td");
            return {
                ir_id: cells[1].textContent.trim(),
                ir_type: Number(cells[2].dataset.value || 0),
                lot_id: cells[3].textContent.trim(),
                proc_id: cells[4].dataset.value || "",
                inspection_date: cells[5].dataset.date || "",
                start_time: cells[5].dataset.start || "",
                end_time: cells[5].dataset.end || "",
                gd_quantity: Number(cells[6].dataset.gd || 0),
                bd_quantity: Number(cells[6].dataset.bd || 0),
                bd_reason: cells[7].textContent.trim() || "",
                remark: cells[8].textContent.trim() || "",
                quality_state: Number(cells[9].dataset.value || 0),
                worker_id: cells[10].dataset.value || ""
            };
        });

        console.log("📤 저장할 데이터:", dataToSave);

        fetch(`${basePath}/inspection?action=saveAll`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dataToSave)
        })
            .then(res => {
                if (!res.ok) throw new Error("HTTP " + res.status);
                return res.json();
            })
            .then(data => {
                if (data.success) {
                    showCustomMessage("데이터가 성공적으로 저장되었습니다.");
                    setTimeout(() => location.reload(), 1500);
                } else {
                    showCustomMessage("저장에 실패했습니다: " + data.message);
                }
            })
            .catch(err => {
                console.error("Error:", err);
                showCustomMessage("데이터 저장 중 오류가 발생했습니다.");
            });
    });

    // 전체 선택 체크박스
    allCheck.addEventListener("change", function () {
        getCheckboxes().forEach(cb => (cb.checked = this.checked));
    });
    inspTabBody.addEventListener("change", e => {
        if (e.target.type === "checkbox") {
            allCheck.checked = Array.from(getCheckboxes()).every(cb => cb.checked);
        }
    });

    // 삭제 버튼
    deleteBtn.addEventListener("click", function () {
        const checkedRows = inspTabBody.querySelectorAll("input[type='checkbox']:checked");
        const irIdsToDelete = Array.from(checkedRows).map(cb => cb.closest("tr").children[1].textContent);

        if (irIdsToDelete.length === 0) {
            showCustomMessage("삭제할 항목을 선택해주세요.");
            return;
        }

        Promise.all(
            irIdsToDelete.map(irId =>
                fetch(`${basePath}/inspection?action=delete&irId=${irId}`, { method: "GET" }).then(res => res.ok)
            )
        ).then(() => {
            showCustomMessage("선택된 항목이 삭제되었습니다. 페이지를 새로고침합니다.");
            setTimeout(() => location.reload(), 1500);
        });
    });
}

function showCustomMessage(message) {
    const messageBox = document.createElement("div");
    messageBox.style.cssText = `
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        padding: 20px;
        background-color: white;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        z-index: 1000;
        text-align: center;
        max-width: 300px;
        font-family: 'Inter', sans-serif;
    `;
    messageBox.innerHTML = `
        <p>${message}</p>
        <button onclick="this.parentNode.remove()" 
            style="margin-top: 10px; padding: 5px 10px; cursor: pointer; background-color: #0056b3; color: white; border: none; border-radius: 4px;">
            확인
        </button>
    `;
    document.body.appendChild(messageBox);
}