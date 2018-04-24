grammar Yirgacheffe;

compilationUnit:
	packageDeclaration
	importStatement*
	(classDeclaration | interfaceDeclaration)*
	EOF;

packageDeclaration: (Package packageName semicolon)?;

packageName: Identifier ('.' Identifier)*;

importStatement: Import packageName '.' Identifier semicolon;

classDeclaration:
	(Class | Identifier) Identifier?
	'{'
		(field | function | interfaceMethodDeclaration)*
	'}';

interfaceDeclaration:
	Interface Identifier?
	'{'
		(field | function | interfaceMethodDeclaration)*
	'}';

function:
	(classMethodDeclaration | mainMethodDeclaration | constructorDeclaration)
	'{'
		(statement semicolon)*
	'}';

classMethodDeclaration: Modifier? type Identifier '(' parameter? (',' parameter)* ')';

mainMethodDeclaration: Modifier? type? Main Identifier '(' parameter? (',' parameter)* ')';

constructorDeclaration: Modifier? Identifier '(' parameter? (',' parameter)* ')';

interfaceMethodDeclaration:
	Modifier? type Identifier '(' parameter? (',' parameter)* ')' semicolon;

field: (fieldInitialisation | fieldDeclaration) semicolon;

fieldInitialisation: fieldDeclaration '=' expression;

fieldDeclaration: type? Identifier;

statement: variableAssignment | variableDeclaration | functionCall | returnStatement;

variableAssignment: (variableDeclaration | variableWrite) '=' expression;

variableDeclaration: type Identifier;

variableWrite: Identifier;

parameter: type? Identifier;

type: primaryType typeParameter?;

primaryType: (simpleType | fullyQualifiedType);

simpleType: Identifier | PrimitiveType;

fullyQualifiedType: packageName '.' Identifier;

typeParameter: '<' type '>';

functionCall: instantiation | (expression methodCall);

methodCall: '.' Identifier arguments;

instantiation: constructor arguments;

constructor: New type;

arguments: '(' expression? (',' expression)* ')';

expression: (instantiation | literal | variableRead | thisRead) (fieldRead | methodCall)*;

variableRead: Identifier;

thisRead: This;

fieldRead: '.' Identifier;

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
PrimitiveType: 'void' | 'bool' | 'char' | 'num';
Modifier: 'public' | 'private';
BooleanLiteral: 'true' | 'false';
New: 'new';
This: 'this';
Return: 'return';
Main: 'main';

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
