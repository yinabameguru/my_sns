# my_sns
我的个人社区

## vrsion1.1
### 更新内容：使用布隆过滤器进行缓存
  社区中存在这样一种情况，绝大多数的账户只是看贴以及评论，其中只有很少一部分用户会在社区中发帖，这里使用布隆过滤器对用户是否发过帖子这一状态进行缓存。项目启动时将所有用户的name通过多次hash加载到bit数组中，在查看某一用户的发帖记录时，先通过布隆过滤器经行一次判断，由于布隆过滤器不会将任何有发帖记录的用户拦截，只会错误放过极少数没有发帖记录的用户，所以这里可以极大减少对数据库的访问。同时，在对内存的占用方面，布隆过滤器比直接使用hashmap节省了很多的空间。这里以1000w个name，每个name有5位汉字，每个utf-8汉字有3个字节计算，使用hashmap需要150MB内存，使用布隆过滤器，在错误率为0.0001的情况下只占用24MB内存。

## vrsion1.0

### 发环境
JDK1.8，IDEA，Git+GitHub，springboot,myBatis，FreeMarker，MySQL，Redis,solr。

### 项目基本功能
  为用户提供社交服务的基本功能。这些功能包括：
  
  1、用户模块：为用户提供注册、激活、登陆的基本功能。用户注册使用solt加密，验证邮件激活，采用t票机制为用户提供登陆状态的保存，并实现单点登陆。
  
  2、发帖模块：为用户提供社区基本的发帖功能和评论功能，并为其添加了html标签过滤，防止向数据库写入html或js脚本，实现了前缀树对帖子和评论中存在的敏感词进行过滤。
  
  3、私信功能，为用户提供了私信功能，为每个用户提供了私信列表。
  
  4、全文搜索。
  
### 项目sns功能
  为用户提供了sns功能。这些功能包括：
  
  1、用户可以根据喜好对帖子进行踩赞。
  
  2、用户可以关注自己感兴趣的人或帖子。
  
  3、为用户提供timeline功能，向用户推送他关注用户的动态。
  
  sns功能大多基于redis实现性能高、方便实现，但是并未将数据入库，虽然开启AOF，但数据仍然存在很大的安全问题，并且对内存的需求很大。
  
### 项目的一些优化
  1、 实现一个可以自动注册任务的小型异步框架，将发邮件、站内消息通知、动态推送这些开销很大，且不需要立刻将结果返回给用户的动作，通过消息队列放在后台线程中异步执行，减少用户操作可能出现的卡顿，提升用户体验。 也可以把这些任务放到单独的服务器执行。
  
  2、优化timeline，采用推拉结合的方式，只针对近期活跃的用户进行推送，在一般情况下社区中活跃用户占总用户的比例是很小的，这样就极大减小了后台服务器的推送压力。不活跃的用户在登陆的时候，第一次请求timeline数据是采用拉的方式，并在之后一段时间享用服务器的推送服务（这里用了24小时）。

