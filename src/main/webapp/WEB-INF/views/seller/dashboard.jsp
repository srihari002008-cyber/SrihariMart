<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 700;">Seller Dashboard</h1>
        <p style="color: var(--text-muted); font-size: 0.95rem;">
            Manage your product inventory, add new listings, and update product pricing.
        </p>
    </div>
    <button class="btn btn-primary" onclick="openModal('createProductModal')">
        + Add New Product
    </button>
</div>

<!-- Seller Stats Summary -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
    <div class="card" style="border-left: 4px solid var(--primary);">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">LISTED PRODUCTS</div>
        <div style="font-size: 1.8rem; font-weight: 800; color: var(--text-main); margin-top: 0.25rem;">
            <c:out value="${products.size()}"/>
        </div>
    </div>
    <div class="card" style="border-left: 4px solid var(--accent);">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;">SELLER STATUS</div>
        <div style="font-size: 1.2rem; font-weight: 700; color: var(--accent); margin-top: 0.5rem;">
            Verified Active Seller
        </div>
    </div>
</div>

<!-- Product Listings Table -->
<div class="card table-responsive">
    <h2 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1rem;">Your Active Listings</h2>

    <c:choose>
        <c:when test="${not empty products}">
            <table class="table">
                <thead>
                    <tr>
                        <th>Product</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Stock Qty</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${products}">
                        <tr>
                            <td style="display: flex; align-items: center; gap: 1rem;">
                                <img src="<c:out value="${not empty p.imageUrl ? p.imageUrl : 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100'}"/>"
                                     alt="<c:out value="${p.name}"/>"
                                     style="width: 45px; height: 45px; object-fit: cover; border-radius: var(--radius); border: 1px solid var(--border-color);"
                                     onerror="this.src='https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100';">
                                <div>
                                    <strong style="color: var(--text-main); font-size: 0.95rem;"><c:out value="${p.name}"/></strong>
                                    <div style="font-size: 0.8rem; color: var(--text-muted); max-width: 250px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                        <c:out value="${p.description}"/>
                                    </div>
                                </div>
                            </td>
                            <td><span class="cat-pill" style="font-size: 0.8rem; padding: 0.2rem 0.6rem;"><c:out value="${p.category}"/></span></td>
                            <td style="font-weight: 700; color: var(--primary);">
                                $<fmt:formatNumber value="${p.price}" minFractionDigits="2" maxFractionDigits="2"/>
                            </td>
                            <td>
                                <span class="product-stock ${p.stockQty > 0 ? 'in-stock' : 'out-of-stock'}">
                                    <c:out value="${p.stockQty}"/> units
                                </span>
                            </td>
                            <td>
                                <div style="display: flex; gap: 0.5rem;">
                                    <button type="button" class="btn btn-outline btn-sm"
                                            onclick="editProductModal('${p.id}', '<c:out value="${p.name}"/>', '<c:out value="${p.category}"/>', '${p.price}', '${p.stockQty}', '<c:out value="${p.imageUrl}"/>', '<c:out value="${p.description}"/>')">
                                        Edit
                                    </button>
                                    <form action="${pageContext.request.contextPath}/seller/products/delete" method="post" onsubmit="return confirm('Are you sure you want to delete this listing?');">
                                        <input type="hidden" name="id" value="${p.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div style="text-align: center; padding: 3rem 1rem; color: var(--text-muted);">
                <p style="margin-bottom: 1rem;">You don't have any products listed yet.</p>
                <button class="btn btn-primary btn-sm" onclick="openModal('createProductModal')">
                    Create Your First Listing
                </button>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Modal: Create Product -->
<div id="createProductModal" style="display: none; position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 2000; align-items: center; justify-content: center; padding: 1rem;">
    <div class="card" style="max-width: 550px; width: 100%; max-height: 90vh; overflow-y: auto;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
            <h2 style="font-size: 1.3rem; font-weight: 700;">Add New Product Listing</h2>
            <button type="button" onclick="closeModal('createProductModal')" style="background: none; border: none; font-size: 1.5rem; cursor: pointer;">&times;</button>
        </div>

        <form action="${pageContext.request.contextPath}/seller/products/create" method="post">
            <div class="form-group">
                <label for="create_name">Product Title</label>
                <input type="text" id="create_name" name="name" class="form-control" required placeholder="e.g. Wireless Noise-Cancelling Headphones">
            </div>

            <div class="form-group" style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="create_category">Category</label>
                    <input type="text" id="create_category" name="category" class="form-control" required list="categoryList" placeholder="e.g. Electronics">
                    <datalist id="categoryList">
                        <option value="Electronics">
                        <option value="Fashion">
                        <option value="Books">
                        <option value="Home">
                    </datalist>
                </div>
                <div>
                    <label for="create_price">Price ($)</label>
                    <input type="number" step="0.01" id="create_price" name="price" class="form-control" required placeholder="49.99">
                </div>
            </div>

            <div class="form-group" style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="create_stock">Stock Quantity</label>
                    <input type="number" id="create_stock" name="stockQty" class="form-control" required min="0" value="10">
                </div>
                <div>
                    <label for="create_image">Image URL</label>
                    <input type="url" id="create_image" name="imageUrl" class="form-control" placeholder="https://images.unsplash.com/...">
                </div>
            </div>

            <div class="form-group">
                <label for="create_desc">Description</label>
                <textarea id="create_desc" name="description" rows="3" class="form-control" placeholder="Describe key features, specifications, and warranty"></textarea>
            </div>

            <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                <button type="button" class="btn btn-outline" onclick="closeModal('createProductModal')">Cancel</button>
                <button type="submit" class="btn btn-primary">Save Listing</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Edit Product -->
<div id="editProductModal" style="display: none; position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 2000; align-items: center; justify-content: center; padding: 1rem;">
    <div class="card" style="max-width: 550px; width: 100%; max-height: 90vh; overflow-y: auto;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
            <h2 style="font-size: 1.3rem; font-weight: 700;">Edit Product Listing</h2>
            <button type="button" onclick="closeModal('editProductModal')" style="background: none; border: none; font-size: 1.5rem; cursor: pointer;">&times;</button>
        </div>

        <form action="${pageContext.request.contextPath}/seller/products/edit" method="post">
            <input type="hidden" id="edit_id" name="id">

            <div class="form-group">
                <label for="edit_name">Product Title</label>
                <input type="text" id="edit_name" name="name" class="form-control" required>
            </div>

            <div class="form-group" style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="edit_category">Category</label>
                    <input type="text" id="edit_category" name="category" class="form-control" required>
                </div>
                <div>
                    <label for="edit_price">Price ($)</label>
                    <input type="number" step="0.01" id="edit_price" name="price" class="form-control" required>
                </div>
            </div>

            <div class="form-group" style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <label for="edit_stock">Stock Quantity</label>
                    <input type="number" id="edit_stock" name="stockQty" class="form-control" required min="0">
                </div>
                <div>
                    <label for="edit_image">Image URL</label>
                    <input type="url" id="edit_image" name="imageUrl" class="form-control">
                </div>
            </div>

            <div class="form-group">
                <label for="edit_desc">Description</label>
                <textarea id="edit_desc" name="description" rows="3" class="form-control"></textarea>
            </div>

            <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                <button type="button" class="btn btn-outline" onclick="closeModal('editProductModal')">Cancel</button>
                <button type="submit" class="btn btn-primary">Update Listing</button>
            </div>
        </form>
    </div>
</div>

<script>
function editProductModal(id, name, category, price, stock, image, desc) {
    document.getElementById('edit_id').value = id;
    document.getElementById('edit_name').value = name;
    document.getElementById('edit_category').value = category;
    document.getElementById('edit_price').value = price;
    document.getElementById('edit_stock').value = stock;
    document.getElementById('edit_image').value = image;
    document.getElementById('edit_desc').value = desc;
    openModal('editProductModal');
}
</script>

<jsp:include page="../footer.jsp" />
