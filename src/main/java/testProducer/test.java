package testProducer;

import java.util.HashSet;

import com.alibaba.fastjson.JSONObject;

import bean.Transaction;

public class test {
	public  static  void main(String[] args) throws InterruptedException {
//	    Transaction student = new Transaction("in","out",10);
//	    String jsonString = JSONObject.toJSONString(student);
//	    System.out.println(jsonString);
	    
        HashSet<Transaction> loadsSet = new HashSet<Transaction>();
        loadsSet.add(new Transaction("in1","out",10));
        loadsSet.add(new Transaction("in2","out",10));
        loadsSet.add(new Transaction("in3","out",10));
        loadsSet.add(new Transaction("in4","out",10));
        loadsSet.add(new Transaction("in1","out",10));
        String jsonString = JSONObject.toJSONString(loadsSet);
        System.out.println(jsonString);
	}

}
