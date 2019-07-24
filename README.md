# tzyrequest [![](https://www.jitpack.io/v/Jeff20172020/tzyrequest.svg)](https://www.jitpack.io/#Jeff20172020/tzyrequest)
对当前公司网络请求库优化版本

公司当前的网络请求库，目前为止发现两个问题：
1. okhttpclient 的timeoutseconds 被写死，无法定制
2. 加密操作必须卸载拦截器里，并且初始化添加拦截器的顺序不能乱，不便于使用

因此对该库进行部分修改：
1. 创建okhttpclient 时传入request 便于判断超时时间是否同默认一致，如一致使用同一的client，不一致则新建client.
2. Okhttpmanager 添加EncryptPlicy 接口，加密操作在对请求添加参数时才进行。

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.Jeff20172020:tzyrequest:Tag'
	}
