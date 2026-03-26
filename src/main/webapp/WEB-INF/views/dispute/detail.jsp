<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết khiếu nại</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .main-content { width: calc(100% - 250px); }

        .chat-container {
            height: 400px;
            overflow-y: auto;
            border: 1px solid #dee2e6;
            padding: 15px;
            background: #fff;
            margin-bottom: 15px;
            display: flex;
            flex-direction: column;
        }

        /* mỗi dòng chat */
        .chat-row {
            display: flex;
            margin-bottom: 10px;
        }

        /* mình */
        .chat-row.self {
            justify-content: flex-end;
        }

        /* người khác (bao gồm admin) */
        .chat-row.other, .chat-row.admin {
            justify-content: flex-start;
        }

        /* khung tin nhắn */
        .message {
            max-width: 60%;
            padding: 10px 14px;
            border-radius: 15px;
            font-size: 14px;
            word-wrap: break-word;
        }

        /* mình (bên phải) */
        .message-sent {
            background-color: #0d6efd;
            color: white;
            border-bottom-right-radius: 5px;
        }

        /* người khác (bên trái) */
        .message-received {
            background-color: #f1f1f1;
            color: black;
            border-bottom-left-radius: 5px;
        }

        /* admin (ở giữa) */
        .message-admin {
            background-color: #ffeeba;
            color: #856404;
            border-radius: 10px;
            text-align: center;
        }

        .message-info {
            font-size: 11px;
            opacity: 0.7;
            margin-bottom: 3px;
        }

        /* input cố định dưới */
        form {
            position: sticky;
            bottom: 0;
            background: white;
            padding-top: 10px;
        }
    </style>
</head>
<body>
<div class="d-flex">
    <jsp:include page="../common/sidebar.jsp"/>
    <div class="main-content">
        <jsp:include page="../common/header.jsp"/>
        <main class="p-4">
            <div class="row">
                <div class="col-md-4">
                    <div class="bg-white p-4 border rounded shadow-sm mb-4">
                        <h5>Thông tin khiếu nại #${dispute.id}</h5>
                        <hr>
                        <p><strong>Trạng thái:</strong> <span class="badge ${dispute.status == 'CLOSED' || dispute.status == 'RESOLVED' ? 'bg-success' : 'bg-warning'}">${dispute.status}</span></p>
                        <p><strong>Đơn hàng:</strong> #${dispute.order.id}</p>
                        <p><strong>Lý do:</strong> ${dispute.reason}</p>
                        <p><strong>Ngày tạo:</strong> ${dispute.createdAt}</p>
                        <hr>
                        <h6>Mô tả:</h6>
                        <p class="text-muted small">${dispute.description}</p>
                    </div>
                </div>
                <div class="col-md-8">
                    <div class="bg-white p-4 border rounded shadow-sm">
                        <h5>Thảo luận khiếu nại</h5>
                        <div class="chat-container d-flex flex-column" id="chatContainer">
                            <c:forEach var="msg" items="${messages}">
                                <c:set var="isSelf" value="${msg.sender.id eq currentUser.id}" />
                                <c:set var="isAdmin" value="${msg.sender.role == 'ADMIN'}" />
                                <div class="chat-row ${isSelf ? 'self' : 'other'}">
                                    <div class="message
                                        ${isSelf ? 'message-sent' : (isAdmin ? 'message-admin' : 'message-received')}">

                                        <div class="message-info">
                                            <strong>${msg.sender.username}</strong> (${msg.sender.role}) - ${msg.createdAt}
                                        </div>

                                        <div class="message-content">
                                                ${msg.content}
                                        </div>

                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <c:if test="${dispute.status != 'CLOSED' && dispute.status != 'RESOLVED' && dispute.status != 'REFUNDED'}">
                            <form action="${pageContext.request.contextPath}/dispute/message" method="post">
                                <input type="hidden" name="disputeId" value="${dispute.id}">
                                <div class="input-group">
                                    <input type="text" name="content" class="form-control" placeholder="Nhập tin nhắn..." required>
                                    <button class="btn btn-primary" type="submit">Gửi</button>
                                </div>
                            </form>
                        </c:if>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
<script>
    var chatContainer = document.getElementById("chatContainer");
    chatContainer.scrollTop = chatContainer.scrollHeight;
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
