package info.fshi.datamule.data;

public class BasicTable {
	protected long _id;
	// Empty constructor
	public BasicTable(){

	}
	
	public BasicTable(long id){
		this._id = id;
	}
	
	public void setId(int id){
		this._id = id;
	}
	
	public long getId(){
		return this._id;
	}
}
