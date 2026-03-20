<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xử lý khiếu nại - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .main-content { width: calc(100% - 250px); }
        .chat-container { height: 400px; overflow-y: auto; border: 1px solid #dee2e6; padding: 15px; background: #fff; margin-bottom: 15px; }
        .message { margin-bottom: 15px; padding: 10px; border-radius: 10px; max-width: 80%; }
        .message-sent { background-color: #fff3cd; align-self: flex-end; margin-left: auto; }
        .message-received { background-color: #f8f9fa; align-self: flex-start; }
        .message-info { font-size: 0.75rem; color: #6c757d; }
    </style>
</head>
<body>
<div class="d-flex">
    <jsp:include page="../../common/sidebar.jsp"/>
    <div class="main-content">
        <jsp:include page="../../common/header.jsp"/>
        <main class="p-4">
            <div class="row">
                <div class="col-md-4">
                    <div class="bg-white p-4 border rounded shadow-sm mb-4">
                        <h5>Xử lý khiếu nại #${dispute.id}</h5>
                        <hr>
                        <p><strong>Trạng thái:</strong> <span class="badge bg-warning">${dispute.status}</span></p>
                        <p><strong>Đơn hàng:</strong> #${dispute.order.id} (Giá: ${dispute.order.priceSnapshot})</p>
                        <p><strong>Người mua:</strong> ${dispute.order.buyer.username}</p>
                        <p><strong>Người bán:</strong> ${dispute.order.seller.username}</p>
                        <hr>
                        <c:if test="${dispute.status != 'RESOLVED' && dispute.status != 'REFUNDED' && dispute.status != 'CLOSED'}">
                            <h6>Quyết định của Admin</h6>
                            <form action="/admin/dispute/resolve" method="post">
                                <input type="hidden" name="disputeId" value="${dispute.id}">
                                <div class="mb-3">
                                    <label class="form-label">Hình thức xử lý</label>
                                    <select name="outcome" class="form-select" required id="outcomeSelect">
                                        <option value="PAY_SELLER">Thanh toán cho người bản</option>
                                        <option value="REFUND_BUYER">Hoàn tiền cho người mua</option>
                                        <option value="SPLIT">Chia tiền (Split)</option>
                                        <option value="CANCEL_ORDER">Hủy đơn hàng</option>
                                    </select>
                                </div>
                                <div class="mb-3 d-none" id="splitAmountDiv">
                                    <label class="form-label">Số tiền hoàn cho người mua (Số còn lại sẽ cho người bán)</label>
                                    <input type="number" name="splitAmount" class="form-control" step="0.01" max="${dispute.order.escrowAmount}">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Ghi chú giải quyết</label>
                                    <textarea name="note" class="form-control" rows="3" required></textarea>
                                </div>
                                <button type="submit" class="btn btn-primary w-100">Xác nhận quyết định</button>
                            </form>
                        </c:if>
                        <c:if test="${dispute.status == 'RESOLVED' || dispute.status == 'REFUNDED' || dispute.status == 'CLOSED'}">
                            <div class="alert alert-info">
                                <h6>Đã giải quyết</h6>
                                <p>${dispute.resolutionNote}</p>
                                <small>Bởi: ${dispute.resolvedBy.username} vào ${dispute.resolvedAt}</small>
                            </div>
                        </c:if>
                    </div>
                </div>
                <div class="col-md-8">
                    <div class="bg-white p-4 border rounded shadow-sm">
                        <h5>Chat trung gian</h5>
                        <div class="chat-container d-flex flex-column" id="chatContainer">
                            <c:forEach var="msg" items="${messages}">
                                <div class="message ${msg.sender.role == 'ADMIN' ? 'message-sent' : 'message-received'}">
                                    <div class="message-info">
                                        <strong>${msg.sender.username}</strong> (${msg.sender.role}) - ${msg.createdAt}
                                    </div>
                                    <div class="message-content">
                                        ${msg.content}
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <form action="/admin/dispute/message" method="post">
                            <input type="hidden" name="disputeId" value="${dispute.id}">
                            <div class="input-group">
                                <input type="text" name="content" class="form-control" placeholder="Admin gửi tin nhắn..." required>
                                <button class="btn btn-warning" type="submit">Gửi</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
<script>
    var chatContainer = document.getElementById("chatContainer");
    chatContainer.scrollTop = chatContainer.scrollHeight;
    
    document.getElementById('outcomeSelect').addEventListener('change', function() {
        var splitDiv = document.getElementById('splitAmountDiv');
        if (this.value === 'SPLIT') {
            splitDiv.classList.remove('d-none');
        } else {
            splitDiv.classList.add('d-none');
        }
    });
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
