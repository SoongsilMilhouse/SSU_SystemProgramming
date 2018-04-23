/*
* 화일명 : my_assembler_00000000.c
* 설  명 : 이 프로그램은 SIC/XE 머신을 위한 간단한 Assembler 프로그램의 메인루틴으로,
* 입력된 파일의 코드 중, 명령어에 해당하는 OPCODE를 찾아 출력한다.
* 파일 내에서 사용되는 문자열 "00000000"에는 자신의 학번을 기입한다.
*/

/*
*
* 프로그램의 헤더를 정의한다.
*
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>


// 파일명의 "00000000"은 자신의 학번으로 변경할 것.
#include "my_assembler_tmp.h"

/* ----------------------------------------------------------------------------------
* 설명 : 사용자로 부터 어셈블리 파일을 받아서 명령어의 OPCODE를 찾아 출력한다.
* 매계 : 실행 파일, 어셈블리 파일
* 반환 : 성공 = 0, 실패 = < 0
* 주의 : 현재 어셈블리 프로그램의 리스트 파일을 생성하는 루틴은 만들지 않았다.
*		   또한 중간파일을 생성하지 않는다.
* ----------------------------------------------------------------------------------
*/
int main(int args, char *arg[])
{

	if (init_my_assembler()< 0)
	{
		printf("init_my_assembler: 프로그램 초기화에 실패 했습니다.\n");
		return -1;
	}
	if (assem_pass1() < 0) {
		printf("assem_pass1: 패스1 과정에서 실패하였습니다.  \n");
		return -1;
	}
	
	make_opcode_output("output.txt");
	//make_symtab_output("symtab");

	/* 새로 구현되는 내용 */
	/*
	make_symtab_output("symtab");		//symbol table 출력
	assem_pass2();								//object code 생성
	make_objectcode_output("output);	//object code 출력
	*/
	//free_memory();

	printf("프로그램이 실행됐습니다.(엔터 키를 누르시면 종료됩니다.)\n");
	getchar();
	return 0;
}

/* ----------------------------------------------------------------------------------
* 설명 : 프로그램 초기화를 위한 자료구조 생성 및 파일을 읽는 함수이다.
* 매계 : 없음
* 반환 : 정상종료 = 0 , 에러 발생 = -1
* 주의 : 각각의 명령어 테이블을 내부에 선언하지 않고 관리를 용이하게 하기
*		   위해서 파일 단위로 관리하여 프로그램 초기화를 통해 정보를 읽어 올 수 있도록
*		   구현하였다.
* ----------------------------------------------------------------------------------
*/
int init_my_assembler(void)
{
	int result;

	if ((result = init_inst_file("inst.data")) < 0)
		return -1;

	if ((result = init_input_file("input.txt")) < 0)
		return -1;

	return result;
}

/* ----------------------------------------------------------------------------------
* 설명 : 머신을 위한 기계 코드목록 파일을 읽어 기계어 목록 테이블(inst_table)을
*        생성하는 함수이다.
* 매계 : 기계어 목록 파일
* 반환 : 정상종료 = 0 , 에러 < 0
* 주의 : 기계어 목록파일 형식은 자유롭게 구현한다. 예시는 다음과 같다.
*
*	===============================================================================
*		   | 이름 | 형식 | 기계어 코드 | 오퍼랜드의 갯수 | NULL|
*	===============================================================================
*
* ----------------------------------------------------------------------------------
* 추가 설명 : inst.data파일은 아래와 같이 구성되어 있다.
*
*			함수명 | Format | Opcode | 오퍼랜드 갯수
*			  ADD  |   3    |   18   |       1
*
*	Format을 3/4로 작성하지 않은 이유는 다음과 같습니다. 추후 +JSUB과 같은 4형식의 함수가 나올 시
* '+' 문자를 체크한 다음 Format안에 있는 숫자를 +1 해주려는 목적입니다.
*/
int init_inst_file(char *inst_file)
{
	FILE *file;
	int errno;
	int i;
	
	file = fopen(inst_file, "r");
	if (file == NULL) {
		printf("파일 열기 실패");
		errno = -1;
		return errno;
	}

	for (i = 0; i < sizeof(inst_table) / sizeof(inst *); i++)
		inst_table[i] = (inst*)malloc(sizeof(inst));

	while (EOF != fscanf(file, "%s %d %s %d", 
		inst_table[inst_count]->inst_name,	&inst_table[inst_count]->format, 
		inst_table[inst_count]->opcode,		&inst_table[inst_count]->operand_count))
			inst_count++;
	
	fclose(file);
	return errno;
}

