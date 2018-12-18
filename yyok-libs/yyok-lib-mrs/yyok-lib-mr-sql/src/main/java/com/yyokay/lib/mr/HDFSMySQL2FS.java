package com.yyokay.lib.mr;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mapr.EJob;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

    /**
     * Function: 测试 mr 与 mysql 的数据交互，此测试用例将一个表中的数据复制到另一张表中 实际当中，可能只需要从 mysql 读，或者写到
     * mysql 中。
     *
     * @author administrator
     *
     */
    public class HDFSMySQL2FS {
        public static class StudentinfoRecord implements Writable, DBWritable {
            int id;
            String name;

            public StudentinfoRecord() {

            }

            public String toString() {
                return new String(this.id + " " + this.name);
            }

            @Override
            public void readFields(ResultSet result) throws SQLException {
                this.id = result.getInt(1);
                this.name = result.getString(2);
            }

            @Override
            public void write(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, this.id);
                stmt.setString(2, this.name);
            }

            @Override
            public void readFields(DataInput in) throws IOException {
                this.id = in.readInt();
                this.name = Text.readString(in);
            }

            @Override
            public void write(DataOutput out) throws IOException {
                out.writeInt(this.id);
                Text.writeString(out, this.name);
            }

        }

        // 记住此处是静态内部类，要不然你自己实现无参构造器，或者等着抛异常：
        // Caused by: java.lang.NoSuchMethodException: DBInputMapper.<init>()
        // http://stackoverflow.com/questions/7154125/custom-mapreduce-input-format-cant-find-constructor
        // 网上脑残式的转帖，没见到一个写对的。。。
        public static class DBInputMapper extends
                Mapper<LongWritable, StudentinfoRecord, LongWritable, Text> {
            @Override
            public void map(LongWritable key, StudentinfoRecord value,
                            Context context) throws IOException, InterruptedException {
                context.write(new LongWritable(value.id), new Text(value.toString()));
            }
        }


        public static class MyReducer extends Reducer<LongWritable, Text, StudentinfoRecord, Text> {
            @Override
            public void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
                String[] splits = values.iterator().next().toString().split(" ");
                StudentinfoRecord r = new StudentinfoRecord();
                r.id = Integer.parseInt(splits[0]);
                r.name = splits[1];
                context.write(r, new Text(r.name));

            }
        }

        @SuppressWarnings("deprecation")
        public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
            File jarfile = EJob.createTempJar("bin");
            EJob.addClasspath("usr/hadoop/conf");

            ClassLoader classLoader = EJob.getClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);

            Configuration conf = new Configuration();
            // 这句话很关键
            conf.set("mapred.job.tracker", "172.30.1.245:9001");
            DistributedCache.addFileToClassPath(new Path(
                    "hdfs://172.30.1.245:9000/user/hadoop/jar/mysql-connector-java-5.1.6-bin.jar"), conf);
            DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver", "jdbc:mysql://172.30.1.245:3306/sqooptest", "sqoop", "sqoop");

            Job job = new  Job(conf, "HDFSMySQL2FS");
//      job.setJarByClass(HDFSMySQL2FS.class);
            ((JobConf)job.getConfiguration()).setJar(jarfile.toString());
            job.setMapOutputKeyClass(LongWritable.class);
            job.setMapOutputValueClass(Text.class);

            job.setMapperClass(DBInputMapper.class);
            job.setReducerClass(MyReducer.class);

            job.setOutputKeyClass(LongWritable.class);
            job.setOutputValueClass(Text.class);

            job.setOutputFormatClass(DBOutputFormat.class);
            job.setInputFormatClass(DBInputFormat.class);

            String[] fields = {"id","name"};
            // 从 t 表读数据
            DBInputFormat.setInput(job, StudentinfoRecord.class, "t", null, "id", fields);
            // mapreduce 将数据输出到 t2 表
            DBOutputFormat.setOutput(job, "t2", "id", "name");

            System.exit(job.waitForCompletion(true)? 0:1);
        }
    }
