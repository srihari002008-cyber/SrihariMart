<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="header.jsp" />

<div class="card auth-card">
    <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem; text-align: center;">Join SrihariMart</h2>
    <p style="color: var(--text-muted); font-size: 0.9rem; text-align: center; margin-bottom: 1.5rem;">
        Create an account to buy products or list your goods as a verified seller.
    </p>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <div class="form-group">
            <label for="name">Full Name</label>
            <input type="text" id="name" name="name" class="form-control" required
                   value="<c:out value="${name}"/>" placeholder="Aditya Kumar">
            <c:if test="${not empty fieldErrors['name']}">
                <small style="color: var(--danger); font-size: 0.8rem;"><c:out value="${fieldErrors['name']}"/></small>
            </c:if>
        </div>

        <div class="form-group">
            <label for="email">Email Address</label>
            <input type="email" id="email" name="email" class="form-control" required
                   value="<c:out value="${email}"/>" placeholder="user@example.com">
            <c:if test="${not empty fieldErrors['email']}">
                <small style="color: var(--danger); font-size: 0.8rem;"><c:out value="${fieldErrors['email']}"/></small>
            </c:if>
        </div>

        <div class="form-group">
            <label for="password">Password (min. 6 characters)</label>
            <input type="password" id="password" name="password" class="form-control" required placeholder="••••••••">
            <c:if test="${not empty fieldErrors['password']}">
                <small style="color: var(--danger); font-size: 0.8rem;"><c:out value="${fieldErrors['password']}"/></small>
            </c:if>
        </div>

        <div class="form-group">
            <label for="role">Account Type</label>
            <select id="role" name="role" class="form-control" required>
                <option value="BUYER" ${selectedRole == 'BUYER' || empty selectedRole ? 'selected' : ''}>
                    Buyer (Browse, Add to Cart, Purchase)
                </option>
                <option value="SELLER" ${selectedRole == 'SELLER' ? 'selected' : ''}>
                    Seller (Create Listings, Manage Inventory)
                </option>
            </select>
        </div>

        <button type="submit" class="btn btn-primary btn-block" style="padding: 0.75rem;">
            Create Account
        </button>
    </form>

    <p style="margin-top: 1.5rem; text-align: center; font-size: 0.9rem; color: var(--text-muted);">
        Already have an account?
        <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); font-weight: 600;">Sign in here</a>
    </p>
</div>

<jsp:include page="footer.jsp" />
