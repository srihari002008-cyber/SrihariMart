<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 700; margin-bottom: 1.5rem;">Checkout & Mock Payment</h1>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">
        <c:out value="${errorMessage}"/>
    </div>
</c:if>

<div class="cart-layout">
    <div>
        <form action="${pageContext.request.contextPath}/checkout/place" method="post" class="card">
            <h2 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1.25rem;">1. Shipping Information</h2>

            <div class="form-group">
                <label for="shippingAddress">Full Delivery Address</label>
                <textarea id="shippingAddress" name="shippingAddress" rows="3" class="form-control" required
                          placeholder="House / Apt #, Street, City, State, ZIP Code"><c:out value="${shippingAddress != null ? shippingAddress : '124 Anna Salai, Guindy, Chennai, Tamil Nadu - 600025'}"/></textarea>
            </div>

            <h2 style="font-size: 1.25rem; font-weight: 700; margin-top: 2rem; margin-bottom: 1.25rem;">2. Mock Payment Method</h2>
            <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 1rem;">
                (Per Project Specification: No real money is charged. Select any mock provider to confirm your order simulation)
            </p>

            <div style="display: flex; flex-direction: column; gap: 0.75rem; margin-bottom: 1.5rem;">
                <label style="display: flex; align-items: center; gap: 0.75rem; padding: 0.85rem; border: 1px solid var(--border-color); border-radius: var(--radius); cursor: pointer;">
                    <input type="radio" name="paymentMethod" value="MOCK_UPI" checked>
                    <div>
                        <strong>Mock UPI Simulation</strong>
                        <div style="font-size: 0.8rem; color: var(--text-muted);">Instant simulated verification (Google Pay / PhonePe mock)</div>
                    </div>
                </label>

                <label style="display: flex; align-items: center; gap: 0.75rem; padding: 0.85rem; border: 1px solid var(--border-color); border-radius: var(--radius); cursor: pointer;">
                    <input type="radio" name="paymentMethod" value="MOCK_CARD">
                    <div>
                        <strong>Mock Credit / Debit Card</strong>
                        <div style="font-size: 0.8rem; color: var(--text-muted);">Simulated Visa / Mastercard tokenized approval</div>
                    </div>
                </label>

                <label style="display: flex; align-items: center; gap: 0.75rem; padding: 0.85rem; border: 1px solid var(--border-color); border-radius: var(--radius); cursor: pointer;">
                    <input type="radio" name="paymentMethod" value="MOCK_COD">
                    <div>
                        <strong>Mock Cash on Delivery (COD)</strong>
                        <div style="font-size: 0.8rem; color: var(--text-muted);">Simulated pay on physical delivery</div>
                    </div>
                </label>
            </div>

            <button type="submit" class="btn btn-accent btn-block" style="padding: 0.85rem; font-size: 1rem;">
                ✓ Confirm Order & Complete Mock Payment
            </button>
        </form>
    </div>

    <!-- Order Review Side Panel -->
    <div>
        <div class="card">
            <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem;">Items in Order (<c:out value="${cart.totalItems}"/>)</h3>

            <div style="max-height: 300px; overflow-y: auto; margin-bottom: 1rem;">
                <c:forEach var="item" items="${cart.items}">
                    <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.6rem 0; border-bottom: 1px solid #f3f4f6;">
                        <div style="flex: 1; margin-right: 0.5rem;">
                            <div style="font-size: 0.9rem; font-weight: 600;"><c:out value="${item.productName}"/></div>
                            <div style="font-size: 0.8rem; color: var(--text-muted);">Qty: <c:out value="${item.quantity}"/> × $<fmt:formatNumber value="${item.price}" minFractionDigits="2" maxFractionDigits="2"/></div>
                        </div>
                        <div style="font-weight: 700; color: var(--text-main);">
                            $<fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2"/>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem; font-size: 0.95rem;">
                <span style="color: var(--text-muted);">Subtotal:</span>
                <span>$<fmt:formatNumber value="${cart.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></span>
            </div>

            <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem; font-size: 0.95rem;">
                <span style="color: var(--text-muted);">Shipping:</span>
                <span style="color: var(--accent); font-weight: 600;">FREE</span>
            </div>

            <div style="display: flex; justify-content: space-between; padding-top: 1rem; margin-top: 1rem; border-top: 1px solid var(--border-color); font-size: 1.25rem; font-weight: 800;">
                <span>Total Due:</span>
                <span style="color: var(--primary);">
                    $<fmt:formatNumber value="${cart.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/>
                </span>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
