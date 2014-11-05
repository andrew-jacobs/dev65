;===============================================================================
;     _    _   _ ____        _____          _   _
;    / \  | \ | / ___|      |  ___|__  _ __| |_| |__
;   / _ \ |  \| \___ \ _____| |_ / _ \| '__| __| '_ \
;  / ___ \| |\  |___) |_____|  _| (_) | |  | |_| | | |
; /_/   \_\_| \_|____/      |_|  \___/|_|   \__|_| |_|
;
; An Indirect Threaded SC/MP ANS Forth
;-------------------------------------------------------------------------------
; Copyright (C),2014 HandCoded Software Ltd.
; All rights reserved.
;
; This software is the confidential and proprietary information of HandCoded
; Software Ltd. ("Confidential Information").  You shall not disclose such
; Confidential Information and shall use it only in accordance with the terms
; of the license agreement you entered into with HandCoded Software.
;
; HANDCODED SOFTWARE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
; SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
; LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
; PARTICULAR PURPOSE, OR NON-INFRINGEMENT. HANDCODED SOFTWARE SHALL NOT BE
; LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
; OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
;===============================================================================
;
; Notes:
;
;
; P1 Data Stack Pointer
; P2 Return Stack Pointer
; P3 Call/Return address/Temp
;
;===============================================================================
; $Id$
;-------------------------------------------------------------------------------

; If the code is compiled for ROM then some of key inner interpreter words must
; be copied into RAM at initialisation to be close to the IP and WA variables.

ROM_BASED	.equ	1

RAM_START	.equ	X'1000
		.if	ROM_BASED
ROM_START	.equ	X'B000
RELOCATION	.equ	ROM_START - RAM_START
		.else
RELOCATION	.equ	0
		.endif

;===============================================================================
; Macros
;-------------------------------------------------------------------------------

LAST		.set	0			; Address of the last word

WORD		.macro	NAME,FLAGS
		.word	LAST
		.byte	FLAGS
LAST		.set	$
		STRING	NAME
		.endm

NORMAL		.equ	X'00
IMMEDIATE	.equ	X'40


STRING		.macro	VALUE
		.byte	.TEXT_END-.TEXT_START
.TEXT_START	.byte	VALUE
.TEXT_END
		.endm

;
;

PRIMITIVE	.macro
		.word	$+1
		.endm

;
;

COMPILED	.macro
		.word	DO_COLON-1
		.endm

CONSTANT	.macro
		.word	DO_CONSTANT-1
		.endm

VARIABLE	.macro
		.word	DO_VARIABLE-1
		.endm

USER		.macro
		.word	DO_USER-1
		.endm



;
;

NEXT		.macro
		ldi	lo(DO_NEXT-1)
		xpal	p3
		ldi	hi(DO_NEXT-1)
		xpah	p3
		xppc	p3
		.endm
		

;===============================================================================
; Initialisation
;-------------------------------------------------------------------------------

		.if	ROM_BASED
		.code
		.org	ROM_START
		.endif

		nop
RESET:
		.if	ROM_BASED
		ldi	lo(CODE_START)		; Prepare to install interpreter
		xpal	p1			; .. into RAM
		ldi	hi(CODE_START)
		xpah	p1
		ldi	lo(CODE_AREA)
		xpal	p2
		ldi	hi(CODE_AREA)
		xpah	p2
		ldi	CODE_SIZE
.Copy:		xae				; Then copy the code
		ld	@(p1)
		st	@(p2)
		xae
		scl
		cai	1
		jnz	.Copy
		.endif
		
		ldi	lo(_DSTACK)
		xpal	p1
		ldi	hi(_DSTACK)
		xpah	p1
		
		ldi	lo(_RSTACK)
		xpal	p2
		ldi	hi(_RSTACK)
		xpah	p2

		ldi	lo(IP)
		xpal	p3
		ldi	hi(IP)
		xpah	p3
		ldi	lo(COLD+2)		; Force cold entry
		st	0(p3)
		ldi	hi(COLD+2)
		st	1(p3)
		NEXT
		jmp	RESET			; Should never get here

