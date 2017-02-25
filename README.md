# CodeCheck
使用编译期注解处理器进行代码检查的示例

目前提供了两个注解Util和Singleton。

## Util注解
使用在工具类上，以便在编译时检查工具类是否符合以下规范：
1. 工具类必须是类类型（不能是接口、枚举等类型），不能是抽象类，不能是内部类。
2. 工具类必须有且仅有一个私有的构造方法。
3. 工具类不能包含任何非静态成员方法。
4. 工具类不能包含任何非静态成员变量。

使用及错误提示示例如下：

```java
@Util
public class ActivityUtils {

    public ActivityUtils() {
    }

}
```
---
```java
@Util
public class ActivityUtils {
}
```
报错：构造方法未声明成private
![构造方法未声明成private](images/util_constructor_not_private.png "构造方法未声明成private")

```java
@Util
public class ActivityUtils {

    private ActivityUtils() {
    }

    private ActivityUtils(Context context) {
    }
}
```
报错：声明了多个构造方法
![声明了多个构造方法](images/util_contains_multi_constructors.png "声明了多个构造方法")

```java
@Util
public class ActivityUtils {

    private ActivityUtils() {
    }

    public void nonStaticMethod() {
    }
}
```
报错：包含了非静态方法nonStaticMethod
![包含了非静态方法](images/util_contains_nonstatic_method.png "包含了非静态方法")

```java
@Util
public class ActivityUtils {

    private Object nonStaticField;

    private ActivityUtils() {
    }
}
```
报错：包含了非静态变量nonStaticField
![包含了非静态变量](images/util_contains_nonstatic_field.png "包含了非静态变量")

## Singleton注解
使用在单例类上，以便在编译时检查单例类是否符合以下规范：
1. 单例类必须是类类型（不能是接口、枚举等类型），不能是抽象类。
2. 单例类必须将所有的构造方法声明成私有的。
3. 单例类必须声明有获取单例类实例的静态方法。

使用及错误提示示例如下：

```java
@Singleton
public class ActivityManager {

    private static ActivityManager sInstance;

    public ActivityManager() {
    }

    public static ActivityManager getInstance() {
        if (sInstance == null) {
            sInstance = new ActivityManager();
        }
        return sInstance;
    }
}
```
报错：构造方法未声明成private
![构造方法未声明成private](images/singleton_constructor_not_private.png "构造方法未声明成private")

```java
@Singleton
public class ActivityManager {

    private static ActivityManager sInstance;

    private ActivityManager() {
    }
}
```
报错：没有声明静态getInstance方法
![未声明静态getInstance方法](images/singleton_getter_not_static.png "未声明静态getInstance方法")
