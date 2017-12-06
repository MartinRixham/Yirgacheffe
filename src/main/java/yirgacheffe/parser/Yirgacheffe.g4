grammar Yirgacheffe;

compilationUnit: typeDeclaration EOF;

typeDeclaration: classDeclaration |	interfaceDeclaration;

classDeclaration:
    'class' Identifier?
    '{'
        fieldDeclaration*
        classMethodDeclaration*
    '}';

classMethodDeclaration:
    methodDeclaration '{' '}';

fieldDeclaration: Type? Identifier ';';

interfaceDeclaration:
    'interface' Identifier?
    '{'
        interfaceFieldDeclaration*
        interfaceMethodDeclaration*
    '}';

interfaceFieldDeclaration: Type Identifier ';';

interfaceMethodDeclaration:
     methodDeclaration ';';

methodDeclaration:
    Modifier? Type Identifier '(' parameter? (',' parameter)* ')';

parameter: Type? Identifier;

Type: 'int' | 'String';

Modifier: 'public' | 'private';

Identifier: Letter LetterOrDigit*;

fragment
Letter: [a-zA-Z$_];

fragment
LetterOrDigit: [a-zA-Z0-9$_];

WHITE_SPACE: [ \t\r\n\u000C]+ -> skip;

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;
