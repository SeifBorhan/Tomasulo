
public class ALUinstruction extends Instruction{
	public String operation;
	public int destinationOperand;
	public int operand1;
	public int operand2;
	public int cycles;
	public ALUinstruction(String operation,String destinationOperand,String operand1,String operand2,int cycles) {
		super(cycles);
		this.operation=operation;
		this.destinationOperand= Integer.parseInt(destinationOperand.substring(1));
		this.operand1= Integer.parseInt(operand1.substring(1));
		this.operand2= Integer.parseInt(operand2.substring(1));
	}

public String toString() {
	String var ="operation:"+this.operation +"\n"+ "destinationOperand:"+ this.destinationOperand +"\n"+
			"Operand 1:"+this.operand1+ "Operand 2:"+this.operand2 +"\n"+"Cycles:"+super.cycles +"";
	return  var;
}
	
	public static void main(String[] args) {
	
	}
	
}
