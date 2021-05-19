# BMonitor 是一个结合grafana的埋点监控客户端
BMonitor 是一个结合grafana的埋点监控客户端，Data Sources采用Graphite, 此客户端打包后项目直接引包即用。

# 埋点监控客户端本地部署
* **配置文件**
    * 修改bmonitor.properties配置文件。
    * > hostname=0.0.0.1
    * hostname 修改为搭建的graphite机器IP
* **maven deploy**
    * deploy到自己的maven私服仓库

# 项目接入步骤
* **pom文件增加依赖 (示例参考-换成自己的deploy到私服的仓库依赖)**
    
    `<groupId>com.xb.common</groupId>`
      
    `<artifactId>BMonitor</artifactId>`
  
    `<version>1.0.1-SNAPSHOT</version>`


* **配置文件**
    * 增加bmonitor.properties配置文件。
    * > appCode=application-center // 接入的项目名 
      > 
      > envCode=beta // 环境
      > 
      > enableMonitor=false // 是否开启上传埋点信息
    * 这里envCode只区分beta和prod环境,因为它仅仅代表测试环境上传不上传埋点信息。如果有多套测试环境统一beta就OK，线上只有一套prod默认上传埋点数据。
    * enableMonitor参数没有特殊情况可忽略，测试环境默认关闭,只有prod线上环境默认开启
    


  