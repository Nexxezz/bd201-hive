# bd201-hive
## TASK 1. 
### Transfer data into HDP sandbox. Create external table based on this data.
  * download confluent-hub-client:
    * http://client.hub.confluent.io/confluent-hub-client-latest.tar.gz?_ga=2.246111364.1893535373.1577546028-1748580960.1570602644
    * tar -xvf /path/to/downloaded/tar/archive
  * move unpacked tar file to the sandbox-hdp:
    * docker cp /path/to/unpacked/tar/archive sandbox-hdp:/target/directory/
  * add confluent-hub-client to the PATH:
    * vim ~/.bash_profile -> add :path/to/conflient-connect-client/bin to the PATH variable
    * source ~/.bash_profile
  * check that confluent-hub-client is avaliable:
    * which confluent-hub
  * download confluent CLI:
    * curl -L https://cnfl.io/cli | sh -s -- -b /path-to-directory/bin
  * download HDFS 3 Sink Connector:
      * confluent-hub install confluentinc/kafka-connect-hdfs3:latest
### Create external table based on expedia data stored in HDFS.
 * read avro schema with avro-tools:
   * download jar file and get avro schema from file: 
   wget https://repo1.maven.org/maven2/org/apache/avro/avro-tools/1.9.1/avro-tools-1.9.1.jar && hadoop -jar avro-tools-1.7.7.jar getschema /PATH/TO/AVRO/FILE  > expedia.avsc
   * move expedia schema to HDFS:
    hdfs dfs -put expedia.avsc /path/to/datset
   * create external table for expedia table:
   check that hive have access to dataset folder(if not hdfs dfs -chmod 777 /path/to/dataset/folder)
    CREATE EXTERNAL TABLE expedia /
    ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' / 
    STORED AS INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat /
    OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat' /
    LOCATION 'hdfs://sandbox-hdp.hortonworks.com:8020/tmp/dataset/expedia/' /
    TBLPROPERTIES('avro.schema.url'='hdfs://sandbox-hdp.hortonworks.com/tmp/dataset/expedia.avsc');
    * check that table have data:
    select * from expedia limit 20;
    * create exteral table for hotel_weater dataset:
    CREATE EXTERNAL TABLE hotels_weather ( double,lat double,avg_tmpr_f double,avg_tmpr_c double,wthr_date string) STORED BY 'org.apache.hadoop.hive.kafka.KafkaStorageHandler' TBLPROPERTIES ("kafka.topic" = "weather-topic", "kafka.bootstrap.servers" = "sandbox-hdp.hortonworks.com:6667");
    CREATE EXTERNAL TABLE hotels_weather (hotel_id double,hotel_name string,avg_tmpr_f double,avg_tmpr_c double,wthr_date string) STORED AS PARQUET LOCATION '/tmp/dataset/join_result/';
    * join tables:
    select * from expedia, hotels_weather where expedia.hotel_id = hotels_weather.hotel_id;
    
    
    
    
    
    
