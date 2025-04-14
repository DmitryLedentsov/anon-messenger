# switch spring.cloud.refresh.enabled to false
cd messenger
mvnd -Pnative package
cd out
rm -rf application
java -Djarmode=tools -jar messenger-0.0.1.jar extract --destination application
cd application
java -XX:ArchiveClassesAtExit=application.jsa -Dspring.context.exit=onRefresh -Dspring.aot.enabled=true -Djava.util.concurrent.ForkJoinPool.common.parallelism=1000 -jar messenger-0.0.1.jar
java -XX:SharedArchiveFile=application.jsa -Dspring.aot.enabled=true -Djava.util.concurrent.ForkJoinPool.common.parallelism=1000 -jar messenger-0.0.1.jar