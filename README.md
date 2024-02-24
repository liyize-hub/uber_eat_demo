# uber_eat_demo

create an app like uber eat

基于黑马的瑞吉/苍穹外卖练手项目

common --放一些通用的类  
config --配置文件  
controller --放一些控制类，用来处理前端请求  
entity --对象实体类  
mapper --操作数据库的接口类  
service --业务层代码  
"service/impl" 包是 "service" 包的一个子包，通常用于存放业务逻辑的具体实现代码。在 "service/impl" 包中，你会找到 "service" 接口的实际实现类，这些类包含了具体的业务逻辑代码。

# Day01

设置静态资源映射：WebMvcConfig

1. 后台登陆功能开发
   R 统一返回格式
   post 127.0.0.1:8081/employee/login

2. 后台退出功能开发
   post 127.0.0.1:8081/employee/logout

# Day02 员工管理功能开发

1. 完善登陆功能
   过滤不需要处理的页面，使未登录用户不能查看信息
   过滤器：LoginCheckFilter/拦截器

2. 新增员工
   全局异常处理（重复插入数据）：GlobalExceptionHandler
   post 127.0.0.1:8081/employee

3. 员工信息分页查询
   配置 MP 的分页插件：MybatisPlusConfig
   get 127.0.0.1:8081/employee/page?page=1&pageSize=10

4. 启用/禁用员工账号

JS 对 Long 型数据处理丢失精度（只能处理 16 位）；将 long 型数字转成 String：

- 通过 JasonObjectMapper 将 java 转换为 json；
- 在 WebMncConfig 中 extendMessageConverters 扩展消息转换器
  put 127.0.0.1:8081/employee

5. 编辑员工信息
   get 127.0.0.1:8081/employee/{id}
   put 127.0.0.1:8081/employee

# Day03 分类管理功能开发

1. 公共字段自动填充
   @TableField：指定自动填充的策略
   @Component MetaObjectHandler，用于处理实体类中的元对象（MetaObject）的默认值、填充值等操作
   通过 TreadLocal 获取数据: 线程的局部变量，为每个线程单独提供一份存储空间

2. 新增分类
   菜品分类，套餐分类
   post 127.0.0.1:8081/category

3. 分类信息分类查询
   get 127.0.0.1:8081/category/page

4. 删除分类
   当分类关联了菜品或者套餐时，此分类不允许删除
   delete 127.0.0.1:8081/category
   自定义业务异常 CustomExeption

5. 修改分类
   put 127.0.0.1:8081/category
