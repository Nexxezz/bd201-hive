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
