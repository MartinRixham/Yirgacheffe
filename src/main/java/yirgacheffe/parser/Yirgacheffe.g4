grammar Yirgacheffe;

compilationUnit:
	packageDeclaration?
	importStatement*
	(malformedDeclaration | classDeclaration | interfaceDeclaration)
	EOF;

packageDeclaration: Package packageName ';';

packageName: Identifier ('.' Identifier)*;

importStatement:
	Import fullyQualifiedType ';';

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

constructorDeclaration: Modifier Identifier '(' parameters ')' '{' '}';

classMethodDeclaration: methodDeclaration '{' '}';

fieldDeclaration: type? Identifier ';';

interfaceDeclaration:
	Interface Identifier?
	'{'
		interfaceFieldDeclaration*
		interfaceMethodDeclaration*
	'}';

interfaceFieldDeclaration: type Identifier ';';

interfaceMethodDeclaration: methodDeclaration ';';

methodDeclaration: Modifier? type Identifier '(' parameters ')';

parameters: parameter? (',' parameter)*;

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

Identifier: Letter LetterOrDigit*;

fragment
Letter: [a-zA-Z$_];

fragment
LetterOrDigit: [a-zA-Z0-9$_];

WHITE_SPACE: [ \t\r\n\u000C]+ -> skip;

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;
