<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mã QR</title>
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
            <div class="card shadow-sm mx-auto" style="max-width: 80%; border: none;">

                <div class="card-body text-center p-4 border border-top-0">
                    <h4 class="fw-bold text-dark mb-4">Quét mã để chuyển khoản</h4>

                    <%-- Tạo URL an toàn với JSTL --%>
                    <c:url var="qrUrl" value="https://qr.sepay.vn/img">
                        <c:param name="acc" value="011120057777" />
                        <c:param name="bank" value="MBBank" />
                        <c:param name="amount" value="${amount}" />
                        <c:param name="des" value="${internalCode}" />
                        <c:param name="template" value="compact" />
                    </c:url>

                    <%-- Hiển thị ảnh --%>
                    <div class="d-flex justify-content-center">
                        <img src="${qrUrl}"
                             alt="QR Code"
                             class="img-fluid border p-2"
                             style="max-width: 80%;">
                    </div>

                    <div class="text-start d-inline-block" style="font-size: 0.95rem;">
                        <ul class="list-unstyled mb-4">
                            <li class="mb-1">• Số tiền: <strong class="text-danger">${amount}đ</strong></li>
                            <li class="mb-1">• Nội dung chuyển khoản: <strong class="text-danger">${internalCode}</strong></li>
                            <li class="mb-1">• Tên chủ tài khoản: <strong class="text-danger text-uppercase">Nguyễn Thanh Tùng</strong></li>
                            <li class="mb-1">• Số tài khoản: <strong class="text-danger">011120057777</strong></li>
                            <li class="mb-1">• <strong class="text-danger">Ngân hàng MB</strong></li>
                        </ul>
                    </div>

                    <div class="text-start mt-3">
                        <h5 class="text-danger fw-bold">Chú ý mỗi mã QRCode chỉ chuyển 1 lần duy nhất</h5>
                        <p class="mb-1 small">Nếu chuyển thủ công điền sai thông tin chuyển khoản hoặc chuyển nhiều lần cùng 1 mã giao dịch, hệ thống sẽ:</p>
                        <ul class="small mb-4">
                            <li><strong>không</strong> cộng tiền vào tài khoản của quý khách</li>
                            <li><strong>không</strong> hoàn trả tiền</li>
                            <li><strong>không</strong> chịu trách nhiệm về khoản tiền chuyển nhầm hoặc chuyển thừa</li>
                        </ul>

                        <p class="mb-1">Vui lòng chờ đợi 1 vài phút để hệ thống cập nhật số dư sau khi đã chuyển khoản.</p>
                        <p class="mb-3">Nếu đợi 20 phút sau khi chuyển khoản không thấy cập nhật thông tin giao dịch, vui lòng liên hệ QTV:</p>

                        <ul class="list-unstyled ps-3 small">
                            <li>• Zalo/Phone <span class="text-danger">(Message Only)</span>: <strong class="text-dark">0914411598</strong></li>
                        </ul>
                    </div>

                    <div class="btn-dark">
                        <a href="${pageContext.request.contextPath}/wallet/payment">Quay lại</a>
                    </div>
                </div>
            </div>
        </main>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>