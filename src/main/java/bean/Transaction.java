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
	
	public boolean valid() {
		return true;
	}
	
	@Override
    public int hashCode() {
        return (this.user_in + this.user_out + Float.toString(this.money)).hashCode();
    }

	 @Override
	 public boolean equals(Object obj) {
		 if (obj instanceof Transaction) {
			 Transaction tmp = (Transaction) obj;
			 return (this.user_in.equals(tmp.user_in) && (this.user_out.equals(tmp.user_out)) && (Math.abs(this.money - tmp.money) < 0.0001 ));
		 }
		 return false;
	 }
}
