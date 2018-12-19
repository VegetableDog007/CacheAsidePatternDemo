package service.impl;

import container.RequestQueueMessageBlock;
import request.ProductInventoryCacheUpdateRequest;
import request.ProductInventoryDBUpdateRequest;
import request.Request;
import service.ProductQueryProcessService;

import java.util.Map;

public class ProductQueryProcessServiceImpl implements ProductQueryProcessService {
    /**
     * 处理进入队列中请求的逻辑
     * */
    public void process(Request request) {
        try{
            RequestQueueMessageBlock requestQueueMessageBlock = RequestQueueMessageBlock.getInstance();
            Map<Integer, Boolean> flagMap = requestQueueMessageBlock.getFlagMap();
            if(request instanceof ProductInventoryDBUpdateRequest){
                // 写请求
                // 设置表示为true
                flagMap.put(((ProductInventoryDBUpdateRequest) request).getProductId(), true);
            }
            else if(request instanceof ProductInventoryCacheUpdateRequest){
                Boolean flag = flagMap.get(((ProductInventoryCacheUpdateRequest) request).getProductId());
                if(flag==null){
                    // 该读请求来之前在队列中没有写请求
                    flagMap.put(((ProductInventoryCacheUpdateRequest) request).getProductId(), false);
                }
                else if(flag!=null && flag){
                    // 该读请求来之前紧接着有一个写请求已经在队列
                    flagMap.put(((ProductInventoryCacheUpdateRequest) request).getProductId(), false);
                }
                else if(flag!=null && !flag){
                    // 该读请求来之前已经是有一对写，读请求在队列中了，那么就绪先等前面的读请求刷新到缓存，自己不加入队列，去外面的轮询缓存的逻辑
                    return;
                }
            }
            // 当前请求需要放到内存队列中
            requestQueueMessageBlock.addRequest(request);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private ProductQueryProcessServiceImpl(){}

    public static ProductQueryProcessServiceImpl getInstance(){
        return Singleton.getInstance();
    }

    private static class Singleton{
        private static ProductQueryProcessServiceImpl instance = new ProductQueryProcessServiceImpl();
        public static ProductQueryProcessServiceImpl getInstance(){
            return instance;
        }
    }
}
