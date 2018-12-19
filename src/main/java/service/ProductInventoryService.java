package service;

import model.ProductInventory;

public interface ProductInventoryService {
    /**
     * 在数据库中根据商品pid减少库存数量decrNum
     * @param productId
     * @param decrNum
     */
    void updateProductInventory(int productId, int decrNum);

    /**
     * 根据pid使缓存中的数据失效
     * @param productId
     */
    void defeatCache(int productId);

    /**
     * 根据pid从数据库查询商品库存
     * @param productId
     * @return
     */
    ProductInventory getProductInventroyFromDB(int productId);

    /**
     * 根据pid从缓存中查询商品库存
     * @param productId
     * @return
     */
    ProductInventory getProductInventoryFromCache(int productId);

    /**
     * 设置缓存中的商品库存
     * @param productInventory
     */
    void setProductInventoryCache(ProductInventory productInventory);
}
