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
    Class Identifier?
    '{'
        fieldDeclaration*
        classMethodDeclaration*
    '}';

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

methodDeclaration: modifier? type Identifier '(' parameter? (',' parameter)* ')';

modifier: Public | Private ;

parameter: type? Identifier;

type: Identifier | PrimitiveType;

// Keywords
Class: 'class';
Interface: 'interface';
PrimitiveType: 'bool' | 'char' | 'num';
Public: 'public';
Private: 'private';


Identifier: Letter LetterOrDigit*;

fragment
Letter: [a-zA-Z$_];

fragment
LetterOrDigit: [a-zA-Z0-9$_];

WHITE_SPACE: [ \t\r\n\u000C]+ -> skip;

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;
