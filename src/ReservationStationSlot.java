
public class ReservationStationSlot {

	public Instruction instruction;
	public boolean busy;
	public double Vj, Vk;
	public String Qj;
	public String Qk;

	public ReservationStationSlot(Instruction instruction) {
		this.instruction = instruction;
		this.busy = false;
		
	}

	public boolean isBusy() {
		return busy;
	}

	public String toString() {
		if (this.instruction == null)
			return "null";
		return (this.busy + " | " + ((ALUinstruction) this.instruction).operation + " | " + (this.Vj) + " | "
				+ (this.Vk) + " | " + (this.Qj) + " | " + (this.Qk) + "");

	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}
}