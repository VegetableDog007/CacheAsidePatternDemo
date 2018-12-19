package controller;

import environment.AppContext;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.util.Random;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args) {
        BasicConfigurator.configure();//这里让log4j快速找到配置文件s
        AppContext.init();
        test01();
    }
    private static void test01() {
        /*测试 写->读1->读2情况*/
        // 这个测试要在ProductInventroyDBUpdateRequest的process()中失效缓存后，数据库更新前进行一个延时长过读请求轮询数据库
        // 并且简单修改一下读请求轮询换成你的时间于InventoryQueryWorker
        int pid;
        int decrNum;
        pid = 1;
        decrNum = 1;
        new Thread(new ProductInventoryQueryWorker(false, pid, decrNum)).start();
        pid = 1;
        new Thread(new ProductInventoryQueryWorker(pid)).start();
        try{
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        pid = 1;
        new Thread(new ProductInventoryQueryWorker(pid)).start();
    }

    private static void test02() {
        int pid = 1;
        int decrNum = 0;
        Random rand = new Random(5);
        for (int i = 0; i < 10; i++) {
            if ((i & 1) == 0) {
                new Thread(new ProductInventoryQueryWorker(pid)).start();
            } else {
                decrNum = 1 + rand.nextInt();
                new Thread(new ProductInventoryQueryWorker(false, pid, decrNum)).start();
            }
        }
    }
}
