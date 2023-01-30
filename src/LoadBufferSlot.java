
public class LoadBufferSlot {
	public Instruction instruction;
	public int address;
	public boolean busy;


	public LoadBufferSlot(Instruction instruction) {
		this.busy = false;
		this.instruction = instruction;

	}

	public String toString() {
		if (this.instruction == null)
			return "null";
		return (this.busy + " | " + ((MemoryInstruction) this.instruction).operation + " | "
				+ ((MemoryInstruction) this.instruction).address +  " | ");

	}
}
