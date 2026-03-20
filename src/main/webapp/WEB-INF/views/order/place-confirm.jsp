<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác nhận đặt hàng</title>
    <link rel="stylesheet" href="/vendor/bootstrap/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-5" style="max-width:480px">
    <div class="card shadow-sm">
        <div class="card-header"><h5 class="mb-0">Xác nhận đặt hàng</h5></div>
        <div class="card-body">
            <p>Bạn có chắc muốn đặt hàng cho listing này?</p>
            <p class="text-muted small">
                Tiền escrow sẽ bị <strong>khóa</strong> trong ví của bạn cho đến khi giao dịch hoàn tất.
            </p>
            <div class="d-flex gap-2">
                <form method="post" action="/orders/place">
                    <input type="hidden" name="listingId" value="${listingId}">
                    <button class="btn btn-success">Xác nhận đặt hàng</button>
                </form>
                <a href="/listings/${listingId}" class="btn btn-outline-secondary">Quay lại</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
