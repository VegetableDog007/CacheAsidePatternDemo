package dao;

import container.Database;
import model.ProductInventory;

import java.util.Map;

public class ProductInventoryDBDAO {

    /*这里由于要模拟数据库的简单原子性直接加个synchronized*/
    public void updateProductInventory(int productId, int decrNum) {
        Map<Integer, Integer> productInventoryMap = Database.getProductInventoryMap();
        Integer currentNum = productInventoryMap.get(productId);
        if(currentNum==null || currentNum<decrNum){
            return;
        }
        synchronized (productInventoryMap){
            currentNum-=decrNum;
            productInventoryMap.put(productId, currentNum);
        }
    }

    public ProductInventory getProductInventroyFromDB(int productId) {
        Map<Integer, Integer> productInventoryMap = Database.getProductInventoryMap();
        Integer currentNum = productInventoryMap.get(productId);
        if(currentNum==null)
            return null;
        return new ProductInventory(productId, currentNum);
    }

    private ProductInventoryDBDAO(){}

    public static ProductInventoryDBDAO getInstance(){
        return Singleton.getInstance();
    }

    private static class Singleton{
        private static ProductInventoryDBDAO instance = new ProductInventoryDBDAO();
        public static ProductInventoryDBDAO getInstance(){
            return instance;
        }
    }
}
