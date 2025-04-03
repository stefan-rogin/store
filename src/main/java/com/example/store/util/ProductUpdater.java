package com.example.store.util;

import com.example.store.model.Product;
import com.example.store.model.Price;

/**
 * Utillity class that is in support for product operations
 */
public class ProductUpdater {

    public static Product prepareUpdate(Product target, Product update) {
        target.setName(update.getName());
        target.setPrice(update.getPrice());
        return target;
    }

    public static Product preparePatchPrice(Product target, Price newPrice) {
        target.setPrice(newPrice);
        return target;
    }

    public static Product preparePatchName(Product product, Product productWithNewName) {
        product.setName(productWithNewName.getName());
        return product;
    }
}