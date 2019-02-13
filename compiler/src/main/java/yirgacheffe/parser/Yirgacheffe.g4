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

classDeclaration: (Class | Identifier) Identifier? implementation?;

implementation: Implements type*;

interfaceDeclaration:
	Interface Identifier?
	'{'
		(function | interfaceMethodDeclaration | field)*
	'}';

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

fieldDeclaration: modifier? type? Identifier;

modifier: Public | Private;

statement:
	block | conditionalStatement |
	((fieldWrite | variableAssignment | variableDeclaration | functionCall | returnStatement)
	semicolon);

block: '{' statement* '}';

conditionalStatement: ifStatement elseStatement | ifStatement | elseStatement;

ifStatement: 'if' '(' expression ')' statement;

elseStatement: 'else' statement;

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

multiply: unaryExpression (multiplicative unaryExpression)*;

multiplicative: Remainder | Divide | Multiply;

unaryExpression:
	(instantiation | literal | variableRead | thisRead | parenthesis | negation)
	(fieldRead | methodCall)*;

variableRead: Identifier;

thisRead: This;

fieldRead: '.' Identifier;

parenthesis: '(' expression ')';

negation: Subtract unaryExpression;

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
Implements: 'implements';
PrimitiveType: 'Void' | 'Bool' | 'Char' | 'Num';
BooleanLiteral: 'true' | 'false';
New: 'new';
This: 'this';
Return: 'return';
Main: 'main';
If: 'if';
Else: 'else';
Parallel: 'parallel';

// operators
Remainder: '%';
Divide: '/';
Multiply: '*';
Add: '+';
Subtract: '-';
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
