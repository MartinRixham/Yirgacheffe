grammar Yirgacheffe;

compilationUnit:
	packageDeclaration
	importStatement*
	(classDeclaration | interfaceDeclaration)
	EOF;

packageDeclaration: (Package packageName semicolon)?;

packageName: Identifier ('.' Identifier)*;

importStatement: Import fullyQualifiedType semicolon;

classDeclaration:
	(Class | Identifier) Identifier?
	'{'
		(field | method | interfaceMethodDeclaration)*
	'}';

interfaceDeclaration:
	Interface Identifier?
	'{'
		(field | method | interfaceMethodDeclaration)*
	'}';

method: classMethodDeclaration '{' (statement semicolon)* '}';

classMethodDeclaration:
	Modifier? (type Identifier | constructorIdentifier) '('
		parameter?(','
		parameter)* ')';

interfaceMethodDeclaration:
	Modifier? type Identifier '(' parameter? (',' parameter)* ')' semicolon;

constructorIdentifier: Identifier;

field: (fieldInitialisation | fieldDeclaration) semicolon;

fieldInitialisation: fieldDeclaration '=' expression;

fieldDeclaration: type? Identifier;

statement: variableAssignment | variableDeclaration | methodCall | instantiation;

variableAssignment: (variableDeclaration | variableWrite) '=' expression;

variableDeclaration: type Identifier;

variableWrite: Identifier;

parameter: type? Identifier;

type: simpleType | fullyQualifiedType;

simpleType: Identifier | PrimitiveType;

fullyQualifiedType: packageName '.' Identifier;

methodCall: expression '.' Identifier '(' expression? (',' expression)* ')';

expression: instantiation | literal | variableRead;

variableRead: Identifier;

instantiation: New type '(' ')';

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
