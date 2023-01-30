import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.*;

public class main {
	static LinkedList<Instruction> instructionMemory = new LinkedList<Instruction>();
	static ReservationStationSlot s = new ReservationStationSlot(null);
	static ReservationStationSlot[] addReservationStation = { s, s, s };
	static ReservationStationSlot[] mulReservationStation = { s, s };
	static LoadBufferSlot l = new LoadBufferSlot(null);
	static LoadBufferSlot[] loadBuffer = { l, l, l };
	static StoreBufferSlot st = new StoreBufferSlot(null);
	static StoreBufferSlot[] storeBuffer = { st, st };
	static double[] memory = new double[64];
	static double[] registerFile = new double[32];
	static String[] registerFileClone = new String[32];
	static LinkedList<TracingTableSlot> tracingTable = new LinkedList<TracingTableSlot>();
	static List<TracingTableSlot> writingTable = new ArrayList<TracingTableSlot>();
	static int pc;
	static int n;
	static int cycleCount = 0;
	boolean stall = false;

	public static void parse() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("instructions"));
		String currentLine = br.readLine();
		Scanner input = new Scanner(System.in);
		System.out.print("Enter add Cycles:");
		int addCycles = input.nextInt();
		System.out.print("Enter sub Cycles:");
		int subCycles = input.nextInt();
		System.out.print("Enter mul Cycles:");
		int mulCycles = input.nextInt();
		System.out.print("Enter div Cycles:");
		int divCycles = input.nextInt();
		System.out.print("Enter load Cycles:");
		int loadCycles = input.nextInt();
		System.out.print("Enter store Cycles:");
		int storeCycles = input.nextInt();

		while (currentLine != null) {

			String[] content = currentLine.split(" ");
			switch (content[0]) {

			case "ADD":
				Instruction i1 = (ALUinstruction) new ALUinstruction(content[0], content[1], content[2], content[3],
						addCycles);
				instructionMemory.add(i1);
				break;
			case "SUB":
				Instruction i2 = (ALUinstruction) new ALUinstruction(content[0], content[1], content[2], content[3],
						subCycles);
				instructionMemory.add(i2);
				break;
			case "MUL":
				Instruction i3 = new ALUinstruction(content[0], content[1], content[2], content[3], mulCycles);
				instructionMemory.add(i3);
				break;
			case "DIV":
				Instruction i4 = new ALUinstruction(content[0], content[1], content[2], content[3], divCycles);
				instructionMemory.add(i4);
				break;
			case "L.D":
				Instruction i5 = new MemoryInstruction(content[0], content[1], Integer.parseInt(content[2]),
						loadCycles);
				instructionMemory.add(i5);
				break;
			case "S.D":
				Instruction i6 = new MemoryInstruction(content[0], content[1], Integer.parseInt(content[2]),
						storeCycles);
				instructionMemory.add(i6);
				break;
			}
			n++;
			currentLine = br.readLine();
		}
		pc = 0;
