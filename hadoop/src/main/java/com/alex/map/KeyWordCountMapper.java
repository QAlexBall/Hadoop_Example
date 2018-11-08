package com.alex.map;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class KeyWordCountMapper extends Mapper<Object, Text, Text, DoubleWritable> {

    private Text resultKey = null;
    private DoubleWritable resultValue = null;

    public void map(Object key, Text value, Mapper<Object, Text, Text, DoubleWritable> .Context context) throws IOException, InterruptedException {
        String goodsInfoStr = value.toString();
        String[] goodsInfos = goodsInfoStr.split("--,--");
        String goodsPrice = goodsInfos[1];
        String goodsKeyWord = goodsInfos[0];
        if (goodsKeyWord.indexOf("惊") >= 0) {
            resultKey = new Text("惊");
        } else if (goodsKeyWord.indexOf("的") >= 0) {
            resultKey = new Text("的");
        } else if (goodsKeyWord.indexOf("好") >= 0) {
            resultKey = new Text("好");
        } else {
            resultKey = new Text("Others");
        }

        try {
            resultValue = new DoubleWritable(Double.parseDouble(goodsPrice));
        } catch (Exception e) {
            System.out.println(goodsPrice);
            resultValue = new DoubleWritable(Double.parseDouble("-1"));
        }
        context.write(resultKey, resultValue);
    }
}
