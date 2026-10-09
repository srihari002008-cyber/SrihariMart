<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="header.jsp" />

<section class="hero-section">
    <h1>Welcome to SrihariMart</h1>
    <p>
        Explore top electronics, fashion, tech books, and home essentials from verified sellers. Fast checkout and premium quality guaranteed.
    </p>
</section>

<!-- Category Filtering Bar -->
<div class="category-bar">
    <a href="${pageContext.request.contextPath}/products" class="cat-pill ${empty selectedCategory ? 'active' : ''}">
        All Products
    </a>
    <c:forEach var="cat" items="${categories}">
        <a href="${pageContext.request.contextPath}/products?category=<c:out value="${cat}"/>"
           class="cat-pill ${selectedCategory == cat ? 'active' : ''}">
            <c:out value="${cat}"/>
        </a>
    </c:forEach>
</div>

<c:if test="${not empty keyword}">
    <p style="margin-bottom: 1.25rem; font-size: 0.95rem; color: var(--text-muted);">
        Showing search results for: <strong>"<c:out value="${keyword}"/>"</strong>
        <a href="${pageContext.request.contextPath}/products" style="margin-left: 0.75rem; color: var(--primary);">Clear Search</a>
    </p>
</c:if>

<div class="product-grid">
    <c:choose>
        <c:when test="${not empty products}">
            <c:forEach var="p" items="${products}">
                <div class="product-card">
                    <img src="<c:out value="${not empty p.imageUrl ? p.imageUrl : 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500'}"/>"
                         alt="<c:out value="${p.name}"/>"
                         class="product-img"
                         onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500';">
                    <div class="product-body">
                        <span class="product-cat"><c:out value="${p.category}"/></span>
                        <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}" class="product-title">
                            <c:out value="${p.name}"/>
                        </a>
                        <p class="product-desc"><c:out value="${p.description}"/></p>

                        <div class="product-meta">
                            <div>
                                <div class="product-price">$<fmt:formatNumber value="${p.price}" minFractionDigits="2" maxFractionDigits="2"/></div>
                                <span class="product-stock ${p.stockQty > 0 ? 'in-stock' : 'out-of-stock'}">
                                    <c:choose>
                                        <c:when test="${p.stockQty > 0}">In Stock (<c:out value="${p.stockQty}"/>)</c:when>
                                        <c:otherwise>Out of Stock</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>

                            <c:if test="${p.stockQty > 0}">
                                <form action="${pageContext.request.contextPath}/cart/add" method="post">
                                    <input type="hidden" name="productId" value="${p.id}">
                                    <input type="hidden" name="quantity" value="1">
                                    <input type="hidden" name="redirect" value="<c:out value="${requestScope['javax.servlet.forward.request_uri']}"/>${not empty pageContext.request.queryString ? '?' : ''}${pageContext.request.queryString}">
                                    <button type="submit" class="btn btn-primary btn-sm">Add to Cart</button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div style="grid-column: 1 / -1; text-align: center; padding: 4rem 1rem; background: #ffffff; border-radius: var(--radius);">
                <h3 style="color: var(--text-muted); margin-bottom: 0.5rem;">No products found</h3>
                <p style="color: var(--text-muted); margin-bottom: 1rem;">Try different search terms or explore all categories.</p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-outline">Browse All Products</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="footer.jsp" />
