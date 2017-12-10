grammar Yirgacheffe;

compilationUnit:
	packageDeclaration?
	importStatement*
	(malformedDeclaration | classDeclaration | interfaceDeclaration)
	EOF;

packageDeclaration: Package packageName ';';

packageName: Identifier ('.' Identifier)*;

importStatement: Import fullyQualifiedType ';';

classIdentifier: Identifier;

interfaceIdentifier: Identifier;

malformedDeclaration:
	Identifier classIdentifier?
	'{'
		field*
		constructorDeclaration*
		classMethodDeclaration*
		interfaceMethodDeclaration*
	'}';

classDeclaration:
	Class classIdentifier?
	'{'
		field*
		constructorDeclaration*
		classMethodDeclaration*
	'}';

constructorDeclaration:
	Modifier? Identifier '(' parameter? (',' parameter)* ')' '{' '}';

classMethodDeclaration:
	Modifier? type Identifier '(' parameter? (',' parameter)* ')' '{' '}';

interfaceDeclaration:
	Interface interfaceIdentifier?
	'{'
		field*
		interfaceMethodDeclaration*
	'}';

interfaceMethodDeclaration:
	Modifier? type Identifier '(' parameter? (',' parameter)* ')' ';';

field: (fieldDeclaration | fieldInitialisation) ';';

fieldDeclaration: type? Identifier;

fieldInitialisation: fieldDeclaration '=' expression;

parameter: type? Identifier;

type: simpleType | fullyQualifiedType;

simpleType: Identifier | PrimitiveType;

fullyQualifiedType: packageName '.' Identifier;

expression: literal;

literal: BooleanLiteral | CharacterLiteral | IntegerLiteral | DecimalLiteral | StringLiteral;

// keywords
Package: 'package';
Import: 'import';
Class: 'class';
Interface: 'interface';
PrimitiveType: 'void' | 'bool' | 'char' | 'num';
Modifier: 'public' | 'private';
BooleanLiteral: 'true' | 'false';

CharacterLiteral: '\'' SingleCharacter '\'' | '\'' EscapeSequence '\'';

fragment
SingleCharacter: ~['\\\r\n];

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
