<table class="table">
    <thead>
        <tr>
            <th>Tiêu đề</th>
            <th>Giá</th>
            <th>Người chịu phí</th>
            <th>Trạng thái</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${listItems}" var="item">
            <tr>
                <td>${item.title}</td>
                <td>${item.price} VNĐ</td>
                <td><span class="badge">${item.feePayer}</span></td>
                <td>${item.status}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>