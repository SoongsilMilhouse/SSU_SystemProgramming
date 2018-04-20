/*
* ȭ�ϸ� : my_assembler_00000000.c
* ��  �� : �� ���α׷��� SIC/XE �ӽ��� ���� ������ Assembler ���α׷��� ���η�ƾ����,
* �Էµ� ������ �ڵ� ��, ��ɾ �ش��ϴ� OPCODE�� ã�� ����Ѵ�.
* ���� ������ ���Ǵ� ���ڿ� "00000000"���� �ڽ��� �й��� �����Ѵ�.
*/

/*
*
* ���α׷��� ����� �����Ѵ�.
*
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>


// ���ϸ��� "00000000"�� �ڽ��� �й����� ������ ��.
#include "my_assembler_20121096.h"

/* ----------------------------------------------------------------------------------
* ���� : ����ڷ� ���� ����� ������ �޾Ƽ� ��ɾ��� OPCODE�� ã�� ����Ѵ�.
* �Ű� : ���� ����, ����� ����
* ��ȯ : ���� = 0, ���� = < 0
* ���� : ���� ����� ���α׷��� ����Ʈ ������ �����ϴ� ��ƾ�� ������ �ʾҴ�.
*		   ���� �߰������� �������� �ʴ´�.
* ----------------------------------------------------------------------------------
*/
int main(int args, char *arg[])
{

	if (init_my_assembler()< 0)
	{
		printf("init_my_assembler: ���α׷� �ʱ�ȭ�� ���� �߽��ϴ�.\n");
		return -1;
	}

	if (assem_pass1() < 0) {
		printf("assem_pass1: �н�1 �������� �����Ͽ����ϴ�.  \n");
		return -1;
	}

	make_opcode_output("output_20121096");

	/*
	* ���� ������Ʈ���� ���Ǵ� �κ�
	*
	if(assem_pass2() < 0 ){
	printf(" assem_pass2: �н�2 �������� �����Ͽ����ϴ�.  \n") ;
	return -1 ;
	}
	*/

	free_memory();

	printf("���α׷��� ����ƽ��ϴ�.(���� Ű�� �����ø� ����˴ϴ�.\n");
	getchar();
	return 0;
}

/* ----------------------------------------------------------------------------------
* ���� : ���α׷� �ʱ�ȭ�� ���� �ڷᱸ�� ���� �� ������ �д� �Լ��̴�.
* �Ű� : ����
* ��ȯ : �������� = 0 , ���� �߻� = -1
* ���� : ������ ��ɾ� ���̺��� ���ο� �������� �ʰ� ������ �����ϰ� �ϱ�
*		   ���ؼ� ���� ������ �����Ͽ� ���α׷� �ʱ�ȭ�� ���� ������ �о� �� �� �ֵ���
*		   �����Ͽ���.
* ----------------------------------------------------------------------------------
*/
int init_my_assembler(void)
{
	int result;

	if ((result = init_inst_file("inst.data")) < 0) {
		getchar();
		return -1;

	}
	if ((result = init_input_file("input.txt")) < 0)
		return -1;
	return result;
}

