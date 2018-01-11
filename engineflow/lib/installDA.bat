@echo off
set groupId=com.experian
set artifactId=DA

mvn install:install-file -Dfile=%artifactId%.jar -DgroupId=%groupId% -DartifactId=%artifactId% -Dversion=1.0 -Dpackaging=jar

@echo on