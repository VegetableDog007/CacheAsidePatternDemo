package environment;

import container.Database;
import thread.pool.ProductInventoryRequestProcessorPool;

public class AppContext {
    public static void init(){
        //初始化这个内存线程池
        ProductInventoryRequestProcessorPool.getInstance();
        //初始化数据库
        Database.getProductInventoryMap().put(1, 100);
        //初始化缓存
        Database.getProductInventoryMap().put(1, 100);
    }
}
