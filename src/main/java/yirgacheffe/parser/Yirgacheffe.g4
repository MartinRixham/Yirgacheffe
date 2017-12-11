grammar Yirgacheffe;

compilationUnit:
	packageDeclaration?
	importStatement*
	(classDeclaration | interfaceDeclaration)
	EOF;

packageDeclaration: Package packageName semiColon;

packageName: Identifier ('.' Identifier)*;

importStatement: Import fullyQualifiedType semiColon;

classDeclaration:
	(Class | Identifier) Identifier?
	'{'
		(field | classMethodDeclaration)*
	closeBlock;

interfaceDeclaration:
	Interface Identifier?
	'{'
		(field | interfaceMethodDeclaration)*
	closeBlock;

classMethodDeclaration:
	Modifier? (type Identifier | constructorIdentifier) '(' parameter? (',' parameter)* ')'
	'{'
	closeBlock;

interfaceMethodDeclaration:
	Modifier? type Identifier '(' parameter? (',' parameter)* ')' semiColon;

constructorIdentifier: Identifier;

field: (fieldInitialisation | fieldDeclaration) semiColon;

fieldDeclaration: type? Identifier;

fieldInitialisation: fieldDeclaration '=' expression;

parameter: type? Identifier;

type: simpleType | fullyQualifiedType;

simpleType: Identifier | PrimitiveType;

fullyQualifiedType: packageName '.' Identifier;

expression: literal;

literal:
	BooleanLiteral |
	CharacterLiteral |
	IntegerLiteral |
	DecimalLiteral |
	StringLiteral;

semiColon: SEMI_COLON?;

closeBlock: CLOSE_BLOCK?;

// keywords
Package: 'package';
Import: 'import';
Class: 'class';
Interface: 'interface';
PrimitiveType: 'void' | 'bool' | 'char' | 'num';
Modifier: 'public' | 'private';
BooleanLiteral: 'true' | 'false';

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

WHITE_SPACE: [ \t\r\n\u000C]+ -> skip;

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;

SEMI_COLON: ';';

CLOSE_BLOCK: '}';
