
public class StoreBufferSlot {
	public Instruction instruction;
	public int address;
	public boolean busy;
	public double V;
	public String Q;
	public StoreBufferSlot (Instruction instruction) {
		this.busy=false;
		this.instruction=instruction;
	}
	public String toString() {
		if (this.instruction == null)
			return "null";
		return (this.busy+" | "+((MemoryInstruction) this.instruction).operation +" | "+
		(this.V)+" | "+(this.Q)+" | ");
		
	}
}
