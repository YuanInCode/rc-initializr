mvn clean package -DskipTests=true && rm -rf target/*.original && rm -rf target/classes && rm -rf target/generated-sources && rm -rf target/maven-status && rm -rf target/maven-archiver
# 如需进行区域发布，根据实际使用取消注释下述两行
# cp ./ConfigProfile.json ./target
# cp ./env.config.tpl ./target