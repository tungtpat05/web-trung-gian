<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết đơn #${order.id}</title>
    <link rel="stylesheet" href="/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="container mt-4" style="max-width:800px">

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Đơn hàng #${order.id}</h5>
            <c:choose>
                <c:when test="${order.status == 'PENDING'}">   <span class="badge bg-warning text-dark">Chờ xác nhận</span></c:when>
                <c:when test="${order.status == 'CONFIRMED'}"> <span class="badge bg-info">Đang thực hiện</span></c:when>
                <c:when test="${order.status == 'DELIVERED'}"> <span class="badge bg-primary">Chờ xác nhận nhận hàng</span></c:when>
                <c:when test="${order.status == 'COMPLETED'}"> <span class="badge bg-success">Hoàn thành</span></c:when>
                <c:when test="${order.status == 'DISPUTED'}">  <span class="badge bg-danger">Đang khiếu nại</span></c:when>
                <c:when test="${order.status == 'CANCELLED'}"> <span class="badge bg-secondary">Đã hủy</span></c:when>
            </c:choose>
        </div>

        <div class="card-body">
            <div class="row">
                <div class="col-md-6">
                    <table class="table table-sm">
                        <tr><td><strong>Listing</strong></td>
                            <td><a href="/listings/${order.listing.id}">${order.listing.title}</a></td></tr>
                        <tr><td><strong>Người bán</strong></td><td>${order.seller.username}</td></tr>
                        <tr><td><strong>Người mua</strong></td><td>${order.buyer.username}</td></tr>
                        <tr><td><strong>Giá</strong></td>
                            <td><fmt:formatNumber value="${order.priceSnapshot}" type="number" maxFractionDigits="0"/> VNĐ</td></tr>
                        <tr><td><strong>Phí GD</strong></td>
                            <td><fmt:formatNumber value="${order.feeSnapshot}" type="number" maxFractionDigits="0"/> VNĐ
                                (${order.feePayerSnapshot} trả)</td></tr>
                        <tr><td><strong>Escrow</strong></td>
                            <td><fmt:formatNumber value="${order.escrowAmount}" type="number" maxFractionDigits="0"/> VNĐ</td></tr>
                        <tr><td><strong>Ngày đặt</strong></td><td>${order.createdAt}</td></tr>
                        <c:if test="${order.completedAt != null}">
                            <tr><td><strong>Hoàn thành</strong></td><td>${order.completedAt}</td></tr>
                        </c:if>
                    </table>
                </div>

                <%-- Nội dung ẩn — hiện khi DELIVERED hoặc COMPLETED --%>
                <div class="col-md-6">
                    <c:if test="${isBuyer && (order.status == 'DELIVERED' || order.status == 'COMPLETED')}">
                        <div class="card border-success">
                            <div class="card-header bg-success text-white fw-bold">Nội dung từ seller</div>
                            <div class="card-body">
                                <pre class="mb-0">${order.listing.hiddenContent}</pre>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${isBuyer && order.status == 'PENDING'}">
                        <div class="alert alert-info">Chờ seller xác nhận. Nội dung sẽ hiện sau khi seller giao hàng.</div>
                    </c:if>
                    <c:if test="${isBuyer && order.status == 'CONFIRMED'}">
                        <div class="alert alert-info">Seller đang thực hiện. Nội dung hiện sau khi seller giao.</div>
                    </c:if>
                </div>
            </div>

            <hr>

            <%-- ACTION BUTTONS --%>
            <div class="d-flex gap-2 flex-wrap">

                <%-- BUYER --%>
                <c:if test="${isBuyer}">
                    <c:if test="${order.status == 'PENDING'}">
                        <form method="post" action="/orders/${order.id}/cancel"
                              onsubmit="return confirm('Hủy đơn? Tiền sẽ được hoàn lại.')">
                            <button class="btn btn-outline-danger">Hủy đơn</button>
                        </form>
                    </c:if>
                    <c:if test="${order.status == 'DELIVERED'}">
                        <form method="post" action="/orders/${order.id}/complete"
                              onsubmit="return confirm('Xác nhận đã nhận hàng? Tiền sẽ chuyển cho seller.')">
                            <button class="btn btn-success">✓ Xác nhận đã nhận hàng</button>
                        </form>
                        <form method="post" action="/orders/${order.id}/dispute"
                              onsubmit="return confirm('Mở khiếu nại?')">
                            <button class="btn btn-danger">Khiếu nại</button>
                        </form>
                    </c:if>
                    <c:if test="${order.status == 'CONFIRMED'}">
                        <form method="post" action="/orders/${order.id}/dispute"
                              onsubmit="return confirm('Mở khiếu nại?')">
                            <button class="btn btn-danger">Khiếu nại</button>
                        </form>
                    </c:if>
                    <c:if test="${order.status == 'DISPUTED'}">
                        <a href="/dispute/create/${order.id}" class="btn btn-warning">Xem khiếu nại</a>
                    </c:if>
                </c:if>

                <%-- SELLER --%>
                <c:if test="${isSeller}">
                    <c:if test="${order.status == 'PENDING'}">
                        <form method="post" action="/orders/${order.id}/confirm">
                            <button class="btn btn-success">Xác nhận đơn</button>
                        </form>
                        <form method="post" action="/orders/${order.id}/reject"
                              onsubmit="return confirm('Từ chối đơn? Tiền hoàn về buyer.')">
                            <button class="btn btn-outline-danger">Từ chối</button>
                        </form>
                    </c:if>
                    <c:if test="${order.status == 'CONFIRMED'}">
                        <form method="post" action="/orders/${order.id}/deliver"
                              onsubmit="return confirm('Xác nhận đã giao hàng/gửi nội dung?')">
                            <button class="btn btn-primary">Đánh dấu đã giao hàng</button>
                        </form>
                    </c:if>
                    <c:if test="${order.status == 'DELIVERED'}">
                        <div class="alert alert-info mb-0">Đã giao. Đang chờ buyer xác nhận.</div>
                    </c:if>
                </c:if>

                <a href="javascript:history.back()" class="btn btn-outline-secondary ms-auto">← Quay lại</a>
            </div>
        </div>
    </div>
</div>
<script src="/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
