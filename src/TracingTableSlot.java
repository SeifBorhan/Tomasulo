
public class TracingTableSlot {
	Instruction instruction;
	int issue;
	int[] executionComplete;
	int writeResult;
	public boolean executing;

	public TracingTableSlot(Instruction instruction, int issue, int[] executionComplete, int writeResult) {
		this.instruction = instruction;
		this.issue = issue;
		this.executionComplete = executionComplete;
		this.writeResult = writeResult;
		this.executing=false;
	}

	
	public String toString(double[] registerFile) {
		if (this.instruction == null)
			return "null";
		else if (this.instruction instanceof ALUinstruction)
			return (" | " +((ALUinstruction) this.instruction).operation + " | " +"F"+((ALUinstruction) this.instruction).destinationOperand+ " | " + registerFile[((ALUinstruction) this.instruction).operand1]
					+ " | " + registerFile[((ALUinstruction) this.instruction).operand2] + " | " + (this.issue) + " | "
					+"[" +(this.executionComplete[0]) + ".." + (this.executionComplete[1])+"]" + "|" + (this.writeResult) +" | ");
		else {
			return (" | " +((MemoryInstruction) this.instruction).operation + " | " +
					"F"+((MemoryInstruction) this.instruction).registerNumber+ " | "
					+ ((MemoryInstruction) this.instruction).address 
					 + " | " + (this.issue) + " | "
					+"["+ (this.executionComplete[0]) + ".." + (this.executionComplete[1]) +"]" + " | " + (this.writeResult) +" | ");
		}
	}
}
