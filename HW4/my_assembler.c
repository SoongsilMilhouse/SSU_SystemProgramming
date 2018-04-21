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
#include "my_assembler.h"

/* ----------------------------------------------------------------------------------
* 설명 : 사용자로 부터 어셈블리 파일을 받아서 명령어의 OPCODE를 찾아 출력한다.
* 매계 : 실행 파일, 어셈블리 파일
* 반환 : 성공 = 0, 실패 = < 0
* 주의 : 현재 어셈블리 프로그램의 리스트 파일을 생성하는 루틴은 만들지 않았다.
* 	또한 중간파일을 생성하지 않는다.
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

	make_opcode_output("output");

	/*
	* 추후 프로젝트에서 사용되는 부분
	*
	if(assem_pass2() < 0 ){
	printf(" assem_pass2: 패스2 과정에서 실패하였습니다.  \n") ;
	return -1 ;
	}
	*/

	free_memory();

	printf("프로그램이 실행됐습니다.(엔터 키를 누르시면 종료됩니다.\n");
	getchar();
	return 0;
}

/* ----------------------------------------------------------------------------------
* 설명 : 프로그램 초기화를 위한 자료구조 생성 및 파일을 읽는 함수이다.
* 매계 : 없음
* 반환 : 정상종료 = 0 , 에러 발생 = -1
* 주의 : 각각의 명령어 테이블을 내부에 선언하지 않고 관리를 용이하게 하기
* 위해서 파일 단위로 관리하여 프로그램 초기화를 통해 정보를 읽어 올 수 있도록
* 구현하였다.
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
* Format을 3/4로 작성하지 않은 이유는 다음과 같습니다. 추후 +JSUB과 같은 4형식의 함수가 나올 시
* '+' 문자를 체크한 다음 Format안에 있는 숫자를 +1 해주려는 목적입니다.
*/
int init_inst_file(char *inst_file)
{
	//char str[100]   : inst_file을 한 줄씩 읽어서 str배열에 저장.
	//char delin[]    : '\t'을 delimiter로 사용.
	//char* tmp_token : strtok를 사용하기 위한 변수.
	FILE * file;
	int errno, token_cnt, i = 0;
	char str[100], delim[] = "\t";
	char* tmp_token;

	file = fopen(inst_file, "r");
	if (file == NULL) {
		printf("파일 열기 실패");
		return -1;
	}


	while (1) {
		token_cnt = 0;
		//str을 '\0'으로 초기화한다.
		memset(str, '\0', sizeof(str));

		//fgets로 문자열을 읽어들인다.
		if (fgets(str, 100, file) == NULL)
			break;

		//한줄씩 읽는다.
		//fgets는 \n까지 얻는다. 맨 마지막 \n를 제거해준다.
		int max_len = strlen(str);
		if (max_len > 0) str[max_len - 1] = '\0';

		//inst_table[i]에 inst를 넣을 수 있는 공간을 만들어준다.
		inst_table[i] = (inst *)malloc(sizeof(inst));

		//strtok를 사용하여 str을 tokenizing한다.
		tmp_token = strtok(str, delim);

		/*
		* inst.data는 총 4개의 토큰이 있기 때문에 token_cnt를 사용하였다.
		* 한 줄을 읽고 나면 token_cnt = 4가 되기 때문에 while문을 탈출하고
		* 그 다음 줄을 읽고 다시 tokenizing을 실행한다.
		*/
		while (tmp_token != NULL) {
			int token_len = strlen(tmp_token) + 1;

			if (token_cnt == 4) break;
			switch (token_cnt) {
			//inst_name을 inst_table[i]->inst_name에 저장한다.
			case 0:
				inst_table[i]->inst_name = (char *)malloc(sizeof(char) * token_len);
				strncpy(inst_table[i]->inst_name, tmp_token, token_len);
				break;
			//format을 inst_table[i]->format에 저장한다.
			case 1:
				inst_table[i]->format = atoi(tmp_token);
				break;
			//opcode을 inst_table[i]->opcode에 저장한다.
			case 2:
				strncpy(inst_table[i]->opcode, tmp_token, token_len);
				break;
			//operand_count을 inst_table[i]->operand_count에 저장한다.
			case 3:
				inst_table[i]->operand_count = atoi(tmp_token);
				break;
			}
			//다음 token을 얻기위해 사용
			tmp_token = strtok(NULL, delim);
			token_cnt++;
		}

		i++;
		//inst_table에 들어있는 instruction의 개수를 저장한다.
		inst_count++;
	}

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
	//char str[100]   : input_file을 한 줄씩 읽어서 str배열에 저장.
	FILE * file;
	line_num = 0;
	int errno, i = 0;
	char str[100];

	file = fopen(input_file, "r");
	if (file == NULL) {
		printf("파일 열기 실패");
		return -1;
	}

	while (1) {
		//str을 '\0'으로 초기화한다.
		memset(str, '\0', sizeof(str));

		//fgets로 문자열을 읽어들인다.
		if (fgets(str, 100, file) == NULL)
			break;

		//'.'을 읽으면 SKIP
		if (!strncmp(str, ".", 1)) continue;

		//한줄씩 읽는다.
		//fgets는 \n까지 얻는다. 맨 마지막 \n를 제거해준다.
		int max_len = strlen(str);
		if (max_len > 0) str[max_len - 1] = '\0';

		//input.txt에서 한줄씩 읽어와 input_data에 저장한다. 
		input_data[i] = (char *)malloc(sizeof(char) * max_len);
		strncpy(input_data[i++], &str, max_len);

		//static 변수인 line_num을 1씩 증가시킨다.
		line_num++;
	}

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
	//static int i = 0 : static 변수를 씀으로써 다음에 호출될 때도 index값을 기억.
	//char delim[]     : '\t'을 delimiter로 사용.
	//char *tmp_token, *tmp_token2  : strtok를 사용하기 위한 변수.
	//char *origin_str : strtok후 str이 달라지니 미리 origin_str에 str을 넣어놓고 함수를 빠져나갈 때 str을 복원한다.
	static int i = 0;
	int errno, token_cnt = 0, comma_cnt = 0, origin_tmp_len = 0;
	char delim[] = "\t", delim2[] = ",";
	char *tmp_token, *tmp_token2, *origin_str, *origin_tmp;

	//매개변수로 전달된 문자열의 길이를 max_len에 저장한다.
	int max_len = strlen(str);
	origin_str = (char *)malloc(sizeof(char) * max_len);
	memset(origin_str, '\0', max_len);
	strcpy(origin_str, str);

	//token_table[i]에 token를 넣을 수 있는 공간을 만들어준다.
	token_table[i] = (token *)malloc(sizeof(token));
	//memset(token_table[i], '\0', sizeof(token));

	for (int j = 0; j < 3; j++)
		token_table[i]->operand[j] = "\0";

	//strtok를 사용하여 str을 tokenizing한다.
	tmp_token = strtok(str, delim);

	/*
	* 매개변수 str은 총 4개의 토큰이 있기 때문에 token_cnt를 사용하였다.
	* 한 줄을 읽고 나면 token_cnt = 3이 되기 때문에 while문을 탈출한다.(case2에서 comment를 바로 복사하기 때문에 token_cnt = 3)
	* while문을 돌며 token_table에 label, instruction, operand, comment가 저장된다.
	*/
	while (tmp_token) {
		int token_len = strlen(tmp_token) + 1;
		int j = 0;

		//'.'을 만나면 tokenizing하지 않는다.
		if (!strncmp(tmp_token, ".", 1)) break;

		//str을 모두 tokenizing한 경우 while문을 탈출한다.
		if (token_cnt == 3) break;
		switch (token_cnt) {
		case 0:
		//처음 읽어들인 것이 label인 경우 
			token_table[i]->label = (char *)malloc(sizeof(char) * token_len);
			memset(token_table[i]->label, '\0', token_len);
			strncpy(token_table[i]->label, tmp_token, token_len);
			break;
		//읽어들인 것이 명령어인 경우
		case 1:
			token_table[i]->instruction = (char *)malloc(sizeof(char) * token_len);
			memset(token_table[i]->instruction, '\0', token_len);
			strncpy(token_table[i]->instruction, tmp_token, token_len);
			break;
		//읽어들인 것이 Operand인 경우
		case 2:
			//orgin_tmp에 tmp_token의 내용을 복사한다.(Operand의 ','(comma)를 구분하기 위해 사용한다.)
			origin_tmp_len = strlen(tmp_token);
			origin_tmp = (char *)malloc(sizeof(char) * origin_tmp_len);
			strcpy(origin_tmp, tmp_token);

			//tmp_token2에서 strtok를 사용하기 전에 comment를 미리 token_table에 복사한다.
			//읽어들인 것이 Comment인 경우 다른 곳에서 strtok를 사용하기 전에 comment를 복사한다.
			tmp_token = strtok(NULL, delim);
			token_table[i]->comment = (char *)malloc(sizeof(char) * token_len);
			memset(token_table[i]->comment, '\0', token_len);
			strncpy(token_table[i]->comment, tmp_token, token_len);

			//Operand를 ',' delimiter를 써서 tokenizing한다.
			tmp_token2 = strtok(origin_tmp, delim2);

			//comma(',')를 delimiter로 사용하여 operand[j]에 문자열 넣어준다.
			while (tmp_token2) {
				token_table[i]->operand[j] = (char *)calloc(sizeof(char), token_len);
				memset(token_table[i]->operand[j], "\0", token_len);
				strncpy(token_table[i]->operand[j++], tmp_token2, token_len);
				tmp_token2 = strtok(NULL, delim2);
			}
			break;
		}
		
		tmp_token = strtok(NULL, delim);
		token_cnt++;
	}

	i++;
	token_line++;
	
	//input_data를 유지하기 위해 원래 str을 기억했다가 다시 복사해준다.
	strcpy(str, origin_str);
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
	int errno = -1, i = 0, is_plus = 0, str_len = strlen(str);
	char* tmp_str = str;
	char  plus[] = "+";
	

	//instruction의 첫 문자가 '+'일 경우 tmp_str++을 통해 '+'다음 문자열을 가리키도록 한다.
	//str_len을 1 감소함으로써 문자열의 비교가 가능하게 한다.
	if (strncmp(str, plus, 1) == 0) {
		str_len--;
		tmp_str++;
	}

	//for문을 통해 파라미터로 전달된 문자열과 inst_table에 있는 명령어를 비교한다.
	for (i = 0; i < inst_count; i++) {
		//파라미터로 전달된 문자열과 inst_table에 있는 명령어의 길이가 다르다면 continue;
		if (str_len != strlen(inst_table[i]->inst_name)) continue;
		//파라미터로 전달된 문자열과 inst_table에 있는 명령어의 길이가 같다면
		else {
			//길이가 같으면서 문자열도 같다면 해당 인덱스 값을 리턴한다.
			if (strncmp(tmp_str, inst_table[i]->inst_name, str_len) == 0)
				return i;
		}
	}
	//파라미터로 전달된 문자열과 매칭되는 명령어가 없다면 -1을 리턴한다.
	return errno;
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
	int errno = -1;

	/*
	* input_data의 문자열을 한줄씩 입력 받아서
	* token_parsing()을 호출하여 token_unit에 저장
	*/
	for (int i = 0; i < line_num; i++) {
		errno = token_parsing(input_data[i]);
	}

	return errno;
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
	int tmp, i = 0;

	file = fopen(file_name, "w");
	if (file == NULL) {
		printf("파일 열기 실패");
		return -1;
	}

	//for문을 통해 token_table에 있는 label, instruction, operand 그리고 search_opcode함수를 통해 opcode 출력
	for (i = 0; i < line_num; i++) {
		//label 출력 
		if (strcmp(token_table[i]->label, "")) {
			fprintf(file, token_table[i]->label);
			fprintf(file, "\t");
		}
		else fprintf(file, "\t");

		//instruction 출력
		if (strcmp(token_table[i]->instruction, "")) {
			fprintf(file, token_table[i]->instruction);
			fprintf(file, "\t");
		}
		else fprintf(file, "\t");

		//operand 개수 count
		int cnt = 0;
		for (int j = 0; j < 3; j++) {
			if (strcmp(token_table[i]->operand[j], ""))
				cnt++;
		}

		//operand 출력 
		for (int j = 0; j < cnt; j++) {
			if (strcmp(token_table[i]->operand[j], "")) {
				fprintf(file, token_table[i]->operand[j]);
				if (j < cnt - 1)
					fprintf(file, ",");
			}
		}
		fprintf(file, "\t");

		//search_opcode 함수를 이용하여 opcode 출력
		tmp = search_opcode(token_table[i]->instruction);
		//tmp != -1인 경우(대응하는 opcode가 있는 경우) opcode 출력
		if (tmp != -1)
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
	//inst_table배열의 메모리를 각각 해제한다.
	for (int i = 0; i < inst_count; i++) {
		free(inst_table[i]->inst_name);
		free(inst_table[i]);
	}

	//input_data배열의 메모리를 각각 해제한다.
	for (int i = 0; i < line_num; i++)
		free(input_data[i]);

	//token_table배열의 메모리를 각각 해제한다.
	for (int i = 0; i < token_line; i++) {
		if (strcmp(token_table[i]->label, ""))
			free(token_table[i]->label);

		if (strcmp(token_table[i]->instruction, ""))
			free(token_table[i]->instruction);

		for (int j = 0; j < 3; j++) {
			if (strcmp(token_table[i]->operand[j], ""))
				free(token_table[i]->operand[j]);
		}

		if (strcmp(token_table[i]->comment, ""))
			free(token_table[i]->comment);

		free(token_table[i]);
	}
}

/* --------------------------------------------------------------------------------*
* ------------------------- 추후 프로젝트에서 사용할 함수 --------------------------*
* --------------------------------------------------------------------------------*/


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
