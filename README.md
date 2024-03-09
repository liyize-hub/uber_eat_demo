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

# Day04 菜品管理业务开发

1. 文件上传下载

   - 上传：form 表单：
     表单属性 取值 说明
     method post 必须选择 post 方式提交
     enctype multipart/form-data 采用 multipart 格式上传文件
     type file 使用 input 的 file 控件上传

   使用的是 Apache 的两个组件：commons-fileupload,commons-io
   文件上传的解析器: CommonsMultipartResolver
   Spring 框架在 spring-web 包中对文件上传进行了封装，大大简化了服务端代码，我们只需要在 Controller 的方法中声明一个 MultipartFile 类型的参数即可接收上传的文件

   - 下载：在浏览器中打开（服务端将文件以输出流的形式写回浏览器）；以附件的形式下载
     post 127.0.0.1:8081/common/upload
     get 127.0.0.1:8081/common/download

2. 菜品新增
   post 127.0.0.1:8081/dish
   自定义一个实体类，然后继承自 Dish，并对 Dish 的属性进行拓展，增加 flavors 集合属性

3. 菜品分页查询
   get 127.0.0.1:8081/dish/page
   需要查 category 表导入 category 的 Name

4. 菜品修改
   get 127.0.0.1:8081/dish/${id}
   put 127.0.0.1:8081/dish

5. 菜品禁用启用
   post 127.0.0.1:8081/dish/status/${params.status}

6. 菜品删除
   批量删除+需要删除 dish_flavor 中的相关字段
   delete 127.0.0.1:8081/dish

# Day 05 套餐管理业务开发

1. 新增套餐
   根据菜品分类 ID,查询菜品列表
   get 127.0.0.1:8081/dish/list?categoryId = xxx
   保存套餐信息
   post 127.0.0.1:8081/setmeal

2. 套餐信息分页查询
   get 127.0.0.1:8081/setmeal/page
   需要查 category 表导入 category 的 Name

3. 删除套餐
   delete 127.0.0.1:8081/setmeal

4. 短信发送
   post 127.0.0.1:8081/user/setMsg

5. 手机验证码登录
   post 127.0.0.1:8081/user/login

# Day 06 菜品展示/购物车/下单

1. 用户地址簿功能

2. 菜品展示

3. 购物车

4. 下单

# 优化篇

# Day 01 缓存优化

## Spring Data Redis

封装了 Jedis,序列化和反序列化

1. 缓存短信验证码  
   验证码都是需要设置过期时间的，存在 Redis 中

2. 缓存菜品信息
   在高并发的情况下，频繁查询 数据库会导致系统性能下降，服务端响应时间增长。

## Spring Cache

Spring Cache 是一个框架，实现了基于注解的缓存功能 ，只需要简单地加一个注解，就能实现缓存功能，大大简化我们在业务中操作缓存的代码。
Spring Cache 只是提供了一层抽象，底层可以切换不同的 cache 实现。具体就是通过 CacheManager 接口 来统一不同的缓存技术。CacheManager 是 Spring 提供的各种缓存技术抽象接口。

｜ CacheManager ｜ 描述 ｜
| ------- | ------- ｜
｜ EhCacheCacheManager ｜ 使用 EhCache 作为缓存技术｜
｜ GuavaCacheManager ｜ 使用 Google 的 GuavaCache 作为缓存技术｜
｜ RedisCacheManager ｜ 使用 Redis 作为缓存技术｜

@EnableCaching 开启缓存注解功能
@Cacheable 在方法执行前 spring 先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；若没有数据，调用方法并将方法返回值放到缓存中
@CachePut 将方法的返回值放到缓存中
@CacheEvict 将一条或多条数据从缓存中删除

套餐数据使用 spring cache 进行保存

# Day 02 读写分离/nginx 代理

主库：事务性增删改 写
从库：查询 读

Sharding-JDBC 以 jar 包形式提供服务，增强版 JDBC 驱动
支持任何第三方数据库连接池：Druid

nginx
反向代理/负载均衡/部署静态资源

# Day 03 前后端分离开发

A. Node.js: Node.js 是一个基于 Chrome V8 引擎的 JavaScript 运行环境。(类似于 java 语言中的 JDK)。
B. Vue : 目前最火的的一个前端 javaScript 框架。
C. ElementUI: 一套为开发者、设计师和产品经理准备的基于 Vue 2.0 的桌面端组件库，通过 ElementUI 组件可以快速构建项目页面。
D. Mock: 生成随机数据，拦截 Ajax 请求，前端可以借助于 Mock 生成测试数据进行功能测试。
E. Webpack: webpack 是一个现代 JavaScript 应用程序的模块打包器(module bundler)，分析你的项目结构，找到 JavaScript 模块以及其它的一些浏览器不能直接运行的拓展语言（Sass，TypeScript 等），并将其转换和打包为合适的格式供浏览器使用。

YApi: api 管理平台
源码地址: https://github.com/YMFE/yapi
官方文档: https://hellosean1025.github.io/yapi/
要使用 YApiYApi 使用教程，项目组需要自己进行部署
postman
要使用 YApi,项目组需要自己进行部署，因此也可以使用 Apifox(Api 文档，Api 调试，Api Mock，Api 自动化测试)

Swagger
https://swagger.io/

knife4j：为 Java MVC 框架集成 Swagger 生成 Api 文档的增强解决方案。

项目部署
