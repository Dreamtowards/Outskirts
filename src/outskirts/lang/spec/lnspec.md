

### Built-in types

- i8, i16, i32, i64
- u8, u16, u32, u64
- f32, f64
- boolean, function<void,..>, array[]

> External Alias:
> - byte, short, int, long,
> - ubyte, ushort, uint, ulong
> - float, double

### Namespaces & Classes

Namespace is 'virtual', its just prefix of classes.

Class ?Struct ?RTTI  
看待的方式. 结构体是没有RTTI的 即 实际操作取决于调用者。而java的RTTI类架构，实际操作取决于被调用者。

`str.length` java: getfield{string.class, length}, c: getslice{0, 4}  
后者更简单高效，但安全性不保证 - 若被调用者编译版本不同的话 可能实际会错误

### Compilied IRs

```
instructions <- function


```







