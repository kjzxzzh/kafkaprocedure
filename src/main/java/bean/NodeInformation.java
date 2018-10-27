package bean;

public class NodeInformation {
	public  String host ;
	public  String zkPort ;
	public  String kafka ;
	
	public NodeInformation(String host,String zkPort , String kafka) {
		this.host = host;
		this.zkPort = zkPort;
		this.kafka = kafka;
	}
}
