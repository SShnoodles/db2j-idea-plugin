# 更新日志
1.2.1 Ultimate 2018.1+
* 修复char、varchar默认为0

1.2.0 Ultimate 2018.1+
* 更新到jdk11
* 添加版本选择
* 添加 Controller的Patch方法
* 移除 New Dto
* 修复一些BUG

1.1.8 Ultimate 2018.1+
* 添加 @Column length,precision,scale 属性
* 修复 @Schema description null
* 修复 Mapper 类有默认方法后被错误移除
* 移除 Record Dto类 javax.validation.* 注解
* 移除 Controller 方法注释

1.1.7 Ultimate 2018.1+
* 修复导入包错误

1.1.6 Ultimate 2018.1+
* 更改包结构

1.1.5 Ultimate 2018.1+
* 添加swagger文档注解
* 添加column不可为空注解属性

1.1.4 Ultimate 2018.1+
* 添加Oracle类型

1.1.3 Ultimate 2018.1+
* 修复Updater中Dto命名错误

1.1.2 Ultimate 2018.1+
* 修复数据类型精度转化

1.1.1 Ultimate 2018.1+
* 修复Do的类名
* 修改NewXXData -> XXNew

1.1.0 Ultimate 2019.1+
* 支持单表的重命名
* 重构一些类名，比如 XXForm -> NewXXData or XXUpdate; XXRef -> XXRecord

1.0.9 Ultimate 2019.1+
* 支持是否覆盖文件选择

1.0.8 Ultimate 2019.1+
* 修复put方法缺少参数问题
* 添加一些方法参数注释
  
1.0.7 Ultimate 2019.1+
* 支持作者名

1.0.6 Ultimate 2018.3+
* 添加package 包名生成

1.0.5 Ultimate 2018.3+
* 支持Controller模板生成
* 生成Controlelr的同时生成Ref、Criteria、Dto、Form
  
1.0.4 Ultimate 2018.3+
* 支持仓库接口生成

1.0.3 Ultimate 2018.3+
* 支持实体类生成