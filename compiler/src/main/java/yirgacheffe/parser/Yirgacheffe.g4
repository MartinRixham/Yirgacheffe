grammar Yirgacheffe;

compilationUnit:
	packageDeclaration
	importStatement*
	(classDefinition | interfaceDeclaration)*
	EOF;

replLine: expression? | importStatement* | statement*;

packageDeclaration: (Package packageName semicolon)?;

packageName: Identifier ('.' Identifier)*;

importStatement: Import packageName '.' Identifier semicolon;

classDefinition: classDeclaration
	'{'
		(function | parallelMethod | interfaceMethodDeclaration | field)*
	'}';

classDeclaration: (Class | Identifier) Identifier? genericTypes? implementation?;

implementation: Implements type*;

interfaceDeclaration:
	Interface Identifier? genericTypes?
	'{'
		(function | interfaceMethodDeclaration | field)*
	'}';

genericTypes: '<' Identifier? (',' Identifier)* '>';

function:
	(classMethodDeclaration | mainMethodDeclaration | constructorDeclaration)
	functionBlock;

parallelMethod:
	parallelMethodDeclaration functionBlock;

functionBlock:
	'{'
		statement*
	'}';

classMethodDeclaration: modifier? returnType signature;

mainMethodDeclaration: modifier? Main signature;

parallelMethodDeclaration: Parallel classMethodDeclaration;

constructorDeclaration: modifier? signature;

interfaceMethodDeclaration: modifier? returnType signature semicolon;

returnType: type;

signature: Identifier '(' parameter? (',' parameter)* ')';

field: (fieldInitialisation | fieldDeclaration) semicolon;

fieldInitialisation: fieldDeclaration '=' expression;

fieldDeclaration: Const? modifier? type? Identifier;

modifier: Public | Private;

statement: block | conditionalStatement | forStatement | (statementLine semicolon);

statementLine:
    unaryStatement |
    fieldWrite |
    variableAssignment |
    variableDeclaration |
    functionCall |
    returnStatement;

unaryStatement:
    preincrementStatement |
    postincrementStatement |
    predecrementStatement |
    postdecrementStatement;

preincrementStatement: PlusPlus expression;

postincrementStatement: expression PlusPlus;

predecrementStatement: MinusMinus expression;

postdecrementStatement: expression MinusMinus;

block: '{' statement* '}';

forStatement: For '(' initialiser ';' exitCondition ';' incrementer ')' statement?;

initialiser: statementLine?;

exitCondition: expression?;

incrementer: statementLine?;

conditionalStatement: ifStatement elseStatement | ifStatement | elseStatement;

ifStatement: If '(' expression ')' statement;

elseStatement: Else statement;

variableAssignment: (variableDeclaration | variableWrite) '=' expression;

variableDeclaration: type Identifier;

variableWrite: Identifier;

parameter: type? Identifier;

type: primaryType typeParameters?;

primaryType: (simpleType | fullyQualifiedType);

simpleType: Identifier | PrimitiveType;

fullyQualifiedType: packageName '.' Identifier;

typeParameters: '<' type? (',' type)* '>';

functionCall: instantiation | (expression methodCall);

methodCall: '.' Identifier arguments;

instantiation: New type arguments;

arguments: '(' expression? (',' expression)* ')';

expression: or;

or: and ('||' and)*;

and: equals ('&&' equals)*;

equals: inequality (equative inequality)*;

equative: NotEqual | Equal;

inequality: add (comparative add)*;

comparative: LessThan | GreaterThan | LessThanOrEqual | GreaterThanOrEqual;

add: multiply (additive multiply)*;

additive: Subtract | Add;

multiply: unaryOperation (multiplicative unaryOperation)*;

multiplicative: Remainder | Divide | Multiply;

unaryOperation:
    postincrement |
    preincrement |
    postdecrement |
    predecrement |
    negation |
    unaryExpression;

negation: Subtract unaryExpression;

postincrement: unaryExpression PlusPlus;

preincrement: PlusPlus unaryExpression;

postdecrement: unaryExpression MinusMinus;

predecrement: MinusMinus unaryExpression;

unaryExpression:
	(instantiation | literal | variableRead | thisRead | parenthesis)
	(fieldRead | methodCall)*;

variableRead: Identifier;

thisRead: This;

fieldRead: '.' Identifier;

parenthesis: '(' expression ')';

fieldWrite: expression '.' Identifier '=' expression;

returnStatement: Return expression?;

literal:
	BooleanLiteral |
	CharacterLiteral |
	IntegerLiteral |
	DecimalLiteral |
	StringLiteral;

semicolon: SEMI_COLON?;

// keywords
Package: 'package';
Import: 'import';
Class: 'class';
Interface: 'interface';
Public: 'public';
Private: 'private';
Const: 'const';
Implements: 'implements';
PrimitiveType: 'Void' | 'Bool' | 'Char' | 'Num';
BooleanLiteral: 'true' | 'false';
New: 'new';
This: 'this';
Return: 'return';
Main: 'main';
If: 'if';
Else: 'else';
For: 'for';
Parallel: 'parallel';

// operators
Remainder: '%';
Divide: '/';
Multiply: '*';
Add: '+';
Subtract: '-';
PlusPlus: '++';
MinusMinus: '--';
Equal: '==';
NotEqual: '!=';
LessThan: '<';
GreaterThan: '>';
LessThanOrEqual: '<=';
GreaterThanOrEqual: '>=';

CharacterLiteral: '\'' StringCharacter '\'';

IntegerLiteral: ('0' | Sign? NonZeroDigit Digit*);

DecimalLiteral: IntegerLiteral '.' Digit+ ;

fragment
Sign: [+-];

fragment
Digit: [0-9];

fragment
NonZeroDigit: [1-9];

StringLiteral: '"' StringCharacter* '"';

fragment
StringCharacter: ~["\\\r\n] | EscapeSequence;

fragment
EscapeSequence:	'\\' [btnfr"'\\];

Identifier: Letter LetterOrDigit*;

fragment
Letter: [a-zA-Z$_];

fragment
LetterOrDigit: [a-zA-Z0-9$_];

SEMI_COLON: ';';

WHITE_SPACE: [ \t\r\n\u000C]+ -> skip;

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;
