package bean;

import java.util.HashSet;

import topology.Constants;

public class Block {
	
	public String pre_hash;
	public String merkle_root;
	public int height;
	public int batchNum;
	public int nodelabel;
	public HashSet<Transaction> transactions;
	
	public Block() {
		this.height = -1;
		this.merkle_root = "-1";
		this.pre_hash = "-2";
		this.batchNum = -1;
		this.nodelabel = -1;
		transactions = new HashSet<Transaction>();
	}

	public Block(HashSet<Transaction> transactionSet, int height , int batchNum) {
		this.height = height;
		pre_hash = Integer.toString(height -1);
		float sum = 0;
		for (Transaction trans : transactionSet) {
			sum += trans.money;
		}
		merkle_root = Float.toString(sum);
		this.batchNum = batchNum;
		nodelabel = Constants.nodeLabel;
		this.transactions = transactionSet;
	}

	
	public Boolean valid() {
		return true;
	}

	@Override
	public String toString() {
		return "Block [height=" + height + ", merkle_root=" + merkle_root + ", pre_hash=" + pre_hash + ", batchNum="
				+ batchNum + ", nodelabel=" + nodelabel + "]";
	}
	

}
