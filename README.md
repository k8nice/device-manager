### 打包命令
mvn clean package -Dmaven.test.skip=true

### 运行命令
java -jar -Dloader.path=resources,lib device-server.jar &

### 查看任务命令
ps -ef|grep java

kill -9 xxx
