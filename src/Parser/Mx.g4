grammar Mx;

program : programSegment* EOF;

programSegment : varDef | funcDef | classDef;

varDef : type singleVarDef (',' singleVarDef)* ';';
funcDef : type? Identifier '(' parameterList? ')' suite;
classDef : Class Identifier '{' (varDef|funcDef)* '}' ';';

singleVarDef : Identifier ('=' expression)?;
parameterList : parameter (',' parameter)*;
parameter : type Identifier;

basicType : Bool | Int | String;
type
    : (basicType | Identifier) ('[' ']')*
    | Void
    ;

suite : '{' statement* '}';

statement
    : suite                                                 #block
    | varDef                                                #vardefStmt
    | If '(' expression ')' trueStmt=statement 
        (Else falseStmt=statement)?                         #ifStmt
    | For '(' init=expression? ';' cond=expression? ';'
        incr=expression? ')' statement                      #forStmt
    | While '(' expression ')' statement                    #whileStmt
    | Return expression? ';'                                #returnStmt
    | Break ';'                                             #breakStmt
    | Continue ';'                                          #continueStmt
    | expression ';'                                        #pureExprStmt
    | ';'                                                   #emptyStmt
    ;

expression
    : primary                                               #atomExpr
    | expression '.' Identifier                             #memberExpr
    | <assoc=right> 'new' creator                           #newExpr
    | expression '[' expression ']'                         #subscript
    | expression '(' expressionList? ')'                    #funcCall
    | expression op=('++' | '--')                           #suffixExpr
    | <assoc=right> op=('+' | '-' | '++' | '--') expression #prefixExpr
    | <assoc=right> op=('~' | '!' ) expression              #prefixExpr
    | expression op=('*' | '/' | '%') expression            #binaryExpr
    | expression op=('+' | '-') expression                  #binaryExpr
    | expression op=('<<' | '>>') expression                #binaryExpr
    | expression op=('<' | '>' | '>=' | '<=') expression    #binaryExpr
    | expression op=('==' | '!=' ) expression               #binaryExpr
    | expression op='&' expression                          #binaryExpr
    | expression op='^' expression                          #binaryExpr
    | expression op='|' expression                          #binaryExpr
    | expression '&&' expression                            #binaryExpr
    | expression '||' expression                            #binaryExpr
    | <assoc=right> expression '=' expression               #assignExpr
    ;

expressionList : expression (',' expression)*;

primary
    : '(' expression ')'
    | This
    | Identifier 
    | literal 
    ;

literal
    : DecimalInteger
    | StringLiteral
    | boolValue=(True | False)
    | Null
    ;

creator
     : (basicType | Identifier) ('[' expression ']')+ ('[' ']')+ ('[' expression ']')+ #errorCreator
     | (basicType | Identifier) ('[' expression ']')+ ('[' ']')* #arrayCreator
     | (basicType | Identifier) '(' ')'                          #classCreator
     | (basicType | Identifier)                                  #basicCreator
     ;

Int : 'int';
Bool : 'bool';
String : 'string';
Void : 'void';
Null : 'null';
If : 'if';
Else : 'else';
Return : 'return';
While : 'while';
For : 'for';
Break : 'break';
Continue : 'continue';
Switch: 'switch';
Class : 'class';
New : 'new';
This : 'this';
True : 'true';
False : 'false';

LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';

Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';
LeftShift : '<<';
RightShift : '>>';

Plus : '+';
Minus : '-';
PlusPlus : '++';
MinusMinus : '--';
Star : '*';
Div : '/';
Mod : '%';

And : '&';
Or : '|';
AndAnd : '&&';
OrOr : '||';
Caret : '^';
Not : '!';
Tilde : '~';

Question : '?';
Colon : ':';
Semi : ';';
Comma : ',';

Assign : '=';
Equal : '==';
NotEqual : '!=';

Dot : '.';

StringLiteral: '"' (ESC|.)*? '"';
fragment
ESC: '\\"' | '\\n' | '\\\\';

Identifier
    : [a-zA-Z] [a-zA-Z_0-9]*
    ;

DecimalInteger
    : [1-9] [0-9]*
    | '0'
    ;

Whitespace
    :   [ \t]+
        -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;