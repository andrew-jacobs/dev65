# DEV65 - A Cross Platform Relocatable Macro Assembler

DEV65 started out as an assembler for the 6502 family of processors supporting macro processing
and generating relocatable code. From the outset the core code for expressions and object modules
was separate from the code that tailored it to the 6502 assembler syntax and opcodes. Over time
this has allowed additional assemblers to be added relatively easily and now the assembler supports

- MOS/Rockwell/WDC 6502,65C02,65SC02,65C816
- Motorola 6800
- Motorola 6809 (in progress)
- Intel 4004
- Intel 4040
- Intel 8008
- Intel 8080
- RCA CDP 1802
- NatSemi SC/MP

The assembler is written in Java and should run on any supported platform. I use it with various
flavours of Windows and Linux (on PC and Raspberry Pi).

The linkers supports several output formats including hex, Intel hex, binary, S19, S28 and S37. Like
the assembler its quite easy to add other formats.

The object modules and libaries are actually XML files under covers. I had intended to pass it
through a ZIP compression but never got round to it and now disks are so big it hardly seems worth
it.

## Structured Assembly

The 6502 family assembler supports structured conditional statements (e.g. if/else/endif) and
iterative constructs in the source code. Using these dramatically reduces the number of labels
needed in your code and makes the logic much easier to understand. The generated code automatically
uses the shortest branch/jump sequence needed. More documentation for about this is available here:

http://www.obelisk.me.uk/dev65/