;-------------------------------------------------------------------------------

CODE_START:
		nop
		
		
		.if	ROM_BASED
DO_QBRA		.set	CODE_AREA + $ - CODE_START + 1
		.else
DO_QBRA		.set	$ + 1
		.endif
		
_DO_QBRA:	.word	DO_QBRA
		ld	@(p1)			; Is the TOS zero?
		or	@(p1)
		jz	_DO_BRA+2
		ccl				; No, skip over target address
		ld	_IP+0
		adi	2
		st	_IP+0
		ld	_IP+1
		adi	0
		st	_IP+1
		jmp	_DO_NEXT		; And continue execution

		.if	ROM_BASED
DO_BRA		.set	CODE_AREA + $ - CODE_START + 1
		.else
DO_BRA		.set	$ + 1
		.endif
		
_DO_BRA:	.word	DO_BRA
		ld	_IP+0			; Load the instruction pointer
		xpal	p3
		ld	_IP+1
		xpah	p3
		ld	0(p3)			; Copy target address into IP
		st	_IP+0
		ld	1(p3)
		st	_IP+1
		jmp	_DO_NEXT		; Done

		.if	ROM_BASED
DO_LITERAL	.set	CODE_AREA + $ - CODE_START + 1
		.else
DO_LITERAL	.set	$ + 1
		.endif
		
_DO_LITERAL:	.word	DO_LITERAL
		scl				; Make room on data stack
		xpal	p1
		cai	2
		xpal	p1
		ccl
		ld	_IP+0			; Fetch address of constant
		xpal	p3			; .. and bump IP
		adi	2
		st	_IP+0
		ld	_IP+1
		xpah	p3
		adi	0
		st	_IP+1
		ld	0(p3)			; Copy literal to data stack
		st	0(p1)
		ld	1(p3)
		st	1(p1)
		jmp	_DO_NEXT

		.if	ROM_BASED
DO_SEMI		.set	CODE_AREA + $ - CODE_START + 1
		.else
DO_SEMI		.set	$ + 1
		.endif
		
_DO_SEMI:	.word	DO_SEMI
		ld	@(p2)			; Pull caller's IP from the
		st	_IP+0			; .. return stack
		ld	@(p2)
		st	_IP+1
		jmp	_DO_NEXT		; And continue execution

		.if	ROM_BASED
IP		.set	CODE_AREA + $ - CODE_START
		.else
IP		.set	$
		.endif
		
_IP:		.space	2

		.if	ROM_BASED
DO_COLON	.set	CODE_AREA + $ - CODE_START
		.else
DO_COLON	.set	$
		.endif
		
_DO_COLON:
		scl				; Push IP to return stack
		xpal	p2
		cai	2
		xpal	p2
		ld	_IP+0
		st	0(p2)
		ld	_IP+1
		st	1(p2)
		ccl				; Use WA to set new IP
		ld	_WA+0
		adi	2
		st	_IP+0
		ld	_WA+1
		adi	0
		st	_WA+1

		.if	ROM_BASED
DO_NEXT		.set	CODE_AREA + $ - CODE_START
		.else
DO_NEXT		.set	$
		.endif
		
_DO_NEXT:
		ccl				; Load and post increment the
		ld	_IP+0			; .. instruction pointer
		xpal	p3
		adi	2
		st	_IP+0
		ld	_IP+1
		xpah	p3
		adi	0
		st	_IP+1

		ld	0(p3)			; Fetch and save the next word
		st	_WA+0			; .. address
		xae
		ld	1(p3)
		st	_WA+1
		xpah	p3
		lde
		xpal	p3

		ld	0(p3)			; Fetch the code address
		xae
		ld	1(p3)
		xpah	p3
		lde
		xpal	p3
		xppc	p3			; And execute the new word
		jmp	_DO_NEXT		; Repeat if primitive returns

		.if	ROM_BASED
WA		.set	CODE_AREA + $ - CODE_START
		.else
WA		.set	$
		.endif
		
