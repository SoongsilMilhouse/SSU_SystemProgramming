/*
* my_assembler �Լ��� ���� ���� ���� �� ��ũ�θ� ��� �ִ� ��� �����̴�.
*
*/
#pragma warning(disable:4996)
#define _CRT_SECURE_NO_WARNINGS

#define MAX_INST 256
#define MAX_LINES 5000
#define MAX_OPERAND 3

/*
* instruction ��� ���Ϸ� ���� ������ �޾ƿͼ� �����ϴ� ����ü �����̴�.
* ������ ������ instruction set�� ��Ŀ� ���� ���� �����ϵ�
* ���� ���� �ϳ��� instruction�� �����Ѵ�.
*/
struct inst_unit {
	char *inst_name;			//��ɾ� �̸�
	int format;					//����
	char opcode[2];				//Opcode
	int operand_count;			//����ϴ� operand ����
};
typedef struct inst_unit inst;
inst *inst_table[MAX_INST];
int inst_count;					//inst_table�� ����ִ� instruction�� ������ �����Ѵ�.(inst_index > inst_count�� ����)

								/*
								* ����� �� �ҽ��ڵ带 �Է¹޴� ���̺��̴�. ���� ������ ������ �� �ִ�.
								*/
char *input_data[MAX_LINES];
static int line_num;

int label_num;

/*
* ����� �� �ҽ��ڵ带 ��ū������ �����ϱ� ���� ����ü �����̴�.
* operator�� renaming�� ����Ѵ�.
* nixbpe�� 8bit �� ���� 6���� bit�� �̿��Ͽ� n,i,x,b,p,e�� ǥ���Ѵ�.
*/
struct token_unit {
	char *label;
	char *instruction;
	char *operand[MAX_OPERAND];
	char *comment;
	//char nixbpe; // ���� ������Ʈ���� ���ȴ�.

};

typedef struct token_unit token;
token *token_table[MAX_LINES];
static int token_line;

/*
* �ɺ��� �����ϴ� ����ü�̴�.
* �ɺ� ���̺��� �ɺ� �̸�, �ɺ��� ��ġ�� �����ȴ�.
*/
struct symbol_unit {
	char symbol[10];
	int addr;
};

typedef struct symbol_unit symbol;
symbol sym_table[MAX_LINES];

static int locctr;
//--------------

static char *input_file;
static char *output_file;
int init_my_assembler(void);
int init_inst_file(char *inst_file);
int init_input_file(char *input_file);
int token_parsing(char *str);
int search_opcode(char *str);
static int assem_pass1(void);
void make_opcode_output(char *file_name);
//�޸� ������ ���� �Լ�
void free_memory(void);

/* ���� ������Ʈ���� ����ϰ� �Ǵ� �Լ�*/
static int assem_pass2(void);
void make_objectcode_output(char *file_name);