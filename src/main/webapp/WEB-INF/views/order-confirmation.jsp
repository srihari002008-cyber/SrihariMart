<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="header.jsp" />

<div class="card" style="max-width: 650px; margin: 2rem auto; text-align: center; padding: 3rem 2rem;">
    <div style="width: 70px; height: 70px; background-color: #ecfdf5; color: var(--accent); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 2.2rem; margin: 0 auto 1.5rem auto;">
        ✓
    </div>

    <h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 0.5rem; color: var(--text-main);">
        Order Placed Successfully!
    </h1>
    <p style="color: var(--text-muted); font-size: 1rem; margin-bottom: 2rem;">
        Thank you for shopping at SrihariMart. Your mock payment confirmation was approved and order is confirmed.
    </p>

    <c:if test="${not empty order}">
        <div style="background-color: #f9fafb; border: 1px solid var(--border-color); border-radius: var(--radius); padding: 1.5rem; text-align: left; margin-bottom: 2rem;">
            <div style="display: flex; justify-content: space-between; margin-bottom: 0.75rem;">
                <span style="color: var(--text-muted);">Order Number:</span>
                <strong style="color: var(--text-main);">#ORD-<c:out value="${order.id}"/></strong>
            </div>
            <div style="display: flex; justify-content: space-between; margin-bottom: 0.75rem;">
                <span style="color: var(--text-muted);">Order Status:</span>
                <span style="background: #dcfce7; color: #166534; font-weight: 700; font-size: 0.8rem; padding: 0.2rem 0.6rem; border-radius: 9999px;">
                    <c:out value="${order.status}"/>
                </span>
            </div>
            <div style="display: flex; justify-content: space-between; margin-bottom: 0.75rem;">
                <span style="color: var(--text-muted);">Total Amount:</span>
                <strong style="color: var(--primary); font-size: 1.1rem;">
                    $<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/>
                </strong>
            </div>

            <div style="margin-top: 1rem; padding-top: 1rem; border-top: 1px solid var(--border-color);">
                <span style="font-weight: 600; font-size: 0.9rem; color: var(--text-main); display: block; margin-bottom: 0.5rem;">Purchased Items:</span>
                <c:forEach var="item" items="${order.items}">
                    <div style="display: flex; justify-content: space-between; font-size: 0.85rem; color: var(--text-muted); margin-bottom: 0.35rem;">
                        <span><c:out value="${item.productName}"/> (×<c:out value="${item.quantity}"/>)</span>
                        <span>$<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:if>

    <div style="display: flex; gap: 1rem; justify-content: center;">
        <a href="${pageContext.request.contextPath}/orders" class="btn btn-primary">
            View All My Orders
        </a>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline">
            Continue Shopping
        </a>
    </div>
</div>

<jsp:include page="footer.jsp" />
