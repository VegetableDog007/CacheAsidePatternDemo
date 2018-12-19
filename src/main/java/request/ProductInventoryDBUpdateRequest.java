package request;

import org.apache.log4j.Logger;
import service.ProductInventoryService;
import service.impl.ProductInventoryServiceImpl;

public class ProductInventoryDBUpdateRequest implements Request{
    private static Logger logger = Logger.getLogger(ProductInventoryDBUpdateRequest.class);
    private int productId;
    private int decrNum;

    public ProductInventoryDBUpdateRequest(int productId, int decrNum){
        this.productId = productId;
        this.decrNum = decrNum;
    }

    public void process() {
        ProductInventoryService productInventoryService = ProductInventoryServiceImpl.getInstance();
        logger.debug("数据库更新请求开始执行, 商品id："+productId+"；"+"需要购买的数量："+decrNum);
        // 失效缓存
        productInventoryService.defeatCache(productId);
        /**增加一个延时测试test01*/
        try{
            Thread.sleep(3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        // 更新数据库
        productInventoryService.updateProductInventory(productId, decrNum);
    }

    public int getProductId() {
        return productId;
    }

    public int getDecrNum() {
        return decrNum;
    }
}
