package app;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.*;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DateUDTF extends GenericUDTF {
    private PrimitiveObjectInspector stringFieldInsp = null;


    public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {
        List<? extends StructField> allStructFieldRefs = argOIs.getAllStructFieldRefs();
        ObjectInspector fieldOneInsp = ((StructField)allStructFieldRefs.get(0)).getFieldObjectInspector();
        this.stringFieldInsp = (PrimitiveObjectInspector)fieldOneInsp;
        List<String> fieldNames = new ArrayList(Arrays.asList("month_at_stay"));
        List<ObjectInspector> fieldOIs = new ArrayList(1);
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    public void process(Object[] objects) throws HiveException {
        String dateFrom = this.stringFieldInsp.getPrimitiveJavaObject(objects[0]).toString();
        String dateTo = this.stringFieldInsp.getPrimitiveJavaObject(objects[1]).toString();
        YearMonth parseFrom = YearMonth.parse(dateFrom);
        YearMonth parseTo = YearMonth.parse(dateTo);
        if (parseTo.isBefore(parseFrom)) {
            throw new IllegalArgumentException("Second date argument dated before first one!");
        } else {
            for(Object[] ret = new Object[1]; !parseFrom.isAfter(parseTo); parseFrom = parseFrom.plusMonths(1L)) {
                ret[0] = parseFrom.toString();
                this.forward(ret);
            }

        }
    }

    public void close() throws HiveException {
    }
}
