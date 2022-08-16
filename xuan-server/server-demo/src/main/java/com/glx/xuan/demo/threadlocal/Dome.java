package com.glx.xuan.demo.threadlocal;

import lombok.Data;

@Data
public class Dome{


	//这个方法判断是否是Perse的实例，用instanceof判断。
	void f(Object o) {//Object可以接收任何的类型。
		if (o instanceof Student)
			System.out.println("你输入的对象是Perse的实例");
		else
			System.out.println("你输入的对象不是Perse的实例");
	}

}


class Perse extends Object{}
class Student extends Perse{}
class A extends Student{}
class B extends A {}

class Dome1{
	public static void main(String[] args){
		Dome dome = new Dome();
		Perse perse =new Perse();
		Student student =new Student();
		Object object = new Object();
		A a = new A();
		B b = new B();
		System.out.println(a  instanceof B);
		dome.f(perse);
		//结果：
		//输入的是student和perse那么instanceof 判断就会是true，if也就执行打印语句System.out.println("你输入的对象是Perse的实例");
		//那么输入object自然instanceof判断就是false。
		// 原因我们开头已经说的很清楚啦。
		//instanceof关键字的作用是判断左边对象是否是右边对象的实例(通俗易懂的说就是：子类，或者右边类本身的对象)
	}
}