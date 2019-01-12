package com.yyok.kafka.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.common.security.JaasUtils;

import kafka.admin.AdminClient;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.admin.TopicCommand;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;

import scala.collection.JavaConversions;

public class KafkaUtils {
	//local dev
	public static String initZKhostsNameLocalDev = "ddx,ddy,ddz";
	public static String initZKIPPortsLocalDev = "192.168.99.50:2181,192.168.99.52:2181,192.168.99.51:2181";
	public static String kafkaIPLocalDev = "192.168.99.50:9092,192.168.99.52:9092,192.168.99.51:9092";
	
	//online dd
	public static String initZKhostsDDDev = "dda,ddb,ddc,dde,ddf";
	public static String initIPPortsDDDev = "101.37.14.63,101.37.14.199,118.31.173.146,47.97.47.214,47.97.3.131";
	public static String initZKIPPortsDDDev = "dda:2181,ddb:2181,ddc:2181,dde:2181,ddf:2181/kafkagroup";
	public static String initZKIPPortsDDProd = "101.37.14.63:2181,101.37.14.199:2181,118.31.173.146:2181,47.97.47.214:2181,47.97.3.131:2181/kafkagroup";
	public static String initZKIPPortsLocalDDProd = "10.80.58.161:2181,10.80.59.53:2181,10.28.140.96:2181,10.80.176.146:2181,10.80.67.106:2181/kafkagroup";
	public static String kafkaBrokerDDProd = "47.97.47.214:9092,47.97.3.131:9092";
	public static String kafkaBrokerlocalDDProd = "10.80.176.146:9092,10.80.67.106:9092";
	//online gaia
	public static String initZKhostsGaiaProd = "gaiaa,gaiab,gaiac,gaiad,gaiae";
	public static String initZKhostPortsGaiaProd = "gaiaa:2181,gaiab:2181,gaiac:2181,gaiad:2181,gaiae:2181/kafkagroup";
	public static String initZKIPGaiaProd = "172.16.75.59,172.16.75.62,172.16.75.60,172.16.75.61,172.16.75.58";
	public static String initZKIPPortsGaiaProd = "172.16.75.59:2181,172.16.75.62:2181,172.16.75.60:2181,172.16.75.61:2181,172.16.75.58:2181/kafkagroup";

	// kafka properties　to app
	public static String kafkaBroker = kafkaBrokerDDProd;//kafkaBrokerlocalDDProd;//
	public static String initZKhostPorts = initZKIPPortsDDProd; //initZKIPPortsLocalDDProd;//

