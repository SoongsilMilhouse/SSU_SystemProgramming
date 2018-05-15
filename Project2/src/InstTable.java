import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * ��� instruction�� ������ �����ϴ� Ŭ����. instruction data���� �����Ѵ�. <br>
 * ���� instruction ���� ����, ���� ��� ����� �����ϴ� �Լ�, ���� ������ �����ϴ� �Լ� ���� ���� �Ѵ�.
 */
public class InstTable {
	/** 
	 * inst.data ������ �ҷ��� �����ϴ� ����.
	 *  ��ɾ��� �̸��� ��������� �ش��ϴ� Instruction�� �������� ������ �� �ִ�.
	 */
	HashMap<String, Instruction> instMap;
	
	/**
	 * Ŭ���� �ʱ�ȭ. �Ľ��� ���ÿ� ó���Ѵ�.
	 * @param instFile : instuction�� ���� ���� ����� ���� �̸�
	 */
	public InstTable(String instFile) {
		instMap = new HashMap<String, Instruction>();
		openFile(instFile);
	}
	
	/**
	 * �Է¹��� �̸��� ������ ���� �ش� ������ �Ľ��Ͽ� instMap�� �����Ѵ�.
	 */
	public void openFile(String fileName) {
		try {
			BufferedReader in = null;
			in = new BufferedReader(new FileReader("./data/inst.data"));
			
			 String line = "";
			 
			 while ((line = in.readLine()) != null) {
				    String parts[] = line.split("\t");
				    instMap.put(parts[0], new Instruction(line));
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �ش� instruction�� instMap�� ������ �����Ѵ�.
	 * @param instruction : instMap���� ã���� �ϴ� instruction
	 * @return instruction : instMap�� �ִ� ��ɾ�
	 * ���� : instMap�� �ش� instruction�� ������ null�� �����Ѵ�.
	 */
	public String getInstruction(String instruction) {
		if (instruction.charAt(0) == '+') {
			instruction = instruction.substring(1, instruction.length());
			
			if (instMap.containsKey(instruction)) {
				return "+" + instMap.get(instruction).instruction;
			}
		}
		else {
			if (instMap.containsKey(instruction)) {
				return instMap.get(instruction).instruction;
			}
		}
		return null;
	}
	
	/**
	 * �ش� instruction�� instMap�� ������ �� instruction�� format�� �����Ѵ�.
	 * @param instruction : instMap���� ã���� �ϴ� instruction
	 * @return format : �ش� instruction�� format
	 * ���� : instMap�� �ش� instruction�� ������ null�� �����Ѵ�.
	 */
	public int getFormat(String instruction) {
		Iterator<String> keys = instMap.keySet().iterator();
		
		if (instruction.charAt(0) == '+') {
			return instMap.get(instruction.substring(1, instruction.length())).format + 1;
		}
		
		while(keys.hasNext()) {
			String key = keys.next();
			if(instruction.equals(instMap.get(key).instruction)) {
				return instMap.get(key).format;
			}
		}
		return -1;
	}
	
	/**
	 * �ش� instruction�� instMap�� ������ �� instruction�� opcode�� �����Ѵ�.
	 * @param instruction : instMap���� ã���� �ϴ� instruction
	 * @return opcode : �ش� instruction�� opcode
	 * ���� : instMap�� �ش� instruction�� ������ null�� �����Ѵ�.
	 */
	public String getOpcode(String instruction) {
		Iterator<String> keys = instMap.keySet().iterator();
		

		if (instruction.charAt(0) == '+') {
			return instMap.get(instruction.substring(1, instruction.length())).opcode;
		}
		
		while(keys.hasNext()) {
			String key = keys.next();
			if(instruction.equals(instMap.get(key).instruction)) {
				return instMap.get(key).opcode;
			}
		}
		return null;
	}
	
	/**
	 * �ش� instruction�� instMap�� ������ �� instruction�� operand�� ������ �����Ѵ�.
	 * @param instruction : instMap���� ã���� �ϴ� instruction
	 * @return numberOfOperand : �ش� instruction�� numberOfOperand
	 * ���� : instMap�� �ش� instruction�� ������ -1�� �����Ѵ�.
	 */
	public int getNumberOfOperand(String instruction) {
		Iterator<String> keys = instMap.keySet().iterator();
		
		while(keys.hasNext()) {
			String key = keys.next();
			if(instruction.equals(instMap.get(key).instruction)) {
				return instMap.get(key).numberOfOperand;
			}
		}
		return -1;
	}
}
/**
 * ��ɾ� �ϳ��ϳ��� ��ü���� ������ InstructionŬ������ ����.
 * instruction�� ���õ� �������� �����ϰ� �������� ������ �����Ѵ�.
 */
class Instruction {
	
	String instruction;
	int format;
	String opcode;
	int numberOfOperand;
	
	/**
	 * Ŭ������ �����ϸ鼭 �Ϲݹ��ڿ��� ��� ������ �°� �Ľ��Ѵ�.
	 * @param line : instruction �����Ϸκ��� ���پ� ������ ���ڿ�
	 */
	public Instruction(String line) {
		parsing(line);
	}
	
	/**
	 * �Ϲ� ���ڿ��� �Ľ��Ͽ� instruction ������ �ľ��ϰ� �����Ѵ�.
	 * @param line : instruction �����Ϸκ��� ���پ� ������ ���ڿ�
	 */
	public void parsing(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, "\t");
		
		while(tokenizer.hasMoreElements()) {
			this.instruction = tokenizer.nextElement().toString();
			this.format = Integer.parseInt(tokenizer.nextToken());
			this.opcode = tokenizer.nextElement().toString();
			this.numberOfOperand = Integer.parseInt(tokenizer.nextToken());
		}
		
	}
}
