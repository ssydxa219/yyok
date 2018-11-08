![alt text](https://raw.githubusercontent.com/cloudera/hue/master/docs/images/hue_logo.png "Hue Logo")


Query. Explore. Repeat.
-----------------------

Hue is an open source Analytic Workbench for browsing, querying and visualizing data with focus on SQL and Search: [gethue.com](http://gethue.com)

It features:

   * [Editors](http://gethue.com/sql-editor/) to query with SQL and submit jobs.
   * [Dashboards](http://gethue.com/search-dashboards/) to dynamically interact and visualize data.
   * [Scheduler](http://gethue.com/scheduling/) of jobs and workflows.
   * [Browsers](http://gethue.com/browsers/) for data and a Data Catalog.


![alt text](https://raw.githubusercontent.com/cloudera/hue/master/docs/images/sql-editor.png "Hue Editor")

![alt text](https://raw.githubusercontent.com/cloudera/hue/master/docs/images/dashboard.png "Hue Dashboard")


Who is using Hue
----------------
Thousands of companies and organizations use Hue to open-up and query their data in order to make smarter decisions. Just at Cloudera, Hue is heavily used by hundreds of customers executing millions of queries daily. Hue directly ships in Cloudera, Amazon, MapR, BigTop and is compatible with the other distributions.


Getting Started
---------------
Add the development packages, build and get the development server running:
```
git clone https://github.com/cloudera/hue.git
cd hue
make apps
build/env/bin/hue runserver
```
Now Hue should be running on [http://localhost:8000](http://localhost:8000) ! The configuration in development mode is ``desktop/conf/pseudo-distributed.ini``.

Read more in the [installation documentation](http://cloudera.github.io/hue/latest/admin-manual/manual.html#installation).


Docker
------
Start Hue in a single click with the [Docker Guide](https://github.com/cloudera/hue/tree/master/tools/docker) or the
[video blog post](http://gethue.com/getting-started-with-hue-in-2-minutes-with-docker/).


Community
-----------
   * User group: http://groups.google.com/a/cloudera.org/group/hue-user
   * Jira: https://issues.cloudera.org/browse/HUE
   * Reviews: https://review.cloudera.org/dashboard/?view=to-group&group=hue (repo 'hue-rw')


License
-----------
Apache License, Version 2.0
http://www.apache.org/licenses/LICENSE-2.0

yyok(开源项目）

《yyok构建微服务架构》微服务化开发平台，具有统一授权、认证后台管理系统，其中包含具备用户管理、资源权限管理、网关API管理等多个模块，支持多业务系统并行开发，可以作为后端服务的开发脚手架。代码简洁，架构清晰，适合学习和直接项目中使用。核心技术采用Spring Boot2以及Spring Cloud (Finchley.M8)相关核心组件，前端采用vue-element-admin组件。
YYOKAY 学习教程

##《YYOKAY构建微服务架构》系列 - version:linqinghong
## YYOKAY微服务实战 : Eureka + Zuul + Feign/Ribbon + Hystrix Turbine + Spring Config + sleuth + zipkin

 yyok

	欢迎大家fork me，项目中用到的技术有：
	
	springboot 快速搭建项目
	
	eureka 服务注册（发现）中心
	
	consul 服务注册（发现）中心，consul单独开consul分支，默认eureka
	
	springcloud config/Apollo 配置中心，apollo会开单独分支，目前未做
	
	ribbon rest请求客户端负载平衡器，springboot自带
	
	feign rest请求声明性REST客户端，基于ribbon
	
	Hystrix 断路器
	
	turbine 聚合多个实例Hystrix指标流
	
	zuul 路由器和过滤器
	
	Sleuth 分布式跟踪
	
	Zipkin 结合Sleuth实现链路跟踪
	
	项目启动顺序：
	
	eureka/consul -> config -> 剩下其他的服务``
