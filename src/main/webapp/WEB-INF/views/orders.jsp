<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 700; margin-bottom: 1.5rem;">My Order History</h1>

<c:choose>
    <c:when test="${not empty orders}">
        <div style="display: flex; flex-direction: column; gap: 1.5rem;">
            <c:forEach var="order" items="${orders}">
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border-color); padding-bottom: 0.75rem; margin-bottom: 1rem; flex-wrap: wrap; gap: 0.5rem;">
                        <div>
                            <span style="font-size: 0.85rem; color: var(--text-muted);">ORDER #</span>
                            <strong style="color: var(--text-main); font-size: 1.05rem;">ORD-<c:out value="${order.id}"/></strong>
                            <span style="margin-left: 1rem; font-size: 0.85rem; color: var(--text-muted);">
                                Placed on <fmt:formatDate value="${order.createdAt}" pattern="MMM dd, yyyy HH:mm"/>
                            </span>
                        </div>
                        <div style="display: flex; align-items: center; gap: 1rem;">
                            <span style="background: #dcfce7; color: #166534; font-weight: 700; font-size: 0.8rem; padding: 0.25rem 0.75rem; border-radius: 9999px;">
                                <c:out value="${order.status}"/>
                            </span>
                            <strong style="font-size: 1.15rem; color: var(--primary);">
                                $<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/>
                            </strong>
                        </div>
                    </div>

                    <div>
                        <c:forEach var="item" items="${order.items}">
                            <div style="display: flex; align-items: center; gap: 1rem; padding: 0.5rem 0;">
                                <img src="<c:out value="${not empty item.productImageUrl ? item.productImageUrl : 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100'}"/>"
                                     alt="<c:out value="${item.productName}"/>"
                                     style="width: 45px; height: 45px; object-fit: cover; border-radius: 4px; border: 1px solid var(--border-color);"
                                     onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100';">
                                <div style="flex: 1;">
                                    <div style="font-weight: 600; font-size: 0.95rem;"><c:out value="${item.productName}"/></div>
                                    <div style="font-size: 0.8rem; color: var(--text-muted);">
                                        Quantity: <c:out value="${item.quantity}"/> × $<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2"/>
                                    </div>
                                </div>
                                <div style="font-weight: 600; color: var(--text-main);">
                                    $<fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2"/>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card" style="text-align: center; padding: 4rem 1.5rem;">
            <div style="font-size: 3.5rem; margin-bottom: 1rem;">📦</div>
            <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem;">No Orders Yet</h2>
            <p style="color: var(--text-muted); margin-bottom: 1.5rem;">
                You haven't placed any orders yet. Check out our catalog to get started!
            </p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                Browse Products
            </a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="footer.jsp" />
