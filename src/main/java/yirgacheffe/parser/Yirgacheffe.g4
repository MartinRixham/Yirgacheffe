grammar Yirgacheffe;

compilationUnit: (malformedDeclaration | classDeclaration | interfaceDeclaration) EOF;

malformedDeclaration:
    Identifier Identifier?
    '{'
        fieldDeclaration*
        classMethodDeclaration*
        interfaceMethodDeclaration*
    '}';

classDeclaration:
    'class' Identifier?
    '{'
        fieldDeclaration*
        classMethodDeclaration*
    '}';

classMethodDeclaration: methodDeclaration '{' '}';

fieldDeclaration: type? Identifier ';';

interfaceDeclaration:
    'interface' Identifier?
    '{'
        interfaceFieldDeclaration*
        interfaceMethodDeclaration*
    '}';

interfaceFieldDeclaration: type Identifier ';';

interfaceMethodDeclaration: methodDeclaration ';';

methodDeclaration: Modifier? type Identifier '(' parameter? (',' parameter)* ')';

parameter: type? Identifier;

type: Identifier | PrimitiveType;

PrimitiveType: 'bool' | 'char' | 'int';

Modifier: 'public' | 'private';

Identifier: Letter LetterOrDigit*;

fragment
Letter: [a-zA-Z$_];

fragment
LetterOrDigit: [a-zA-Z0-9$_];

WHITE_SPACE: [ \t\r\n\u000C]+ -> skip;

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;
