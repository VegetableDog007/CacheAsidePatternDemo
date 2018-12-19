package service.impl;

import dao.ProductInventoryCacheDAO;
import dao.ProductInventoryDBDAO;
import model.ProductInventory;
import org.apache.log4j.Logger;
import service.ProductInventoryService;

public class ProductInventoryServiceImpl implements ProductInventoryService {

    private static Logger logger = Logger.getLogger(ProductInventoryServiceImpl.class);

    public void updateProductInventory(int productId, int decrNum) {
        ProductInventoryDBDAO.getInstance().updateProductInventory(productId, decrNum);
        logger.debug("已经从数据库中减少库存，商品id："+productId+"； "+"减少数目为："+decrNum);
    }

    public ProductInventory getProductInventroyFromDB(int productId) {
        ProductInventory productInventory = ProductInventoryDBDAO.getInstance().getProductInventroyFromDB(productId);
        if(productInventory!=null){
            logger.debug("成功从缓存获取到商品库存信息，商品id： "+productInventory.getProductId()+"； "+"库存数量： "+productInventory.getCurrentNum());
        }
        else{
            logger.debug("缓存中没有商品相关信息，商品id："+productId);
        }
        return productInventory;
    }

    public void defeatCache(int productId) {
        ProductInventoryCacheDAO.getInstance().defeatCache(productId);
        logger.debug("已经删除缓存，key："+productId);
    }

    public ProductInventory getProductInventoryFromCache(int productId) {
        ProductInventory productInventory = ProductInventoryCacheDAO.getInstance().getProductInventoryFromCache(productId);
        if(productInventory!=null){
            logger.debug("成功从缓存获取到商品库存信息，商品id： "+productInventory.getProductId()+"； "+"库存数量： "+productInventory.getCurrentNum());
        }
        else{
            logger.debug("缓存中没有商品相关信息，商品id："+productId);
        }
        return productInventory;
    }

    public void setProductInventoryCache(ProductInventory productInventory) {
        ProductInventoryCacheDAO.getInstance().setProductInventoryCache(productInventory);
        logger.debug("设置了库存信息到缓存，商品id： "+productInventory.getProductId()+"； "+"库存数量： "+productInventory.getCurrentNum());
    }

    private ProductInventoryServiceImpl(){}

    public static ProductInventoryServiceImpl getInstance(){
        return Singleton.getInstance();
    }

    private static class Singleton{
        private static ProductInventoryServiceImpl instance = new ProductInventoryServiceImpl();
        public static ProductInventoryServiceImpl getInstance(){
            return instance;
        }
    }
}
