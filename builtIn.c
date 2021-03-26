#include <stdlib.h>
#include <string.h>
#include <stdio.h>

void builtIn_print(char *s)
{
	printf("%s", s);
}
void builtIn_println(char *s)
{
	printf("%s\n",s);
}
void builtIn_printInt(int x)
{
	printf("%d",x);
}
void builtIn_printlnInt(int x)
{
	printf("%d\n",x);
}
int builtIn_getInt()
{
	int tmp;
	scanf("%d",&tmp);
	return tmp;
}
char* builtIn_getString()
{
	char *tmp=malloc(sizeof(char)*1000);
	scanf("%s",tmp);
	return tmp;
}
char* builtIn_toString(int x)
{
	if(x==0){
		char *tmp=(char*)malloc(sizeof(char)*2);
		tmp[0]=48;
		tmp[1]='\0';
		return tmp;
	}
	int a[11];
	int pos=0,neg=0;
	if(x<0){
		neg=1;
		x=-x;
	}
	while(x>0){
		a[pos++]=x%10;
		x/=10;
	}
	char *tmp=(char*)malloc(sizeof(char)*(pos+1+neg));
	if(neg)tmp[0]='-';
	int i=0;
	for(;i<pos;i++)
		tmp[neg+i]=a[pos-i-1]+48;
	tmp[neg+pos]='\0';
	return tmp;
}
int builtIn_string_length(char *s)
{
	return (int)strlen(s);
}
char* builtIn_string_substring(char *s,int L,int R)
{
	char *tmp=malloc(sizeof(char)*1000);
	memcpy(tmp,s+L,R-L);
	tmp[R-L]='\0';
	return tmp;
}
int builtIn_string_parseInt(char *s)
{
	int tmp;
	sscanf(s,"%d",&tmp);
	return tmp;
}
int builtIn_string_ord(char *s,int pos)
{
	return (int)s[pos];
}
char *builtIn_string_add(char *a,char *b)
{
	char *tmp=malloc(sizeof(char)*1000);
	int len=strlen(a);
    	memcpy(tmp,a,len);
    	tmp[len]='\0';
    	strcat(tmp,b);
   	return tmp;
}
int builtIn_string_Less(char *a, char *b)
{
    return strcmp(a,b)<0;
}
int builtIn_string_Greater(char *a, char *b)
{
    return strcmp(a, b)>0;
}
int builtIn_string_LessEqual(char *a, char *b)
{
    return strcmp(a, b)<=0;
}
int builtIn_string_GreaterEqual(char *a, char *b)
{
    return strcmp(a, b)>=0;
}
int builtIn_string_Equal(char *a, char *b)
{
    return strcmp(a, b)==0;
}
int builtIn_string_NotEqual(char *a, char *b)
{
    return strcmp(a, b)!=0;
}
