<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="SrihariMart - The Premium Multi-Seller Marketplace for exclusive products, fashion, and electronics.">
    <title><c:out value="${pageTitle != null ? pageTitle : 'SrihariMart — Premium Marketplace'}"/></title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Outfit:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Phosphor Icons -->
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body>
<header class="navbar">
    <div class="nav-container">
        <a href="${pageContext.request.contextPath}/products" class="nav-brand">
            🛒 SrihariMart
        </a>

        <form action="${pageContext.request.contextPath}/products" method="get" class="nav-search">
            <input type="text" name="keyword" placeholder="Search products, brands, essentials..." value="<c:out value="${param.keyword}"/>">
            <button type="submit">Search</button>
        </form>

        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/products" class="nav-link">Shop</a></li>

            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <c:if test="${sessionScope.currentUser.role == 'BUYER'}">
                        <li>
                            <a href="${pageContext.request.contextPath}/cart" class="nav-link">
                                🛒 Cart
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/orders" class="nav-link">
                                📦 My Orders
                            </a>
                        </li>
                    </c:if>

                    <c:if test="${sessionScope.currentUser.role == 'SELLER' || sessionScope.currentUser.role == 'ADMIN'}">
                        <li>
                            <a href="${pageContext.request.contextPath}/seller/dashboard" class="nav-link">
                                📊 Seller Dashboard
                            </a>
                        </li>
                    </c:if>

                    <li>
                        <span class="user-badge">
                            👤 <c:out value="${sessionScope.currentUser.name}"/> (<c:out value="${sessionScope.currentUser.role}"/>)
                        </span>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/logout" class="nav-link" style="color: var(--danger);">Logout</a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li><a href="${pageContext.request.contextPath}/login" class="nav-link">Login</a></li>
                    <li><a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Register</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</header>

<main class="container">
    <c:if test="${not empty sessionScope.cartSuccessMessage}">
        <div class="alert alert-success">
            <c:out value="${sessionScope.cartSuccessMessage}"/>
        </div>
        <c:remove var="cartSuccessMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.cartErrorMessage}">
        <div class="alert alert-danger">
            <c:out value="${sessionScope.cartErrorMessage}"/>
        </div>
        <c:remove var="cartErrorMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.sellerSuccess}">
        <div class="alert alert-success">
            <c:out value="${sessionScope.sellerSuccess}"/>
        </div>
        <c:remove var="sellerSuccess" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.sellerError}">
        <div class="alert alert-danger">
            <c:out value="${sessionScope.sellerError}"/>
        </div>
        <c:remove var="sellerError" scope="session"/>
    </c:if>
