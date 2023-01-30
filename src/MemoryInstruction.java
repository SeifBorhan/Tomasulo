
public class MemoryInstruction extends Instruction {



	public String operation;
	public int address;
	public int registerNumber;
	public int cycles;
	public MemoryInstruction(String operation,String register,int address,int cycles) {
		super(cycles);
		this.operation=operation;
		this.address=address;
		this.registerNumber=Integer.parseInt(register.substring(1));
	}
	
	public String toString() {
		String var ="operation:"+this.operation +"\n"+ "Register"+ this.registerNumber +"\n"+
				"Address:"+this.address+"\n"+"Cycles:"+super.cycles +"";
		return  var;
	}
	
	public static void main(String[] args) {
	
		
		
	}

}
