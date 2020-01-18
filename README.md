# bd201-hive
## TASK 1. 
### Transfer data into HDP sandbox. Create external table based on this data.
Follow steps for TopicSaver3 in [README.md](https://github.com/Nexxezz/kafkastreamsapp/blob/master/README.md)
 ### Create external table based on expedia data stored in HDFS.
 * read avro schema with avro-tools:
   * download jar file and get avro schema from file:  
   ```wget https://repo1.maven.org/maven2/org/apache/avro/avro-tools/1.9.1/avro-tools-1.9.1.jar && hadoop -jar avro-tools-1.7.7.jar getschema /PATH/TO/AVRO/FILE  > expedia.avsc```
 * move expedia schema to HDFS:  
 ```hdfs dfs -put expedia.avsc /path/to/datset```
 * create external table for expedia table:
    * check that hive have access to dataset folder(if not ```hdfs dfs -chmod 777 /path/to/dataset/folder```)
      ```CREATE EXTERNAL TABLE expedia /  
      ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' /  
      STORED AS INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat' /  
      OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat' /  
      LOCATION 'hdfs://sandbox-hdp.hortonworks.com:8020/tmp/dataset/expedia/' /  
      TBLPROPERTIES('avro.schema.url'='hdfs://sandbox-hdp.hortonworks.com/tmp/dataset/expedia.avsc'); ```
  * check that table have data:  
  ```SELECT * FROM expedia LIMIT 20;```
  * create exteral table for hotel_weater dataset:  
    ```CREATE EXTERNAL TABLE hotels_weather (hotel_id double,hotel_name string,avg_tmpr_f double,avg_tmpr_c double,wthr_date string)  
    STORED AS PARQUET LOCATION '/tmp/dataset/join_result/';```
  ### Join hotel/weather table with expedia table via Hive by hotel_id (data will be multiplied by date) 
    ```CREATE TABLE hotels_weather_expedia AS SELECT * FROM hotels_weather INNER JOIN expedia ON hotels_weather.hotelId = expedia.hotel_id LIMIT 10000;```  
### Using Hive calculate:  
#### Top 10 hotels with max absolute temperature difference by month:
