import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Assembler : 
 * �� ���α׷��� SIC/XE �ӽ��� ���� Assembler ���α׷��� ���� ��ƾ�̴�.
 * ���α׷��� ���� �۾��� ������ ����. <br>
 * 1) ó�� �����ϸ� Instruction ���� �о�鿩�� assembler�� �����Ѵ�. <br>
 * 2) ����ڰ� �ۼ��� input ������ �о���� �� �����Ѵ�. <br>
 * 3) input ������ ������� �ܾ�� �����ϰ� �ǹ̸� �ľ��ؼ� �����Ѵ�. (pass1) <br>
 * 4) �м��� ������ �������� ��ǻ�Ͱ� ����� �� �ִ� object code�� �����Ѵ�. (pass2) <br>
 * 
 * <br><br>
 * �ۼ����� ���ǻ��� : <br>
 *  1) ���ο� Ŭ����, ���ο� ����, ���ο� �Լ� ������ �󸶵��� ����. ��, ������ ������ �Լ����� �����ϰų� ������ ��ü�ϴ� ���� �ȵȴ�.<br>
 *  2) ���������� �ۼ��� �ڵ带 �������� ������ �ʿ信 ���� ����ó��, �������̽� �Ǵ� ��� ��� ���� ����.<br>
 *  3) ��� void Ÿ���� ���ϰ��� ������ �ʿ信 ���� �ٸ� ���� Ÿ������ ���� ����.<br>
 *  4) ����, �Ǵ� �ܼ�â�� �ѱ��� ��½�Ű�� �� ��. (ä������ ����. �ּ��� ���Ե� �ѱ��� ��� ����)<br>
 * 
 * <br><br>
 *  + �����ϴ� ���α׷� ������ ��������� �����ϰ� ���� �е��� ������ ��� �޺κп� ÷�� �ٶ��ϴ�. ���뿡 ���� �������� ���� �� �ֽ��ϴ�.
 */
public class Assembler {
	/** instruction ���� ������ ���� */
	InstTable instTable;
	/** �о���� input ������ ������ �� �� �� �����ϴ� ����. */
	ArrayList<String> lineList;
	/** ���α׷��� section���� symbol table�� �����ϴ� ����*/
	ArrayList<SymbolTable> symtabList;
	/** ���α׷��� section���� ���α׷��� �����ϴ� ����*/
	ArrayList<TokenTable> TokenList;
	/** ���α׷��� section���� literal table�� �����ϴ� ����*/
	ArrayList<LiteralTable> literalList;
	/** ���α׷��� section���� program length�� �����ϴ� ����*/
	ArrayList<Integer> progLengthList;
	/** ���α׷��� section���� object program�� �����ϴ� ����*/
	ArrayList<ObjectProgTable> ObjectProgList;
	/** 
	 * Token, �Ǵ� ���þ ���� ������� ������Ʈ �ڵ���� ��� ���·� �����ϴ� ����. <br>
	 * �ʿ��� ��� String ��� ������ Ŭ������ �����Ͽ� ArrayList�� ��ü�ص� ������.
	 */
	ArrayList<CodeTable> codeList;

	/**
	 * Ŭ���� �ʱ�ȭ. instruction Table�� �ʱ�ȭ�� ���ÿ� �����Ѵ�.
	 * 
	 * @param instFile : instruction ���� �ۼ��� ���� �̸�. 
	 */
	public Assembler(String instFile) {
		instTable = new InstTable(instFile);
		lineList = new ArrayList<String>();
		symtabList = new ArrayList<SymbolTable>();
		TokenList = new ArrayList<TokenTable>();
		codeList = new ArrayList<CodeTable>();
		literalList = new ArrayList<LiteralTable>();
		progLengthList = new ArrayList<Integer>();
		ObjectProgList = new ArrayList<ObjectProgTable>();
	}

	/** 
	 * ������� ���� ��ƾ
	 */
	public static void main(String[] args) {
		Assembler assembler = new Assembler("inst.data");
		assembler.loadInputFile("input.txt");
		
		assembler.pass1();
		assembler.printSymbolTable("symtab_20120196");
		
		assembler.pass2();
		assembler.printObjectCode("output_20121096");
	}

