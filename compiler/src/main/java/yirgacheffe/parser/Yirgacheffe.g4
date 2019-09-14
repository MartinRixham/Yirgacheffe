grammar Yirgacheffe;

compilationUnit:
	packageDeclaration
	importStatement*
	(classDefinition | interfaceDefinition | enumerationDefinition)*
	EOF;

replLine: expression? | importStatement* | statement*;

packageDeclaration: (Package packageName semicolon)?;

packageName: Identifier ('.' Identifier)*;

importStatement: Import packageName '.' Identifier semicolon;

classDefinition: classDeclaration
	'{'
		(function | parallelMethod | interfaceMethodDeclaration | field)*
	'}';

classDeclaration: (Class | Identifier) Identifier? genericTypes? implementation?;

enumerationDefinition: enumerationDeclaration
	'{'
		(function | parallelMethod | interfaceMethodDeclaration | field | constantConstructor)*
	'}';

enumerationDeclaration: Enumeration Identifier Of type;

implementation: Implements type? (',' type)*;

interfaceDefinition: interfaceDeclaration
	'{'
		(function | interfaceMethodDeclaration | field)*
	'}';

interfaceDeclaration: Interface Identifier? genericTypes?;

genericTypes: '<' Identifier? (',' Identifier)* '>';

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

constantConstructor: literal ':' arguments semicolon;

returnType: type;

signature: Identifier '(' parameter? (',' parameter)* ')';

field: (fieldInitialisation | fieldDeclaration) semicolon;

fieldInitialisation: fieldDeclaration '=' expression;

fieldDeclaration: Const? modifier? type? Identifier;

modifier: Public | Private;

statement: block | conditionalStatement | forStatement | (statementLine semicolon) | SEMI_COLON;

statementLine:
    attemptedStatement |
    unaryStatement |
    fieldWrite |
    variableAssignment |
    variableDeclaration |
    functionCall |
    returnStatement;

attemptedStatement: Try (unaryStatement | functionCall);

unaryStatement:
    preincrementStatement |
    postincrementStatement |
    predecrementStatement |
    postdecrementStatement;

preincrementStatement: PlusPlus expression;

postincrementStatement: expression PlusPlus;

predecrementStatement: MinusMinus expression;

postdecrementStatement: expression MinusMinus;

block: '{' statement* '}';

forStatement: For '(' initialiser ';' exitCondition ';' incrementer ')' statement?;

initialiser: statementLine?;

exitCondition: expression?;

incrementer: statementLine?;

conditionalStatement: ifStatement elseStatement | ifStatement | elseStatement;

ifStatement: If '(' expression ')' statement;

elseStatement: Else statement;

variableAssignment: (variableDeclaration | variableWrite) '=' expression;

variableDeclaration: type Identifier;

variableWrite: Identifier;

parameter: type? Identifier;

type: primaryType typeParameters?;

primaryType: (simpleType | fullyQualifiedType);

simpleType: Identifier | PrimitiveType;

fullyQualifiedType: packageName '.' Identifier;

typeParameters: '<' type? (',' type)* '>';

functionCall: instantiation | selfInstantiation | delegation | (expression methodCall);

methodCall: '.' Identifier arguments;

instantiation: New type arguments;

selfInstantiation: This arguments;

delegation: Delegate arguments;

arguments: '(' expression? (',' expression)* ')';

expression: or;

or: and ('||' and)*;

and: equation ('&&' equation)*;

equation: add (comparative add)*;

comparative: NotEqual | Equal | LessThan | GreaterThan | LessThanOrEqual | GreaterThanOrEqual;

add: multiply (additive multiply)*;

additive: Subtract | Add;

multiply: attemptedExpression (multiplicative attemptedExpression)*;

multiplicative: Remainder | Divide | Multiply;

attemptedExpression: unaryOperation | (Try unaryOperation);

unaryOperation:
    postincrement |
    preincrement |
    postdecrement |
    predecrement |
    negation |
    unaryExpression;

negation: Subtract unaryExpression;

postincrement: unaryExpression PlusPlus;

preincrement: PlusPlus unaryExpression;

postdecrement: unaryExpression MinusMinus;

predecrement: MinusMinus unaryExpression;

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
Enumeration: 'enumeration';
Public: 'public';
Private: 'private';
Const: 'const';
Implements: 'implements';
PrimitiveType: 'Void' | 'Bool' | 'Char' | 'Num';
BooleanLiteral: 'true' | 'false';
New: 'new';
This: 'this';
Return: 'return';
Main: 'main';
If: 'if';
Else: 'else';
For: 'for';
Parallel: 'parallel';
Try: 'try';
Delegate: 'delegate';
Of: 'of';

// operators
Remainder: '%';
Divide: '/';
Multiply: '*';
Add: '+';
Subtract: '-';
PlusPlus: '++';
MinusMinus: '--';
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
