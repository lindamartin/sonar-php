<?php

/**
 * Function calls arguments indentation
 */
doSomething($p1,             // NOK {{Either split this list into multiple lines, aligned at column "4" or put all arguments on line "6".}}
    $p2
);

doSomething(
    $p1, $p2                 // NOK {{Either split this list into multiple lines, aligned at column "4" or put all arguments on line "10".}}
);

doSomething(
    $p1,                     // NOK {{Align all arguments in this list at column "4".}}
   $p2);                     // NOK {{Move the closing parenthesis on the next line.}}

doSomething($p1, something(  // NOK
    $p1,
    $p2,
    $p3,
    $p4
));

doSomething(
    $p1,                     // OK
    array(
        $p1,
        $p2
    ),
    $p2
);

doSomething($p1, $p2);       // OK

doSomething(                 // OK
    $p1,
    $p2
);

doSomething(anotherThing(    // OK
    $p1,
    $p2,
    $p2
));

/**
 * Method declaration argument indentation
 */
function f($p1,         // NOK {{Either split this list into multiple lines, aligned at column "4" or put all arguments on line "50".}}
           $p2
) {
}

function g(
    $p1, $p2            // NOK {{Either split this list into multiple lines, aligned at column "4" or put all arguments on line "55".}}
) {
}

function h(
   $p1,                 // NOK {{Align all arguments in this list at column "4".}}
    $p2)                // NOK {{Move the closing parenthesis with the opening brace on the next line.}}
{
}

function j($p1, $p2)    // OK
{
}

function k(             // OK
    $p1,
    $p2
) {
}

/**
 * Implement list indentation
 */

class C1 implements A,    // NOK {{Either split this list into multiple lines or move it on the same line "80".}}
                    B
{}

class C2 implements
        A, B              // NOK {{Either split this list into multiple lines or move it on the same line "84".}}
{}

class C3 implements
    A,                    // NOK {{Align all interfaces in this list at column "4".}}
     B
{}

class C4 implements A, B  // OK
{}

class C5 implements       // OK
    A,
    B
{}
