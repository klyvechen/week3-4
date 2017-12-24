package org.test.myevent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppCleanup;
import org.zkoss.zk.ui.util.WebAppInit;

public class SampleExecutorHolder {

    private static volatile ExecutorService executor;

    public static ExecutorService getExecutor() {
        return executor;
    }
    public void cleanup(WebApp wapp) throws Exception {
        if (executor != null) {
            executor.shutdown();
            System.out.println("ExecutorService shut down");
        }
    }
    
    public SampleExecutorHolder(){
    	executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        System.out.println("Initialized an ExecutorService");
        System.out.println("thread number is " + Runtime.getRuntime().availableProcessors());
    }
    

}