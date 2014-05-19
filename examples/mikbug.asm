; MIKBUG
;
;

PIASB	.EQU	$8007
PIADB	.EQU	$8006
PIAS	.EQU	$8005
PIAD	.EQU	$8004

	.ORG	$E000
	
; I/O Interrupt Sequence

IO	LDX	IOV
	JMP	X
		
; NMI Sequence

POWDWN	LDX	NIO
	JMP 	X

LOAD	.EQU	*
	LDA A	#$3C
	STA A	PIASB		; Reader relay on
	LDA A	#@21
	BSR	OUTCH		; Output char
	
LOAD3	BSR	INCH
	CMP A	#'S'
	BNE	LOAD3		; 1st char not (S)
	BSR	INCH		; Read char
	CMP A	#'9'
	BEQ	LOAD21
	CMP A	#'1'
	BNE	LOAD3		; 2nd char not (1)
	CLR	CKSM		; Zero checksum
	BSR	BYTE		; Read byte
	SUB A 	#2
	STA A 	BYTECT		; Byte count
; Build Address
	BSR	BADDR
; Store Data
LOAD11	BSR	BYTE
	DEC	BYTECT
	BEQ	LOAD15		; Zero byte count
	STA A	X		; Store data
	INX
	BRA	LOAD11
	
LOAD15	INC	CKSM
	BEQ	LOAD3
LOAD19	LDA A 	#'?'		; Print question mark
	BSR	OUTCH
LOAD21	.EQU	*
	JMP	CONTRL
	
; Build Address
BADDR	BSR	BYTE		; Read 2 frames
	STA A	XHI
	BSR	BYTE
	STA A	XLOW
	LDX	XHI		; (X) Address we built
	RTS
	
; Inout Byte (Two Frames)
BYTE	BSR	INHEX		; Get hex char
	ASL A
	ASL A
	ASL A
	ASL A
	TAB
	BSR	INHEX
	ABA
	TAB
	ADD B 	CKSM
	STA B	CKSM
	RTS
	
OUTHL	LSR A			; Out hex left BDC digit
	LSR A
	LSR A
	LSR A
	
OUTHR	AND A	#$F		; Out hex right BCD digit
	ADD A 	#$30
	CMP A	#$39
	BLS	OUTCH
	ADD A	#$7

; Output one char
OUTCH	JMP	OUTEEE
INCH	JMP	INEEE

; Print data pointed at by X-reg
PDATA2	BSR	OUTCH
	INX
PDATA1	LDA A	X
	CMP A 	#4
	BNE	PDATA2
	RTS			; Stop on EOT
	
; Change Memory (M AAAA DD NN)
CHANGE	BSR	BADDR		; Build address
CHA51	LDX	#MCL
	BSR	PDATA1		; C/R L/F
	LDX	#XHI
	BSR	OUT4HS		; Print address
	LDX	XHI
	BSR	OUT2HS		; Print data (old)
	STX	XHI		; Save data address
	BSR	INCH		; Input one char
	CMP A	#$20
	BNE	CHA51		; Not space
	BSR 	BYTE		; Input new data
	DEX
	STA A 	X		; Change memory
	CMP A	X
	BEQ	CHA51		; Did change
	BRA	LOAD19		; Not changed
	
; Input Hex Char
INHEX	BSR	INCH
	SUB A	#$30
	BMI	C1		; Not hex
	CMP A 	#$09
	BLE	IN1HG
	CMP A 	#$11
	BMI	C1		; Not hex
	CMP A 	#$16
	BGT	C1		; Not hex
	SUB A	#7
IN1HG	RTS

OUT2H	LDA A 	0,X		; Output 2 hex char
OUT2HA	BSR	OUTHL		; Out left hex char
	LDA A	0,X
	INX
	BRA	OUTHR		; Output right hex char and r
	
OUT4HS	BSR	OUT2H		; Output 4 hex char + space
OUT2HS	BSR	OUT2H		; Output 2 hex char + space
OUTS	LDA A 	#$20		; Space
	BRA	OUTCH		; (BSR & RTS)
	
; Enter Power on sequence
START	.EQU	*
	LDS	#STACK
	STS	SP		; Inz target's stack pointer
; inz PIA
	LDX	#PIAD
	INC	0,X
	LDA A 	#$7
	STA A	1,X
	INC	0,X
	STA	2,X
CONTRL



MCL
C1


OUTEEE
INEEE

	.ORG	$A000
	
IOV	.SPACE	2		; IO Interrupt Pointer
BEGA	.SPACE	2		; Beginning addr print/punch
ENDA	.SPACE	2		; Ending addr print/punch
NIO	.SPACE	2		; NMI interrupt pointer
SP	.SPACE	1
	.SPACE	1
CKSM	.SPACE	1
BYTECT	.SPACE	1
XHI	.SPACE	1
XLOW	.SPACE 	1
TEMP	.SPACE	1		; Temp /
TW	.SPACE	2		; Temp
MCONT	.SPACE	1
XTEMP	.SPACE 	2
	.SPACE	46
STACK	.SPACE	1		; Stack pointer




	.END