package app;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;

public class ExampleUTDF extends GenericUDTF {
    Integer count = Integer.valueOf(0);
    Object forwardObj[] = new Object[1];

    public void process(Object[] objects) throws HiveException {
        count = Integer.valueOf(count.intValue() + 1);
    }

    public void close() throws HiveException {
        forwardObj[0] = count;
        forward(forwardObj);
    }
}
