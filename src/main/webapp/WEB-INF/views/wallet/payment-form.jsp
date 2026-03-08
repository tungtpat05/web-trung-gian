<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nạp tiền</title>
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
                    <h6 class="mb-0">Yêu cầu nạp tiền</h6>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/wallet/payment">

                        <div class="row mb-3">
                            <label class="col-sm-3 col-form-label text-end fw-bold">Số tiền (VND) (*)</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="amount" required>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-sm-9 offset-sm-3 text-center">
                                <button type="submit" class="btn btn-success px-4">
                                    <i class="bi bi-arrow-right"></i> NẠP TIỀN
                                </button>
                            </div>
                        </div>

                    </form>
                </div>
            </div>

            <div class="btn-dark">
                <a href="${pageContext.request.contextPath}/home">Quay lại</a>
            </div>

        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>