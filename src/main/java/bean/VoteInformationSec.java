package bean;

import java.util.LinkedList;
import java.util.List;


public class VoteInformationSec {
	public List<VoteInformationFirst> voteInformations ;
	public int blockheight;
	
	public VoteInformationSec() {
		voteInformations = new LinkedList<VoteInformationFirst>();
	}
	
	public void Add(VoteInformationFirst vote) {
		voteInformations.add(vote);
	}
	
	public boolean valid() {
		return (voteInformations.size() >= 4 );
	}
}
