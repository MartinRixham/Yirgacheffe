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
		(field | classMethodDeclaration | interfaceMethodDeclaration)*
	closeBlock;

interfaceDeclaration:
	Interface Identifier?
	'{'
		(field | classMethodDeclaration | interfaceMethodDeclaration)*
	closeBlock;

classMethodDeclaration:
	Modifier? (type Identifier | constructorIdentifier) '('
		parameter?(','
		parameter)* closeBracket
	'{'
		(statement semicolon) *
	closeBlock;

interfaceMethodDeclaration:
	Modifier? type Identifier '(' parameter? (',' parameter)* closeBracket semicolon;

constructorIdentifier: Identifier;

field: (fieldInitialisation | fieldDeclaration) semicolon;

fieldInitialisation: fieldDeclaration '=' expression;

fieldDeclaration: type? Identifier;

statement: variableAssignment | variableDeclaration | methodCall | instantiation;

variableAssignment: (variableDeclaration | variableReference) '=' expression;

variableDeclaration: type Identifier;

variableReference: Identifier;

parameter: type? Identifier;

type: simpleType | fullyQualifiedType;

simpleType: Identifier | PrimitiveType;

fullyQualifiedType: packageName '.' Identifier;

methodCall: expression '.' Identifier '(' ')';

expression: instantiation | literal;

instantiation: New type '(' closeBracket;

literal:
	BooleanLiteral |
	CharacterLiteral |
	IntegerLiteral |
	DecimalLiteral |
	StringLiteral;

semicolon: SEMI_COLON?;

closeBlock: CLOSE_BLOCK?;

closeBracket: CLOSE_BRACKET?;

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

CLOSE_BLOCK: '}';

CLOSE_BRACKET: ')';

WHITE_SPACE: [ \t\r\n\u000C]+ -> skip;

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;
