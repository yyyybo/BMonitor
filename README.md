# BMonitor 是一个结合grafana的埋点监控客户端
# 接入步骤
* **配置文件**
    * 增加bmonitor.properties配置文件。
    * > appCode=application-center // 接入的项目名
        envCode=beta // 环境(beta,prod线上)
        enableMonitor=true // 测试环境默认关闭,只有prod环境默认开启
    * 这里只区分beta和prod环境,因为它仅仅代表测试环境上传不上传埋点信息。如果有多套测试环境统一beta就OK，线上只有一套prod默认上传埋点数据。

  