_WA:		.space	2

		.if	ROM_BASED
DO_CONSTANT	.set	CODE_AREA + $ - CODE_START
		.else
DO_CONSTANT	.set	$
		.endif
		
_DO_CONSTANT:
		scl				; Make room on data stack
		xpal	p1
		cai	2
		xpal	p1
		ccl				; Work out address of value
		ld	_WA+0
		adi	2
		xpal	p3
		ld	_WA+1
		adi	0
		xpah	p3
		ld	0(p3)			; Fetch and push it
		st	0(p1)
		ld	1(p3)
		st	0(p1)
		jmp	_DO_NEXT		; And continue

		.if	ROM_BASED
DO_VARIABLE	.set	CODE_AREA + $ - CODE_START
		.else
DO_VARIABLE	.set	$
		.endif
		
_DO_VARIABLE:
		scl				; Make room on data stack
		xpal	p1
		cai	2
		xpal	p1
		ccl				; Push address of value
		ld	_WA+0
		adi	2
		st	0(p1)
		ld	_WA+1
		adi	0
		st	1(p1)
		jmp	_DO_NEXT		; And continue

		.if	ROM_BASED
DO_USER		.set	CODE_AREA + $ - CODE_START
		.else
DO_USER		.set	$
		.endif
		
_DO_USER:
		scl				; Make room on data stack
		xpal	p1
		cai	2
		xpal	p1
		ccl				; Work out address of value
		ld	_WA+0
		adi	2
		xpal	p3
		ld	_WA+1
		adi	0
		xpah	p3
		ccl
		ld	0(p3)			; Fetch and push it
		adi	lo(_USER)
		st	0(p1)
		ld	1(p3)
		adi	hi(_USER)
		st	0(p1)
		jmp	_DO_NEXT		; And continue

;-------------------------------------------------------------------------------

CODE_SIZE	.equ	$ - CODE_START

;===============================================================================
;-------------------------------------------------------------------------------



		WORD	"COLD",NORMAL
COLD:		COMPILED
		.word	ABORT



		WORD	"ABORT",NORMAL
ABORT:		COMPILED
		.word	QUIT


		WORD	"QUIT",NORMAL
QUIT:		COMPILED

;===============================================================================
; Constants
;-------------------------------------------------------------------------------

		WORD	"0",NORMAL
ZERO:		CONSTANT
		.word	0

		WORD	"TRUE",NORMAL
TRUE:		CONSTANT
		.word	-1

		WORD	"FALSE",NORMAL
FALSE:		CONSTANT
		.word	0

;===============================================================================
; User Variables
;-------------------------------------------------------------------------------

		WORD	"BASE",NORMAL
BASE:		USER
		.word	2

		WORD	"STATE",NORMAL
STATE:		.word	4


;===============================================================================
;
;-------------------------------------------------------------------------------

; DECIMAL ( -- )
;
; Set the numeric conversion radix to ten (decimal).

		WORD	"DECIMAL",NORMAL
DECIMAL:	COMPILED
		.word	DO_LITERAL,10
		.word	BASE
		.word	STORE
		.word	DO_SEMI

; HEX ( -- )
;
; Set contents of BASE to sixteen.

		WORD	"HEX",NORMAL
HEX:		COMPILED
		.word	DO_LITERAL,16
		.word	BASE
		.word	STORE
		.word	DO_SEMI

;===============================================================================
; Stack Operations
;-------------------------------------------------------------------------------

; 2DROP ( x1 x2 -- )

		WORD	"2DROP",NORMAL
TWO_DROP:	PRIMITIVE
		ld	@(p1)			; Drop the top two stop stack
		ld	@(p1)			; .. items
		ld	@(p1)
		ld	@(p1)
		xppc	p3			; And continue

; DROP ( x1 -- )

		WORD	"DROP",NORMAL
DROP:		PRIMITIVE
		ld	@(p1)			; Drop the stop stack item
		ld	@(p1)
		xppc	p3			; And continue

; DUP ( x -- x x )

		WORD	"DUP",NORMAL
