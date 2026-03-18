<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý khiếu nại</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .main-content { width: calc(100% - 250px); }
    </style>
</head>
<body>
<div class="d-flex">
    <jsp:include page="../../common/sidebar.jsp"/>
    <div class="main-content">
        <jsp:include page="../../common/header.jsp"/>
        <main class="p-4">
            <div class="bg-white p-4 border rounded shadow-sm">
                <h5 class="mb-4">Danh sách khiếu nại</h5>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>#</th>
                                <th>Đơn hàng</th>
                                <th>Người tạo</th>
                                <th>Lý do</th>
                                <th>Ngày tạo</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="d" items="${disputes}">
                                <tr>
                                    <td>${d.id}</td>
                                    <td>#${d.order.id}</td>
                                    <td>${d.createdBy.username}</td>
                                    <td>${d.reason}</td>
                                    <td>${d.createdAt}</td>
                                    <td><span class="badge ${d.status == 'CLOSED' || d.status == 'RESOLVED' ? 'bg-success' : 'bg-warning'}">${d.status}</span></td>
                                    <td>
                                        <a href="/admin/dispute/detail/${d.id}" class="btn btn-sm btn-outline-primary">Xem chi tiết</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
