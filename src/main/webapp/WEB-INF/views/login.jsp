<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="header.jsp" />

<div class="card auth-card">
    <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem; text-align: center;">Sign In to SrihariMart</h2>
    <p style="color: var(--text-muted); font-size: 0.9rem; text-align: center; margin-bottom: 1.5rem;">
        Enter your registered email and password to access your account.
    </p>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <c:if test="${param.registered == 'true'}">
        <div class="alert alert-success">
            Registration successful! Please login with your credentials.
        </div>
    </c:if>

    <c:if test="${param.loggedOut == 'true'}">
        <div class="alert alert-success">
            You have been logged out successfully.
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <input type="hidden" name="redirect" value="<c:out value="${param.redirect}"/>">

        <div class="form-group">
            <label for="email">Email Address</label>
            <input type="email" id="email" name="email" class="form-control" required
                   value="<c:out value="${email != null ? email : param.email}"/>" placeholder="user@example.com">
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" class="form-control" required placeholder="••••••••">
        </div>

        <button type="submit" class="btn btn-primary btn-block" style="padding: 0.75rem;">
            Sign In
        </button>
    </form>

    <!-- Quick Demo Accounts (Seed accounts) -->
    <div class="demo-credentials-box">
        <strong>Quick Test Accounts:</strong>
        <p style="color: var(--text-muted); margin-top: 0.25rem;">Click to pre-fill seed credentials:</p>
        <div style="margin-top: 0.5rem;">
            <button type="button" class="demo-btn" onclick="fillCredentials('buyer@sriharimart.com', 'Buyer@123')">
                Buyer Demo
            </button>
            <button type="button" class="demo-btn" onclick="fillCredentials('seller@sriharimart.com', 'Seller@123')">
                Seller Demo
            </button>
            <button type="button" class="demo-btn" onclick="fillCredentials('admin@sriharimart.com', 'Admin@123')">
                Admin Demo
            </button>
        </div>
    </div>

    <p style="margin-top: 1.5rem; text-align: center; font-size: 0.9rem; color: var(--text-muted);">
        Don't have an account yet?
        <a href="${pageContext.request.contextPath}/register" style="color: var(--primary); font-weight: 600;">Create account</a>
    </p>
</div>

<jsp:include page="footer.jsp" />
