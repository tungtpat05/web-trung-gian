<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rút tiền</title>
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
            <div class="card shadow-sm">
                <div class="card-header bg-light">
                    <h6 class="mb-0">Yêu cầu rút tiền</h6>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/payment/withdraw">

                        <div class="row mb-3">
                            <label class="col-sm-3 col-form-label text-end fw-bold">Số tiền (VND) (*)</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="amount" required>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <label class="col-sm-3 col-form-label text-end fw-bold">Ngân hàng thụ hưởng (*)</label>
                            <div class="col-sm-9">
                                <select name="bank-name" class="form-select" required>
                                    <option value="" disabled selected>Chọn ngân hàng</option>
                                    <option value="VietinBank">Ngân hàng TMCP Công thương Việt Nam (VietinBank)</option>
                                    <option value="Vietcombank">Ngân hàng TMCP Ngoại Thương Việt Nam (Vietcombank)</option>
                                    <option value="MBBank">Ngân hàng TMCP Quân đội (MBBank)</option>
                                    <option value="TPBank">Ngân hàng TMCP Tiên Phong (TPBank)</option>
                                    <option value="VPBank">Ngân hàng TMCP Việt Nam Thịnh Vượng (VPBank)</option>
                                    <option value="BIDV">Ngân hàng TMCP Đầu tư và Phát triển Việt Nam (BIDV)</option>
                                    <option value="Techcombank">Ngân hàng TMCP Kỹ thương Việt Nam (Techcombank)</option>
                                </select>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <label class="col-sm-3 col-form-label text-end fw-bold">Số tài khoản thụ hưởng (*)</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="bank-acc" required>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-sm-9 offset-sm-3 text-center">
                                <button type="submit" class="btn btn-success px-4">
                                    RÚT TIỀN
                                </button>
                            </div>
                        </div>

                    </form>
                </div>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success mt-3" role="alert">
                        ${successMessage}
                </div>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger mt-3" role="alert">
                        ${errorMessage}
                </div>
            </c:if>

            <div>
                <a href="${pageContext.request.contextPath}/home">Quay lại</a>
            </div>

        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>