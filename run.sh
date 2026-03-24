mvn clean compile dependency:build-classpath -Dmdep.outputFile=cp.txt && java -XstartOnFirstThread --enable-native-access=ALL-UNNAMED -cp "target/classes:$(cat cp.txt)" com.gravitysim.Main
