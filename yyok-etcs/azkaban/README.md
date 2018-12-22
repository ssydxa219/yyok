Azkaban搭建 3.32.0 版本

1、mysql安装及配置

安装教程

安装完之后启动：

1)为Azkaban创建一个数据库：

#Example database creation command, although the db name doesn't need to be 'azkaban'
mysql> CREATE DATABASE jianbing_azkaban;


2) 为Azkaban创建一个数据库用户:

# Example database creation command. The user name doesn't need to be 'azkaban'
mysql> CREATE USER 'root'@'%' IDENTIFIED BY 'fdk3MaXxEM51yxQJ';



3) 为用户赋予Azkaban数据库的增删查改的权限：

# Replace db, username with the ones created by the previous steps.
mysql> GRANT SELECT,INSERT,UPDATE,DELETE ON <database>.* to '<username>'@'%' WITH GRANT OPTION;

GRANT SELECT,INSERT,UPDATE,DELETE ON jianbing_azkaban.* to 'root'@'%' WITH GRANT OPTION;

GRANT ALL ON jianbing_azkaban.* to 'root'@'%' WITH GRANT OPTION;

4) 设置mysql的信息包大小，默认的太小，改大一点(允许上传较大压缩包)：

Linux配置打开 mysql/conf/my.cnf :

[mysqld]

...

max_allowed_packet=1024M


5)重启mysql：

$ sudo /sbin/service mysqld restart


2、Azkaban Web Server安装和配置

1) 从GitHub上下载源码：

git clone https://github.com/azkaban/azkaban.git


2) build:

压缩为tar包:

./gradlew distTar


或者压缩为zip包:

./gradlew distZip


如果不是第一次building，最好先clean一下:

./gradlew clean


在编译的过程中，可能出现如下提示（可以不用管，显示 BUILD SUCCESSFUL 就行）：

注: 有关详细信息, 请使用 -Xlint:deprecation 重新编译。
注: 某些输入文件使用了未经检查或不安全的操作。
注: 有关详细信息, 请使用 -Xlint:unchecked 重新编译。


创建的.tar.gz 文件的目录：例如/azkaban-web-server/build/distributions/azkaban-web-server-3.32.0-7-gcdfb2c8.tar.gz

m


这里由于编译是在 Windows 下做的，运行在 Linux 服务器，下面会有坑，先上传到服务器

3) 解压缩

cd /opt
tar zxvf azkaban-exec-server-3.32.0-7-gcdfb2c8.tar.gz
tar zxvf azkaban-web-server-3.32.0-7-gcdfb2c8.tar.gz
tar zxvf azkaban-db-3.32.0-7-gcdfb2c8.tar.gz


4) 将azkaban sql表结构导入mysql

mysql> use azkaban;
mysql> source /opt/azkaban-db-3.32.0-7-gcdfb2c8/create-all-sql-3.32.0-7-gcdfb2c8.sql


中间有部分语句失败，是因为该 SQL 包含从 upgrade.3.20.0.to.3.22.0 版本的 SQL，由于这里是直接从 github 上下载的最新的，所以更新会失败，不用理会

5）配置keystore

azkaban-web-server-3.1.0$ keytool -keystore keystore -alias jetty -genkey -keyalg RSA
输入密钥库口令:  
密钥库口令太短 - 至少必须为 6 个字符
输入密钥库口令:  
再次输入新口令: 
您的名字与姓氏是什么?
[Unknown]:  firstName
您的组织单位名称是什么?
[Unknown]:  companyName
您的组织名称是什么?
[Unknown]:  groupName
您所在的城市或区域名称是什么?
[Unknown]:  beijing
您所在的省/市/自治区名称是什么?
[Unknown]:  beijing
该单位的双字母国家/地区代码是什么?
[Unknown]:  CN
CN= firstName, OU= companyName, O= groupName, L=beijing, ST=beijing, C=CN是否正确?[否]:  Y



以上配置完成之后会在当前目录生成一个keystore文件。以下配置会用到。

6) 配置conf/azkaban.properties：

如果Azkaban WebServer下面没有conf目录，将azkaban-solo-web 下的conf目录拷贝过来

并作以下配置：

数据库使用步骤一配置的数据库，jetty.keystore 使用上一步生成的

cat conf/azkaban.properties 

# Azkaban Personalization Settings

azkaban.name=Test

azkaban.label=My Local Azkaban

azkaban.color=#FF3601

azkaban.default.servlet.path=/index

web.resource.dir=web/

default.timezone.id=America/Los_Angeles

# Azkaban UserManager class

user.manager.class=azkaban.user.XmlUserManager

user.manager.xml.file=conf/azkaban-users.xml

# Loader for projects

