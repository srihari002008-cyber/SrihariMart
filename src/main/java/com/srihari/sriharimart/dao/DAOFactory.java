package com.srihari.sriharimart.dao;

import com.srihari.sriharimart.dao.impl.CartDAOImpl;
import com.srihari.sriharimart.dao.impl.OrderDAOImpl;
import com.srihari.sriharimart.dao.impl.ProductDAOImpl;
import com.srihari.sriharimart.dao.impl.UserDAOImpl;

/**
 * Factory class providing single points of instantiation for DAO implementations.
 * Satisfies the Factory design pattern requirement in Section 12.
 */
public final class DAOFactory {

    private static final UserDAO USER_DAO = new UserDAOImpl();
    private static final ProductDAO PRODUCT_DAO = new ProductDAOImpl();
    private static final CartDAO CART_DAO = new CartDAOImpl();
    private static final OrderDAO ORDER_DAO = new OrderDAOImpl();

    private DAOFactory() {
        // Prevent instantiation
    }

    /**
     * Obtains the UserDAO instance.
     *
     * @return UserDAO instance
     */
    public static UserDAO getUserDAO() {
        return USER_DAO;
    }

    /**
     * Obtains the ProductDAO instance.
     *
     * @return ProductDAO instance
     */
    public static ProductDAO getProductDAO() {
        return PRODUCT_DAO;
    }

    /**
     * Obtains the CartDAO instance.
     *
     * @return CartDAO instance
     */
    public static CartDAO getCartDAO() {
        return CART_DAO;
    }

    /**
     * Obtains the OrderDAO instance.
     *
     * @return OrderDAO instance
     */
    public static OrderDAO getOrderDAO() {
        return ORDER_DAO;
    }
}
