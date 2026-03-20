
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chợ công khai</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .main-content { width: calc(100% - 250px); }
        .page-title { font-size: 1.3rem; font-weight: 700; color: #222; }
        .page-subtitle { color: #888; font-size: 0.85rem; }
        .card-table { background: #fff; border-radius: 6px; box-shadow: 0 1px 4px rgba(0,0,0,0.07); }
        .table { font-size: 0.85rem; margin-bottom: 0; }
        .table thead th {
            background: #fff; font-weight: 600; color: #555;
            border-bottom: 2px solid #e0e0e0; white-space: nowrap; padding: 10px 8px;
        }
        .table td { vertical-align: middle; padding: 8px; border-color: #f0f0f0; }
        .table tbody tr:hover { background: #f8f9fc; }
        .filter-row td { padding: 4px 6px !important; background: #fafafa; }
        .filter-row input, .filter-row select {
            font-size: 0.8rem; padding: 3px 6px; height: 28px;
            border: 1px solid #ddd; border-radius: 3px; width: 100%;
        }
        .btn-clear { background: #e55; color: #fff; border: none; font-size: 0.8rem; padding: 3px 10px; border-radius: 3px; cursor: pointer; }
        .btn-collapse { background: #17a2b8; color: #fff; border: none; font-size: 0.8rem; padding: 3px 10px; border-radius: 3px; cursor: pointer; }
        .btn-detail { background: #17a2b8; color: #fff; border: none; font-size: 0.8rem; padding: 4px 10px; border-radius: 3px; cursor: pointer; white-space: nowrap; }
        .btn-detail:hover { background: #138496; }
        .btn-hd { background: #17a2b8; color: #fff; border: none; padding: 6px 14px; border-radius: 4px; font-size: 0.85rem; }
        .pagination-bar {
            display: flex; align-items: center; justify-content: space-between;
            padding: 10px 16px; border-top: 1px solid #eee;
            background: #fff; font-size: 0.85rem; border-radius: 0 0 6px 6px;
        }
        .pagination-bar button { background: #fff; border: 1px solid #ddd; padding: 4px 18px; border-radius: 3px; color: #555; cursor: pointer; }
        .pagination-bar button:disabled { color: #bbb; cursor: default; }
        .pagination-bar select { font-size: 0.85rem; padding: 3px 8px; border: 1px solid #ddd; border-radius: 3px; }
        /* Modal */
        .modal-label { font-weight: 500; color: #444; font-size: 0.85rem; padding-top: 6px; }
        .modal-field { background: #f5f5f5; border: 1px solid #ddd; border-radius: 4px; padding: 5px 9px; width: 100%; color: #333; font-size: 0.85rem; }
        textarea.modal-field { resize: none; }
        .modal-field-small { font-size: 0.78rem; color: #888; margin-top: 2px; }
        .btn-buy-modal { background: #17a2b8; color: #fff; border: none; padding: 7px 28px; border-radius: 4px; font-size: 0.9rem; cursor: pointer; }
        .btn-buy-modal:hover { background: #138496; }
        .fp-btn { font-size: 0.78rem; padding: 3px 14px; }
    </style>
</head>
<body>
<div class="d-flex">

    <jsp:include page="../common/sidebar.jsp"/>

    <div class="main-content">
        <jsp:include page="../common/header.jsp"/>

        <main class="p-4">
            <%-- Header row --%>
            <div class="d-flex justify-content-between align-items-start mb-3">
                <div>
                    <div class="page-title">Đơn trung gian công khai</div>
                    <div class="page-subtitle">Tổng cộng: <span id="totalCount">${listings.size()}</span> bản ghi</div>
                </div>
                <button class="btn-hd">📋 HƯỚNG DẪN SỬ DỤNG</button>
            </div>

            <%-- Table --%>
            <div class="card-table">
                <div class="table-responsive">
                    <table class="table table-bordered">
                        <thead>
                        <tr>
                            <th>Mã trung gian</th>
                            <th>Chủ đề trung gian</th>
                            <th>Phương thức liên hệ</th>
                            <th>Giá tiền</th>
                            <th>Bên chịu phí TG</th>
                            <th>Phí TG (5%)</th>
                            <th>Tổng phí cần TT</th>
                            <th>Người bán</th>
                            <th>Thời gian tạo</th>
                            <th>Cập nhật cuối</th>
                            <th>Hành động</th>
                        </tr>
                        <tr class="filter-row" id="filterRow">
                            <td><input type="text" id="fCode" placeholder=""></td>
                            <td><input type="text" id="fTitle" placeholder=""></td>
                            <td></td>
                            <td>
                                <div class="d-flex gap-1">
                                    <input type="number" id="fPriceFrom" placeholder="Từ" style="width:60px">
                                    <input type="number" id="fPriceTo" placeholder="Đến" style="width:60px">
                                </div>
                            </td>
                            <td>
                                <select id="fFeePayer">
                                    <option value="">All</option>
                                    <option value="BUYER">Bên mua</option>
                                    <option value="SELLER">Bên bán</option>
                                    <option value="SPLIT">Chia đôi</option>
                                </select>
                            </td>
                            <td></td><td></td>
                            <td><input type="text" id="fSeller" placeholder=""></td>
                            <td></td><td></td>
                            <td>
                                <div class="d-flex gap-1">
                                    <button class="btn-clear" onclick="clearFilters()">✕BỎ LỌC</button>
                                    <button class="btn-collapse" onclick="toggleFilter()">THU GỌN ›</button>
                                </div>
                            </td>
                        </tr>
                        </thead>
                        <tbody id="tbody">
                        <c:forEach var="l" items="${listings}">
                            <tr class="listing-row"
                                data-id="${l.id}"
                                data-title="${l.title}"
                                data-seller="${l.seller.username}"
                                data-price="${l.price}"
                                data-feepayer="${l.feePayer}"
                                data-contact="${l.hiddenContent}"
                                data-createdat="${l.createdAt}">
                                <td class="text-muted" style="max-width:130px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap" title="${l.id}">${l.id}</td>
                                <td style="max-width:150px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">${l.title}</td>
                                <td class="text-muted">${not empty l.hiddenContent ? l.hiddenContent : ''}</td>
                                <td class="num-cell" data-val="${l.price}"></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${l.feePayer == 'BUYER'}">Bên mua</c:when>
                                        <c:when test="${l.feePayer == 'SELLER'}">Bên bán</c:when>
                                        <c:otherwise>Chia đôi</c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="fee-cell" data-price="${l.price}"></td>
                                <td class="total-cell" data-price="${l.price}" data-fp="${l.feePayer}"></td>
                                <td>${l.seller.username}</td>
                                <td style="white-space:nowrap;font-size:0.8rem">${l.createdAt}</td>
                                <td style="white-space:nowrap;font-size:0.8rem">${l.createdAt}</td>
                                <td>
                                    <button class="btn-detail" onclick="showDetail(this)">ℹ CHI TIẾT</button>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="pagination-bar">
                    <button id="btnPrev" onclick="changePage(-1)" disabled>Trước</button>
                    <span>Trang <span id="curPage">1</span> / <span id="totalPages">1</span></span>
                    <select id="pageSize" onchange="applyFilters()">
                        <option value="10" selected>10 Bản ghi</option>
                        <option value="20">20 Bản ghi</option>
                        <option value="50">50 Bản ghi</option>
                    </select>
                    <button id="btnNext" onclick="changePage(1)">Sau</button>
                </div>
            </div>
        </main>
    </div>
</div>

<%-- MODAL CHI TIẾT --%>
<div class="modal fade" id="detailModal" tabindex="-1">
    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header" style="background:#f8f9fa">
                <h6 class="modal-title fw-bold">Thông tin đơn trung gian</h6>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body" id="modalBody"></div>
            <div class="modal-footer justify-content-between">
                <c:choose>
                    <c:when test="${currentUser != null}">
                        <form method="post" action="${pageContext.request.contextPath}/orders/place">
                            <input type="hidden" name="listingId" id="modalListingId">
                            <button type="submit" class="btn-buy-modal"
                                    onclick="return confirm('Xác nhận đặt hàng? Tiền escrow sẽ bị khóa.')">
                                🛒 MUA
                            </button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/auth/login" class="btn-buy-modal text-decoration-none">Đăng nhập để mua</a>
                    </c:otherwise>
                </c:choose>
                <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">CLOSE</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const fmt = n => n != null ? new Intl.NumberFormat('vi-VN').format(Math.round(Number(n))) : '';

    // Format số trong bảng
    document.querySelectorAll('.num-cell').forEach(td => td.textContent = fmt(td.dataset.val));
    document.querySelectorAll('.fee-cell').forEach(td => td.textContent = fmt(Number(td.dataset.price) * 0.05));
    document.querySelectorAll('.total-cell').forEach(td => {
        const p = Number(td.dataset.price), fp = td.dataset.fp, fee = p * 0.05;
        td.textContent = fmt(fp === 'BUYER' ? p + fee : fp === 'SELLER' ? p : p + fee / 2);
    });

    function numToWords(n) {
        n = Math.round(n);
        if (!n) return 'không';
        const u = ['','một','hai','ba','bốn','năm','sáu','bảy','tám','chín'];
        const t = ['','mười','hai mươi','ba mươi','bốn mươi','năm mươi','sáu mươi','bảy mươi','tám mươi','chín mươi'];
        if (n >= 1000000000) return Math.floor(n/1e9) + ' tỷ ' + (n%1e9 ? numToWords(n%1e9) : '');
        if (n >= 1000000) return Math.floor(n/1e6) + ' triệu ' + (n%1e6 ? numToWords(n%1e6) : '');
        if (n >= 1000) return Math.floor(n/1000) + ' nghìn ' + (n%1000 ? numToWords(n%1000) : '');
        if (n >= 100) return u[Math.floor(n/100)] + ' trăm ' + (n%100 ? numToWords(n%100) : '');
        if (n >= 10) return t[Math.floor(n/10)] + (n%10 ? ' ' + u[n%10] : '');
        return u[n];
    }

    function showDetail(btn) {
        const row = btn.closest('tr');
        const price = Number(row.dataset.price), fp = row.dataset.feepayer;
        const fee = price * 0.05;
        const total = fp === 'BUYER' ? price + fee : fp === 'SELLER' ? price : price + fee / 2;
        const btnBan = fp === 'SELLER' ? 'btn-primary fp-btn' : 'btn-outline-secondary fp-btn';
        const btnMua = fp === 'BUYER'  ? 'btn-primary fp-btn' : 'btn-outline-secondary fp-btn';

        document.getElementById('modalBody').innerHTML = `
        <div class="row g-2 align-items-start">
            <div class="col-4 modal-label">Mã trung gian</div>
            <div class="col-8"><input class="modal-field" readonly value="\${row.dataset.id}"></div>
            <div class="col-4 modal-label">Người bán</div>
            <div class="col-8"><input class="modal-field" readonly value="\${row.dataset.seller}"></div>
            <div class="col-4 modal-label">Chủ đề trung gian <span class="text-danger">(*)</span></div>
            <div class="col-8"><textarea class="modal-field" rows="2" readonly>\${row.dataset.title}</textarea></div>
            <div class="col-4 modal-label">Giá tiền <span class="text-danger">(*)</span></div>
            <div class="col-8">
                <input class="modal-field" readonly value="\${fmt(price)}">
                <div class="modal-field-small">\${numToWords(price)}</div>
            </div>
            <div class="col-4 modal-label">Bên chịu phí TG <span class="text-danger">(*)</span></div>
            <div class="col-8 d-flex gap-2 pt-1">
                <button class="btn btn-sm \${btnBan}" disabled>BÊN BÁN</button>
                <button class="btn btn-sm \${btnMua}" disabled>BÊN MUA</button>
            </div>
            <div class="col-4 modal-label">Phí trung gian</div>
            <div class="col-8">
                <input class="modal-field" readonly value="\${fmt(fee)}">
                <div class="modal-field-small">\${numToWords(fee)}</div>
            </div>
            <div class="col-4 modal-label">Tổng tiền cần thanh toán</div>
            <div class="col-8">
                <input class="modal-field" readonly value="\${fmt(total)}">
                <div class="modal-field-small">\${numToWords(total)}</div>
            </div>
            <div class="col-4 modal-label">Mô tả <span class="text-danger">(*)</span></div>
            <div class="col-8"><textarea class="modal-field" rows="3" readonly>(Ẩn — hiện sau khi mua)</textarea></div>
            <div class="col-4 modal-label">Phương thức liên hệ</div>
            <div class="col-8"><textarea class="modal-field" rows="2" readonly>\${row.dataset.contact || '(Ẩn — hiện sau khi mua)'}</textarea></div>
            <div class="col-4 modal-label">Thời gian tạo</div>
            <div class="col-8"><input class="modal-field" readonly value="\${row.dataset.createdat}"></div>
            <div class="col-4 modal-label">Cập nhật cuối</div>
            <div class="col-8"><input class="modal-field" readonly value="\${row.dataset.createdat}"></div>
            <div class="col-4 modal-label">Link chia sẻ đơn TG</div>
            <div class="col-8"><input class="modal-field" readonly value="\${window.location.origin}/listings/\${row.dataset.id}"></div>
        </div>`;
        document.getElementById('modalListingId').value = row.dataset.id;
        new bootstrap.Modal(document.getElementById('detailModal')).show();
    }

    // Filter + pagination
    let allRows = [], filtered = [], currentPage = 1;
    let filterVisible = true;

    function getPS() { return parseInt(document.getElementById('pageSize').value); }

    function applyFilters() {
        const code = document.getElementById('fCode').value.toLowerCase();
        const title = document.getElementById('fTitle').value.toLowerCase();
        const seller = document.getElementById('fSeller').value.toLowerCase();
        const fp = document.getElementById('fFeePayer').value;
        const pFrom = parseFloat(document.getElementById('fPriceFrom').value) || 0;
        const pTo = parseFloat(document.getElementById('fPriceTo').value) || Infinity;

        filtered = allRows.filter(r => {
            const p = parseFloat(r.dataset.price);
            return (!code   || r.dataset.id.toLowerCase().includes(code))
                && (!title  || r.dataset.title.toLowerCase().includes(title))
                && (!seller || r.dataset.seller.toLowerCase().includes(seller))
                && (!fp     || r.dataset.feepayer === fp)
                && p >= pFrom && p <= pTo;
        });
        currentPage = 1;
        renderPage();
    }

    function clearFilters() {
        ['fCode','fTitle','fSeller','fPriceFrom','fPriceTo'].forEach(id => { document.getElementById(id).value = ''; });
        document.getElementById('fFeePayer').value = '';
        applyFilters();
    }

    function toggleFilter() {
        filterVisible = !filterVisible;
        document.getElementById('filterRow').style.display = filterVisible ? '' : 'none';
        document.querySelector('.btn-collapse').textContent = filterVisible ? 'THU GỌN ›' : 'MỞ RỘNG ‹';
    }

    function renderPage() {
        const ps = getPS();
        const total = Math.max(1, Math.ceil(filtered.length / ps));
        currentPage = Math.min(Math.max(1, currentPage), total);
        document.getElementById('curPage').textContent = currentPage;
        document.getElementById('totalPages').textContent = total;
        document.getElementById('totalCount').textContent = filtered.length;
        document.getElementById('btnPrev').disabled = currentPage <= 1;
        document.getElementById('btnNext').disabled = currentPage >= total;
        const start = (currentPage - 1) * ps;
        const show = new Set(filtered.slice(start, start + ps).map(r => r.dataset.id));
        allRows.forEach(r => r.style.display = show.has(r.dataset.id) ? '' : 'none');
    }

    function changePage(d) { currentPage += d; renderPage(); }

    document.addEventListener('DOMContentLoaded', () => {
        allRows = Array.from(document.querySelectorAll('.listing-row'));
        filtered = [...allRows];
        renderPage();
        ['fCode','fTitle','fSeller','fFeePayer','fPriceFrom','fPriceTo'].forEach(id => {
            document.getElementById(id)?.addEventListener('input', applyFilters);
        });
    });
</script>
</body>
</html>
