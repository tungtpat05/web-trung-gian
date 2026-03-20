<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết listing</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .main-content { width: calc(100% - 250px); }
        .page-title { font-size: 1.3rem; font-weight: 700; color: #222; }
        .card { border-radius: 8px; }
        .muted-label { color: #666; font-size: 0.9rem; }
        pre { white-space: pre-wrap; word-break: break-word; }
    </style>
</head>
<body>
<div class="d-flex">
    <jsp:include page="../common/sidebar.jsp"/>

    <div class="main-content">
        <jsp:include page="../common/header.jsp"/>

        <main class="p-4" style="max-width: 900px;">
            <div class="d-flex justify-content-between align-items-start mb-3">
                <div>
                    <div class="page-title">Chi tiết listing #${listing.id}</div>
                    <div class="text-muted small">Người bán: ${listing.seller.username}</div>
                </div>
                <a href="${pageContext.request.contextPath}/listings" class="btn btn-outline-secondary btn-sm">Quay lại</a>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <div class="card shadow-sm">
                <div class="card-body">
                    <div class="mb-3">
                        <div class="muted-label mb-1">Chủ đề</div>
                        <div class="fw-semibold">${listing.title}</div>
                    </div>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="muted-label mb-1">Giá tiền</div>
                            <div class="fw-semibold">${listing.price}</div>
                        </div>
                        <div class="col-md-6">
                            <div class="muted-label mb-1">Phí giao dịch (5%)</div>
                            <div class="fw-semibold">${fee}</div>
                            <div class="text-muted small">${feeNote}</div>
                        </div>
                    </div>

                    <hr/>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="muted-label mb-1">Công khai / riêng tư</div>
                            <div class="fw-semibold">${listing.visibility}</div>
                        </div>
                        <div class="col-md-6">
                            <div class="muted-label mb-1">Bên chịu phí TG</div>
                            <div class="fw-semibold">${listing.feePayer}</div>
                        </div>
                    </div>

                    <div class="mt-3">
                        <div class="muted-label mb-1">Nội dung (ẩn — hiển thị theo trạng thái giao dịch)</div>
                        <pre class="p-2 bg-light rounded">${listing.hiddenContent}</pre>
                    </div>
                </div>
                <div class="card-footer d-flex justify-content-end gap-2">
                    <c:if test="${currentUser != null}">
                        <form method="post" action="${pageContext.request.contextPath}/orders/place">
                            <input type="hidden" name="listingId" value="${listing.id}">
                            <button type="submit"
                                    class="btn btn-success"
                                    onclick="return confirm('Xác nhận đặt hàng? Tiền escrow sẽ bị khóa.')">
                                🛒 MUA
                            </button>
                        </form>
                    </c:if>
                    <c:if test="${currentUser == null}">
                        <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-success">Đăng nhập để mua</a>
                    </c:if>
                </div>
            </div>
        </main>
    </div>
</div>
</body>
</html>