	/**
	 * �ۼ��� codeList�� ������¿� �°� ����Ѵ�.<br>
	 * @param fileName : ����Ǵ� ���� �̸�
	 */
	private void printObjectCode(String fileName) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter (new FileWriter(fileName)));
		
			Iterator<ObjectProgTable> itr = ObjectProgList.iterator();
			while ( itr.hasNext() ) {
				ObjectProgTable objtable = itr.next();
				out.printf("%s\r\n", objtable.header);
				if ( objtable.extdef.equals("D") == false) {
					out.printf("%s\r\n", objtable.extdef);
				}
				out.printf("%s\r\n", objtable.extref);
				out.printf("%s\r\n", objtable.text);
				out.printf("%s", objtable.modification);
				out.printf("%s\r\n", objtable.end);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �ۼ��� SymbolTable���� ������¿� �°� ����Ѵ�.<br>
	 * @param fileName : ����Ǵ� ���� �̸�
	 */
	private void printSymbolTable(String fileName) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter (new FileWriter(fileName)));
			
			Iterator<SymbolTable> itr = symtabList.iterator();
			while ( itr.hasNext() ) {
				SymbolTable symtable = itr.next();
				for ( Map.Entry<String, Integer> entry : symtable.symbolHashMap.entrySet() ) {
					out.print(entry.getKey());
					out.println("\t" + decToHex(entry.getValue()));
				}
				out.println();	  
				out.flush();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	
	
	/** 
	 * pass1 ������ �����Ѵ�.<br>
	 *   1) ���α׷� �ҽ��� ��ĵ�Ͽ� ��ū������ �и��� �� ��ū���̺� ����<br>
	 *   2) label�� symbolTable�� ����<br>
	 *   <br><br>
	 *    ���ǻ��� : SymbolTable�� TokenTable�� ���α׷��� section���� �ϳ��� ����Ǿ�� �Ѵ�.
	 */
	private void pass1() {
		int section = 0;
		int locctr = 0;
		int literalIndex = 0;	/** for literal table */ 
		int i = 0, j = 0, k = 0;
		
		for (i = 0; i < lineList.size() - 1; ++i) {
			Token token = new Token(lineList.get(i));
			 
			if ( token.operator != null ) {
				if ( token.operator.equals("START") ) {
					symtabList.add(new SymbolTable());
					literalList.add(new LiteralTable());
					TokenList.add(new TokenTable(symtabList.get(section), instTable));
				}
				if ( token.operator.equals("CSECT") ) {
					progLengthList.add(locctr);
					locctr = 0;  section++;	j = 0; literalIndex = 0;
					symtabList.add(new SymbolTable());
					literalList.add(new LiteralTable());
					TokenList.add(new TokenTable(symtabList.get(section), instTable));
				}
			}
			
			/** add to tokenList */ 
			TokenList.get(section).putToken(lineList.get(i));
			/** allocate token location */
			TokenList.get(section).tokenList.get(j++).location = locctr;
			
			if ( token.operator.equals("EXTDEF") ) {
				for (k = 0; k < token.operand.length; k++) 
					symtabList.get(section).putExternalDefine(token.operand[k]);
				
			}
			if ( token.operator.equals("EXTREF") ) {
				for (k = 0; k < token.operand.length; k++) 
					symtabList.get(section).putExternalReference(token.operand[k]);
			}
			
			if ( token.operator.equals("END") == false) {
				/** put label into symbol table */ 
				if ( token.label != null ) {
					if ( symtabList.get(section).search(token.label) == -1 ) {
						symtabList.get(section).putSymbol(token.label, locctr);
					}
				}
				/** LTORG */
				if ( token.operator.equals("LTORG") ) {
					literalList.get(section).getLiteral(literalIndex++).modifyAddress(locctr);
					locctr += 3;
				}
				/** Literal */
				if ( (lineList.get(i).split("\t").length > 2) ) {
					if ( !token.operator.equals("RSUB") && token.operand[0].charAt(0) == '=' ) {
						literalList.add(new LiteralTable());
						literalList.get(section).putLiteral(lineList.get(i), locctr, section);
					}
				}
				/** EQU */
				if ( token.operator.equals("EQU") ) {
					if ( token.operand[0].charAt(0) == '*' ) {
						TokenList.get(section).tokenList.get(j-1).location = locctr;
					}
					else {
						String array[] = token.operand[0].split("-");
						TokenList.get(section).tokenList.get(j-1).location = symtabList.get(section).symbolHashMap.get(array[0]) - symtabList.get(section).symbolHashMap.get(array[1]);
						symtabList.get(section).modifySymbol(token.label, TokenList.get(section).tokenList.get(j-1).location);
					}
				}
				/** If you find a instruction, then add instruction's format to location counter */
				if ( token.operator.equals(instTable.getInstruction(token.operator)) ) {
					locctr += instTable.getFormat(token.operator);
				}
				else if ( token.operator.equals("WORD") ) {
					locctr += 3;
				}
				else if ( token.operator.equals("RESW") ) {
					locctr += 3 * Integer.parseInt((token.operand[0]));
				}				
				else if ( token.operator.equals("RESB") ) {
					locctr += Integer.parseInt(token.operand[0]);
				}
				else if ( token.operator.equals("BYTE") ) {
					String[] array = token.operand[0].split("'");
					locctr += array[1].length() / 2;
				}
			} /** End of "if ( token.operator.equals("END") == false)" */
		} /** End of for */
		/** save last program length  */
		if ( literalList.get(section).getLiteral(0).name.charAt(1) == 'C' ) {
			progLengthList.add(locctr);
		}
		else if ( literalList.get(section).getLiteral(0).name.charAt(1) == 'X' ) {
			progLengthList.add(locctr+1);
		}
		/** modify literal(=X'05') address */
		literalList.get(section).getLiteral(literalIndex).modifyAddress(locctr);
	}
	
	/**
	 * pass2 ������ �����Ѵ�.<br>
	 *   1) �м��� ������ �������� object code�� �����Ͽ� codeList�� ����.
	 */
	private void pass2() {
		final int TEXT_MAX_LENGTH = 60;
		int section = 0;
		int i = 0;
		int literalIndex = 0;
		int format = 0;
		int ta = 0;
		int pc = 0;
		String objectCode;
		
		Iterator<TokenTable> itr = TokenList.iterator();
		while ( itr.hasNext() ) {
			TokenTable tokentable = itr.next();
			codeList.add(new CodeTable());
			literalIndex = 0;
			
			for (Token token : tokentable.tokenList) {
				ta = 0;
				pc = 0;
				objectCode = "";
				format = 0;
				
				/** if token's operator is not in instTable, then skip that token*/
				if ( instTable.getInstruction(token.operator) == null ){
					/** if OPCODE = 'BYTE' or 'WORD' then */
					if ( token.operator.equals("BYTE") ) {
						String[] array = token.operand[0].split("'");
						objectCode += array[1];
						codeList.get(section).putOpjectCode(objectCode);
						
					}
					else if ( token.operator.equals("WORD") ) {
						objectCode += "000000";
						codeList.get(section).putOpjectCode(objectCode);
					}
					continue; 
				} 
				else {
					/** format 1 */
					if ( instTable.getFormat(token.operator) == 1 ) {
						String opcode = instTable.getOpcode(token.operator);
						/** save opcode */
						format = calculateOpcode(opcode);
						objectCode += Integer.toHexString(format);			
						codeList.get(section).putOpjectCode(objectCode);
					}
					/** format 2 */
					else if ( instTable.getFormat(token.operator) == 2 ) {
						objectCode += instTable.getOpcode(token.operator);
						objectCode += ConvertRegisterToNum(token.operand[0]);

						if ( token.operand.length < 2 ) 
							objectCode += "0";
						else {
							objectCode += ConvertRegisterToNum(token.operand[1]);
						}
						codeList.get(section).putOpjectCode(objectCode);
					}
					/** format 3 */
					else if ( instTable.getFormat(token.operator) == 3 ) {
						String opcode = instTable.getOpcode(token.operator);
						/** save opcode */
						format = format | (calculateOpcode(opcode) << 16);
						
						/** RSUB */
						if ( token.operator.equals("RSUB") ) {
							token.setFlag(TokenTable.nFlag, 1);
							token.setFlag(TokenTable.iFlag, 1);
							/** set nixbpe */
							format = format | (token.nixbpe << 12);
							
							objectCode = Integer.toHexString(format).toUpperCase();
							codeList.get(section).putOpjectCode(objectCode);
							continue;
						}
						
						/** immediate */
						if ( token.operand[0].charAt(0) == '#' ) {
							ta = Integer.parseInt(token.operand[0].substring(1, token.operand[0].length()));
							token.setFlag(TokenTable.iFlag, 1);
						}
						/** indirect */
						else if ( token.operand[0].charAt(0) == '@' ) {
							/** @RETADR case -> remove '@' */
							token.operand[0] = token.operand[0].substring(1, token.operand[0].length());
							token.setFlag(TokenTable.nFlag, 1);
						}
						else {
							token.setFlag(TokenTable.nFlag, 1);
							token.setFlag(TokenTable.iFlag, 1);
						}
						
						/** Index register */
						if ( token.operand[0].split(",").length > 1 ) {
							if ( token.operand[0].split(",")[1].charAt(0) == 'X') 
								token.setFlag(TokenTable.xFlag, 1);
						}
						/** PC relative */
						if ( token.operand[0].charAt(0) != '#' ) 
							token.setFlag(TokenTable.pFlag, 1);

						/** set nixbpe */
						format = format | (token.nixbpe << 12);
						
						/** if there is a symbol in OPERAND field then */
						if ( symtabList.get(section).search(token.operand[0]) != -1 ) {
							if ( ta == 0 ) {
									ta = symtabList.get(section).search(token.operand[0]);
							}
							pc = token.location + instTable.getFormat(token.operator);
						}
						
						if ( token.operand[0].charAt(0) == '=' ) {
							ta = literalList.get(section).getLiteralAddress(literalIndex);
							pc = token.location + instTable.getFormat(token.operator);
						}
					
						/** set displacement */
						format = format | ((ta - pc) & 0x00000FFF);
						objectCode = Integer.toHexString(format).toUpperCase();	
						if ( objectCode.length() < 6 )
							objectCode = "0" + objectCode;
						
						codeList.get(section).putOpjectCode(objectCode);
					}
					/** format 4 */
					else if ( instTable.getFormat(token.operator) == 4 ) {
						String opcode = instTable.getOpcode(token.operator);
						format = format | ((calculateOpcode(opcode) << 24));

						token.setFlag(TokenTable.nFlag, 1);
						token.setFlag(TokenTable.iFlag, 1);
						token.setFlag(TokenTable.eFlag, 1);
						
						/** Index register */
						if ( token.operand.length > 1 ) {
							if ( token.operand[1].charAt(0) == 'X') 
								token.setFlag(TokenTable.xFlag, 1);
						}
						
						/** set nixbpe */
						format = format | (token.nixbpe << 20);
						
						objectCode = Integer.toHexString(format).toUpperCase();	
						codeList.get(section).putOpjectCode(objectCode);
					}	
				}
			} /** End of for */
			/**
			 * �ش� section�� literal table�� �ִ� ���ͷ��� codeList�� �߰��Ѵ�.
			 */
			if ( literalList.get(section).literalList.isEmpty() == false )
				codeList.get(section).putOpjectCode(literalList.get(section).getLiteral(literalIndex++).value);
			
			section++;
		} /** End of while */

		/**
		 * make object program (header, extdef, extref, modification, end record)
		 */
		int progLengthListIndex = 0;
		section = 0;
		int sumOfLength = 0;
		Iterator<TokenTable> itr2 = TokenList.iterator();
		while ( itr2.hasNext() ) {
			TokenTable tokentable = itr2.next();
			
			for (Token token : tokentable.tokenList) {
				/** if opcode = 'START' */
				if ( token.operator.equals("START") ) {
					sumOfLength = 0;
					ObjectProgList.add(new ObjectProgTable(symtabList.get(section).search(token.label)));
					ObjectProgList.get(section).putHeader(token.label, progLengthList.get(progLengthListIndex++));
				}
				/** if opcode = 'CSECT' */
				else if ( token.operator.equals("CSECT") ) {
					sumOfLength = 0;
					ObjectProgList.add(new ObjectProgTable(symtabList.get(section).search(token.label)));
					ObjectProgList.get(section).putHeader(token.label, progLengthList.get(progLengthListIndex++));
				}
				/** if opcode = 'EXTDEF' */
				else if ( token.operator.equals("EXTDEF") ) {
					for ( i = 0; i < token.operand.length; i++) {
						ObjectProgList.get(section).putExtdef(token.operand[i], symtabList.get(section).search(token.operand[i]));
					}
				}
				/** if opcode = 'EXTREF' */
				else if ( token.operator.equals("EXTREF") ) {
					for ( i = 0; i < token.operand.length; i++) {
						ObjectProgList.get(section).putExtref(token.operand[i]);
					}
				}
				else {
					SymbolTable symtable = symtabList.get(section);
					if ( !token.operator.equals("LTORG") && !token.operator.equals("RSUB") && symtable.externalReference.contains(token.operand[0])) {
						ObjectProgList.get(section).modification += "M";
						ObjectProgList.get(section).putModification(token.location + 1, "05", token.operand[0]);
						ObjectProgList.get(section).modification += "\r\n";
					}
					if ( token.operator.equals("WORD") ) {
						String[] array = token.operand[0].split("-");
						ObjectProgList.get(section).modification += "M";
						ObjectProgList.get(section).putModification(token.location, "06", array[0]);
						ObjectProgList.get(section).modification += "\r\n";
						ObjectProgList.get(section).modification += "M";
						ObjectProgList.get(section).putModification(token.location, "06", array[1]);
						ObjectProgList.get(section).modification += "\r\n";
					}
				}
			}
			ObjectProgList.get(section).putEnd();
			ObjectProgList.get(section).end += "\r\n";

			section++;
		}
		/**
		 * make object program (text record)
		 */
		Iterator<CodeTable> itr3 = codeList.iterator();
		int innerSection = 0;
		while ( itr3.hasNext() ) {
			CodeTable codetable = itr3.next();
			sumOfLength = 0;
			
			for (i = 0; i < codetable.codeList.size(); i++) {
				sumOfLength += codetable.getObjectCode(i).length();
				if ( sumOfLength > TEXT_MAX_LENGTH) {
					sumOfLength -= codetable.getObjectCode(i).length();
					ObjectProgList.get(innerSection).modifyLength(Integer.toHexString(sumOfLength/2));
					ObjectProgList.get(innerSection).text += "\r\n";
					ObjectProgList.get(innerSection).text += "T" + ObjectProgList.get(innerSection).text.substring(3, 9) + "--";
					ObjectProgList.get(innerSection).text += codetable.getObjectCode(i);
					
					sumOfLength = 0;
				}
				else {
					if ( codetable.getObjectCode(i).equals("454F46") ) {
						ObjectProgList.get(innerSection).text += "\r\n";
						ObjectProgList.get(innerSection).modifyLength(String.format("%02X", sumOfLength/2));
						ObjectProgList.get(innerSection).text += "T" + String.format("%06X", literalList.get(innerSection).getLiteralAddress(0)) + "--" + codetable.getObjectCode(i);
						ObjectProgList.get(innerSection).modifyLength(String.format("%02X", codetable.getObjectCode(i).length()/2));
						break;
					}
					else if ( codetable.getObjectCode(i).equals("000000") ) {
						ObjectProgList.get(innerSection).modifyLength(String.format("%02X", (codetable.getObjectCode(i).length() + sumOfLength)/2));
						ObjectProgList.get(innerSection).text += codetable.getObjectCode(i);
						break;
					}
					else if ( codetable.getObjectCode(i).equals("05") ) {
						ObjectProgList.get(innerSection).modifyLength(String.format("%02X", sumOfLength/2));
						ObjectProgList.get(innerSection).text += codetable.getObjectCode(i);
					}
 					else {
						ObjectProgList.get(innerSection).text += codetable.getObjectCode(i);
					}
				}
			}
			innerSection++;
		}
	}
	
	/**
	 * inputFile�� �о�鿩�� lineList�� �����Ѵ�.<br>
	 * @param inputFile : input ���� �̸�.
	 */
	private void loadInputFile(String inputFile) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("./data/input.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		 
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				if ( line.contains(".") ) continue;
				lineList.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * opcode�� int������ ��ȯ�� ���� �����Ѵ�.
	 * @param opcode : int������ ��ȯ�� opcode
	 * @return tmp : int������ ��ȯ�� opcode
	 */
	public int calculateOpcode (String opcode) {
		int tmp = 0;
		
		if ( 'A' <= opcode.charAt(0) && opcode.charAt(0) <= 'Z' )
			tmp = opcode.charAt(0) - ('0' + 7) << 4;
		else 
			tmp = (opcode.charAt(0) - '0') << 4;
		  
		if ( 'A' <= opcode.charAt(1) && opcode.charAt(0) <= 'Z' )
			tmp += opcode.charAt(1) - ('0' + 7);
		else
			tmp += opcode.charAt(1) - '0';
		
		return tmp;
	}
	
	/**
	 * format 2�� �������� ������ ���� �ش� number ��ȯ
	 * @param register 
	 * @return : �ش� �������� number
	 */
	public String ConvertRegisterToNum(String register) {
		if ( register.equals("A") ) 	  return "0";
		else if ( register.equals("X") )  return "1";
		else if ( register.equals("L") )  return "2";
		else if ( register.equals("PC") ) return "8";
		else if ( register.equals("SW") ) return "9";
		else if ( register.equals("B") )  return "3";
		else if ( register.equals("S") )  return "4";
		else if ( register.equals("T") )  return "5";
		else if ( register.equals("F") )  return "6";
									  
		return null;
	}
	/**
	 * 10������ 16������ ��ȯ�ϰ� �� ���� �����Ѵ�.
	 * @param value : 16������ �ٲٰ��� �ϴ� 10����
	 * @return Integer.toHexString(value).toUpperCase() : 16������ ����� ��
	 */
	public static String decToHex(int value)
	 {
		return Integer.toHexString(value).toUpperCase();
	 }
}


