;===============================================================================
;-------------------------------------------------------------------------------


; P1 Data Stack Pointer
; P2 Return Stack Pointer
; P3 Temp 

;===============================================================================
;-------------------------------------------------------------------------------

PRIMITIVE	.macro
		.word	$+1
		.endm
		

NEXT		.macro
		ldi	lo(DO_NEXT-1)
		xpal	p3
		ldi	hi(DO_NEXT-1)
		xpah	p3
		xppc	p3
		.endm

		.code
		.org	X'1000
		
		nop

		
		
		
;===============================================================================
; Stack Operations
;-------------------------------------------------------------------------------

; 2DROP ( x1 x2 -- )

TWO_DROP:	PRIMITIVE
		ld	@(p1)			; Drop the top two stop stack
		ld	@(p1)			; .. items
		ld	@(p1)
		ld	@(p1)
		NEXT				; Done

; DROP ( x1 -- )

DROP:		PRIMITIVE
		ld	@(p1)			; Drop the stop stack item
		ld	@(p1)
		NEXT				; Done

; DUP ( x -- x x )
		
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
		NEXT				; Done

; NIP

; OVER

; SWAP

; TUCK		

;===============================================================================
;-------------------------------------------------------------------------------

; AND ( n1 n2 -- n3 )

AND:		PRIMITIVE
		ld	@(p1)			; AND the top two stack items
		and	1(p1)
		st	1(p1)
		ld	@(p1)
		and	1(p1)
		st	1(p1)
		NEXT				; Done

; INVERT ( n1 -- n2 )
		
INVERT:		PRIMITIVE
		ldi	X'ff			; Invert the top stack value
		xor	0(p1)
		st	0(p1)
		ldi	X'ff
		xor	1(p1)
		st	1(p1)
		NEXT				; Done

; OR ( n1 n2 -- n3 )

OR:		PRIMITIVE
		ld	@(p1)			; OR the top two stack items
		or	1(p1)
		st	1(p1)
		ld	@(p1)
		or	1(p1)
		st	1(p1)
		NEXT				; Done

; XOR ( n1 n2 -- n3 )

XOR:		PRIMITIVE
		ld	@(p1)			; XOR the top two stack items
		xor	1(p1)
		st	1(p1)
		ld	@(p1)
		xor	1(p1)
		st	1(p1)
		NEXT				; Done
		

;===============================================================================
;-------------------------------------------------------------------------------
				
IP		.space	2
	
; :

DO_SEMI:
	
		
DO_NEXT:	ccl				; Get next word address and
		ld	IP+0			; .. increment pointer
		xpal	p3
		adi	2
		st	IP+0
		ld	IP+1
		xpah	p3
		adi	0
		st	IP+1

		ld	0(p3)			; Fetch the code address
		xae
		ld	1(p3)
		xpah	p3
		lde
		xpal	p3

; [IF]

_IF_:		PRIMITIVE
		ld	@(p1)			; Is the top value on the stack
		or	@(p1)			; .. zero?
		jnz	_ELSE_+2		; Yes, follow the branch
		ccl				; No, skip over the target
		ld	IP+0			; .. address
		adi	2
		st	IP+0
		ld	IP+1
		adi	0
		st	IP+1
		jmp	DO_NEXT			; Done
; [ELSE]
	
_ELSE_:		PRIMITIVE
		ld	IP+0			; Load the instruction pointer
		xpal	p3
		ld	IP+1
		xpah	p3
		ld	0(p3)			; Copy target address into IP
		st	IP+0
		ld	1(p3)
		st	IP+1
		jmp	DO_NEXT			; Done
		

		
		
		
		.end