<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
        <span>Số dư: <b><fmt:formatNumber value="${user.wallet.balance}" pattern="#,###"/>đ</b></span>


        <!-- USER DROPDOWN -->
        <div class="dropdown">
            <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle"
               id="userDropdown"
               data-bs-toggle="dropdown"
               aria-expanded="false">

                ${user.username}
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