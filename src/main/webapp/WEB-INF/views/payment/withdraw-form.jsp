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
            <div class="bg-white p-4 border rounded shadow-sm" style="min-height: 500px;">
                <h5 class="mb-4">Nạp vào là mất rồi, chưa rút được đâu</h5>
            </div>

        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>