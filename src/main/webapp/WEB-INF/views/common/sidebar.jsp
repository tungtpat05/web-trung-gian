<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="sidebar bg-dark text-light p-3" style="width: 250px; min-height: 100vh;">
    <div class="brand-logo mb-4 text-center border-bottom pb-3">
        <h4 class="text-uppercase fw-bold m-0">Web trung gian</h4>
    </div>

    <ul class="nav flex-column gap-2">
        <c:if test="${currentUser.role == 'USER'}">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/home" class="nav-link text-light">
                    <i class="bi bi-house-door me-2"></i> Trang chủ
                </a>
            </li>
        </c:if>
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

                    <c:if test="${currentUser.role == 'USER'}">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/wallet/payment"
                               class="nav-link text-light opacity-75">
                                <i class="bi bi-wallet2 me-2"></i>
                                Nạp tiền
                            </a>
                        </li>
                    </c:if>

                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/wallet/stransactions"
                           class="nav-link text-light opacity-75">
                            <i class="bi bi-clock-history me-2"></i>
                            Lịch sử giao dịch
                        </a>
                    </li>

                    <c:if test="${currentUser.role == 'USER'}">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/wallet/withdraw-requests"
                               class="nav-link text-light opacity-75">
                                <i class="bi bi-arrow-up-circle me-2"></i>
                                Yêu cầu rút tiền
                            </a>
                        </li>
                    </c:if>

                    <c:if test="${currentUser.role == 'ADMIN'}">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/wallet/withdraw-requests"
                               class="nav-link text-light opacity-75">
                                <i class="bi bi-arrow-up-circle me-2"></i>
                                Xử lý yêu cầu rút tiền
                            </a>
                        </li>
                    </c:if>

                </ul>
            </div>
        </li>

        <c:if test="${currentUser.role == 'USER'}">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/listings" class="nav-link text-light">
                    <i class="bi bi-shop me-2"></i> Mua hàng
                </a>
            </li>
        </c:if>

        <c:if test="${currentUser.role == 'USER'}">
            <li class="nav-item">
                <a class="nav-link text-light d-flex justify-content-between align-items-center"
                   data-bs-toggle="collapse"
                   href="#escrowMenu"
                   role="button"
                   aria-expanded="false">
                    <span><i class="bi bi-shield-check me-2"></i> Trung gian</span>
                    <i class="bi bi-chevron-down small"></i>
                </a>
                <div class="collapse ps-4" id="escrowMenu">
                    <ul class="nav flex-column gap-1">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/listings"
                               class="nav-link text-light opacity-75">
                                <i class="bi bi-cart me-2"></i> Chợ công khai
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/orders/my-sales"
                               class="nav-link text-light opacity-75">
                                <i class="bi bi-bag-check me-2"></i> Đơn bán của tôi
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/orders/my-purchases"
                               class="nav-link text-light opacity-75">
                                <i class="bi bi-bag me-2"></i> Đơn mua của tôi
                            </a>
                        </li>
                    </ul>
                </div>
            </li>
        </c:if>

        <li class="nav-item">
            <a class="nav-link text-light d-flex justify-content-between align-items-center"
               data-bs-toggle="collapse"
               href="#disputeMenu"
               role="button"
               aria-expanded="false">
                <span>
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Khiếu nại
                </span>
                <i class="bi bi-chevron-down small"></i>
            </a>
            <div class="collapse ps-4" id="disputeMenu">
                <ul class="nav flex-column gap-1">
                    <c:if test="${currentUser.role == 'ADMIN'}">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/dispute/listings"
                               class="nav-link text-light opacity-75">
                                <i class="bi bi-list-task me-2"></i>
                                Quản lý khiếu nại
                            </a>
                        </li>
                    </c:if>
                    <c:if test="${currentUser.role == 'USER'}">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/dispute/list"
                               class="nav-link text-light opacity-75">
                                <i class="bi bi-person-badge me-2"></i>
                                Khiếu nại của tôi
                            </a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </li>
    </ul>
</div>

