
## Syntaxical

### Statements

- Blank
- Block
- DefClass
- DefFunc
- DefVar
- StmtExpr
- Namespace
- Using
- If
- While
- Return

### Expressions

- FuncCall
- Lambda
- MemberAccess
- OperBinary
- OperConditional
- OperNew
- OperNew_Stackalloc
- OperSizeof
- OperUnary
- PrimaryIdentifier
- PrimaryLiteral {String, Char, Int, Long, Float, Double, Bool}
- Dereference
- Reference


## Features

### Generics

***Types***
- Reified Generics
- Const Generics

***Applies***
- Generics Class
- Generics Function

### Pointers


### Stack-Objects


## Instructions


## MemoryModel

### CallFrame

```
Stack
+---------+
|
| temp_operands...
| locals...
| args...
| ebp
| eip
| ret-val
+---------+

Example.

int f2(int i) {
    return i;
}
int f(int i) {
    int i2 = i;
    return f2(i+i2);
}
void main() {
    f(4);
}

.main: args=0, locals=0
  push ebp
  mov ebp, esp
  
  push<sizeof(int)>   // ret-val space
  push eip+n
  push ebp
  mov ebp, esp
  iconst 4
  mov eip .f
  
  pop ebp
  pop eip
  
.f: args=1, locals=1
  mov [ebp+4], [ebp]
  push [ebp+4]
  
  push eip
  mov eip, .f2
  pop<4>
  
  

```