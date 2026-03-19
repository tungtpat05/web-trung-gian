<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang chủ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body {
            background-color: #f4f6f9;
        }

        .main-content {
            width: calc(100% - 250px);
        }
    </style>
</head>
<body>
<div class="d-flex">

    <jsp:include page="../common/sidebar.jsp"/>

    <div class="main-content">
        <jsp:include page="../common/header.jsp"/>

        <main class="p-4">
            <div class="bg-white p-4 border rounded shadow-sm" style="min-height: 500px;">
                <h5 class="mb-4">Thông tin cá nhân</h5>

                <form action="${pageContext.request.contextPath}/profile/update-username" method="post">
                    <div class="row">
                        <!-- USER INFO -->
                        <div class="col-md-6">
                            <h6 class="mb-3 text-primary">Thông tin User</h6>

                            <div class="mb-3">
                                <label class="form-label">Username</label>
                                <input type="text" name="username" class="form-control"
                                       value="${currentUser.username}">
                                <small class="text-muted">Được đổi username</small>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Email</label>
                                <input type="text" class="form-control"
                                       value="${currentUser.email}" readonly>
                            </div>

                            <!-- SAVE BUTTON -->
                            <div class="mt-4">
                                <button type="submit" class="btn btn-primary">
                                    Lưu thay đổi
                                </button>
                            </div>
                        </div>

                        <!-- WALLET INFO -->
                        <div class="col-md-6">
                            <h6 class="mb-3 text-success">Thông tin ví</h6>

                            <div class="mb-3">
                                <label class="form-label">Số dư khả dụng</label>
                                <input type="text" class="form-control"
                                       value="${currentUser.wallet.balance}" readonly>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Số dư bị khóa</label>
                                <input type="text" class="form-control"
                                       value="${currentUser.wallet.lockedBalance}" readonly>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Ngày tạo ví</label>
                                <input type="text" class="form-control"
                                       value="${currentUser.wallet.createdAt}" readonly>
                            </div>
                        </div>
                    </div>
                </form>

            </div>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>