//		System.out.println("Total Number of Instructions: " + n);
		br.close();
	}

	public static int instructionSearch(ReservationStationSlot[] ReservationStation, ReservationStationSlot r) {
		for (int x = 0; x < ReservationStation.length; x++) {
			if (ReservationStation[x].equals(r))
				return x;
		}

		return -1;
	}

	public static int instructionSearch(LoadBufferSlot[] loadBuffer, LoadBufferSlot r) {
		for (int x = 0; x < loadBuffer.length; x++) {
			if (loadBuffer[x].equals(r))
				return x;
		}

		return -1;
	}

	public static int instructionSearch(LoadBufferSlot[] loadBuffer, Instruction i) {
		for (int x = 0; x < loadBuffer.length; x++) {
			if (loadBuffer[x].instruction.equals(i))
				return x;
		}

		return -1;
	}

	public static int instructionSearch(StoreBufferSlot[] storeBuffer, Instruction i) {
		for (int x = 0; x < storeBuffer.length; x++) {
			if (storeBuffer[x].instruction.equals(i))
				return x;
		}

		return -1;
	}

	public static int instructionSearch(ReservationStationSlot[] addReservationStation, Instruction i) {
		for (int x = 0; x < addReservationStation.length; x++) {
			if (addReservationStation[x].instruction.equals(i))
				return x;
		}

		return -1;
	}

	public static int instructionSearch(StoreBufferSlot[] storeBuffer, StoreBufferSlot r) {
		for (int x = 0; x < storeBuffer.length; x++) {
			if (storeBuffer[x].equals(r))
				return x;
		}

		return -1;
	}

	public static int instructionSearch(LinkedList<TracingTableSlot> tracingTable, Instruction i) {
		for (int x = 0; x < tracingTable.size(); x++) {
			if (tracingTable.get(x).instruction.equals(i))
				return x;
		}

		return -1;
	}

	public static void issue(Instruction i) {
		int[] e = { -1, -1 };
		if (i instanceof ALUinstruction) {
			if (((ALUinstruction) i).operation.equals("ADD") || ((ALUinstruction) i).operation.equals("SUB")) {
				int index = -1;
				for (ReservationStationSlot s : addReservationStation) {
					if (!s.busy) {
						index = instructionSearch(addReservationStation, s);

						break;
					}

				}
				if (index != -1) {
					addReservationStation[index] = new ReservationStationSlot(i);
					addReservationStation[index].busy = true;
					tracingTable.addLast(new TracingTableSlot(i, cycleCount, e, -1));
					if (registerFileClone[((ALUinstruction) i).operand1] == "") {
						addReservationStation[index].Vj = registerFile[((ALUinstruction) i).operand1];
					} else {
						addReservationStation[index].Qj = registerFileClone[((ALUinstruction) i).operand1];
					}
					if (registerFileClone[((ALUinstruction) i).operand2] == "") {
						addReservationStation[index].Vk = registerFile[((ALUinstruction) i).operand2];
					} else {
						addReservationStation[index].Qk = registerFileClone[((ALUinstruction) i).operand2];
					}

					registerFileClone[((ALUinstruction) i).destinationOperand] = "A" + (index + 1);
					instructionMemory.removeFirst();
					return;
				}
			} else {
				int index = -1;
				for (ReservationStationSlot s : mulReservationStation) {
					if (!s.busy) {
						index = instructionSearch(mulReservationStation, s);
						break;

					}
				}
				if (index != -1) {
					mulReservationStation[index] = new ReservationStationSlot(i);
					mulReservationStation[index].busy = true;
					tracingTable.addLast(new TracingTableSlot(i, cycleCount, e, -1));
					if (registerFileClone[((ALUinstruction) i).operand1] == "") {
						mulReservationStation[index].Vj = registerFile[((ALUinstruction) i).operand1];
					} else {
						mulReservationStation[index].Qj = registerFileClone[((ALUinstruction) i).operand1];
					}
					if (registerFileClone[((ALUinstruction) i).operand2] == "") {
						mulReservationStation[index].Vk = registerFile[((ALUinstruction) i).operand2];
					} else {
						mulReservationStation[index].Qk = registerFileClone[((ALUinstruction) i).operand2];
					}

					registerFileClone[((ALUinstruction) i).destinationOperand] = "M" + (index + 1);
					instructionMemory.removeFirst();
					return;
				}

			}
		} else {
			int index = -1;
			if (((MemoryInstruction) i).operation.equals("L.D")) {
				for (LoadBufferSlot s : loadBuffer) {
					if (!s.busy) {
						index = instructionSearch(loadBuffer, s);
						break;

					}
				}
				if (index != -1) {
					loadBuffer[index] = new LoadBufferSlot(i);
					loadBuffer[index].busy = true;
					tracingTable.addLast(new TracingTableSlot(i, cycleCount, e, -1));
					registerFileClone[((MemoryInstruction) i).registerNumber] = "L" + (index + 1);
					instructionMemory.removeFirst();
					return;
				}
			} else {
				int ind = -1;
				for (StoreBufferSlot s : storeBuffer) {
					if (!s.busy) {
						ind = instructionSearch(storeBuffer, s);
						break;
					}
				}
				if (ind != -1) {

					storeBuffer[ind] = new StoreBufferSlot(i);
					storeBuffer[ind].busy = true;

					tracingTable.addLast(new TracingTableSlot(i, cycleCount, e, -1));

					if (registerFileClone[((MemoryInstruction) i).registerNumber] == "") {
						storeBuffer[ind].V = registerFile[((MemoryInstruction) i).registerNumber];

					} else {
						storeBuffer[ind].Q = registerFileClone[((MemoryInstruction) i).registerNumber];
					}

					registerFileClone[((MemoryInstruction) i).registerNumber] = "S" + (ind + 1);
					instructionMemory.removeFirst();
					return;
				}
			}
		}

	}

	public static void executeAddResStation() {
		for (ReservationStationSlot s : addReservationStation) {
			if (s.instruction == null)
				return;
			int index = instructionSearch(tracingTable, s.instruction);
			int ind = instructionSearch(addReservationStation, s.instruction);
			if (tracingTable.get(index).issue == cycleCount)
				return;
			else if (addReservationStation[ind].Qj != null || addReservationStation[ind].Qk != null) {
				if (registerFileClone[((ALUinstruction) s.instruction).operand1] == "") {
					addReservationStation[ind].Vj = registerFile[((ALUinstruction) s.instruction).operand1];
					addReservationStation[ind].Qj = null;
				}
				if (registerFileClone[((ALUinstruction) s.instruction).operand2] == "") {
					addReservationStation[ind].Vk = registerFile[((ALUinstruction) s.instruction).operand2];
					addReservationStation[ind].Qk = null;
				}

			}

			if (addReservationStation[ind].Qj == null && addReservationStation[ind].Qk == null
					&& tracingTable.get(index).executionComplete[0] == -1) {
				tracingTable.get(index).executionComplete[0] = cycleCount;
				s.instruction.cycles--;
				tracingTable.get(index).executing = true;
			} else if (addReservationStation[ind].Qj == null && addReservationStation[ind].Qk == null
					&& s.instruction.cycles != 0) {
				s.instruction.cycles--;
			}
			if (addReservationStation[ind].Qj == null && addReservationStation[ind].Qk == null
					&& s.instruction.cycles == 0 && tracingTable.get(index).executing == true) {
				tracingTable.get(index).executionComplete[1] = cycleCount;
				tracingTable.get(index).executing = false;

			}

		}
	}

	public static void writeAddResStation() {

		for (ReservationStationSlot s : addReservationStation) {

			if (s.instruction == null)
				return;
			int index = instructionSearch(tracingTable, s.instruction);
			if (s.busy == true && tracingTable.get(index).executionComplete[1] != -1 && s.instruction.cycles == 0
					&& tracingTable.get(index).executionComplete[1] != cycleCount) {
				if (!writingTable.contains(tracingTable.get(index))) {
					writingTable.add(tracingTable.get(index));
				}
				Collections.sort(writingTable, new Comparator<TracingTableSlot>() {

					public int compare(TracingTableSlot o1, TracingTableSlot o2) {
						if (o1.issue < o2.issue) {
							return -1;
						}
						if (o1.issue == o2.issue) {
							return 0;
						} else {
							return 1;
						}
					}
				});

			}

		}
		return;

	}

	public static void writeMulResStation() {

		for (ReservationStationSlot s : mulReservationStation) {

			if (s.instruction == null)
				return;
			int index = instructionSearch(tracingTable, s.instruction);
			if (s.busy == true && tracingTable.get(index).executionComplete[1] != -1 && s.instruction.cycles == 0
					&& tracingTable.get(index).executionComplete[1] != cycleCount) {
				if (!writingTable.contains(tracingTable.get(index))) {
					writingTable.add(tracingTable.get(index));
				}
			}

			Collections.sort(writingTable, new Comparator<TracingTableSlot>() {

				public int compare(TracingTableSlot o1, TracingTableSlot o2) {
					if (o1.issue < o2.issue) {
						return -1;
					}
					if (o1.issue == o2.issue) {
						return 0;
					} else {
						return 1;
					}
				}
			});
			System.out.println("writingTable");
			for (TracingTableSlot m : writingTable) {
				System.out.println(m.toString(registerFile));

			}

		}
		return;

	}

	public static void writeLoadBuffer() {

		for (LoadBufferSlot s : loadBuffer) {

			if (s.instruction == null)
				return;
			int index = instructionSearch(tracingTable, s.instruction);
			if (s.busy == true && tracingTable.get(index).executionComplete[1] != -1 && s.instruction.cycles == 0
					&& tracingTable.get(index).executionComplete[1] != cycleCount) {
				if (!writingTable.contains(tracingTable.get(index))) {
					writingTable.add(tracingTable.get(index));
				}

				Collections.sort(writingTable, new Comparator<TracingTableSlot>() {

					public int compare(TracingTableSlot o1, TracingTableSlot o2) {
						if (o1.issue < o2.issue) {
							return -1;
						}
						if (o1.issue == o2.issue) {
							return 0;
						} else {
							return 1;
						}
					}
				});

			}

		}
		return;

	}

	public static void writeStoreBuffer() {

		for (StoreBufferSlot s : storeBuffer) {

			if (s.instruction == null)
				return;
			int index = instructionSearch(tracingTable, s.instruction);
			if (s.busy == true && tracingTable.get(index).executionComplete[1] != -1 && s.instruction.cycles == 0
					&& tracingTable.get(index).executionComplete[1] != cycleCount) {

				if (!writingTable.contains(tracingTable.get(index))) {
					writingTable.add(tracingTable.get(index));
				}

				Collections.sort(writingTable, new Comparator<TracingTableSlot>() {

					public int compare(TracingTableSlot o1, TracingTableSlot o2) {
						if (o1.issue < o2.issue) {
							return -1;
						}
						if (o1.issue == o2.issue) {
							return 0;
						} else {
							return 1;
						}
					}
				});

			}

		}
		return;

	}

	public static void pushToBus(ReservationStationSlot a) {
		int index = -1;
		String tag = "";
		index = ((ALUinstruction) a.instruction).destinationOperand;

		if (((ALUinstruction) a.instruction).operation.equals("ADD")
				|| ((ALUinstruction) a.instruction).operation.equals("SUB")) {
			tag = "A" + ((instructionSearch(addReservationStation, a) + 1));
		} else {
			tag = "M" + ((instructionSearch(mulReservationStation, a) + 1));
		}
		for (ReservationStationSlot s : addReservationStation) {
			if (s.instruction != null) {
				if (s.Qj != null) {
					if (s.Qj.equals(tag)) {
						s.Qj = null;
						s.Vj = registerFile[index];
					}
				}
				if (s.Qk != null) {
					if (s.Qk.equals(tag)) {
						s.Qk = null;
						s.Vk = registerFile[index];
					}
				}

			}
		}
		for (ReservationStationSlot s : mulReservationStation) {
			if (s.instruction != null) {
				if (s.Qj != null) {
					if (s.Qj.equals(tag)) {
						s.Qj = null;
						s.Vj = registerFile[index];
					}
				}
				if (s.Qk != null) {
					if (s.Qk.equals(tag)) {
						s.Qk = null;
						s.Vk = registerFile[index];
					}
				}
			}
		}
		for (StoreBufferSlot s : storeBuffer) {
			if (s.instruction != null) {
				if (s.Q != null) {
					if (s.Q.equals(tag)) {
						s.Q = null;
						s.V = registerFile[index];
					}
				}
			}

		}
	}

	public static void pushToBus(LoadBufferSlot a) {
		int index = -1;
		String tag = "";
		System.out.println(a);
		index = ((MemoryInstruction) a.instruction).registerNumber;

		tag = "L" + ((instructionSearch(loadBuffer, a) + 1));

		for (ReservationStationSlot s : addReservationStation) {
			if (s.instruction != null) {
				if (s.Qj != null) {
					if (s.Qj.equals(tag)) {
						s.Qj = null;
						s.Vj = registerFile[index];
					}
				}
				if (s.Qk != null) {
					if (s.Qk.equals(tag)) {
						s.Qk = null;
						s.Vk = registerFile[index];
					}
				}

			}
		}
		for (ReservationStationSlot s : mulReservationStation) {
			if (s.instruction != null) {
				if (s.Qj != null) {
					if (s.Qj.equals(tag)) {
						s.Qj = null;
						s.Vj = registerFile[index];
					}
				}
				if (s.Qk != null) {
					if (s.Qk.equals(tag)) {
						s.Qk = null;
						s.Vk = registerFile[index];
					}
				}
			}
		}
		for (StoreBufferSlot s : storeBuffer) {
			if (s.instruction != null) {
				if (s.Q != null) {
					if (s.Q.equals(tag)) {
						s.Q = null;
						s.V = registerFile[index];
					}
				}
			}

		}

	}

	public static void writeBack() {
		if (writingTable.isEmpty())
			return;
		TracingTableSlot t = writingTable.remove(0);
		System.out.println(t);
		int ind = instructionSearch(tracingTable, t.instruction);
		tracingTable.get(ind).writeResult = cycleCount;
		if (t.instruction instanceof MemoryInstruction) {
			if (((MemoryInstruction) t.instruction).operation.equals("L.D")) {
				int index = instructionSearch(loadBuffer, t.instruction);
				loadBuffer[index].busy = false;

				if (registerFileClone[((MemoryInstruction) t.instruction).registerNumber]
						.equals("L" + (instructionSearch(loadBuffer, t.instruction) + 1))) {
					registerFileClone[((MemoryInstruction) t.instruction).registerNumber] = "";

				}
				registerFile[((MemoryInstruction) t.instruction).registerNumber] = memory[((MemoryInstruction) t.instruction).address];
				pushToBus(loadBuffer[index]);

			} else {
				int index = instructionSearch(storeBuffer, t.instruction);
				storeBuffer[index].busy = false;
				memory[((MemoryInstruction) t.instruction).address] = registerFile[((MemoryInstruction) t.instruction).registerNumber];
				if (registerFileClone[((MemoryInstruction) t.instruction).registerNumber]
						.equals("S" + (instructionSearch(storeBuffer, t.instruction) + 1))) {
					registerFileClone[((MemoryInstruction) t.instruction).registerNumber] = "";

				}
			}
		} else {
			switch (((ALUinstruction) t.instruction).operation) {

			case "ADD":
				registerFile[((ALUinstruction) t.instruction).destinationOperand] = registerFile[((ALUinstruction) t.instruction).operand1]
						+ registerFile[((ALUinstruction) t.instruction).operand2];

				if (registerFileClone[((ALUinstruction) t.instruction).destinationOperand]
						.equals("A" + (instructionSearch(addReservationStation, t.instruction) + 1))) {
					registerFileClone[((ALUinstruction) t.instruction).destinationOperand] = "";
				}
				int index1 = instructionSearch(addReservationStation, (ALUinstruction) t.instruction);
				addReservationStation[index1].busy = false;
				pushToBus(addReservationStation[index1]);

				break;
			case "SUB":
				registerFile[((ALUinstruction) t.instruction).destinationOperand] = registerFile[((ALUinstruction) t.instruction).operand1]
						- registerFile[((ALUinstruction) t.instruction).operand2];
				if (registerFileClone[((ALUinstruction) t.instruction).destinationOperand]
						.equals("A" + (instructionSearch(addReservationStation, t.instruction) + 1))) {
					registerFileClone[((ALUinstruction) t.instruction).destinationOperand] = "";
				}
				int index2 = instructionSearch(addReservationStation, t.instruction);
				addReservationStation[index2].busy = false;
				pushToBus(addReservationStation[index2]);

				break;
			case "MUL":
				registerFile[((ALUinstruction) t.instruction).destinationOperand] = registerFile[((ALUinstruction) t.instruction).operand1]
						* registerFile[((ALUinstruction) t.instruction).operand2];

				if (registerFileClone[((ALUinstruction) t.instruction).destinationOperand]
						.equals("M" + (instructionSearch(mulReservationStation, t.instruction) + 1))) {
					registerFileClone[((ALUinstruction) t.instruction).destinationOperand] = "";
				}
				int index3 = instructionSearch(mulReservationStation, t.instruction);
				mulReservationStation[index3].busy = false;
				pushToBus(mulReservationStation[index3]);

				break;
			case "DIV":
				registerFile[((ALUinstruction) t.instruction).destinationOperand] = registerFile[((ALUinstruction) t.instruction).operand1]
						/ registerFile[((ALUinstruction) t.instruction).operand2];
				if (registerFileClone[((ALUinstruction) t.instruction).destinationOperand]
						.equals("M" + (instructionSearch(mulReservationStation, t.instruction) + 1))) {
					registerFileClone[((ALUinstruction) t.instruction).destinationOperand] = "";
				}
				int index4 = instructionSearch(mulReservationStation, t.instruction);
				mulReservationStation[index4].busy = false;
				pushToBus(mulReservationStation[index4]);

				break;

			}
		}
	}

	public static void executeMulResStation() {
		for (ReservationStationSlot s : mulReservationStation) {
			if (s.instruction == null)
				return;
			int index = instructionSearch(tracingTable, s.instruction);
			int ind = instructionSearch(mulReservationStation, s.instruction);
			if (tracingTable.get(index).issue == cycleCount)
				return;
			else if (mulReservationStation[ind].Qj != null || mulReservationStation[ind].Qk != null) {
				if (registerFileClone[((ALUinstruction) s.instruction).operand1] == "") {
					mulReservationStation[ind].Vj = registerFile[((ALUinstruction) s.instruction).operand1];
					mulReservationStation[ind].Qj = null;
				}
				if (registerFileClone[((ALUinstruction) s.instruction).operand2] == "") {
					mulReservationStation[ind].Vk = registerFile[((ALUinstruction) s.instruction).operand2];
					mulReservationStation[ind].Qk = null;
				}

			}

			if (mulReservationStation[ind].Qj == null && mulReservationStation[ind].Qk == null
					&& tracingTable.get(index).executionComplete[0] == -1) {
				tracingTable.get(index).executionComplete[0] = cycleCount;
				s.instruction.cycles--;
				tracingTable.get(index).executing = true;
			} else if (s.Qj == null && s.Qk == null && s.instruction.cycles != 0) {
				s.instruction.cycles--;
			}

			if (mulReservationStation[ind].Qj == null && mulReservationStation[ind].Qk == null
					&& s.instruction.cycles == 0 && tracingTable.get(index).executing == true) {
				tracingTable.get(index).executionComplete[1] = cycleCount;
				if (((ALUinstruction) tracingTable.get(index).instruction).operation.equals("MUL")) {
					registerFile[((ALUinstruction) tracingTable
							.get(index).instruction).destinationOperand] = registerFile[((ALUinstruction) tracingTable
									.get(index).instruction).operand1]
									* registerFile[((ALUinstruction) tracingTable.get(index).instruction).operand2];
				} else {
					registerFile[((ALUinstruction) tracingTable
							.get(index).instruction).destinationOperand] = registerFile[((ALUinstruction) tracingTable
									.get(index).instruction).operand1]
									/ registerFile[((ALUinstruction) tracingTable.get(index).instruction).operand2];

				}
				tracingTable.get(index).executing = false;

			}

		}
	}

	public static void executeLoadBuffer() {
		for (LoadBufferSlot s : loadBuffer) {
			if (s.instruction == null)
				return;
			int index = instructionSearch(tracingTable, s.instruction);
			if (tracingTable.get(index).issue == cycleCount)
				return;
			if (tracingTable.get(index).executionComplete[0] == -1 && tracingTable.get(index).issue != cycleCount) {
				tracingTable.get(index).executionComplete[0] = cycleCount;
				s.instruction.cycles--;
				tracingTable.get(index).executing = true;

			} else if (s.instruction.cycles != 0) {
				s.instruction.cycles--;
			}

			if (s.instruction.cycles == 0 && tracingTable.get(index).executing == true) {
				tracingTable.get(index).executionComplete[1] = cycleCount;
				memory[((MemoryInstruction) tracingTable
						.get(index).instruction).address] = registerFile[((MemoryInstruction) tracingTable
								.get(index).instruction).registerNumber];
				tracingTable.get(index).executing = false;

			}

		}
	}

	public static void executeStoreBuffer() {
		for (StoreBufferSlot s : storeBuffer) {

			if (s.instruction == null)
				return;
			int index = instructionSearch(tracingTable, s.instruction);
			if (tracingTable.get(index).issue == cycleCount)
				return;

			if (s.Q == null && tracingTable.get(index).executionComplete[0] == -1) {
				tracingTable.get(index).executionComplete[0] = cycleCount;
				s.instruction.cycles--;
				tracingTable.get(index).executing = true;

			} else if (s.Q == null && s.instruction.cycles != 0) {
				s.instruction.cycles--;
			}
			if (s.Q == null && s.instruction.cycles == 0 && tracingTable.get(index).executing == true) {
				tracingTable.get(index).executionComplete[1] = cycleCount;
				registerFile[((MemoryInstruction) tracingTable
						.get(index).instruction).address] = memory[((MemoryInstruction) tracingTable
								.get(index).instruction).registerNumber];
				tracingTable.get(index).executing = false;

			} else if (s.Q != null) {
				if (registerFileClone[((MemoryInstruction) s.instruction).registerNumber] == "") {
					s.V = registerFile[((MemoryInstruction) s.instruction).registerNumber];
					s.Q = null;
				}

			}

		}
	}

	public static void execute() {
		executeAddResStation();
		executeMulResStation();
		executeLoadBuffer();
		executeStoreBuffer();
	}

	public static void printLoadBuffer() {
		System.out.println("Load Buffer:");
		for (LoadBufferSlot s : loadBuffer) {
			System.out.println(s.toString());
		}

	}

	public static void printAddReservationStation() {
		System.out.println("Add Reservation Station:");

		for (ReservationStationSlot s : addReservationStation) {
			System.out.println(s.toString());
		}

	}

	public static void printMulReservationStation() {
		System.out.println("Mul Reservation Station:");

		for (ReservationStationSlot s : mulReservationStation) {
			System.out.println(s.toString());
		}

	}

	public static void printStoreBuffer() {
		System.out.println("Store Buffer:");

		for (StoreBufferSlot s : storeBuffer) {
			System.out.println(s.toString());
		}
	}

	public static void printTracinTable() {
		System.out.println("Tracing Table:");

		for (TracingTableSlot s : tracingTable) {
			System.out.println(s.toString(registerFile));
		}

	}

	public static void printRegisterFileClone() {
		System.out.println("Register File Clone:");
		System.out.println(Arrays.toString(registerFileClone));

	}

	public static void printRegisterFile() {
		System.out.println("Register File:");
		System.out.println(Arrays.toString(registerFile));
	}

	public static void printMemory() {
		System.out.println("Memory:");
		System.out.println(Arrays.toString(memory));

	}

	public static void initRegisterFileClone() {
		for (int i = 0; i < registerFileClone.length; i++) {
			registerFileClone[i] = "";
		}
	}

	public static void initRegisterFile() {
		for (int i = 0; i < registerFile.length; i++) {
			registerFile[i] = i;
		}
	}

	public static void main(String[] args) throws IOException {
		parse();
		initRegisterFileClone();
		initRegisterFile();
		while (true) {
			System.out.println("------------------------------------------------------------------------------"+cycleCount);
			int exitCounter = 0;

			if (!instructionMemory.isEmpty()) {
				issue(instructionMemory.get(0));

			}
			execute();
			writeAddResStation();
			writeMulResStation();
			writeLoadBuffer();
			writeStoreBuffer();
			writeBack();
			printRegisterFileClone();
			printMulReservationStation();
			printAddReservationStation();
			printTracinTable();
			cycleCount++;

			for (String s : registerFileClone) {
				if (s.equals("")) {
					exitCounter++;
				}
			}
			if (exitCounter == registerFileClone.length) {
				break;
			}
		}

	}

}
