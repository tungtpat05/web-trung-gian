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

    <jsp:include page="sidebar.jsp"/>

    <div class="main-content">
        <header class="bg-white border-bottom p-3 d-flex justify-content-between align-items-center">
            <div></div>
            <!--Bell + Balance + User -->
            <div class="d-flex align-items-center gap-3">
                <span class="me-3 position-relative">
                    <i class="bi bi-bell"></i>
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
                          style="font-size: 0.6em;">
                        4
                    </span>
                </span>
                <span>Số dư: <b>0đ</b></span>

                <!-- USER DROPDOWN -->
                <div class="dropdown">
                    <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle"
                       id="userDropdown"
                       data-bs-toggle="dropdown"
                       aria-expanded="false">

                        <!-- Avatar -->
                        <img src="https://i.pravatar.cc/40"
                             alt="avatar"
                             width="36"
                             height="36"
                             class="rounded-circle border">
                    </a>

                    <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="userDropdown">
                        <li>
                            <a class="dropdown-item d-flex align-items-center" href="#">
                                <i class="bi bi-person me-2"></i>
                                Thông tin người dùng
                            </a>
                        </li>
                        <li>
                            <hr class="dropdown-divider">
                        </li>
                        <li>
                            <a class="dropdown-item d-flex align-items-center text-danger"
                               href="${pageContext.request.contextPath}/logout">
                                <i class="bi bi-box-arrow-right me-2"></i>
                                Đăng xuất
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </header>

        <main class="p-4">
            <div class="bg-white p-4 border rounded shadow-sm" style="min-height: 500px;">
                <h5 class="mb-4">Kho dịch vụ cá nhân</h5>
                <p class="text-muted">Nội dung main content nằm ở đây...</p>
            </div>

        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>