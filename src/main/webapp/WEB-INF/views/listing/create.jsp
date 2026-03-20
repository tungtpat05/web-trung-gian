<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo listing mới</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/style.css">
    <style>
        .main-content { width: calc(100% - 250px); }
        .page-title { font-size: 1.3rem; font-weight: 700; color: #222; }
        .page-subtitle { color: #888; font-size: 0.85rem; }
        .card-form { background: #fff; border-radius: 6px; box-shadow: 0 1px 4px rgba(0,0,0,0.07); padding: 18px; }
        .form-label { font-weight: 600; color: #333; font-size: 0.9rem; }
        .form-control, select.form-control { font-size: 0.9rem; }
        .btn-primary { background: #17a2b8; border-color: #17a2b8; }
        .btn-primary:hover { background: #138496; border-color: #138496; }
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
                    <div class="page-title">Tạo listing mới</div>
                    <div class="page-subtitle">Seller tạo đơn trung gian để người mua đặt hàng</div>
                </div>
                <a href="${pageContext.request.contextPath}/listings" class="btn btn-outline-secondary btn-sm">Quay lại</a>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <div class="card-form">
                <form method="post" action="${pageContext.request.contextPath}/listings/create">
                    <div class="mb-3">
                        <label class="form-label">Chủ đề trung gian <span class="text-danger">*</span></label>
                        <input class="form-control" name="title" required maxlength="255" placeholder="Ví dụ: Thiết kế logo...">
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Giá tiền (VNĐ) <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" name="price" required min="0" step="1" placeholder="Ví dụ: 500000">
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Phương thức liên hệ (ẩn) <span class="text-danger">*</span></label>
                        <textarea class="form-control" name="hiddenContent" required rows="3" placeholder="Ví dụ: Email/Số điện thoại/Zalo..."></textarea>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Công khai <span class="text-danger">*</span></label>
                            <select class="form-control" name="visibility" required>
                                <c:forEach var="v" items="${visibilities}">
                                    <option value="${v}" <c:if test="${v=='PUBLIC'}">selected</c:if>>${v}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Bên chịu phí TG (5%) <span class="text-danger">*</span></label>
                            <select class="form-control" name="feePayer" required>
                                <c:forEach var="f" items="${feePayers}">
                                    <option value="${f}" <c:if test="${f=='BUYER'}">selected</c:if>>${f}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary px-4">TẠO LISTING</button>
                        <a href="${pageContext.request.contextPath}/listings" class="btn btn-outline-secondary px-4">HỦY</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

