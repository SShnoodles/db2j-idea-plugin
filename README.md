# Db2j idea plugin
[中文](README_CN.md)

[Community(社区版)](https://github.com/SShnoodles/db2j-ce-idea-plugin)

Features：use in database tool, select database tables to generate java files. includes: domain、repository、controller.
Idea Version：Ultimate 2018.1+
[Plugin url](https://plugins.jetbrains.com/plugin/11965-db2j)

# Plugin Installation：
- Marketplace:
  - **Preferences** > **Plugins** > **Marketplace** > **Search for "db2j"** > **Install Plugin**
- Manual:
  - Download the [zip](https://github.com/SShnoodles/db2j-idea-plugin/releases) and install it manually using **Preferences** > **Plugins** > **Install plugin from disk...**

# Quick Start
![image](images/video.gif)

# Update log
[UPDATE_LOG](UPDATE_LOG.md)

# Using screenshots：
### 1、Add Database
![image](images/step1.png)

### 2、Config db
![image](images/step2.png)

![image](images/step3.png)

### 3、Select the tables and right-click the display menu
![image](images/step4.png)

### 4、Ok
![image](images/step5.png)

* Repository Interface: default Domain Path
* Controller:
  * {Controller Path} Controller、`DataMapper(updater)`、`Updater(updater)`
  * {Controller Path}/data `Ref、Form、Dto、Criteria`
* Overwrite Files: default false
* Template: `Jpa、Dto、Common`
  * Jpa `javax.persistence`
  * Dto `public`
  * Common `private`
* Domain Path:
  * Single Module：`src/main/java/cc/ssnoodles/demo/domain`
  * Multiple Modules：`base/src/main/java/cc/ssnoodles/demo/base/domain`
* Controller Path:
  * Single Module：`src/main/java/cc/ssnoodles/demo/api`
  * Multiple Modules：`base/src/main/java/cc/ssnoodles/demo/base/api`
* Single rename: Single table support rename class and interface, default table hump name.

# Introduced projects
* [db2j](https://github.com/SShnoodles/database2javafiles)
* [lombok](https://www.projectlombok.org)

# Reference projects
* [plugin doc](http://www.jetbrains.org/intellij/sdk/docs/tutorials/build_system/prerequisites.html)
* [better-mybatis-generator](https://github.com/kmaster/better-mybatis-generator)

# Development
gradle.properties

ideaSDKlocalPath=

# other
[PredicateUtils](code.md)