/* ----------------------------------------------------------------------------------
* 설명 : 어셈블리 할 소스코드를 읽어 소스코드 테이블(input_data)를 생성하는 함수이다.
* 매계 : 어셈블리할 소스파일명
* 반환 : 정상종료 = 0 , 에러 < 0
* 주의 : 라인단위로 저장한다.
*
* ----------------------------------------------------------------------------------
*/
int init_input_file(char *input_file)
{
	FILE * file;
	int errno;
	int i;
	int max_len;

	file = fopen(input_file, "r");
	if (file == NULL) {
		printf("파일 열기 실패");
		errno = -1;
		return errno;
	}

	while (1) {
		input_data[line_num] = (char*)malloc(sizeof(char) * 100);
		if (fgets(input_data[line_num], 100, file) == NULL)
			break;

		//fgets로 인한 개행문자 -> 널문자로 처리
		max_len = strlen(input_data[line_num]);
		if (max_len > 0) input_data[line_num][max_len - 1] = '\0';

		line_num++;
	}
	
	fclose(file);
	return errno;
}

/* ----------------------------------------------------------------------------------
* 설명 : 소스 코드를 읽어와 토큰단위로 분석하고 토큰 테이블을 작성하는 함수이다.
*        패스 1로 부터 호출된다.
* 매계 : 파싱을 원하는 문자열
* 반환 : 정상종료 = 0 , 에러 < 0
* 주의 : my_assembler 프로그램에서는 라인단위로 토큰 및 오브젝트 관리를 하고 있다.
* ----------------------------------------------------------------------------------
*/
int token_parsing(char *str)
{
	char* ptr;
	char* tmp = NULL;
	char* operand_tmp = NULL;
	int errno;
	int i, j;

	//. 으로 시작하는 주석은 token_table에 넣지 않는다.
	if (str[0] == '.')
		return errno;

	if (str == NULL) {
		return errno;
	}
	else {
		token_table[token_line] = (token*)malloc(sizeof(token));
		
		//label, instruction, operand, comment -> NULL 초기화
		token_table[token_line]->label = NULL;
		token_table[token_line]->instruction = NULL;
		for (i = 0; i < MAX_OPERAND; i++) 
			token_table[token_line]->operand[i] = NULL;
		token_table[token_line]->comment = NULL;

		//label이 없는 경우 -> strtok 진행
		if (str[0] == '\t') {
			ptr = strtok(str, "\t");
		}
		else {
			ptr = strtok(str, "\t");
			token_table[token_line]->label = ptr;
			ptr = strtok(NULL, "\t");
		}

		//instruction 저장
		token_table[token_line]->instruction = ptr;
		//instruction이 RSUB이 아닌 경우 strtok 진행("\t\tCOMMENT" 에서 strtok를 할 경우 두 개의 '\t'가 지나가기 때문에 예외 처리)
		if(strcmp(token_table[token_line]->instruction, "RSUB"))
			ptr = strtok(NULL, "\t");

		//instruction만 있는 경우
		if (ptr == NULL) {
			token_line++;
			return errno;
		}

		//operand_tmp에 operand 저장
		operand_tmp = ptr;
		ptr = strtok(NULL, "\t");

		//comment 있는 경우 
		if (ptr != NULL)
			token_table[token_line]->comment = ptr;

		//operand_tmp에 넣어놨던 operand를 넣어주는 과정(Instruction - "RSUB"인 경우 예외처리)
		if (strcmp(token_table[token_line]->instruction, "RSUB") && operand_tmp != NULL) {
			token_table[token_line]->operand[0] = strtok(operand_tmp, ",");
			
			for (j = 1; j < MAX_OPERAND; j++) {
				tmp = strtok(NULL, ",");
				if (tmp == NULL)
					break;
				token_table[token_line]->operand[j] = tmp;
			}
		}

		token_line++;
	}

	return errno;
}


/* ----------------------------------------------------------------------------------
* 설명 : 입력 문자열이 기계어 코드인지를 검사하는 함수이다.
* 매계 : 토큰 단위로 구분된 문자열
* 반환 : 정상종료 = 기계어 테이블 인덱스, 에러 < 0
*
* ----------------------------------------------------------------------------------
*/
int search_opcode(char *str)
{
	//파라미터로 전달된 문자열의 길이를 str_len에 저장한다.
	char* tmp_str = str;
	int errno;
	int i = 0;
	int str_len = strlen(str);

	if (!strncmp(str, "+", 1)) {
		str_len--;
		tmp_str++;
	}

	for (i = 0; i < inst_count; i++) {
		if (!strncmp(tmp_str, inst_table[i]->inst_name, str_len))
			return i;
	}
	
	return -1;
}

/* ----------------------------------------------------------------------------------
* 설명 : 어셈블리 코드를 위한 패스1과정을 수행하는 함수이다.
*		   패스1에서는..
*		   1. 프로그램 소스를 스캔하여 해당하는 토큰단위로 분리하여 프로그램 라	인별 토큰
*		   테이블을 생성한다.
*
* 매계 : 없음
* 반환 : 정상 종료 = 0 , 에러 = < 0
* 주의 : 현재 초기 버전에서는 에러에 대한 검사를 하지 않고 넘어간 상태이다.
*	  따라서 에러에 대한 검사 루틴을 추가해야 한다.
*
* -----------------------------------------------------------------------------------
*/
static int assem_pass1(void)
{
	int errno;
	int i;

	for (i = 0; i < line_num; i++) {
		if (token_parsing(input_data[i]) < 0) {
			errno = -1;
			break;
		}
	}

	return errno;
}