/* ----------------------------------------------------------------------------------
* ���� : �ӽ��� ���� ��� �ڵ��� ������ �о� ���� ��� ���̺�(inst_table)��
*        �����ϴ� �Լ��̴�.
* �Ű� : ���� ��� ����
* ��ȯ : �������� = 0 , ���� < 0
* ���� : ���� ������� ������ �����Ӱ� �����Ѵ�. ���ô� ������ ����.
*
*	===============================================================================
*		   | �̸� | ���� | ���� �ڵ� | ���۷����� ���� | NULL|
*	===============================================================================
*
* ----------------------------------------------------------------------------------
* �߰� ���� : inst.data������ �Ʒ��� ���� �����Ǿ� �ִ�.
*
*			�Լ��� | Format | Opcode | ���۷��� ����
*			  ADD  |   3    |   18   |       1
*
*	Format�� 3/4�� �ۼ����� ���� ������ ������ �����ϴ�. ���� +JSUB�� ���� 4������ �Լ��� ���� ��
* '+' ���ڸ� üũ�� ���� Format�ȿ� �ִ� ���ڸ� +1 ���ַ��� �����Դϴ�.
*/
int init_inst_file(char *inst_file)
{
	//char str[100]   : inst_file�� �� �پ� �о str�迭�� ����.
	//char delin[]    : '\t'�� delimiter�� ���.
	//char* tmp_token : strtok�� ����ϱ� ���� ����.
	FILE * file;
	int errno, token_cnt, i = 0;
	char str[100], delim[] = "\t";
	char* tmp_token;

	file = fopen(inst_file, "r");
	if (file == NULL) {
		printf("���� ���� ����");
		return -1;
	}


	while (1) {
		token_cnt = 0;
		//str�� '\0'���� �ʱ�ȭ�Ѵ�.
		memset(str, '\0', sizeof(str));

		//fgets�� ���ڿ��� �о���δ�.
		if (fgets(str, 100, file) == NULL)
			break;

		//���پ� �д´�.
		//fgets�� \n���� ��´�. �� ������ \n�� �������ش�.
		int max_len = strlen(str);
		if (max_len > 0) str[max_len - 1] = '\0';

		//inst_table[i]�� inst�� ���� �� �ִ� ������ ������ش�.
		inst_table[i] = (inst *)malloc(sizeof(inst));

		//strtok�� ����Ͽ� str�� tokenizing�Ѵ�.
		tmp_token = strtok(str, delim);

		/*
		* inst.data�� �� 4���� ��ū�� �ֱ� ������ token_cnt�� ����Ͽ���.
		* �� ���� �а� ���� token_cnt = 4�� �Ǳ� ������ while���� Ż���ϰ�
		* �� ���� ���� �а� �ٽ� tokenizing�� �����Ѵ�.
		*/
		while (tmp_token != NULL) {
			int token_len = strlen(tmp_token) + 1;

			if (token_cnt == 4) break;
			switch (token_cnt) {
				//inst_name�� inst_table[i]->inst_name�� �����Ѵ�.
			case 0:
				inst_table[i]->inst_name = (char *)malloc(sizeof(char) * token_len);
				strncpy(inst_table[i]->inst_name, tmp_token, token_len);
				break;
				//format�� inst_table[i]->format�� �����Ѵ�.
			case 1:
				inst_table[i]->format = atoi(tmp_token);
				break;
				//opcode�� inst_table[i]->opcode�� �����Ѵ�.
			case 2:
				strncpy(inst_table[i]->opcode, tmp_token, token_len);
				break;
				//operand_count�� inst_table[i]->operand_count�� �����Ѵ�.
			case 3:
				inst_table[i]->operand_count = atoi(tmp_token);
				break;
			}
			//���� token�� ������� ���
			tmp_token = strtok(NULL, delim);
			token_cnt++;
		}

		i++;
		//inst_table�� ����ִ� instruction�� ������ �����Ѵ�.
		inst_count++;
	}

	return errno;
}

/* ----------------------------------------------------------------------------------
* ���� : ����� �� �ҽ��ڵ带 �о� �ҽ��ڵ� ���̺�(input_data)�� �����ϴ� �Լ��̴�.
* �Ű� : ������� �ҽ����ϸ�
* ��ȯ : �������� = 0 , ���� < 0
* ���� : ���δ����� �����Ѵ�.
*
* ----------------------------------------------------------------------------------
*/
int init_input_file(char *input_file)
{
	//char str[100]   : input_file�� �� �پ� �о str�迭�� ����.
	FILE * file;
	line_num = 0;
	int errno, i = 0;
	char str[100];

	file = fopen(input_file, "r");
	if (file == NULL) {
		printf("���� ���� ����");
		return -1;
	}

	while (1) {
		//str�� '\0'���� �ʱ�ȭ�Ѵ�.
		memset(str, '\0', sizeof(str));

		//fgets�� ���ڿ��� �о���δ�.
		if (fgets(str, 100, file) == NULL)
			break;

		//'.'�� ������ SKIP
		if (!strncmp(str, ".", 1)) continue;

		//���پ� �д´�.
		//fgets�� \n���� ��´�. �� ������ \n�� �������ش�.
		int max_len = strlen(str);
		if (max_len > 0) str[max_len - 1] = '\0';

		//input.txt���� ���پ� �о�� input_data�� �����Ѵ�. 
		input_data[i] = (char *)malloc(sizeof(char) * max_len);
		strncpy(input_data[i++], &str, max_len);

		//static ������ line_num�� 1�� ������Ų��.
		line_num++;
	}

	return errno;
}

