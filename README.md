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
```CREATE TABLE  hotels_weather_abs_temp_diff AS SELECT hotelid, hotelname, month(weatherdate) AS wthr_month, MAX(averagetemperaturecelsius)-MIN(averagetemperaturecelsius) AS c_diff, MAX(averagetemperaturefahrenheit)-MIN(averagetemperaturefahrenheit) AS f_diff FROM hotels_weather_expedia GROUP BY month(weatherdate), hotelid, hotelname;```    
``` WITH top_hotels AS (SELECT wthr_month, hotelname, c_diff, f_diff, ROW_NUMBER() OVER (PARTITION BY wthr_month ORDER BY c_diff DESC) AS rank FROM hotels_weather_abs_temp_diff) SELECT DISTINCT wthr_month, hotelname, c_diff, f_diff, rank FROM top_hotels WHERE rank<=10 ORDER BY wthr_month DESC, rank ASC;```
#### Top 10 busy (e.g. with the biggest visits count) hotels for each month. If visit dates refer to several months it should be counted for all affected months:
```CREATE TABLE hotel_residents_month(id bigint, hotel_id bigint, month_of_stay string);```
```CREATE TABLE hotel_residents_month(ID bigint, hotel_id bigint, month_of_stay string);
   ADD JAR /home/201bd/jars/bd201-m05-hive-1.0-SNAPSHOT.jar
   CREATE TEMPORARY FUNCTION dateexplode AS "app.DateUDTF";
   INSERT INTO TABLE hotel_residents_month SELECT e.id, e.hotel_id, l.month_of_stay FROM expedia AS e lateral view dateexplode(substr(e.srch_ci,1,7),substr(e.srch_co,1,7)) l AS month_of_stay WHERE e.srch_co>e.srch_ci;```  
#### For visits with extended stay (more than 7 days) calculate weather trend (the day temperature difference between last and first day of stay) and average temperature during stay:  
``` WITH avg_temp_stay AS (SELECT hotelname, hotelid, id, srch_ci, srch_co, AVG(averagetemperaturecelsius), AVG(averagetemperaturefahrenheit) AS temp_average FROM hotels_weather_expedia WHERE datediff(srch_co,srch_ci)>=7 GROUP BY hotelname, hotelid, id, srch_ci, srch_co) SELECT *, w1.averagetemperaturecelsius-w2.averagetemperaturecelsius AS temp_diff_c, w1.averagetemperaturefahrenheit-w2.averagetemperaturefahrenheit AS temp_diff_f FROM avg_temp_stay AS hwe INNER JOIN hotels_weather_expedia AS w1 ON hwe.id=w1.id AND hwe.hotelid=w1.hotelid AND hwe.hotelname=w1.hotelname AND hwe.srch_ci=w1.weatherdate INNER JOIN hotels_weather_expedia AS w2 ON hwe.id=w2.id AND hwe.hotelid=w2.hotelid AND hwe.hotelname=w2.hotelname AND hwe.srch_co=w2.weatherdate;```
