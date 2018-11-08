package com.alex.reduce;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class KeyWordCountReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

    public void reduce(Text key, Iterable<DoubleWritable> values,
                       Reducer<Text, DoubleWritable, Text, DoubleWritable>.Context context) throws IOException, InterruptedException {
        double sum = 0;
        int quantity = 0;

        Iterator<DoubleWritable> valuesIterator = values.iterator();
        while (valuesIterator.hasNext()) {
            double value = valuesIterator.next().get();
            sum = sum + value;
            quantity = quantity + 1;
        }
        double averagePrice = sum / quantity;
        context.write(key, new DoubleWritable(averagePrice));
    }
}
