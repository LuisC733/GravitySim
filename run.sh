mvn clean compile dependency:build-classpath -Dmdep.outputFile=cp.txt && java -XstartOnFirstThread -cp "target/classes:$(cat cp.txt)" com.gravitysim.Main
