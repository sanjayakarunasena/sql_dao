# sql_dao

sql_dao is a simple DAO abstraction to avoid boilerplate code and better exception handling while maintaining the power of SQL.

### Features
  - Easy implementation of basic CRUD operations by extending the _AbstractDAO_ class and implementing the method _getClazz()_.
  - SQL required for each method is maintained with the method using annotations making the DAO logic cohesive.
  - Complete flexibility in parameter and result mapping from Java types to SQL and SQL types to Java.
  - Ability to handle IN clauses.
  - Ability to handle basic transactions within the application logic using the _Transaction_ class.
  - Retry _SQLTransientException_ enabling transparent failover in certain cluster configurations. Defualt retry count (3) can be modified by the developer within the DAO logic as required.

### Installation

sql_dao requires Java 8 or higher. You may be able to get it going in Java 7 but I have not tested it. To build the software maven is required.

Download the source from [here](https://github.com/sanjayakarunasena/sql_dao/releases) or you may download the latest code from this [link](https://github.com/sanjayakarunasena/sql_dao/archive/master.zip).

```sh
$ unzip sql_dao-0.4.1-rc1.zip
$ cd sql_dao-0.4.1-rc1
$ mvn install
```

Include the following dependency in your project's maven pom file.
```xml
<dependency>
  <groupId>tech.sqldao</groupId>
  <artifactId>sql_dao</artifactId>
  <version>0.4.1-RC1</version>
</dependency>
```
### Todos

 - Write more tests and sample usage code
 - Improve documentation

License
----

Apache 2.0


__Free and Open Source Software, Yap!__

