
编译项目

```shell script
./mvnw clean install
```

检查代码风格

```shell script
./mvnw checkstyle:check
```

其它编译参数

```shell script
# 跳过代码风格检车
-Dcheckstyle.skip
# 跳过静态代码检查
-Dspotbugs.skip
```