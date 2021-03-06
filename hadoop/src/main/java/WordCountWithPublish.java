import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WordCountWithPublish {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
        private Text resultKey = null;
        private IntWritable resultValue = new IntWritable(1);
        private String[] keys = new String[]{"机械工业出版社", "电子工业出版社", "中国水利水电出版社", "清华大学出版社", "人民邮电出版社"};
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String goodsInfoStr = value.toString();
            String[] goodsInfos = goodsInfoStr.split("\n");
            String goodsKeyWord = goodsInfos[0];
            for (String ignored : keys) {
                if (goodsKeyWord.indexOf(ignored) >= 0) {
                    resultKey = new Text(ignored);
                    break;
                } else {
                    resultKey = new Text("Others");
                }
            }
            context.write(resultKey, resultValue);
        }
    }

    public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}