grammar Yirgacheffe;

compilationUnit: typeDeclaration EOF;

typeDeclaration: classDeclaration |	interfaceDeclaration;

classDeclaration: 'class' Identifier body;

interfaceDeclaration: 'interface' Identifier body;

body: '{' fieldDeclaration? '}';

fieldDeclaration: Type Identifier ';';

Type: 'int' | 'String';

Identifier: Letter LetterOrDigit*;

fragment
Letter: [a-zA-Z$_];

fragment
LetterOrDigit: [a-zA-Z0-9$_];

WS: [ \t\r\n\u000C]+ -> skip;

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;
