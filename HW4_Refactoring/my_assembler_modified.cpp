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
#include "my_assembler_tmp.h"

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
	
	make_opcode_output("output.txt");
	//make_symtab_output("symtab");

	/* ���� �����Ǵ� ���� */
	/*
	make_symtab_output("symtab");		//symbol table ���
	assem_pass2();								//object code ����
	make_objectcode_output("output);	//object code ���
	*/
	//free_memory();

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

	if ((result = init_inst_file("inst.data")) < 0)
		return -1;

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
	FILE *file;
	int errno;
	int i;
	
	file = fopen(inst_file, "r");
	if (file == NULL) {
		printf("���� ���� ����");
		errno = -1;
		return errno;
	}

	for (i = 0; i < sizeof(inst_table) / sizeof(inst *); i++)
		inst_table[i] = (inst*)malloc(sizeof(inst));

	while (EOF != fscanf(file, "%s %d %s %d", 
		inst_table[inst_count]->inst_name,  &inst_table[inst_count]->format, 
		inst_table[inst_count]->opcode,		&inst_table[inst_count]->operand_count))
			inst_count++;
	
	fclose(file);
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
	FILE * file;
	int errno;
	int i;
	int max_len;

	file = fopen(input_file, "r");
	if (file == NULL) {
		printf("���� ���� ����");
		errno = -1;
		return errno;
	}

	while (1) {
		input_data[line_num] = (char*)malloc(sizeof(char) * 100);
		if (fgets(input_data[line_num], 100, file) == NULL)
			break;

		//fgets�� ���� ���๮�� -> �ι��ڷ� ó��
		max_len = strlen(input_data[line_num]);
		if (max_len > 0) input_data[line_num][max_len - 1] = '\0';

		line_num++;
	}
	
	fclose(file);
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
	char* ptr;
	char* tmp = NULL;
	char* operand_tmp = NULL;
	int errno;
	int i, j;

	//. ���� �����ϴ� �ּ��� token_table�� ���� �ʴ´�.
	if (str[0] == '.')
		return errno;

	if (str == NULL) {
		return errno;
	}
	else {
		token_table[token_line] = (token*)malloc(sizeof(token));
		
		//label, instruction, operand, comment > NULL �ʱ�ȭ
		token_table[token_line]->label = NULL;
		token_table[token_line]->instruction = NULL;
		for (i = 0; i < MAX_OPERAND; i++) 
			token_table[token_line]->operand[i] = NULL;
		token_table[token_line]->comment = NULL;

		//label�� ���� ��� -> strtok ����
		if (str[0] == '\t') {
			ptr = strtok(str, "\t");
		}
		else {
			ptr = strtok(str, "\t");
			token_table[token_line]->label = ptr;
			ptr = strtok(NULL, "\t");
		}

		//instruction ����
		token_table[token_line]->instruction = ptr;
		//instruction�� RSUB�� �ƴ� ��� strtok ����("\t\tCOMMENT" ���� strtok�� �� ��� �� ���� '\t'�� �������� ������ ���� ó��)
		if(strcmp(token_table[token_line]->instruction, "RSUB"))
			ptr = strtok(NULL, "\t");

		//instruction�� �ִ� ���
		if (ptr == NULL) {
			token_line++;
			return errno;
		}

		//operand_tmp�� operand ����
		operand_tmp = ptr;
		ptr = strtok(NULL, "\t");

		//comment �ִ� ��� 
		if (ptr != NULL)
			token_table[token_line]->comment = ptr;

		//operand_tmp�� �־���� operand�� �־��ִ� ����(Instruction - "RSUB"�� ��� ����ó��)
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
* ���� : �Է� ���ڿ��� ���� �ڵ������� �˻��ϴ� �Լ��̴�.
* �Ű� : ��ū ������ ���е� ���ڿ�
* ��ȯ : �������� = ���� ���̺� �ε���, ���� < 0
*
* ----------------------------------------------------------------------------------
*/
int search_opcode(char *str)
{
	//�Ķ���ͷ� ���޵� ���ڿ��� ���̸� str_len�� �����Ѵ�.
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
* ���� : token_parsing() �Լ� ���� token_table�� �ִ� ������ �������� symbol_table�� �����Ѵ�.
* �Ű� : ����
* ��ȯ : ����
* -----------------------------------------------------------------------------------
*/
void make_symtab(void)
{

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
	int result;
	int i, j;
	int cnt = 0;

	file = fopen(file_name, "w");
	if (file == NULL) {
		printf("���� ���� ����");
		return;
	}

	for (i = 0; i < token_line; i++) {
		//label ��� 
		if (token_table[i]->label != NULL) {
			fprintf(file, token_table[i]->label);
			fprintf(file, "\t");
		}
		else fprintf(file, "\t");

		//instruction ���
		if (token_table[i]->instruction != NULL) {
			fprintf(file, token_table[i]->instruction);
			fprintf(file, "\t");
		}
		else fprintf(file, "\t");

		//operand ���� count
		cnt = 0;
		for (j = 0; j < 3; j++) {
			if (token_table[i]->operand[j] != NULL)
				cnt++;
		}

		//operand ��� 
		for (j = 0; j < cnt; j++) {
			if (token_table[i]->operand[j] != NULL) {
				fprintf(file, token_table[i]->operand[j]);
				if (j < cnt - 1)
					fprintf(file, ",");
			}
		}
		fprintf(file, "\t");

		//search_opcode �Լ��� �̿��Ͽ� opcode ���
		result = search_opcode(token_table[i]->instruction);
		//result != -1�� ���(�����ϴ� opcode�� �ִ� ���) opcode ���
		if (result != -1)
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
	int i;

	for (i = 0; i < sizeof(inst_table) / sizeof(inst *); i++) 
		free(inst_table[i]);

	for (i = 0;i < line_num; i++) 
		free(input_data[i]);

	for (i = 0; i < token_line; i++) 
		free(token_table[i]);
}

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
* ���� : pass1 ���� �� symbol table�� ����� ������ �Էµ� ���ڿ��� �̸��� ���� ���Ͽ� �����ϴ� �Լ��̴�.
* �Ű� : ������ ������Ʈ ���ϸ�
* ��ȯ : ����
* ���� : ���� ���ڷ� NULL���� ���´ٸ� ���α׷��� ����� ǥ��������� ������
*        ȭ�鿡 ������ش�.
*
* -----------------------------------------------------------------------------------
*/
void make_symtab_output(char *file_name)
{

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
