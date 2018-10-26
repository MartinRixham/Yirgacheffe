grammar Yirgacheffe;

compilationUnit:
	packageDeclaration
	importStatement*
	(classDeclaration | interfaceDeclaration)*
	EOF;

replLine: expression? | importStatement* | statement*;

packageDeclaration: (Package packageName semicolon)?;

packageName: Identifier ('.' Identifier)*;

importStatement: Import packageName '.' Identifier semicolon;

classDeclaration:
	(Class | Identifier) Identifier?
	'{'
		(function | interfaceMethodDeclaration | field)*
	'}';

interfaceDeclaration:
	Interface Identifier?
	'{'
		(function | interfaceMethodDeclaration | field)*
	'}';

function:
	(classMethodDeclaration | mainMethodDeclaration | constructorDeclaration)
	block;

classMethodDeclaration: Modifier? type signature;

mainMethodDeclaration: Modifier? type? Main signature;

constructorDeclaration: Modifier? signature;

interfaceMethodDeclaration:
	Modifier? type signature semicolon;

signature: Identifier '(' parameter? (',' parameter)* ')';

field: (fieldInitialisation | fieldDeclaration) semicolon;

fieldInitialisation: fieldDeclaration '=' expression;

fieldDeclaration: Modifier? type? Identifier;

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

equals: notEquals ('==' notEquals)*;

notEquals: subtract ('!=' subtract)*;

subtract: add ('-' add)*;

add: multiply ('+' multiply)*;

multiply: unaryExpression ((multiplicative)  unaryExpression)*;

multiplicative: Remainder | Divide | Multiply;

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
PrimitiveType: 'Void' | 'Bool' | 'Char' | 'Num';
Modifier: 'public' | 'private';
BooleanLiteral: 'true' | 'false';
New: 'new';
This: 'this';
Return: 'return';
Main: 'main';
If: 'if';
Else: 'else';

// operators
Remainder: '%';
Divide: '/';
Multiply: '*';

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
