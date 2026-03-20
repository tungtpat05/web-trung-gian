<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử giao dịch ví</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f8f9fa; }
        .main-content { min-height: 100vh; transition: all 0.3s; }
        .table thead th { border-top: none; background-color: #f1f3f5; color: #495057; font-weight: 600; }
        .badge { font-weight: 500; padding: 0.5em 0.75em; }
        .pagination .page-link { color: #495057; border: 1px solid #dee2e6; margin: 0 2px; border-radius: 4px; }
        .pagination .page-item.active .page-link { background-color: #0d6efd; border-color: #0d6efd; color: white; }
        .amount-pos { color: #198754; font-weight: bold; }
        .amount-neg { color: #dc3545; font-weight: bold; }
    </style>
</head>
<body>
<div class="d-flex">
    <jsp:include page="../common/sidebar.jsp"/>

    <div class="main-content w-100">
        <jsp:include page="../common/header.jsp"/>

        <main class="p-4">
            <div class="container-fluid bg-white p-4 border rounded shadow-sm">
                <div class="row mb-4 align-items-center">
                    <div class="col-lg-3">
                        <h4 class="m-0"><i class="bi bi-clock-history me-2"></i>Lịch sử giao dịch</h4>
                    </div>
                    <div class="col-lg-9">
                        <form action="${pageContext.request.contextPath}/wallet/transactions" method="GET" class="row g-2 justify-content-end">
                            <div class="col-md-2">
                                <input type="number" name="id" class="form-control" placeholder="Mã giao dịch" value="${param.id}">
                            </div>
                            <div class="col-md-3">
                                <select name="type" class="form-select">
                                    <option value="">Loại giao dịch</option>
                                    <option value="TOP_UP" ${param.type == 'PAYMENT' ? 'selected' : ''}>Nạp tiền</option>
                                    <option value="WITHDRAW" ${param.type == 'WITHDRAW' ? 'selected' : ''}>Rút tiền</option>
                                    <option value="BUY" ${param.type == 'BUY' ? 'selected' : ''}>Mua hàng</option>
                                    <option value="SELL" ${param.type == 'SELL' ? 'selected' : ''}>Bán hàng</option>
                                    <option value="REFUND" ${param.type == 'REFUND' ? 'selected' : ''}>Hoàn tiền</option>
                                    <option value="FEE" ${param.type == 'FEE' ? 'selected' : ''}>Phí dịch vụ</option>
                                    <option value="ESCROW_LOCK" ${param.type == 'ESCROW_LOCK' ? 'selected' : ''}>Tạm giữ</option>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <div class="input-group">
                                    <input type="date" name="startDate" class="form-control" value="${param.startDate}">
                                    <span class="input-group-text">to</span>
                                    <input type="date" name="endDate" class="form-control" value="${param.endDate}">
                                </div>
                            </div>
                            <div class="col-md-2">
                                <button type="submit" class="btn btn-primary w-100">
                                    <i class="bi bi-search me-1"></i> Lọc
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover align-middle border-top">
                        <thead>
                        <tr>
                            <th>Mã giao dịch</th>
                            <th>Người tạo giao dịch</th>
                            <th>Loại giao dịch</th>
                            <th>Tham chiếu</th>
                            <th>Số tiền giao dịch</th>
                            <th>Số dư trước</th>
                            <th>Số dư sau</th>
                            <th>Ngày tạo</th>
                            <th>Ghi chú</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="tx" items="${transactionPage.content}">
                            <tr>
                                <td class="text-muted">#${tx.id}</td>
                                <td>${tx.wallet.user.username}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${tx.type == 'PAYMENT' || tx.type == 'REFUND' || tx.type == 'SELL' || tx.type == 'ESCROW_RELEASE'}">
                                            <span class="badge bg-success-subtle text-success border border-success-subtle">
                                                <i class="bi bi-arrow-down-left-circle me-1"></i>
                                                <c:choose>
                                                    <c:when test="${tx.type == 'PAYMENT'}">Nạp tiền</c:when>
                                                    <c:when test="${tx.type == 'REFUND'}">Hoàn tiền</c:when>
                                                    <c:when test="${tx.type == 'SELL'}">Bán hàng</c:when>
                                                    <c:otherwise>Giải ngân</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </c:when>
                                        <c:when test="${tx.type == 'WITHDRAW' || tx.type == 'BUY' || tx.type == 'FEE'}">
                                            <span class="badge bg-danger-subtle text-danger border border-danger-subtle">
                                                <i class="bi bi-arrow-up-right-circle me-1"></i>
                                                <c:choose>
                                                    <c:when test="${tx.type == 'WITHDRAW'}">Rút tiền</c:when>
                                                    <c:when test="${tx.type == 'BUY'}">Mua hàng</c:when>
                                                    <c:otherwise>Phí dịch vụ</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle">
                                                <i class="bi bi-lock me-1"></i>Tạm giữ
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="text-uppercase fw-semibold small text-secondary">${tx.referenceType}</span>
                                    <div class="small text-primary">ID: #${tx.referenceId}</div>
                                </td>
                                <td class="${tx.amount > 0 ? 'amount-pos' : 'amount-neg'}">
                                    <c:if test="${tx.amount > 0}">+</c:if><fmt:formatNumber value="${tx.amount}" pattern="#,###"/> đ
                                </td>
                                <td class="fw-semibold">
                                    <fmt:formatNumber value="${tx.balanceBefore}" pattern="#,###"/> đ
                                </td>
                                <td class="fw-semibold">
                                    <fmt:formatNumber value="${tx.balanceAfter}" pattern="#,###"/> đ
                                </td>
                                <td class="small text-muted">
                                    ${tx.createdAt}
                                </td>
                                <td class="small" style="max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                        ${tx.note}
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty transactionPage.content}">
                            <tr>
                                <td colspan="9" class="text-center py-4 text-muted">Không tìm thấy giao dịch nào.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>

                <c:if test="${transactionPage.totalPages > 1}">
                    <nav class="mt-4">
                        <ul class="pagination justify-content-center">
                            <c:set var="adjacents" value="2" />
                            <c:set var="curr" value="${transactionPage.number}" />
                            <c:set var="total" value="${transactionPage.totalPages}" />
                                <%-- Sửa lại dòng c:set query trong JSP của bạn --%>
                            <c:set var="query" value="&id=${param.id}&type=${param.type}&startDate=${param.startDate}&endDate=${param.endDate}" />

                                <%-- Nút Đầu --%>
                            <li class="page-item ${curr == 0 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=0${query}"><i class="bi bi-chevron-double-left"></i></a>
                            </li>

                                <%-- Dấu ba chấm trái --%>
                            <c:if test="${curr > adjacents}">
                                <li class="page-item disabled"><span class="page-link">...</span></li>
                            </c:if>

                                <%-- Các số trang (Window) --%>
                            <c:forEach begin="${curr - adjacents < 0 ? 0 : curr - adjacents}"
                                       end="${curr + adjacents >= total ? total - 1 : curr + adjacents}" var="i">
                                <li class="page-item ${curr == i ? 'active' : ''}">
                                    <a class="page-link" href="?page=${i}${query}">${i + 1}</a>
                                </li>
                            </c:forEach>

                                <%-- Dấu ba chấm phải --%>
                            <c:if test="${curr < total - adjacents - 1}">
                                <li class="page-item disabled"><span class="page-link">...</span></li>
                            </c:if>

                                <%-- Nút Cuối --%>
                            <li class="page-item ${curr == total - 1 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${total - 1}${query}"><i class="bi bi-chevron-double-right"></i></a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>