    /**
	 * kafka properties pull Consumer
	 * 
	 * @param groupid
	 * @return
	 */
	public static Properties initKafkaPull(String groupid, String clientid) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaBroker);
		props.put("group.id", groupid);
		props.put("client.id", clientid);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.offset.reset", "earliest");// earliest latest
		props.put("session.timeout.ms", "30000");
		props.put("max.poll.records", 3000000);
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		return props;
	}

	/*
	 * 
	 * kafka properties push Producer
	 */
	public static Properties initKafkaPush(String groupid, String clientid) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaBroker);
		props.put("acks", "all");
		props.put("group.id", groupid);
		props.put("client.id", clientid);
		props.put("retries", 0);
		props.put("batch.size", 1000000);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 1035544320);
		props.put("max.request.size", 2000000000);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		return props;
	}

	/*
	 * 创建主题 kafka-topics.sh --zookeeper localhost:2181 --create --topic kafka-action
	 * --replication-factor 2 --partitions 3
	 */
	public static void createTopica(String zkhosts, KafkaTopicBean topic) {
		ZkUtils zkUtils = null;
		try {
			zkUtils = ZkUtils.apply(initZKhostPorts, 30000, 30000, JaasUtils.isZkSecurityEnabled());
			if (!AdminUtils.topicExists(zkUtils, topic.getTopicName())) {
				AdminUtils.createTopic(zkUtils, topic.getTopicName(), topic.getPartition(),
						topic.getReplicationFactor(), initKafkaPush("da", topic.getTopicName()),
						AdminUtils.createTopic$default$6());
				System.out.println("messages:successful create!");
			} else {
				System.out.println(topic.getTopicName() + " is exits!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zkUtils != null) {
				zkUtils.close();
			}
		}
	}

	/*
	 * 创建主题 kafka-topics.sh --zookeeper localhost:2181 --create --topic kafka-action
	 * --replication-factor 2 --partitions 3 zkUtils: ZkUtils, topic: String,
	 * partitions: Int, replicationFactor: Int, topicConfig: Properties = new
	 * Properties, rackAwareMode: RackAwareMode = RackAwareMode.Enforced
	 * 
	 */
	public static void createKafaTopic(String ZkStr, String groupid, KafkaTopicBean topic) {
		ZkUtils zkUtils = null;
		try {
			zkUtils = ZkUtils.apply(initZKhostPorts, 30000, 30000, JaasUtils.isZkSecurityEnabled());
			if (!AdminUtils.topicExists(zkUtils, topic.getTopicName())) {
				AdminUtils.createTopic(zkUtils, topic.getTopicName(), topic.getPartition(),
						topic.getReplicationFactor(), initKafkaPush(groupid, topic.getTopicName()),
						RackAwareMode.Enforced$.MODULE$);
			} else {
				System.out.println(topic.getTopicName() + " is exits!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zkUtils != null) {
				zkUtils.close();
			}
		}

	}

	/**
	 * 创建主题（采用TopicCommand的方式）
	 * 
	 * @param config String s = "--zookeeper localhost:2181 --create --topic
	 *               kafka-action " + " --partitions 3 --replication-factor 1" + "
	 *               --if-not-exists --config max.message.bytes=204800 --config
	 *               flush.messages=2"; 执行：TopicsController.createTopic(s);
	 */
	public static void createTopic(String config) {
		String[] args = config.split(" ");
		System.out.println(Arrays.toString(args));
		TopicCommand.main(args);
	}

	/*
	 * 查看所有主题 kafka-topics.sh --zookeeper localhost:2181 --list
	 */
	public static List<String> listAllTopic(String zkUrl) {
		ZkUtils zkUtils = null;
		List<String> topics = null;
		try {
			zkUtils = ZkUtils.apply(initZKhostPorts, 30000, 30000, JaasUtils.isZkSecurityEnabled());

			topics = JavaConversions.seqAsJavaList(zkUtils.getAllTopics());
			/*topics.forEach(System.out::println);

			// 获取topic 'test'的topic属性属性
			Properties props = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), "log-aaa");
			// 查询topic-level属性
			Iterator it = props.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				System.out.println(key + " = " + value);
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zkUtils != null) {
				zkUtils.close();
			}
		}
		return topics;
	}

	/**
	 * 修改主题配置 kafka-config --zookeeper localhost:2181 --entity-type topics
	 * --entity-name kafka-action --alter --add-config max.message.bytes=202480
	 * --alter --delete-config flush.messages
	 */
	public static void alterTopicConfig(String topicName, Properties properties) {
		ZkUtils zkUtils = null;
		try {
			zkUtils = ZkUtils.apply(initZKhostPorts, 30000, 30000, JaasUtils.isZkSecurityEnabled());
			// 先取得原始的参数，然后添加新的参数同时去除需要去除的参数
			Properties oldproperties = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), topicName);
			properties.putAll(new HashMap<>(oldproperties));
			properties.remove("max.message.bytes");
			AdminUtils.changeTopicConfig(zkUtils, topicName, properties);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zkUtils != null) {
				zkUtils.close();
			}
		}
	}

	/*
	 * 删除某主题 kafka-topics.sh --zookeeper localhost:2181 --topic kafka-action
	 * --delete
	 */
	public static void deleteTopic(String topic) {
		ZkUtils zkUtils = null;
		try {
			zkUtils = ZkUtils.apply(initZKhostPorts, 30000, 30000, JaasUtils.isZkSecurityEnabled());
			AdminUtils.deleteTopic(zkUtils, topic);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zkUtils != null) {
				zkUtils.close();
			}
		}
	}

	/**
	 * 得到所有topic的配置信息 kafka-configs.sh --zookeeper localhost:2181 --entity-type
	 * topics --describe
	 */
	public static void listTopicAllConfig(String zkhosts) {
		ZkUtils zkUtils = null;
		try {
			zkUtils = ZkUtils.apply(initZKhostPorts, 30000, 30000, JaasUtils.isZkSecurityEnabled());
			Map<String, Properties> configs = JavaConversions.mapAsJavaMap(AdminUtils.fetchAllTopicConfigs(zkUtils));
			for (Map.Entry<String, Properties> entry : configs.entrySet()) {
				System.out.println("key=" + entry.getKey() + " ;value= " + entry.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zkUtils != null) {
				zkUtils.close();
			}
		}
	}
	
	public static void main(String[] args) {

		KafkaTopicBean topic = new KafkaTopicBean("da-TEST_USER", 3, 1, "TEST");

		// 创建topic
		// createTopica(initZKhostPorts, topic);

		createKafaTopic(initZKhostPorts, "da", topic);
		// 删除topic
		// deleteKafaTopic(initZKhostPorts,topic);

		listAllTopic(initZKhostPorts);
	}

}
