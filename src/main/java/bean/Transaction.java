package bean;

public class Transaction {
	public Transaction(String user_in , String user_out ,float money) {
		this.user_in = user_in;
		this.user_out = user_out;
		this.money = money;
	}
	public String user_in;
	
	public String user_out;
	
	public float money;
	
}
