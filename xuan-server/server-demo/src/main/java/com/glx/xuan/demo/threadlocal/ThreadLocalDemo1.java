package com.glx.xuan.demo.threadlocal;

import org.springframework.web.bind.annotation.RestController;


public class ThreadLocalDemo1 {

    private static final ThreadLocal<String> LOGIN_CACHE = new ThreadLocal<>();


    public static void login(String a) {
        LOGIN_CACHE.set(a);
    }


    /**
     * 获取用户(多级缓存)
     */
    public static String get() {
        String loginUser = LOGIN_CACHE.get();
        if (loginUser != null) {
            return loginUser;
        }
        return "is null";
    }


    public static void main(String[] args) {
        login("123");

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(get());
            }
        }).start();

        System.out.println(get());
    }

}
