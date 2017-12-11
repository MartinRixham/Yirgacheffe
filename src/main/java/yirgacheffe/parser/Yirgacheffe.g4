grammar Yirgacheffe;

compilationUnit:
	packageDeclaration?
	importStatement*
	(classDeclaration | interfaceDeclaration)
	EOF;

packageDeclaration: Package packageName ';';

packageName: Identifier ('.' Identifier)*;

importStatement: Import fullyQualifiedType ';';

classDeclaration:
	(Class | Identifier) Identifier?
	'{'
		(field | classMethodDeclaration)*
	'}';

interfaceDeclaration:
	Interface Identifier?
	'{'
		(field | interfaceMethodDeclaration)*
	'}';

classMethodDeclaration:
	Modifier? (type Identifier | constructorIdentifier) '(' parameter? (',' parameter)* ')'
	'{'
	'}';

interfaceMethodDeclaration:
	Modifier? type Identifier '(' parameter? (',' parameter)* ')' ';';

constructorIdentifier: Identifier;

field: (fieldDeclaration | fieldInitialisation) ';';

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
