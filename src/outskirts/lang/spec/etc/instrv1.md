
## Prereqs

- ptr.size: 8bytes

### lload \<li: u8>

push ptr of local variable_i onto the stack.

```
lload 0

push_ptr( ebp+localptrs[i] );
```

### lstore \<li: u8>

pop ptr and cpy data(len=sizeof(var_type)) onto local variable_i.

```
lstore 0

ptr p_src = pop_ptr();
ptr p_dsc = ebp+localptrs[i];
size_t = sz = localsz[i];
memcpy(p_src, p_dsc, sz);
```

### ldc \<i: u16>

push constant value onto the stack

```
ldc 1

Constant c = cp.at(i);
if (c.type == INT64) {
    push_i64(c.val);
} else ...
```

### dup \<n: u8>

push stack-top n bytes

```
dup 12

memcpy(esp-n, esp, n);
esp += n;
```

### invokeproc <cidx: u16>

call function.

```
invokeproc "stl.lang.string.length():int"

SymbolFunction sf = resolveFunction(s);
VMExec(sf.codebuf);
```

### ldptr, stptr

### stackalloc \<cidx: u8 -> utf8>

### getfield \<>, putfield


### jmp, jmp_f

### i32add, i32mul

### icmp, cmp_eq, cmp_ne, cmp_lt, cmp_gt, cmp_le, cmp_ge

### pop <n: u8>















