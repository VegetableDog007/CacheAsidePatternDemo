package dao;

import container.Cache;
import model.ProductInventory;

import java.util.Map;

public class ProductInventoryCacheDAO {
    /*这里由于要模拟缓存的简单原子性直接加个synchronized*/
    public void defeatCache(int productId) {
        Map<Integer, Integer> productInventoryMap = Cache.getProductInventoryMap();
        synchronized (productInventoryMap){
            productInventoryMap.remove(productId);
        }
    }

    public ProductInventory getProductInventoryFromCache(int productId) {
        Map<Integer, Integer> productInventoryMap = Cache.getProductInventoryMap();
        Integer currentNum = productInventoryMap.get(productId);
        if(currentNum==null){
            return null;
        }
        return new ProductInventory(productId, currentNum);
    }

    public void setProductInventoryCache(ProductInventory productInventory) {
        Map<Integer, Integer> productInventoryMap = Cache.getProductInventoryMap();
        synchronized (productInventoryMap){
            productInventoryMap.put(productInventory.getProductId(), productInventory.getCurrentNum());
        }
    }

    private ProductInventoryCacheDAO(){}

    public static ProductInventoryCacheDAO getInstance(){
        return Singleton.getInstance();
    }

    private static class Singleton{
        private static ProductInventoryCacheDAO instance = new ProductInventoryCacheDAO();
        public static ProductInventoryCacheDAO getInstance(){
            return instance;
        }
    }
}
