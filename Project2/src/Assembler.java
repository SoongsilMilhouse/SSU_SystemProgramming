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
 * 이 프로그램은 SIC/XE 머신을 위한 Assembler 프로그램의 메인 루틴이다.
 * 프로그램의 수행 작업은 다음과 같다. <br>
 * 1) 처음 시작하면 Instruction 명세를 읽어들여서 assembler를 세팅한다. <br>
 * 2) 사용자가 작성한 input 파일을 읽어들인 후 저장한다. <br>
 * 3) input 파일의 문장들을 단어별로 분할하고 의미를 파악해서 정리한다. (pass1) <br>
 * 4) 분석된 내용을 바탕으로 컴퓨터가 사용할 수 있는 object code를 생성한다. (pass2) <br>
 * 
 * <br><br>
 * 작성중의 유의사항 : <br>
 *  1) 새로운 클래스, 새로운 변수, 새로운 함수 선언은 얼마든지 허용됨. 단, 기존의 변수와 함수들을 삭제하거나 완전히 대체하는 것은 안된다.<br>
 *  2) 마찬가지로 작성된 코드를 삭제하지 않으면 필요에 따라 예외처리, 인터페이스 또는 상속 사용 또한 허용됨.<br>
 *  3) 모든 void 타입의 리턴값은 유저의 필요에 따라 다른 리턴 타입으로 변경 가능.<br>
 *  4) 파일, 또는 콘솔창에 한글을 출력시키지 말 것. (채점상의 이유. 주석에 포함된 한글은 상관 없음)<br>
 * 
 * <br><br>
 *  + 제공하는 프로그램 구조의 개선방법을 제안하고 싶은 분들은 보고서의 결론 뒷부분에 첨부 바랍니다. 내용에 따라 가산점이 있을 수 있습니다.
 */
public class Assembler {
	/** instruction 명세를 저장한 공간 */
	InstTable instTable;
	/** 읽어들인 input 파일의 내용을 한 줄 씩 저장하는 공간. */
	ArrayList<String> lineList;
	/** 프로그램의 section별로 symbol table을 저장하는 공간*/
	ArrayList<SymbolTable> symtabList;
	/** 프로그램의 section별로 프로그램을 저장하는 공간*/
	ArrayList<TokenTable> TokenList;
	/** 프로그램의 section별로 literal table을 저장하는 공간*/
	ArrayList<LiteralTable> literalList;
	/** 프로그램의 section별로 program length를 저장하는 공간*/
	ArrayList<Integer> progLengthList;
	/** 프로그램의 section별로 object program을 저장하는 공간*/
	ArrayList<ObjectProgTable> ObjectProgList;
	/** 
	 * Token, 또는 지시어에 따라 만들어진 오브젝트 코드들을 출력 형태로 저장하는 공간. <br>
	 * 필요한 경우 String 대신 별도의 클래스를 선언하여 ArrayList를 교체해도 무방함.
	 */
	ArrayList<CodeTable> codeList;

	/**
	 * 클래스 초기화. instruction Table을 초기화와 동시에 세팅한다.
	 * 
	 * @param instFile : instruction 명세를 작성한 파일 이름. 
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
	 * 어셈블러의 메인 루틴
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
	 * 작성된 codeList를 출력형태에 맞게 출력한다.<br>
	 * @param fileName : 저장되는 파일 이름
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
	 * 작성된 SymbolTable들을 출력형태에 맞게 출력한다.<br>
	 * @param fileName : 저장되는 파일 이름
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
	 * pass1 과정을 수행한다.<br>
	 *   1) 프로그램 소스를 스캔하여 토큰단위로 분리한 뒤 토큰테이블 생성<br>
	 *   2) label을 symbolTable에 정리<br>
	 *   <br><br>
	 *    주의사항 : SymbolTable과 TokenTable은 프로그램의 section별로 하나씩 선언되어야 한다.
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
	 * pass2 과정을 수행한다.<br>
	 *   1) 분석된 내용을 바탕으로 object code를 생성하여 codeList에 저장.
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
			 * 해당 section의 literal table에 있는 리터럴을 codeList에 추가한다.
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
	 * inputFile을 읽어들여서 lineList에 저장한다.<br>
	 * @param inputFile : input 파일 이름.
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
	 * opcode를 int형으로 변환한 값을 리턴한다.
	 * @param opcode : int형으로 변환할 opcode
	 * @return tmp : int형으로 변환된 opcode
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
	 * format 2의 레지스터 종류에 따라 해당 number 반환
	 * @param register 
	 * @return : 해당 레지스터 number
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
	 * 10진수를 16진수로 변환하고 그 값을 리턴한다.
	 * @param value : 16진수로 바꾸고자 하는 10진수
	 * @return Integer.toHexString(value).toUpperCase() : 16진수로 변경된 값
	 */
	public static String decToHex(int value)
	 {
		return Integer.toHexString(value).toUpperCase();
	 }
}


