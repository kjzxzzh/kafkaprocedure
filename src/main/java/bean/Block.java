package bean;

import java.util.HashSet;

public class Block {
	
	public String pre_hash;
	public String merkle_root;
	public int height;
	
	public void Block(HashSet<Transaction> transactionSet , int height) {
		this.height = height;
		pre_hash = Integer.toString(height -1);
		float sum = 0;
		for (Transaction trans : transactionSet) {
			sum += trans.money;
		}
		merkle_root = Float.toString(sum);
	}

	public Boolean valid() {
		return true;
	}
	
}