/* ----------------------------------------------------------------------------------
* 설명 : token_parsing() 함수 이후 token_table에 있는 내용을 바탕으로 symbol_table을 생성한다.
* 매계 : 없음
* 반환 : 없음
* -----------------------------------------------------------------------------------
*/
void make_symtab(void)
{

}
/* ----------------------------------------------------------------------------------
* 설명 : 입력된 문자열의 이름을 가진 파일에 프로그램의 결과를 저장하는 함수이다.
*        여기서 출력되는 내용은 명령어 옆에 OPCODE가 기록된 표(과제 4번) 이다.
* 매계 : 생성할 오브젝트 파일명
* 반환 : 없음
* 주의 : 만약 인자로 NULL값이 들어온다면 프로그램의 결과를 표준출력으로 보내어
*        화면에 출력해준다.
*        또한 과제 4번에서만 쓰이는 함수이므로 이후의 프로젝트에서는 사용되지 않는다.
* -----------------------------------------------------------------------------------
*/
void make_opcode_output(char *file_name)
{
	FILE * file;
	int result;
	int i, j;
	int cnt = 0;

	file = fopen(file_name, "w");
	if (file == NULL) {
		printf("파일 열기 실패");
		return;
	}

	for (i = 0; i < token_line; i++) {
		//label 출력 
		if (token_table[i]->label != NULL) {
			fprintf(file, token_table[i]->label);
			fprintf(file, "\t");
		}
		else fprintf(file, "\t");

		//instruction 출력
		if (token_table[i]->instruction != NULL) {
			fprintf(file, token_table[i]->instruction);
			fprintf(file, "\t");
		}
		else fprintf(file, "\t");

		//operand 개수 count
		cnt = 0;
		for (j = 0; j < 3; j++) {
			if (token_table[i]->operand[j] != NULL)
				cnt++;
		}

		//operand 출력 
		for (j = 0; j < cnt; j++) {
			if (token_table[i]->operand[j] != NULL) {
				fprintf(file, token_table[i]->operand[j]);
				if (j < cnt - 1)
					fprintf(file, ",");
			}
		}
		fprintf(file, "\t");

		//search_opcode 함수를 이용하여 opcode 출력
		result = search_opcode(token_table[i]->instruction);
		//result != -1인 경우(대응하는 opcode가 있는 경우) opcode 출력
		if (result != -1)
			fprintf(file, "%s", inst_table[(search_opcode(token_table[i]->instruction))]->opcode);

		fprintf(file, "\n");
	}

	fclose(file);
}
/* ----------------------------------------------------------------------------------
* 설명 : 동적으로 할당한 메모리를 모두 해제한다.
* 매계 : 없음
* 반환 : 없음
* -----------------------------------------------------------------------------------
*/
void free_memory(void)
{
	int i;

	for (i = 0; i < sizeof(inst_table) / sizeof(inst *); i++) 
		free(inst_table[i]);

	for (i = 0;i < line_num; i++) 
		free(input_data[i]);

	for (i = 0; i < token_line; i++) 
		free(token_table[i]);
}

/* ----------------------------------------------------------------------------------
* 설명 : 어셈블리 코드를 기계어 코드로 바꾸기 위한 패스2 과정을 수행하는 함수이다.
*		   패스 2에서는 프로그램을 기계어로 바꾸는 작업은 라인 단위로 수행된다.
*		   다음과 같은 작업이 수행되어 진다.
*		   1. 실제로 해당 어셈블리 명령어를 기계어로 바꾸는 작업을 수행한다.
* 매계 : 없음
* 반환 : 정상종료 = 0, 에러발생 = < 0
* 주의 :
* -----------------------------------------------------------------------------------
*/
static int assem_pass2(void)
{

	/* add your code here */

}
/* ----------------------------------------------------------------------------------
* 설명 : pass1 종료 후 symbol table에 저장된 내용을 입력된 문자열의 이름을 가진 파일에 저장하는 함수이다.
* 매계 : 생성할 오브젝트 파일명
* 반환 : 없음
* 주의 : 만약 인자로 NULL값이 들어온다면 프로그램의 결과를 표준출력으로 보내어
*        화면에 출력해준다.
*
* -----------------------------------------------------------------------------------
*/
void make_symtab_output(char *file_name)
{

}
/* ----------------------------------------------------------------------------------
* 설명 : 입력된 문자열의 이름을 가진 파일에 프로그램의 결과를 저장하는 함수이다.
*        여기서 출력되는 내용은 object code (프로젝트 1번) 이다.
* 매계 : 생성할 오브젝트 파일명
* 반환 : 없음
* 주의 : 만약 인자로 NULL값이 들어온다면 프로그램의 결과를 표준출력으로 보내어
*        화면에 출력해준다.
*
* -----------------------------------------------------------------------------------
*/
void make_objectcode_output(char *file_name)
{
	/* add your code here */

}