/* ----------------------------------------------------------------------------------
* ���� : �ҽ� �ڵ带 �о�� ��ū������ �м��ϰ� ��ū ���̺��� �ۼ��ϴ� �Լ��̴�.
*        �н� 1�� ���� ȣ��ȴ�.
* �Ű� : �Ľ��� ���ϴ� ���ڿ�
* ��ȯ : �������� = 0 , ���� < 0
* ���� : my_assembler ���α׷������� ���δ����� ��ū �� ������Ʈ ������ �ϰ� �ִ�.
* ----------------------------------------------------------------------------------
*/
int token_parsing(char *str)
{
	//static int i = 0 : static ������ �����ν� ������ ȣ��� ���� index���� ���.
	//char delim[]     : '\t'�� delimiter�� ���.
	//char *tmp_token, *tmp_token2  : strtok�� ����ϱ� ���� ����.
	//char *origin_str : strtok�� str�� �޶����� �̸� origin_str�� str�� �־���� �Լ��� �������� �� str�� �����Ѵ�.
	static int i = 0;
	int errno, token_cnt = 0, comma_cnt = 0, origin_tmp_len = 0;
	char delim[] = "\t", delim2[] = ",";
	char *tmp_token, *tmp_token2, *origin_str, *origin_tmp;

	//�Ű������� ���޵� ���ڿ��� ���̸� max_len�� �����Ѵ�.
	int max_len = strlen(str);
	origin_str = (char *)malloc(sizeof(char) * max_len);
	memset(origin_str, '\0', max_len);
	strcpy(origin_str, str);

	//token_table[i]�� token�� ���� �� �ִ� ������ ������ش�.
	token_table[i] = (token *)malloc(sizeof(token));
	//memset(token_table[i], '\0', sizeof(token));

	for (int j = 0; j < 3; j++)
		token_table[i]->operand[j] = "\0";

	//strtok�� ����Ͽ� str�� tokenizing�Ѵ�.
	tmp_token = strtok(str, delim);

	/*
	* �Ű����� str�� �� 4���� ��ū�� �ֱ� ������ token_cnt�� ����Ͽ���.
	* �� ���� �а� ���� token_cnt = 3�� �Ǳ� ������ while���� Ż���Ѵ�.(case2���� comment�� �ٷ� �����ϱ� ������ token_cnt = 3)
	* while���� ���� token_table�� label, instruction, operand, comment�� ����ȴ�.
	*/
	while (tmp_token) {
		int token_len = strlen(tmp_token) + 1;
		int j = 0;

		//'.'�� ������ tokenizing���� �ʴ´�.
		if (!strncmp(tmp_token, ".", 1)) break;

		//str�� ��� tokenizing�� ��� while���� Ż���Ѵ�.
		if (token_cnt == 3) break;
		switch (token_cnt) {
		case 0:
			//ó�� �о���� ���� label�� �ƴ� ���, search_opcode �Լ��� ����ؼ� ��ɾ��� ���� Ȯ���� �� token�� �����Ѵ�.
			if ((search_opcode(tmp_token)) != -1) {
				//label �ʱ�ȭ
				token_table[i]->label = (char *)malloc(sizeof(char) * token_len);
				memset(token_table[i]->label, '\0', token_len);

				//instruction ����
				token_table[i]->instruction = (char *)malloc(sizeof(char) * token_len);
				memset(token_table[i]->instruction, '\0', token_len);
				strncpy(token_table[i]->instruction, tmp_token, token_len);
				token_cnt++;
				break;
			}
			//ó�� �о���� ���� label�� ��� 
			else {
				token_table[i]->label = (char *)malloc(sizeof(char) * token_len);
				memset(token_table[i]->label, '\0', token_len);
				strncpy(token_table[i]->label, tmp_token, token_len);
				break;
			}
			//�о���� ���� ��ɾ��� ���
		case 1:
			token_table[i]->instruction = (char *)malloc(sizeof(char) * token_len);
			memset(token_table[i]->instruction, '\0', token_len);
			strncpy(token_table[i]->instruction, tmp_token, token_len);
			break;
			//�о���� ���� Operand�� ���
		case 2:
			//orgin_tmp�� tmp_token�� ������ �����Ѵ�.(Operand�� ','(comma)�� �����ϱ� ���� ����Ѵ�.)
			origin_tmp_len = strlen(tmp_token);
			origin_tmp = (char *)malloc(sizeof(char) * origin_tmp_len);
			strcpy(origin_tmp, tmp_token);

			//tmp_token2���� strtok�� ����ϱ� ���� comment�� �̸� token_table�� �����Ѵ�.
			//�о���� ���� Comment�� ��� �ٸ� ������ strtok�� ����ϱ� ���� comment�� �����Ѵ�.
			tmp_token = strtok(NULL, delim);
			token_table[i]->comment = (char *)malloc(sizeof(char) * token_len);
			memset(token_table[i]->comment, '\0', token_len);
			strncpy(token_table[i]->comment, tmp_token, token_len);

			//Operand�� ',' delimiter�� �Ἥ tokenizing�Ѵ�.
			tmp_token2 = strtok(origin_tmp, delim2);

			//comma(',')�� delimiter�� ����Ͽ� operand[j]�� ���ڿ� �־��ش�.
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
	//input_data�� �����ϱ� ���� ���� str�� ����ߴٰ� �ٽ� �������ش�.
	strcpy(str, origin_str);
	return errno;
}


/* ----------------------------------------------------------------------------------
* ���� : �Է� ���ڿ��� ���� �ڵ������� �˻��ϴ� �Լ��̴�.
* �Ű� : ��ū ������ ���е� ���ڿ�
* ��ȯ : �������� = ���� ���̺� �ε���, ���� < 0
* ���� :
*
* ----------------------------------------------------------------------------------
*/
int search_opcode(char *str)
{
	//�Ķ���ͷ� ���޵� ���ڿ��� ���̸� str_len�� �����Ѵ�.
	int i = 0, is_plus = 0, str_len = strlen(str);
	char* tmp_str = str;
	char  plus[] = "+";

	//instruction�� ù ���ڰ� '+'�� ��� tmp_str++�� ���� '+'���� ���ڿ��� ����Ű���� �Ѵ�.
	//str_len�� 1 ���������ν� ���ڿ��� �񱳰� �����ϰ� �Ѵ�.
	if (strncmp(str, plus, 1) == 0) {
		str_len--;
		tmp_str++;
	}

	//for���� ���� �Ķ���ͷ� ���޵� ���ڿ��� inst_table�� �ִ� ��ɾ ���Ѵ�.
	for (i = 0; i < inst_count; i++) {
		//�Ķ���ͷ� ���޵� ���ڿ��� inst_table�� �ִ� ��ɾ��� ���̰� �ٸ��ٸ� continue;
		if (str_len != strlen(inst_table[i]->inst_name)) continue;
		//�Ķ���ͷ� ���޵� ���ڿ��� inst_table�� �ִ� ��ɾ��� ���̰� ���ٸ�
		else {
			//���̰� �����鼭 ���ڿ��� ���ٸ� �ش� �ε��� ���� �����Ѵ�.
			if (strncmp(tmp_str, inst_table[i]->inst_name, str_len) == 0)
				return i;
		}
	}
	//�Ķ���ͷ� ���޵� ���ڿ��� ��Ī�Ǵ� ��ɾ ���ٸ� -1�� �����Ѵ�.
	return -1;
}

/* ----------------------------------------------------------------------------------
* ���� : ����� �ڵ带 ���� �н�1������ �����ϴ� �Լ��̴�.
*		   �н�1������..
*		   1. ���α׷� �ҽ��� ��ĵ�Ͽ� �ش��ϴ� ��ū������ �и��Ͽ� ���α׷� ��	�κ� ��ū
*		   ���̺��� �����Ѵ�.
*
* �Ű� : ����
* ��ȯ : ���� ���� = 0 , ���� = < 0
* ���� : ���� �ʱ� ���������� ������ ���� �˻縦 ���� �ʰ� �Ѿ �����̴�.
*	  ���� ������ ���� �˻� ��ƾ�� �߰��ؾ� �Ѵ�.
*
* -----------------------------------------------------------------------------------
*/
static int assem_pass1(void)
{
	int i = 0;

	/*
	* input_data�� ���ڿ��� ���پ� �Է� �޾Ƽ�
	* token_parsing()�� ȣ���Ͽ� token_unit�� ����
	*/
	for (i = 0; i < line_num; i++) {
		token_parsing(input_data[i]);
	}
}


/* ----------------------------------------------------------------------------------
* ���� : �Էµ� ���ڿ��� �̸��� ���� ���Ͽ� ���α׷��� ����� �����ϴ� �Լ��̴�.
*        ���⼭ ��µǴ� ������ ��ɾ� ���� OPCODE�� ��ϵ� ǥ(���� 4��) �̴�.
* �Ű� : ������ ������Ʈ ���ϸ�
* ��ȯ : ����
* ���� : ���� ���ڷ� NULL���� ���´ٸ� ���α׷��� ����� ǥ��������� ������
*        ȭ�鿡 ������ش�.
*        ���� ���� 4�������� ���̴� �Լ��̹Ƿ� ������ ������Ʈ������ ������ �ʴ´�.
* -----------------------------------------------------------------------------------
*/
void make_opcode_output(char *file_name)
{
	FILE * file;
	int tmp, i = 0;

	file = fopen(file_name, "w");
	if (file == NULL) {
		printf("���� ���� ����");
		return -1;
	}

	//for���� ���� token_table�� �ִ� label, instruction, operand �׸��� search_opcode�Լ��� ���� opcode ���
	for (i = 0; i < line_num; i++) {
		//label ��� 
		if (strcmp(token_table[i]->label, "")) {
			fprintf(file, token_table[i]->label);
			fprintf(file, "\t");
		}
		else fprintf(file, "\t");

		//instruction ���
		if (strcmp(token_table[i]->instruction, "")) {
			fprintf(file, token_table[i]->instruction);
			fprintf(file, "\t");
		}
		else fprintf(file, "\t");

		//operand ���� count
		int cnt = 0;
		for (int j = 0; j < 3; j++) {
			if (strcmp(token_table[i]->operand[j], ""))
				cnt++;
		}

		//operand ��� 
		for (int j = 0; j < cnt; j++) {
			if (strcmp(token_table[i]->operand[j], "")) {
				fprintf(file, token_table[i]->operand[j]);
				if (j < cnt - 1)
					fprintf(file, ",");
			}
		}
		fprintf(file, "\t");

		//search_opcode �Լ��� �̿��Ͽ� opcode ���
		tmp = search_opcode(token_table[i]->instruction);
		//tmp != -1�� ���(�����ϴ� opcode�� �ִ� ���) opcode ���
		if (tmp != -1)
			fprintf(file, "%s", inst_table[(search_opcode(token_table[i]->instruction))]->opcode);

		fprintf(file, "\n");
	}

	fclose(file);
}
/* ----------------------------------------------------------------------------------
* ���� : �������� �Ҵ��� �޸𸮸� ��� �����Ѵ�.
* �Ű� : ����
* ��ȯ : ����
* -----------------------------------------------------------------------------------
*/
void free_memory(void)
{
	//inst_table�迭�� �޸𸮸� ���� �����Ѵ�.
	for (int i = 0; i < inst_count; i++) {
		free(inst_table[i]->inst_name);
		free(inst_table[i]);
	}

	//input_data�迭�� �޸𸮸� ���� �����Ѵ�.
	for (int i = 0; i < line_num; i++)
		free(input_data[i]);

	//token_table�迭�� �޸𸮸� ���� �����Ѵ�.
	for (int i = 0; i < token_line; i++) {
		if (strcmp(token_table[i]->label, "\0"))
			free(token_table[i]->label);

		if (strcmp(token_table[i]->instruction, "\0"))
			free(token_table[i]->instruction);

		for (int j = 0; j < 3; j++) {
			if (strcmp(token_table[i]->operand[j], "\0"))
				free(token_table[i]->operand[j]);
		}

		if (strcmp(token_table[i]->comment, "\0"))
			free(token_table[i]->comment);

		free(token_table[i]);
	}
}

/* --------------------------------------------------------------------------------*
* ------------------------- ���� ������Ʈ���� ����� �Լ� --------------------------*
* --------------------------------------------------------------------------------*/


/* ----------------------------------------------------------------------------------
* ���� : ����� �ڵ带 ���� �ڵ�� �ٲٱ� ���� �н�2 ������ �����ϴ� �Լ��̴�.
*		   �н� 2������ ���α׷��� ����� �ٲٴ� �۾��� ���� ������ ����ȴ�.
*		   ������ ���� �۾��� ����Ǿ� ����.
*		   1. ������ �ش� ����� ��ɾ ����� �ٲٴ� �۾��� �����Ѵ�.
* �Ű� : ����
* ��ȯ : �������� = 0, �����߻� = < 0
* ���� :
* -----------------------------------------------------------------------------------
*/
static int assem_pass2(void)
{

	/* add your code here */

}

/* ----------------------------------------------------------------------------------
* ���� : �Էµ� ���ڿ��� �̸��� ���� ���Ͽ� ���α׷��� ����� �����ϴ� �Լ��̴�.
*        ���⼭ ��µǴ� ������ object code (������Ʈ 1��) �̴�.
* �Ű� : ������ ������Ʈ ���ϸ�
* ��ȯ : ����
* ���� : ���� ���ڷ� NULL���� ���´ٸ� ���α׷��� ����� ǥ��������� ������
*        ȭ�鿡 ������ش�.
*
* -----------------------------------------------------------------------------------
*/
void make_objectcode_output(char *file_name)
{
	/* add your code here */

}
