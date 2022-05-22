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

The object modules and libaries are actually XML files under the covers. I had intended to pass it
through a ZIP compression but never got round to it and now disks are so big it hardly seems worth
it.

## Structured Assembly

The 6502 family assembler supports structured conditional statements (e.g. if/else/endif) and
iterative constructs in the source code. Using these dramatically reduces the number of labels
needed in your code and makes the logic much easier to understand. The generated code automatically
uses the shortest branch/jump sequence needed. More documentation for about this is available here:

http://www.obelisk.me.uk/dev65/

## Requirements

    Runtime: Java 1.6 or greater
    Compile: Java 1.6 to Java 11

Although the sources are written to Java 1.4, there is a 3rd party jar file that requires Java 1.6 at
runtime. So you must have Java 1.6 or greater to run it. It doesn't matter if you have the JRE or JDK.

Additionally, IzPack, the installer component, requires at least Java 1.6.

To support the oldest possible Java at runtime, the compile step specifies Java 1.6 bytecode for the
output. This will be the format of the class files in dev65.jar. This means that Java 1.6 will be
required to run the code. But, at most Java 11 is require to perform the compile step because it is
the last version to be able to generate bytecode as old as Java 1.6.

Why is support being provided for a version of Java as old as 1.6? Why not only support the latest
version of Java? To answer that, ask yourself why you want an assembler for a CPU as old as the 6502.
Additionally, the latest version of Java is only available for the latest versions of Linux, Mac,
and Windows on 64-bit AMD/Intel hardware. If you have older hardware, non-Intel hardware, and/or
an older operating system, you can't run the lastest version of Java. So, supporting the oldest
version of Java possible expands the types of systems this software will run on. To achieve,
"Write once, run anywhere" an older version of Java is required. For example,
it would be preferrable to support these older system which were supported until the most recent code
was added:

    Mac G3 - can run MacOS 10.3 at most - Java 1.4 at most
    Mac G4 - can run MacOS 10.4 at most - Java 1.5 at most
    Solaris 8 - Java 1.5 at most