executor.global.properties=conf/global.properties

azkaban.project.dir=projects

database.type=mysql

mysql.port=3306

mysql.host=localhost

mysql.database=azkaban

mysql.user=azkaban

mysql.password=azkaban

mysql.numconnections=100

# Velocity dev mode

velocity.dev.mode=false

# Azkaban Jetty server properties.

jetty.maxThreads=25

jetty.ssl.port=8443

# jetty.use.ssl=false

jetty.port=8081

jetty.keystore=keystore

jetty.password=azkaban

jetty.keypassword=azkaban

jetty.truststore=keystore

jetty.trustpassword=azkaban

jetty.excludeCipherSuites=SSL_RSA_WITH_DES_CBC_SHA,SSL_DHE_RSA_WITH_DES_CBC_SHA,SSL_DHE_DSS_WITH_DES_CBC_SHA,SSL_RSA_EXPORT_WITH_RC4_40_MD5,SSL_RSA_EXPORT_WITH_DES40_CBC_SHA,SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA,SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA,SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA,TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,TLS_DHE_DSS_WITH_AES_256_CBC_SHA256,TLS_DHE_RSA_WITH_AES_256_CBC_SHA,TLS_DHE_DSS_WITH_AES_256_CBC_SHA,TLS_DHE_RSA_WITH_AES_128_CBC_SHA256,TLS_DHE_DSS_WITH_AES_128_CBC_SHA256,TLS_DHE_RSA_WITH_AES_128_CBC_SHA,TLS_DHE_DSS_WITH_AES_128_CBC_SHA

# Azkaban Executor settings

executor.port=12321

# mail settings

mail.sender=

mail.host=

job.failure.email=

job.success.email=

lockdown.create.projects=false

cache.directory=cache

# JMX stats

jetty.connector.stats=true

executor.connector.stats=true


注意:

配置的默认时区是：default.timezone.id=America/Los_Angeles 修改为：default.timezone.id=Asia/Shanghai

7) 用户设置

进入 azkaban web 服务器 conf 目录, azkaban-users.xml ，管理用户，这里使用默认，不做修改：

<azkaban-users>
  <user groups="azkaban" password="azkaban" roles="admin" username="azkaban"/>
  <user password="metrics" roles="metrics" username="metrics"/>

  <role name="admin" permissions="ADMIN"/>
  <role name="metrics" permissions="METRICS"/>
</azkaban-users>


8) 启动azkaban web服务器

cd /opt/azkaban-web-server-3.32.0-7-gcdfb2c8/
sh bin/azkaban-web-start.sh


命令无法执行

由于是在 Windows 下编译，上传至服务器运行，需要添加执行权限

chmod a+x * 


还需要修改 azkaban-web-start.sh 的文件格式

vi azkaban-web-start.sh 
#Esc ：
set ff 
#发现格式是 Dos 格式，修改格式 Unix
#Esc ：
set ff=unix
#保存退出


重新执行


报错：Exception: java.lang.StackOverflowError thrown from the UncaughtExceptionHandler in thread “main”

解决方式：在Azkaban-web-server 的conf目录下创建文件夹conf/log4j.properties：

log4j.rootLogger=INFO,C
log4j.appender.C=org.apache.log4j.ConsoleAppender
log4j.appender.C.Target=System.err
log4j.appender.C.layout=org.apache.log4j.PatternLayout
log4j.appender.C.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n



9) 访问验证https://ip:8443/

用户名:azkaban

密码：azkaban
　３、 Azkaban Executor Server 安装和配置

进入azkaban-executor-server目录，将azkaban-web-server下的conf目录拷贝到

该目录下

1)配置executor端的azkaban.properties

mysql 使用第一步配置的数据库

# Azkaban

 default.timezone.id=America/Los_Angeles

# Azkaban JobTypes Plugins

 azkaban.jobtype.plugin.dir=plugins/jobtypes

# Loader for projects

 executor.global.properties=conf/global.properties

 azkaban.project.dir=projects

database.type=mysql

mysql.port=3306

mysql.host=localhost

mysql.database=azkaban

mysql.user=azkaban

mysql.password=azkaban

mysql.numconnections=100

# Azkaban Executor settings

executor.maxThreads=50

executor.port=12321

executor.flow.threads=30

# JMX stats

jetty.connector.stats=true

executor.connector.stats=true

# uncomment to enable inmemory stats for azkaban

#executor.metric.reports=true

#executor.metric.milisecinterval.default=60000


配置的默认时区是：default.timezone.id=America/Los_Angeles 修改为：default.timezone.id=Asia/Shanghai

2) 启动执行服务器（这里也会出现web启动出现的问题，相同处理）:

sh bin/azkaban-executor-start.sh
