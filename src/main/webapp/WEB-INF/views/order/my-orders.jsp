<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:choose><c:when test="${viewType=='buyer'}">Đơn mua của tôi</c:when><c:otherwise>Đơn bán của tôi</c:otherwise></c:choose></title>
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
        .btn-detail { background: #17a2b8; color: #fff; border: none; font-size: 0.8rem; padding: 4px 10px; border-radius: 3px; text-decoration: none; display: inline-block; }
        .btn-detail:hover { background: #138496; color: #fff; }
        .btn-add { background: #17a2b8; color: #fff; border: none; padding: 6px 14px; border-radius: 4px; font-size: 0.85rem; text-decoration: none; }
        .btn-hd { background: #17a2b8; color: #fff; border: none; padding: 6px 14px; border-radius: 4px; font-size: 0.85rem; }
        .pagination-bar {
            display: flex; align-items: center; justify-content: space-between;
            padding: 10px 16px; border-top: 1px solid #eee;
            background: #fff; font-size: 0.85rem; border-radius: 0 0 6px 6px;
        }
        .pagination-bar button { background: #fff; border: 1px solid #ddd; padding: 4px 18px; border-radius: 3px; color: #555; cursor: pointer; }
        .pagination-bar button:disabled { color: #bbb; cursor: default; }
        .pagination-bar select { font-size: 0.85rem; padding: 3px 8px; border: 1px solid #ddd; border-radius: 3px; }
        .no-rows { text-align: center; padding: 60px 0; color: #999; }
        .badge-pending   { background: #fff3cd; color: #856404; font-size: 0.78rem; padding: 3px 8px; border-radius: 3px; display: inline-block; }
        .badge-confirmed { background: #d1ecf1; color: #0c5460; font-size: 0.78rem; padding: 3px 8px; border-radius: 3px; display: inline-block; }
        .badge-delivered { background: #cce5ff; color: #004085; font-size: 0.78rem; padding: 3px 8px; border-radius: 3px; display: inline-block; }
        .badge-completed { background: #d4edda; color: #155724; font-size: 0.78rem; padding: 3px 8px; border-radius: 3px; display: inline-block; }
        .badge-disputed  { background: #f8d7da; color: #721c24; font-size: 0.78rem; padding: 3px 8px; border-radius: 3px; display: inline-block; }
        .badge-cancelled { background: #e2e3e5; color: #383d41; font-size: 0.78rem; padding: 3px 8px; border-radius: 3px; display: inline-block; }
    </style>
</head>
<body>
<div class="d-flex">

    <jsp:include page="../common/sidebar.jsp"/>

    <div class="main-content">
        <jsp:include page="../common/header.jsp"/>

        <main class="p-4">
            <div class="d-flex justify-content-between align-items-start mb-3">
                <div>
                    <div class="page-title">
                        <c:choose>
                            <c:when test="${viewType=='buyer'}">Đơn mua của tôi</c:when>
                            <c:otherwise>Đơn bán của tôi</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="page-subtitle">Tổng cộng: ${orders.totalElements} bản ghi</div>
                </div>
                <div class="d-flex gap-2">
                    <c:if test="${viewType=='seller'}">
                        <a href="${pageContext.request.contextPath}/listings" class="btn-add">+ THÊM MỚI</a>
                    </c:if>
                    <button class="btn-hd">📋 HƯỚNG DẪN SỬ DỤNG</button>
                </div>
            </div>

            <div class="card-table">
                <div class="table-responsive">
                    <table class="table table-bordered">
                        <thead>
                        <tr>
                            <th>Mã trung gian</th>
                            <th>Trạng thái</th>
                            <c:choose>
                                <c:when test="${viewType=='buyer'}"><th>Người bán</th></c:when>
                                <c:otherwise><th>Người mua</th></c:otherwise>
                            </c:choose>
                            <th>Chủ đề trung gian</th>
                            <th>Phương thức...</th>
                            <th>Công khai /...</th>
                            <th>Giá tiền</th>
                            <th>Bên chịu phí</th>
                            <th>Phí giao dịch</th>
                            <th>Tổng phí đã TT</th>
                            <th>Thời gian tạo</th>
                            <th>Cập nhật</th>
                            <th>Hành động</th>
                        </tr>
                        <tr class="filter-row" id="filterRow">
                            <td><input type="text" id="fCode"></td>
                            <td>
                                <select id="fStatus">
                                    <option value="">All</option>
                                    <option value="PENDING">Chờ xác nhận</option>
                                    <option value="CONFIRMED">Đang xử lý</option>
                                    <option value="DELIVERED">Đã giao</option>
                                    <option value="COMPLETED">Hoàn thành</option>
                                    <option value="DISPUTED">Khiếu nại</option>
                                    <option value="CANCELLED">Đã hủy</option>
                                </select>
                            </td>
                            <td>
                                <div class="d-flex gap-1">
                                    <input type="text" id="fCounterpart" style="flex:1">
                                    <button class="btn btn-sm btn-outline-secondary" style="padding:2px 6px">+</button>
                                </div>
                            </td>
                            <td><input type="text" id="fTitle"></td>
                            <td></td>
                            <td>
                                <select id="fVisibility">
                                    <option value="">All</option>
                                    <option value="PUBLIC">Công khai</option>
                                    <option value="PRIVATE">Riêng tư</option>
                                </select>
                            </td>
                            <td>
                                <div class="d-flex gap-1">
                                    <input type="number" id="fPriceFrom" placeholder="Từ" style="width:55px">
                                    <input type="number" id="fPriceTo" placeholder="Đến" style="width:55px">
                                </div>
                            </td>
                            <td>
                                <select id="fFeePayer">
                                    <option value="">All</option>
                                    <option value="BUYER">Bên mua</option>
                                    <option value="SELLER">Bên bán</option>
                                </select>
                            </td>
                            <td></td><td></td>
                            <td>
                                <div class="d-flex gap-1">
                                    <input type="text" id="fDateFrom" placeholder="Từ" style="width:55px">
                                    <input type="text" id="fDateTo" placeholder="Đến" style="width:55px">
                                </div>
                            </td>
                            <td></td>
                            <td>
                                <div class="d-flex gap-1">
                                    <button class="btn-clear" onclick="clearFilters()">✕BỎ LỌC</button>
                                    <button class="btn-collapse" onclick="toggleFilter()">THU GỌN ›</button>
                                </div>
                            </td>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty orders.content}">
                                <tr><td colspan="13" class="no-rows">No rows found</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="o" items="${orders.content}">
                                    <tr>
                                        <td class="text-muted" style="font-size:0.8rem;max-width:100px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">${o.id}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${o.status=='PENDING'}">  <span class="badge-pending">Chờ xác nhận</span></c:when>
                                                <c:when test="${o.status=='CONFIRMED'}"><span class="badge-confirmed">Đang xử lý</span></c:when>
                                                <c:when test="${o.status=='DELIVERED'}"><span class="badge-delivered">Đã giao</span></c:when>
                                                <c:when test="${o.status=='COMPLETED'}"><span class="badge-completed">Hoàn thành</span></c:when>
                                                <c:when test="${o.status=='DISPUTED'}"> <span class="badge-disputed">Khiếu nại</span></c:when>
                                                <c:when test="${o.status=='CANCELLED'}"><span class="badge-cancelled">Đã hủy</span></c:when>
                                                <c:otherwise><span>${o.status}</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <c:choose>
                                            <c:when test="${viewType=='buyer'}"><td>${o.seller.username}</td></c:when>
                                            <c:otherwise><td>${o.buyer.username}</td></c:otherwise>
                                        </c:choose>
                                        <td style="max-width:140px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">${o.listing.title}</td>
                                        <td></td>
                                        <td>${o.listing.visibility}</td>
                                        <td class="price-fmt" data-val="${o.priceSnapshot}"></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${o.feePayerSnapshot=='BUYER'}">Bên mua</c:when>
                                                <c:when test="${o.feePayerSnapshot=='SELLER'}">Bên bán</c:when>
                                                <c:otherwise>Chia đôi</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="price-fmt" data-val="${o.feeSnapshot}"></td>
                                        <td class="price-fmt" data-val="${o.escrowAmount}"></td>
                                        <td style="white-space:nowrap;font-size:0.8rem">${o.createdAt}</td>
                                        <td style="white-space:nowrap;font-size:0.8rem">${o.createdAt}</td>
                                        <td>
                                            <div class="d-flex flex-column gap-1">
                                                <a href="${pageContext.request.contextPath}/orders/detail/${o.id}" class="btn-detail">ℹ CHI TIẾT</a>
                                                <c:if test="${viewType=='buyer'}">
                                                    <a href="${pageContext.request.contextPath}/dispute/create/${o.id}" class="btn-detail" style="background: #dc3545;">⚠ KHIẾU NẠI</a>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>

                <div class="pagination-bar">
                    <button onclick="changePage(-1)" ${orders.number == 0 ? 'disabled' : ''}>Trước</button>
                    <span>Trang <strong>${orders.number + 1}</strong> / <strong>${orders.totalPages < 1 ? 1 : orders.totalPages}</strong></span>
                    <select onchange="changeSize(this.value)">
                        <option value="10" selected>10 Bản ghi</option>
                        <option value="20">20 Bản ghi</option>
                        <option value="50">50 Bản ghi</option>
                    </select>
                    <button onclick="changePage(1)" ${orders.last ? 'disabled' : ''}>Sau</button>
                </div>
            </div>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const fmt = n => n != null ? new Intl.NumberFormat('vi-VN').format(Math.round(Number(n))) : '';
    document.querySelectorAll('.price-fmt').forEach(td => td.textContent = fmt(td.dataset.val));

    const curPage = ${orders.number};
    const viewType = '${viewType}';
    const baseUrl = viewType === 'buyer' ? '/orders/my-purchases' : '/orders/my-sales';

    function changePage(d) {
        const np = curPage + d;
        if (np < 0) return;
        window.location.href = baseUrl + '?page=' + np;
    }
    function changeSize(s) { window.location.href = baseUrl + '?page=0&size=' + s; }

    let filterVisible = true;
    function toggleFilter() {
        filterVisible = !filterVisible;
        document.getElementById('filterRow').style.display = filterVisible ? '' : 'none';
        document.querySelector('.btn-collapse').textContent = filterVisible ? 'THU GỌN ›' : 'MỞ RỘNG ‹';
    }
    function clearFilters() {
        ['fCode','fStatus','fCounterpart','fTitle','fVisibility','fFeePayer','fPriceFrom','fPriceTo','fDateFrom','fDateTo']
            .forEach(id => { const el = document.getElementById(id); if(el) el.value=''; });
    }
</script>
</body>
</html>
