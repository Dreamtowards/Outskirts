
# 1. Directions

## 1.1 Consistence. i.e. "Feel-Similar"

O.O.P. Objects

O.P.P. Procedural

only allowed Classes.? or allowed external var/funcs..?

# 2. Syntax


## 2.1 Basics

### 2.1.1 Comments

#### Single-line comment

    // any chars until end of line.

#### Multi-line comment

    /* any chars
       across lines
       until reach the comment encloser */

### 2.1.2 Operators

<table>
<tr>
    <th>Precedence</th>
    <th>Operator</th>
    <th>Type</th>
    <th>Associativity</th>
</tr>

<tr>
    <td rowspan="1">15</td>
    <td><kbd>.</kbd> Member selection</td>
    <td>Bi</td>
    <td>-></td>
</tr>

<tr>
    <td rowspan="3">14</td>
    <td><kbd>++</kbd> Post-increase</td>
    <td rowspan="3">Unary-Suffix</td>
</tr>
<tr><td><kbd>--</kbd> Post-decrease</td></tr>
<tr><td><kbd>(<i>args</i>)</kbd> Function Call (?)</td></tr>

<tr>
    <td rowspan="7">13</td>
    <td><kbd>++</kbd> Pre-increase</td>
    <td rowspan="6">Unary-Prefix</td>
</tr>
<tr><td><kbd>--</kbd> Pre-decrease</td></tr>
<tr><td><kbd>+</kbd> Positive</td></tr>
<tr><td><kbd>-</kbd> Negative</td></tr>
<tr><td><kbd>!</kbd> Logical negation</td></tr>
<tr><td><kbd>~</kbd> Bitwise complement</td></tr>

<tr>
    <td><kbd>as</kbd> Type cast</td>
    <td rowspan="21">Bi</td>
</tr>

<!-- Mul Div -->
<tr>
    <td rowspan="3">12</td>
    <td><kbd>*</kbd> Multiplication</td>
    <td rowspan="20">-></td>
</tr>
<tr><td><kbd>/</kbd> Division</td></tr>
<tr><td><kbd>%</kbd> Modulus</td></tr>

<!-- Add Sub -->
<tr>
    <td rowspan="2">11</td>
    <td><kbd>+</kbd> Addition</td>
</tr>
<tr><td><kbd>-</kbd> Subtraction</td></tr>

<!-- Bitwise shift -->
<tr>
    <td rowspan="3">10</td>
    <td><kbd><<</kbd> Bitwise left shift</td>
</tr>
<tr><td><kbd>>></kbd> Bitwise right shift sign ext</td></tr>
<tr><td><kbd>>>></kbd> Bitwise right shift zero ext</td></tr>

<!-- Relation -->
<tr>
    <td rowspan="5">9</td>  <td><kbd><</kbd> Less than</td>
</tr>
<tr><td><kbd><=</kbd> Less than or equal</td></tr>
<tr><td><kbd>></kbd> Greater than</td></tr>
<tr><td><kbd>>=</kbd> Greater than or equal</td></tr>
<tr><td><kbd>is</kbd> Type instance of</td></tr>

<!-- Equals, NotEquals -->
<tr>
    <td rowspan="2">8</td>
    <td><kbd>==</kbd> Is equal to</td>
</tr>
<tr><td><kbd>!=</kbd> Is not equal to</td></tr>

<!-- Bitwise AND/ XOR/ OR -->
<tr><td>7</td> <td><kbd>&</kbd> Bitwise AND</td></tr>
<tr><td>6</td> <td><kbd>^</kbd> Bitwise exclusive OR</td></tr>
<tr><td>5</td> <td><kbd>|</kbd> Bitwise inclusive OR</td></tr>

<!-- Logical AND/ OR -->
<tr><td>4</td> <td><kbd>&&</kbd> Logical AND</td></tr>
<tr><td>3</td> <td><kbd>||</kbd> Logical OR</td></tr>

<!-- Ternary -->
<tr>
    <td>2</td>
    <td><kbd>? :</kbd> Conditional</td>
    <td>Ternary</td>
    <td rowspan="6"><-</td>
</tr>

<!-- Assignment -->
<tr>
    <td rowspan="5">1</td>
    <td><kbd>=</kbd> Assignment</td>
    <td rowspan="5">Bi</td>
</tr>
<tr><td><kbd>*=</kbd></td></tr>
<tr><td><kbd>/=</kbd></td></tr>
<tr><td><kbd>+=</kbd></td></tr>
<tr><td><kbd>-=</kbd></td></tr>
</table>


# 3. Type and Class

"Everything is Objects" - java.

## 3.1 Built-in types

### Numbers


### Strings


### Arrays


### Booleans


### Functions




## 3.2 Inheritance

the way of inheritance, only this one.

    class SubClass : SupClass {
        ...
    }

> why not use 'extends'.  
> why not use interfaces and 'implements'






# 4. External Syntax Sugar

### 4.1 Local Variable Type Inference

use of "auto" declaration-keyword. only allowed use in local scopes.

    // local scope:
    auto var1 = ...;

> why not use keyword "let", "var", "val"  
> val: loosed. wasted.  var: script dynamic language.  let: too high-level.


### 4.2 For Each

    for (Type var1 : iterable) {
        ...
    }


### 4.3 Variable Arguments

    void print(String s, Object... args) {
        ...
    }


