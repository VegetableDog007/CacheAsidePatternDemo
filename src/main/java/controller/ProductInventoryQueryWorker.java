package controller;

import model.ProductInventory;
import org.apache.log4j.Logger;
import request.ProductInventoryCacheUpdateRequest;
import request.ProductInventoryDBUpdateRequest;
import request.Request;
import service.ProductInventoryService;
import service.ProductQueryProcessService;
import service.impl.ProductInventoryServiceImpl;
import service.impl.ProductQueryProcessServiceImpl;

/**
 * 原理：
 * 约定以下讨论的都是对于同一个商品的读写
 * 其实要看一致性只要看一个写1->读1->读2->写2->读2 类似这样的片段即可
 * 根本上是要使得成功的写请求，后面的读请求都要读到写后的数据，采用队列的策略即可
 * 写1直接入队列，更新标识列表，失效缓存，写数据库（这里写的request.process中应该加一个超时策略，在）
 * 后面再来一个读1是写之后第一个读请求也入队列，读1从数据库中拉去数据，然后填到缓存
 * 再来一个读2这时候发现队列中已经有一个写读的情形的话那么就轮询查缓存，等读1直接放新数据到缓存再返回，如果延迟内都没有返回读2直接去拉去数据库拉去数据
 * 恶劣情况：延迟内读2没轮询出缓存数据，说明写1有问题（网络延迟，数据库问题）或者读1（网络问题，读2写缓存失败）
 * 以上恶劣情况还可以继续优化，但是现在暂时先不考虑处理这种问题
 * 读2直接去轮询缓存成功
 * 写2来更新标识列表重复上述轮回
 */
public class ProductInventoryQueryWorker implements Runnable {
    private boolean isRead = true;
    private int productId = 1;
    private int decrNum = 0;
    private static ProductInventoryService productInventoryService = ProductInventoryServiceImpl.getInstance();
    private static ProductQueryProcessService productQueryProcessService = ProductQueryProcessServiceImpl.getInstance();
    private static Logger logger = Logger.getLogger(ProductInventoryQueryWorker.class);

    public ProductInventoryQueryWorker(int productId) {
        this.productId = productId;
    }

    public ProductInventoryQueryWorker(boolean isRead, int productId, int decrNum) {
        this.isRead = isRead;
        this.productId = productId;
        this.decrNum = decrNum;
    }

    public void run() {
        Request request = null;

        if (isRead) {
            logger.debug("接收到写请求，商品id为："+productId);
            try {
                // 包装读请求
                request = new ProductInventoryCacheUpdateRequest(productId);
                // 看要不要加一个读request进入队列，让工作线程拉去
                productQueryProcessService.process(request);
                long startMoment = System.currentTimeMillis();
                long endMoment = 0L;
                long delay = 0L;
                // 如果已经有一对写读请求在队列那么就自己轮询缓存即可，做了去重
                ProductInventory productInventory = null;
                while (true) {
//                    if(delay>200){
//                        break;
//                    }
                    /**Main中测试test01轮询要长一些*/
                    if (delay > 5000) {
                        break;
                    }
                    // 去缓存读取数据
                    productInventory = productInventoryService.getProductInventoryFromCache(productId);
                    if (productInventory != null) {
                        // 包装响应对象
                        return;
                    } else {
                        Thread.sleep(20);
                        endMoment = System.currentTimeMillis();
                        delay = endMoment - startMoment;
                    }
                }
                // 延迟了200ms还是没有从缓存读出东西
                // 说明该读请求针对pid的商品的读取，前已经有一个写请求和一个读请求在队列当中了
                // 其中写请求使缓存失效了，但是更新到数据库的时间却是迟迟没完成
                // 这时候导致了该读请求直接去数据库中拉去数据
                productInventory = productInventoryService.getProductInventroyFromDB(productId);
                if (productInventory != null) {
                    // 需要更新一下缓存
                    productInventoryService.setProductInventoryCache(productInventory);
                    // 包装响应对象
                }
                // 数据库中根本没有如此数据,以后还可以在这里添加响应的别的统计逻辑，比如：预防有的而已请求大批量访问不存在的商品，在这里加一个预防策略等等
                // 包装响应对象
            } catch (Exception e) {
                // 包装响应对象
                e.printStackTrace();
            }
        } else {
            logger.debug("接收到写请求，商品id为："+productId+"； "+"需要购买的数量："+decrNum);
            try {
                // 包装写请求
                request = new ProductInventoryDBUpdateRequest(productId, decrNum);
                productQueryProcessService.process(request);
                // 包装响应对象
            } catch (Exception e) {
                // 包装响应对象
                e.printStackTrace();
            }
        }
    }
}
