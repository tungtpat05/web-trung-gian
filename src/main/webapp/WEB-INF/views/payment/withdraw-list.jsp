<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Yêu cầu rút tiền</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body {
            background-color: #f8f9fa;
        }

        .main-content {
            min-height: 100vh;
            transition: all 0.3s;
        }

        .table thead th {
            border-top: none;
            background-color: #f1f3f5;
            color: #495057;
            font-weight: 600;
        }

        .badge {
            font-weight: 500;
            padding: 0.5em 0.75em;
        }

        .pagination .page-link {
            color: #495057;
            border: 1px solid #dee2e6;
            margin: 0 2px;
            border-radius: 4px;
        }

        .pagination .page-item.active .page-link {
            background-color: #0d6efd;
            border-color: #0d6efd;
            color: white;
        }

        .amount-pos {
            color: #198754;
            font-weight: bold;
        }

        .amount-neg {
            color: #dc3545;
            font-weight: bold;
        }
    </style>
</head>
<body>
<div class="d-flex">
    <jsp:include page="../common/sidebar.jsp"/>

    <div class="main-content w-100">
        <jsp:include page="../common/header.jsp"/>

        <main class="p-4">
            <div class="container-fluid bg-white p-4 border rounded shadow-sm">
                <div class="d-flex justify-content-end mb-3">
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/payment/withdraw-requests/new">Tạo
                        yêu cầu rút tiền</a>
                </div>
                <div class="row mb-4 align-items-center">
                    <div class="col-lg-3">
                        <h4 class="m-0">Yêu cầu rút tiền</h4>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover align-middle border-top">
                        <thead>
                        <tr>
                            <th style="width: 80px;">STT</th>
                            <th>Mã yêu cầu rút</th>
                            <th>Số tiền rút</th>
                            <th>Trạng thái</th>
                            <th>Ngân hàng thụ hưởng</th>
                            <th>Tài khoản thụ hưởng</th>
                            <th>Thời gian tạo</th>
                            <th>Cập nhật cuối</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="wr" items="${withdrawRequests.content}" varStatus="varStatus">
                            <tr>
                                <td class="text-muted">${withdrawRequests.number * withdrawRequests.size + varStatus.index + 1}</td>
                                <td>${wr.internalCode}</td>
                                <td>
                                    <fmt:formatNumber value="${wr.amount}" pattern="#,###" var="formattedAmount"/>
                                        ${formattedAmount} đ
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${wr.status == 'PENDING'}">
                                            <span class="badge bg-warning text-dark">Đang xử lý</span>
                                        </c:when>
                                        <c:when test="${wr.status == 'COMPLETED'}">
                                            <span class="badge bg-success">Hoàn thành</span>
                                        </c:when>
                                        <c:when test="${wr.status == 'REJECTED'}">
                                            <span class="badge bg-danger">Từ chối</span>
                                        </c:when>
                                        <c:when test="${wr.status == 'CANCELED'}">
                                            <span class="badge bg-dark">Đã huỷ</span>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td>
                                        ${wr.bankName}
                                </td>
                                <td>
                                        ${wr.bankAcc}
                                </td>
                                <td class="small text-muted">
                                        ${wr.createdAt}
                                </td>
                                <td class="small text-muted">
                                        ${wr.updatedAt}
                                </td>
                                <td class="d-flex gap-2">
                                    <button class="btn btn-sm btn-outline-secondary"
                                            onclick="openQRModal('${wr.bankName}','${wr.bankAcc}','${wr.amount}','${wr.internalCode}')">
                                        <i class="bi bi-qr-code"></i>
                                    </button>

                                    <c:if test="${wr.status == 'PENDING'}">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/payment/withdraw-requests/${wr.id}/cancel"
                                              onsubmit="return confirm('Bạn có chắc muốn huỷ yêu cầu rút tiền này?');">
                                            <button class="btn btn-sm btn-danger">
                                                <i class="bi bi-trash"></i> Huỷ yêu cầu
                                            </button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty withdrawRequests.content}">
                            <tr>
                                <td colspan="9" class="text-center py-4 text-muted">Không tìm thấy yêu cầu rút tiền
                                    nào.
                                </td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>

                <c:if test="${withdrawRequests.totalPages > 1}">
                    <nav class="mt-4">
                        <ul class="pagination justify-content-center">
                            <c:set var="adjacents" value="2"/>
                            <c:set var="curr" value="${withdrawRequests.number}"/>
                            <c:set var="total" value="${withdrawRequests.totalPages}"/>

                                <%-- Nút Đầu --%>
                            <li class="page-item ${curr == 0 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=0"><i
                                        class="bi bi-chevron-double-left"></i></a>
                            </li>

                                <%-- Dấu ba chấm trái --%>
                            <c:if test="${curr > adjacents}">
                                <li class="page-item disabled"><span class="page-link">...</span></li>
                            </c:if>

                                <%-- Các số trang (Window) --%>
                            <c:forEach begin="${curr - adjacents < 0 ? 0 : curr - adjacents}"
                                       end="${curr + adjacents >= total ? total - 1 : curr + adjacents}" var="i">
                                <li class="page-item ${curr == i ? 'active' : ''}">
                                    <a class="page-link" href="?page=${i}">${i + 1}</a>
                                </li>
                            </c:forEach>

                                <%-- Dấu ba chấm phải --%>
                            <c:if test="${curr < total - adjacents - 1}">
                                <li class="page-item disabled"><span class="page-link">...</span></li>
                            </c:if>

                                <%-- Nút Cuối --%>
                            <li class="page-item ${curr == total - 1 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${total - 1}"><i
                                        class="bi bi-chevron-double-right"></i></a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </main>
    </div>
</div>

<%--QR popup--%>
<div class="modal fade" id="qrModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">

            <!-- Header -->
            <div class="modal-header">
                <h5 class="modal-title">QR chuyển khoản</h5>

                <!-- X button -->
                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="modal">
                </button>
            </div>

            <!-- Body -->
            <div class="modal-body text-center">
                <div id="qrContainer">
                    <%-- Hiển thị ảnh --%>
                    <img id="qrImage"
                         class="img-fluid border p-2"
                         style="max-width: 80%;">
                </div>

            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function openQRModal(bankName, bankAcc, amount, internalCode) {
        console.log(bankName, bankAcc, amount, internalCode);

        let qrUrl = "https://qr.sepay.vn/img?acc=" + bankAcc +
            "&bank=" + bankName +
            "&amount=" + amount +
            "&des=" + internalCode +
            "&template=compact";
        console.log(qrUrl);

        document.getElementById("qrImage").src = qrUrl;

        let modal = new bootstrap.Modal(document.getElementById("qrModal"));
        modal.show();
    }
</script>

</body>
</html>