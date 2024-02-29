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
