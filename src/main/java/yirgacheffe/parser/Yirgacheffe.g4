grammar Yirgacheffe;

compilationUnit:
	packageDeclaration?
	importStatement*
	(malformedDeclaration | classDeclaration | interfaceDeclaration)
	EOF;

packageDeclaration: Package packageName ';';

packageName: Identifier ('.' Identifier)*;

importStatement: Import fullyQualifiedType ';';

malformedDeclaration:
	Identifier Identifier?
	'{'
		fieldDeclaration*
		constructorDeclaration*
		classMethodDeclaration*
		interfaceMethodDeclaration*
	'}';

classDeclaration:
	Class Identifier?
	'{'
		fieldDeclaration*
		constructorDeclaration*
		classMethodDeclaration*
	'}';

constructorDeclaration:
	Modifier? Identifier '(' parameter? (',' parameter)* ')' '{' '}';

classMethodDeclaration:
	Modifier? type Identifier '(' parameter? (',' parameter)* ')' '{' '}';

fieldDeclaration: type? Identifier ('=' StringLiteral)? ';';

interfaceDeclaration:
	Interface Identifier?
	'{'
		interfaceFieldDeclaration*
		interfaceMethodDeclaration*
	'}';

interfaceFieldDeclaration: type Identifier ('=' StringLiteral)? ';';

interfaceMethodDeclaration:
	Modifier? type Identifier '(' parameter? (',' parameter)* ')' ';';

parameter: type? Identifier;

type: simpleType | fullyQualifiedType;

simpleType: Identifier | PrimitiveType;

fullyQualifiedType: packageName '.' Identifier;

// keywords
Package: 'package';
Import: 'import';
Class: 'class';
Interface: 'interface';
PrimitiveType: 'void' | 'bool' | 'char' | 'num';
Modifier: 'public' | 'private';

StringLiteral: '"' StringCharacters? '"';

fragment
StringCharacters: StringCharacter+;

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
