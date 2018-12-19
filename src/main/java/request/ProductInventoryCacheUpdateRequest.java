package request;

import org.apache.log4j.Logger;
import service.ProductInventoryService;
import model.ProductInventory;
import service.impl.ProductInventoryServiceImpl;

public class ProductInventoryCacheUpdateRequest implements Request{

    private static Logger logger = Logger.getLogger(ProductInventoryCacheUpdateRequest.class);
    private int productId;

    public ProductInventoryCacheUpdateRequest(int productId){
        this.productId = productId;
    }

    public void process() {
        ProductInventoryService productInventoryService = ProductInventoryServiceImpl.getInstance();
        // 从数据库中查询库存数量
        ProductInventory productInventory = productInventoryService.getProductInventroyFromDB(productId);
        if(productInventory!=null){
            logger.debug("已从数据库查询到商品库存信息，商品id： "+productInventory.getProductId()+"； "+"商品库存量："+productInventory.getCurrentNum());
            // 成功把从数据库读出来的库存信息存入缓存
            productInventoryService.setProductInventoryCache(productInventory);
        }
        else{
            //该读请求针对前一个商品的写请求，是的数据库那条记录没有了
            //这种情况需要报出一个异常或者以后
            //这时候pool中的processor不需要负责放回缓存了，跳出去让外面的轮询缓存的超过延时后自动去数据库再拉去即可
            //或者这里以后需要加fastfail机制，让外面的不再轮询了
        }
    }

    public int getProductId() {
        return productId;
    }
}
