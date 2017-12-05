grammar Yirgacheffe;

compilationUnit: typeDeclaration EOF;

typeDeclaration: classDeclaration |	interfaceDeclaration;

classDeclaration: 'class' Identifier body;

interfaceDeclaration: 'interface' Identifier body;

body: '{' '}';

Identifier: JavaLetter JavaLetterOrDigit*;

fragment
JavaLetter: [a-zA-Z$_];

fragment
JavaLetterOrDigit: [a-zA-Z0-9$_];

WS: [ \t\r\n\u000C]+ -> skip;

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;
