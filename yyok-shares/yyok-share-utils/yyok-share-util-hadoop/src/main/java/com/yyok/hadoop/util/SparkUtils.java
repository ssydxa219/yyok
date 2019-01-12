package com.yyok.hadoop.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.security.JaasUtils;

import kafka.admin.AdminClient;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.admin.TopicCommand;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;

import scala.collection.JavaConversions;

public class SparkUtils {

	// kafka properties
	public static String initZKhostsDev = "dda,ddb,ddc,dde,ddf";
	public static String initZKhostsProd = "gaiaa,gaiab,gaiac,gaiad,gaiae";

	public static String initZKhostPortsDev = "dda:2181,ddb:2181,ddc:2181,dde:2181,ddf:2181/kafkagroup";
	public static String initZKhostPortsProd = "gaiaa:2181,gaiab:2181,gaiac:2181,gaiad:2181,gaiae:2181/kafkagroup";

	public static String initZKIPPortsProd = "101.37.14.63:2181,101.37.14.199:2181,118.31.173.146:2181,47.97.47.214:2181,47.97.3.131:2181/kafkagroup";
	public static String initZKhosts = initZKhostsDev;
	public static String initZKhostPorts = initZKIPPortsProd;

	

	public static void main(String[] args) {

	}

}
