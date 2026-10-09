<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notice — SrihariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
</head>
<body>
<header class="navbar">
    <div class="nav-container">
        <a href="${pageContext.request.contextPath}/products" class="nav-brand">
            🛒 SrihariMart
        </a>
    </div>
</header>

<main class="container">
    <div class="card" style="max-width: 550px; margin: 4rem auto; text-align: center; padding: 3rem 2rem;">
        <div style="font-size: 3rem; margin-bottom: 1rem;">⚠️</div>
        <h1 style="font-size: 1.8rem; font-weight: 700; margin-bottom: 0.5rem; color: var(--text-main);">
            <c:choose>
                <c:when test="${pageContext.errorData.statusCode == 404}">Page Not Found (404)</c:when>
                <c:when test="${pageContext.errorData.statusCode == 403}">Access Forbidden (403)</c:when>
                <c:otherwise>Something went wrong (500)</c:otherwise>
            </c:choose>
        </h1>
        <p style="color: var(--text-muted); font-size: 0.95rem; margin-bottom: 2rem;">
            <c:choose>
                <c:when test="${pageContext.errorData.statusCode == 404}">
                    The page or resource you requested could not be located on SrihariMart.
                </c:when>
                <c:when test="${pageContext.errorData.statusCode == 403}">
                    You do not have permission to access this resource.
                </c:when>
                <c:otherwise>
                    An internal error occurred while processing your request. Our engineering team has logged this incident.
                </c:otherwise>
            </c:choose>
        </p>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
            Return to Marketplace Home
        </a>
    </div>
</main>

<footer class="footer">
    <p>&copy; 2026 SrihariMart. All rights reserved.</p>
</footer>
</body>
</html>
