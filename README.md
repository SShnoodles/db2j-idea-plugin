# Db2j idea plugin
插件功能：在idea的database工具中使用，选择表（可多张），生成相关的代码。包括: domain、repository。<br>

# Plugin Installation：
- 在idea插件系统里安装:
  - <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "better-mybatis-generator"</kbd> > <kbd>Install Plugin</kbd>
- 手动zip安装:
  - Download the [latest release](https://github.com/SShnoodles/db2j-idea-plugin/releases) and install it manually using <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>

# Using sample screenshots：
###1、添加Database
![image](images/step1.png)<br>

###2、配置数据库，显示表空间。
![image](images/step2.png)<br>
![image](images/step3.png)<br>

###3、在需要生成代码的表上右键，选择Db2j，打开预览界面。
![image](images/step4.png)<br>

###4、设置确认完成后，点击ok，开始生成代码。
![image](images/step5.png)<br>

# Reference projects
* [plugin doc](http://www.jetbrains.org/intellij/sdk/docs/tutorials/build_system/prerequisites.html)
* [better-mybatis-generator](https://github.com/kmaster/better-mybatis-generator)
