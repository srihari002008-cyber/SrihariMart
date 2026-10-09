<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 700; margin-bottom: 1.5rem;">Shopping Cart</h1>

<c:choose>
    <c:when test="${not empty cart.items}">
        <div class="cart-layout">
            <!-- Items Table -->
            <div class="card table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Price</th>
                            <th>Quantity</th>
                            <th>Subtotal</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cart.items}">
                            <tr>
                                <td style="display: flex; align-items: center; gap: 1rem;">
                                    <img src="<c:out value="${not empty item.productImageUrl ? item.productImageUrl : 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200'}"/>"
                                         alt="<c:out value="${item.productName}"/>"
                                         style="width: 55px; height: 55px; object-fit: cover; border-radius: var(--radius); border: 1px solid var(--border-color);"
                                         onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200';">
                                    <div>
                                        <a href="${pageContext.request.contextPath}/product-detail?id=${item.productId}"
                                           style="font-weight: 600; color: var(--text-main); text-decoration: none;">
                                            <c:out value="${item.productName}"/>
                                        </a>
                                        <div style="font-size: 0.8rem; color: var(--text-muted);">
                                            <c:out value="${item.productCategory}"/>
                                        </div>
                                    </div>
                                </td>
                                <td style="font-weight: 600;">
                                    $<fmt:formatNumber value="${item.price}" minFractionDigits="2" maxFractionDigits="2"/>
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/cart/update" method="post" style="display: flex; align-items: center; gap: 0.4rem;">
                                        <input type="hidden" name="cartItemId" value="${item.id}">
                                        <input type="number" name="quantity" value="${item.quantity}" min="1" max="${item.stockQty}"
                                               class="form-control" style="width: 65px; padding: 0.3rem 0.5rem; text-align: center;">
                                        <button type="submit" class="btn btn-outline btn-sm" title="Update quantity">↻</button>
                                    </form>
                                </td>
                                <td style="font-weight: 700; color: var(--primary);">
                                    $<fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2"/>
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/cart/remove" method="post">
                                        <input type="hidden" name="cartItemId" value="${item.id}">
                                        <button type="submit" class="btn btn-danger btn-sm" title="Remove item">✕</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 1rem;">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline">
                        ← Continue Shopping
                    </a>
                    <form action="${pageContext.request.contextPath}/cart/clear" method="post">
                        <button type="submit" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: #fca5a5;">
                            Clear Entire Cart
                        </button>
                    </form>
                </div>
            </div>

            <!-- Order Summary Card -->
            <div>
                <div class="card">
                    <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem;">Order Summary</h3>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 0.75rem; font-size: 0.95rem;">
                        <span style="color: var(--text-muted);">Items count:</span>
                        <strong style="color: var(--text-main);"><c:out value="${cart.totalItems}"/> items</strong>
                    </div>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 0.75rem; font-size: 0.95rem;">
                        <span style="color: var(--text-muted);">Delivery Fee:</span>
                        <strong style="color: var(--accent);">FREE</strong>
                    </div>

                    <div style="display: flex; justify-content: space-between; padding-top: 1rem; margin-top: 1rem; border-top: 1px solid var(--border-color); font-size: 1.2rem;">
                        <span style="font-weight: 700;">Running Total:</span>
                        <strong style="color: var(--primary); font-weight: 800;">
                            $<fmt:formatNumber value="${cart.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/>
                        </strong>
                    </div>

                    <div style="margin-top: 1.5rem;">
                        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary btn-block" style="padding: 0.75rem;">
                            Proceed to Checkout →
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card" style="text-align: center; padding: 4rem 1.5rem;">
            <div style="font-size: 3.5rem; margin-bottom: 1rem;">🛒</div>
            <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem;">Your Cart is Empty</h2>
            <p style="color: var(--text-muted); margin-bottom: 1.5rem;">
                Looks like you haven't added anything to your cart yet.
            </p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary" style="padding: 0.75rem 2rem;">
                Start Shopping
            </a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="footer.jsp" />
