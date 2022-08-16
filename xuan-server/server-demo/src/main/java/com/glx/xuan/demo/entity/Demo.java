package com.glx.xuan.demo.entity;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.Synchronized;

import java.lang.reflect.Field;

/**
 * @author gaozepeng
 * @since 2022/7/19
 */
@Data
public class Demo {
    private String a;
    private Demo1 demo1;
    private Demo2 demo2;
    private Demo3 demo3;

    @Synchronized
    public static void trans1() throws InterruptedException {
        System.out.println("1231");
        Thread.sleep(2000L);
    }

    public static void main(String[] args) throws InterruptedException {
//        Demo demo = new Demo();
//        demo.setDemo1(new Demo1());
//        demo.setDemo2(null);
//        demo.setDemo3(null);
////        demo.setDemo3(new Demo3("aaa"));
//        demo.setA("");
//
//        System.out.println(demo);
//
//        System.out.println(isAllFieldNull(demo));


            new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    for(int i=0; i<10; i++) {
                     trans1();
                        System.out.println("1");
                    }
                }
            }).start();

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                for(int i=0; i<5; i++) {
                    trans1();
                    System.out.println("@");
                }
            }
        }).start();

    }

    public static boolean isAllFieldNull(Object obj) {
        // 得到类对象
        Class clazz = (Class) obj.getClass();
        //得到属性集合
        Field[] fs = clazz.getDeclaredFields();
        boolean flag = true;
        //遍历属性
        for (Field f : fs) {
            try {
                // 设置属性是可以访问的(私有的也可以)
                f.setAccessible(true);
                // 得到此属性的值
                Object val = f.get(obj);
                // 只要有1个属性不为空,那么就不是所有的属性值都为空
                if (f.getType().getName().contains("String")) {
                    if(val != null && val != "") {
                        flag = false;
                        break;
                    }
                } else if (f.getType().getName().contains("Object")) {
                    if (ObjectUtil.isNotEmpty(val)) {
                        flag = false;
                        break;
                    }
                }

            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
        return flag;
    }
}
