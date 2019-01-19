package com.yyok.kafka.util;

public class KafkaTopicBean {
    /**
     * @param topicName topic名称  
     * @param partition 分区数量设置为1
     * @param replicationFactor 副本数量设置为1
     * @param descrbe
     */
	public KafkaTopicBean(String topicName,int partition,int replicationFactor,String descrbe) {
		this.topicName = topicName;
		this.partition = partition;
		this.replicationFactor = replicationFactor;
		this.descrbe = descrbe;
	}
	
    private String topicName;       // topic 名称
    private int partition;      // partition 分区数量
    private int replicationFactor;    // replicationFactor 副本数量
    private String descrbe;
    
    public String getTopicName() {  
        return topicName;  
    }  
  
    public void setTopicName(String topicName) {  
        this.topicName = topicName;  
    }  
  
    public Integer getPartition() {  
        return partition;  
    }  
  
    public void setPartition(Integer partition) {  
        this.partition = partition;  
    }  
  
    public Integer getReplicationFactor() {  
        return replicationFactor;  
    }  
  
    public void setReplicationFactor(Integer replicationFactor) {  
        this.replicationFactor = replicationFactor;  
    }  
  
    public String getDescrbe() {  
        return descrbe;  
    }  
  
    public void setDescrbe(String descrbe) {  
        this.descrbe = descrbe;  
    }  
  
    @Override  
    public String toString() {  
        return "KafkaTopicBean [topicName=" + topicName + ", partition=" + partition  
                + ", replicationFactor=" + replicationFactor + ", descrbe=" + descrbe +"]";  
    }  

}