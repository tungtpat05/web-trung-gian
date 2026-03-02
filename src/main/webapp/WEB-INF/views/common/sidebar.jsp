<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="sidebar bg-dark text-light p-3" style="width: 250px; min-height: 100vh;">
    <div class="brand-logo mb-4 text-center border-bottom pb-3">
        <h4 class="text-uppercase fw-bold m-0">Web trung gian</h4>
    </div>

    <ul class="nav flex-column gap-2">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/home" class="nav-link text-light">
                <i class="bi bi-house-door me-2"></i> Trang chủ
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link text-light d-flex justify-content-between align-items-center"
               data-bs-toggle="collapse"
               href="#paymentMenu"
               role="button"
               aria-expanded="false">

            <span>
                <i class="bi bi-credit-card me-2"></i>
                Quản lý thanh toán
            </span>

                <i class="bi bi-chevron-down small"></i>
            </a>

            <div class="collapse ps-4" id="paymentMenu">
                <ul class="nav flex-column gap-1">

                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/payment/deposit"
                           class="nav-link text-light opacity-75">
                            <i class="bi bi-wallet2 me-2"></i>
                            Nạp tiền
                        </a>
                    </li>

                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/payment/history"
                           class="nav-link text-light opacity-75">
                            <i class="bi bi-clock-history me-2"></i>
                            Lịch sử giao dịch
                        </a>
                    </li>

                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/payment/withdraw"
                           class="nav-link text-light opacity-75">
                            <i class="bi bi-arrow-up-circle me-2"></i>
                            Yêu cầu rút tiền
                        </a>
                    </li>

                </ul>
            </div>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/order" class="nav-link text-light">
                <i class="bi bi-bag me-2"></i> Mua hàng
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/escrow" class="nav-link text-light">
                <i class="bi bi-shield-check me-2"></i> Trung gian
            </a>
        </li>
    </ul>
</div>