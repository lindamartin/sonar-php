<?php

$a = 1;                                       // $a

list($l1, $l2) = $unassigned;                 // $l1, $l2, $unassigned

foreach (array("a", "b") as $key => $val) {}  // $key, $val

function f() {                                // f
  static $static;                             // $static
  $a = 1;                                     // $a
}

class A {                                     // A
  public function f($p) {                     // f, $p
  }
}

