<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Khiếu nại giao dịch</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .main-content { width: calc(100% - 250px); }
    </style>
</head>
<body>
<div class="d-flex">
    <jsp:include page="../common/sidebar.jsp"/>
    <div class="main-content">
        <jsp:include page="../common/header.jsp"/>
        <main class="p-4">
            <div class="bg-white p-4 border rounded shadow-sm">
                <h5 class="mb-4">Tạo khiếu nại cho đơn hàng #${orderId}</h5>
                <form action="${pageContext.request.contextPath}/dispute/create" method="post">
                    <input type="hidden" name="orderId" value="${orderId}">
                    <div class="mb-3">
                        <label for="reason" class="form-label">Lý do khiếu nại</label>
                        <select class="form-select" id="reason" name="reason" required>
                            <option value="">Chọn lý do...</option>
                            <option value="Hàng không đúng mô tả">Hàng không đúng mô tả</option>
                            <option value="Không nhận được hàng">Không nhận được hàng</option>
                            <option value="Lỗi kỹ thuật / Hư hỏng">Lỗi kỹ thuật / Hư hỏng</option>
                            <option value="Khác">Khác</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Mô tả chi tiết</label>
                        <textarea class="form-control" id="description" name="description" rows="5" required placeholder="Nhập chi tiết vấn đề bạn đang gặp phải..."></textarea>
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Hủy</a>
                        <button type="submit" class="btn btn-danger">Gửi khiếu nại</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