DUP:		PRIMITIVE
		ccl				;
		xpal	p1
		adi	lo(-2)
		xpal	p1
		xpah	p1
		adi	hi(-2)
		xpah	p1
		ld	2(p1)
		st	0(p1)
		ld	3(p1)
		st	1(p1)
		xppc	p3			; And continue

; NIP

; OVER

; SWAP ( x1 x2 -- x2 x1 )

		WORD	"SWAP",NORMAL
SWAP:		PRIMITIVE
		ld	0(p1)			; Exchange LSBs
		xae
		ld	2(p1)
		st	0(p1)
		xae
		st	2(p1)
		ld	1(p1)			; And then the MSBs
		xae
		ld	3(p1)
		st	1(p1)
		xae
		st	3(p1)
		xppc	p3			; And continue

; TUCK

;===============================================================================
;-------------------------------------------------------------------------------

; AND ( n1 n2 -- n3 )

		WORD	"AND",NORMAL
AND:		PRIMITIVE
		ld	@(p1)			; AND the top two stack items
		and	1(p1)
		st	1(p1)
		ld	@(p1)
		and	1(p1)
		st	1(p1)
		xppc	p3			; And continue

; INVERT ( n1 -- n2 )

		WORD	"INVERT",NORMAL
INVERT:		PRIMITIVE
		ldi	X'ff			; Invert the top stack value
		xor	0(p1)
		st	0(p1)
		ldi	X'ff
		xor	1(p1)
		st	1(p1)
		xppc	p3			; And continue

; OR ( n1 n2 -- n3 )

		WORD	"OR",NORMAL
OR:		PRIMITIVE
		ld	@(p1)			; OR the top two stack items
		or	1(p1)
		st	1(p1)
		ld	@(p1)
		or	1(p1)
		st	1(p1)
		xppc	p3			; And continue

; XOR ( n1 n2 -- n3 )

		WORD	"XOR",NORMAL
XOR:		PRIMITIVE
		ld	@(p1)			; XOR the top two stack items
		xor	1(p1)
		st	1(p1)
		ld	@(p1)
		xor	1(p1)
		st	1(p1)
		xppc	p3			; And continue

;===============================================================================
; Memory Access
;-------------------------------------------------------------------------------

; @ ( a -- n )

		WORD	"@",NORMAL
FETCH:		PRIMITIVE
		ld	0(p1)			; Fetch the memory address
		xpal	p3
		ld	1(p1)
		xpah	p3
		ld	0(p3)			; Replace with stored value
		st	0(p1)
		ld	1(p3)
		st	1(p1)
		NEXT				; And continue

; ! ( n a -- )

		WORD	"!",NORMAL
STORE:		PRIMITIVE
		ld	@(p1)			; Fetch the memory address
		xpal	p3
		ld	@(p1)
		xpah	p3
		ld	@(p1)			; Then save the data value
		st	0(p3)
		ld	@(p1)
		st	1(p3)
		NEXT				; And continue

; +! ( n a -- )

		WORD	"+!",NORMAL
PLUS_STORE:	PRIMITIVE
		ld	@(p1)			; Fetch the memory address
		xpal	p3
		ld	@(p1)
		xpah	p3
		ccl				; Then add data value to memory
		ld	@(p1)
		add	0(p3)
		st	0(p3)
		ld	@(p1)
		add	1(p3)
		st	1(p3)
		NEXT				; And continue

;===============================================================================
;-------------------------------------------------------------------------------

; :

		WORD	":",NORMAL
COLON:



; ;

		WORD	";",IMMEDIATE
SEMI:







		NEXT

;===============================================================================
; RAM
;-------------------------------------------------------------------------------

		.if	ROM_BASED
		.bss
		.org	RAM_START

CODE_AREA:	.space	256		; Reserves space for interpreter
		.endif


_HLD:		.space	2

_USER:		.space	10


_TIB:		.space	128

;===============================================================================
; Stacks
;-------------------------------------------------------------------------------


		.data

		.space	128
_DSTACK:
		.space	128
_RSTACK:

		.end