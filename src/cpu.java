import java.math.BigInteger;

class Registers
{
    public int data;
    public String address;
}

public class cpu
{
    // Instruction codes hard-coded in
    private final String arithmeticInstruction = "00";
    private final String conditionalInstruction = "01";
    private final String unconditionalInstruction = "10";
    private final String inputOutputInstruction = "11";

    // Determine opcode by converting binary to decimal and indexing opcodeArray
    private String opcode;
    private String[] opcodeArray = { "RD", "WR", "ST", "LW", "MOV", "ADD", "SUB", "MUL", "DIV", "AND",
            "OR", "MOVI", "ADDI", "MULI", "DIVI", "LDI", "SLT", "SLTI", "HLT", "NOP", "JMP", "BEQ",
            "BNE", "BEZ", "BNZ", "BGZ", "BLZ" };

    // Properties of binary instructions
    private String binary;
    private int reg1Index;
    private int reg2Index;
    private int reg3Index;
    private int addressIndex;

    private mmu mmu = new mmu();
    private Registers[] reg = new Registers[16];

    public static void main(String[] args) {
        // Declare a new cpu
        cpu cpu = new cpu();

        // Loads instructions into the mmu ram array
        loader.load(cpu.mmu);

        // Fetch the hex instruction from the mmu
        String hex = cpu.fetch(0);

        // Decode the hex instruction, sets the properties (reg1, reg2, address, etc.)
        cpu.decode(hex);
    }

    private String fetch(int index) {
        return mmu.load(index);
    }

    private void decode(String hex) {
        // Adds leading zeros if binary string is less than 32 chars long
        binary = new BigInteger(hex, 16).toString(2);
        while (binary.length() > 32) {
            binary = "0" + binary;
        }

        // First two chars of binary string specifies instruction format
        String instructionFormat = binary.substring(0, 2);
        switch (instructionFormat) {
            case arithmeticInstruction: {
                arithmetic();
            }

            case conditionalInstruction: {
                conditionalImmediate();
            }

            case unconditionalInstruction: {
                unconditional();
            }

            case inputOutputInstruction: {
                inputOutput();
            }
        }

        // Chars 3-8 specify opcode, we convert it to decimal and use it to index opcodeArray
        String opcodeBinary = binary.substring(2, 8);
        int opcodeIndex = Integer.parseInt(opcodeBinary, 2);
        opcode = opcodeArray[opcodeIndex];
    }

    private void execute(String binary) {

    }

    private void arithmetic() {
        String reg1 = binary.substring(8, 12);
        reg1Index = Integer.parseInt(reg1, 2);

        String reg2 = binary.substring(12, 16);
        reg2Index = Integer.parseInt(reg2, 2);

        String reg3 = binary.substring(16, 20);
        reg3Index = Integer.parseInt(reg3, 2);

        evaluate();
    }

    private void conditionalImmediate() {
        String reg1 = binary.substring(8, 12);
        reg1Index = Integer.parseInt(reg1, 2);

        String reg2 = binary.substring(12, 16);
        reg2Index = Integer.parseInt(reg2, 2);

        String address = binary.substring(16, 32);
        addressIndex = Integer.parseInt(address, 2) / 4;

        evaluate();
    }

    private void unconditional() {
        String address = binary.substring(8, 32);
        addressIndex = Integer.parseInt(address, 2) / 4;

        evaluate();
    }

    private void inputOutput() {
        String reg1 = binary.substring(8, 12);
        reg1Index = Integer.parseInt(reg1, 2);

        String reg2 = binary.substring(12, 16);
        reg2Index = Integer.parseInt(reg2, 2);

        String address = binary.substring(16, 32);
        addressIndex = Integer.parseInt(address, 2) / 4;

        evaluate();
    }

    private void evaluate() {

        switch (opcode) {
            case "RD": {
                reg[reg1Index].data = reg[addressIndex].data;
            }

            case "WR": {
                reg[addressIndex].data = reg[reg1Index].data;
            }

            case "ST": {
                if (reg2Index == 0) {
                    reg[addressIndex].data = reg[reg1Index].data;
                } else {
                    reg[reg2Index].data = reg[reg1Index].data;
                }
            }

            case "LW": {
                reg[reg1Index].data = reg[addressIndex].data;
            }

            case "MOV": {
                reg[reg3Index].data = reg[reg1Index].data;
            }

            case "ADD": {
                reg[reg3Index].data = reg[reg1Index].data + reg[reg2Index].data;
            }

            case "SUB": {
                reg[reg3Index].data = reg[reg1Index].data - reg[reg2Index].data;
            }

            case "MUL": {
                reg[reg3Index].data = reg[reg1Index].data * reg[reg2Index].data;
            }

            case "DIV": {
                reg[reg3Index].data = reg[reg1Index].data / reg[reg2Index].data;
            }

            case "AND": {
                if (reg[reg1Index].data != 0 && reg[reg2Index].data != 0) {
                    reg[reg3Index].data = 1;
                } else {
                    reg[reg3Index].data = 0;
                }
            }
        }

    }
}