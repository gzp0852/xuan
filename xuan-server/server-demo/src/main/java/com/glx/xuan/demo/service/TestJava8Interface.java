package com.glx.xuan.demo.service;

/**
 * java8接口新用法，创建一个单方法的接口，用lambda表达式，在其他的类中实现次接口的实现方法，好处可能就是简化代码
 * @author gaozepeng
 * @since 2022/7/29
 */

@FunctionalInterface
public interface TestJava8Interface {

    String heiHeiHei(String str);
//    String heiHeiHei2(String str);
}

class TestJava8 {

    public static TestJava8Interface testA() {
        return str -> {
            return str;
        };
    }

    public static TestJava8Interface testB() {
        return stra -> {
            return stra+"aaa";
        };
    }

    public static void main(String[] args) {
        System.out.println(testB().heiHeiHei("123"));
    }

}
