<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="header.jsp" />

<div class="card" style="display: grid; grid-template-columns: 1fr 1fr; gap: 2.5rem; align-items: start; margin-top: 1rem;">
    <div>
        <img src="<c:out value="${not empty product.imageUrl ? product.imageUrl : 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800'}"/>"
             alt="<c:out value="${product.name}"/>"
             style="width: 100%; max-height: 420px; object-fit: cover; border-radius: var(--radius); border: 1px solid var(--border-color);"
             onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800';">
    </div>

    <div>
        <div style="font-size: 0.85rem; text-transform: uppercase; color: var(--primary); font-weight: 700; margin-bottom: 0.5rem;">
            <c:out value="${product.category}"/>
        </div>
        <h1 style="font-size: 1.8rem; font-weight: 700; margin-bottom: 1rem; color: var(--text-main);">
            <c:out value="${product.name}"/>
        </h1>

        <div style="font-size: 2rem; font-weight: 800; color: var(--primary); margin-bottom: 1rem;">
            $<fmt:formatNumber value="${product.price}" minFractionDigits="2" maxFractionDigits="2"/>
        </div>

        <div style="margin-bottom: 1.5rem;">
            <span class="product-stock ${product.stockQty > 0 ? 'in-stock' : 'out-of-stock'}" style="font-size: 0.95rem;">
                <c:choose>
                    <c:when test="${product.stockQty > 0}">● In Stock (<c:out value="${product.stockQty}"/> available)</c:when>
                    <c:otherwise>● Out of Stock</c:otherwise>
                </c:choose>
            </span>
        </div>

        <div style="margin-bottom: 2rem; font-size: 1rem; color: var(--text-muted); line-height: 1.7;">
            <c:out value="${product.description}"/>
        </div>

        <c:choose>
            <c:when test="${product.stockQty > 0}">
                <form action="${pageContext.request.contextPath}/cart/add" method="post" style="display: flex; gap: 1rem; align-items: center;">
                    <input type="hidden" name="productId" value="${product.id}">
                    <div style="display: flex; align-items: center; gap: 0.5rem;">
                        <label for="quantity" style="font-weight: 600;">Qty:</label>
                        <input type="number" id="quantity" name="quantity" value="1" min="1" max="${product.stockQty}"
                               class="form-control" style="width: 80px; text-align: center;">
                    </div>
                    <button type="submit" class="btn btn-primary" style="flex: 1; padding: 0.75rem 1.5rem;">
                        🛒 Add to Cart
                    </button>
                </form>
            </c:when>
            <c:otherwise>
                <button class="btn btn-outline btn-block" disabled style="opacity: 0.6; cursor: not-allowed;">
                    Currently Unavailable
                </button>
            </c:otherwise>
        </c:choose>

        <div style="margin-top: 2rem; padding-top: 1.5rem; border-top: 1px solid var(--border-color); font-size: 0.85rem; color: var(--text-muted);">
            <p>✓ Verified marketplace product</p>
            <p>✓ Fast secure dispatch & mock payment confirmation</p>
            <p>✓ Free delivery on all orders</p>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
