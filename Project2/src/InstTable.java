import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * 모든 instruction의 정보를 관리하는 클래스. instruction data들을 저장한다. <br>
 * 또한 instruction 관련 연산, 예를 들면 목록을 구축하는 함수, 관련 정보를 제공하는 함수 등을 제공 한다.
 */
public class InstTable {
	/** 
	 * inst.data 파일을 불러와 저장하는 공간.
	 *  명령어의 이름을 집어넣으면 해당하는 Instruction의 정보들을 리턴할 수 있다.
	 */
	HashMap<String, Instruction> instMap;
	
	/**
	 * 클래스 초기화. 파싱을 동시에 처리한다.
	 * @param instFile : instuction에 대한 명세가 저장된 파일 이름
	 */
	public InstTable(String instFile) {
		instMap = new HashMap<String, Instruction>();
		openFile(instFile);
	}
	
	/**
	 * 입력받은 이름의 파일을 열고 해당 내용을 파싱하여 instMap에 저장한다.
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
	 * 해당 instruction이 instMap에 있으면 리턴한다.
	 * @param instruction : instMap에서 찾고자 하는 instruction
	 * @return instruction : instMap에 있는 명령어
	 * 주의 : instMap에 해당 instruction이 없으면 null을 리턴한다.
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
	 * 해당 instruction이 instMap에 있으면 그 instruction의 format을 리턴한다.
	 * @param instruction : instMap에서 찾고자 하는 instruction
	 * @return format : 해당 instruction의 format
	 * 주의 : instMap에 해당 instruction이 없으면 null을 리턴한다.
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
	 * 해당 instruction이 instMap에 있으면 그 instruction의 opcode를 리턴한다.
	 * @param instruction : instMap에서 찾고자 하는 instruction
	 * @return opcode : 해당 instruction의 opcode
	 * 주의 : instMap에 해당 instruction이 없으면 null을 리턴한다.
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
	 * 해당 instruction이 instMap에 있으면 그 instruction의 operand의 개수를 리턴한다.
	 * @param instruction : instMap에서 찾고자 하는 instruction
	 * @return numberOfOperand : 해당 instruction의 numberOfOperand
	 * 주의 : instMap에 해당 instruction이 없으면 -1을 리턴한다.
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
 * 명령어 하나하나의 구체적인 정보는 Instruction클래스에 담긴다.
 * instruction과 관련된 정보들을 저장하고 기초적인 연산을 수행한다.
 */
class Instruction {
	
	String instruction;
	int format;
	String opcode;
	int numberOfOperand;
	
	/**
	 * 클래스를 선언하면서 일반문자열을 즉시 구조에 맞게 파싱한다.
	 * @param line : instruction 명세파일로부터 한줄씩 가져온 문자열
	 */
	public Instruction(String line) {
		parsing(line);
	}
	
	/**
	 * 일반 문자열을 파싱하여 instruction 정보를 파악하고 저장한다.
	 * @param line : instruction 명세파일로부터 한줄씩 가져온 문자열
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
