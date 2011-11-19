
	.EXTERN	ExtLab
	.GLOBAL GblLab
	
	.CODE
		
	.EXTERN LABA
	.EXTERN LABB
	.EXTERN LABC
	
LABD .EQU (LABA-LABB)*2+LABC/3
	
;==============================================================================
; Test conditional compilation
;------------------------------------------------------------------------------	

	.IF	1
	.IF	1
	NOP		;++
	.ELSE
	NOP		;--
	.ENDIF
	.ELSE
	.IF	1
	NOP		;--
	.ELSE
	NOP		;--
	.ENDIF
	.ENDIF

;==============================================================================
; Test repeat sections
;------------------------------------------------------------------------------

	.IF	1
	.REPEAT 4
	NOP
	.ENDR
	.ELSE
	.REPEAT 4
	CLC
	.ENDR
	.ENDIF
	
;==============================================================================
; Test macros sections
;------------------------------------------------------------------------------

M1	.MACRO
	CLC
	.REPEAT 3
	NOP
	.ENDR
	CLI
	.ENDM
	
	M1
	
M2	.MACRO	VA,VB,VC
	.BYTE	VA,VB,VC
L\?	.BYTE	\0,\1,\2
	.ENDM


	M2 1,"Hello",2+5
	
M3	.MACRO
	M1
	M2 2,"World",ExtLab
	.ENDM
	
	M3

OPCODE	.MACRO	A,B,C
		.WORD ((\0 - '@') << 10) | ((\1 - '@') << 5) | (\2 - '@')
		.ENDM

		OPCODE 'B','R','K'

;==============================================================================
; Test size of address generated for global/external references
;------------------------------------------------------------------------------

GblLab:

	JMP GblLab
	JMP ExtLab
	JMP (GblLab)
	JMP (ExtLab)
	JSR GblLab
	JSR ExtLab
	
FIXED	.EQU	1234
UNKNWN	.EQU	ExtLab+2*3
COUNT	.SET 	0
COUNT	.SET	COUNT+1
		
;==============================================================================
; 65016 Opcodes & Addressing modes
;------------------------------------------------------------------------------
	
	ADC #$1234
	ADC <$1234
	ADC <$1234,X
	ADC $12345678
	ADC $12345678,X
	ADC $12345678,Y
	ADC ($1234,X)
	ADC ($1234),Y
	ADC ($1234)
	AND #$1234
	AND <$1234
	AND <$1234,X
	AND $12345678
	AND $12345678,X
	AND $12345678,Y
	AND ($1234,X)
	AND ($1234),Y
	AND ($1234)
	ASL A
	ASL <$1234
	ASL <$1234,X
	ASL $12345678
	ASL $12345678,X
	BBR0 $1234,$+3
	BBR1 $1234,$+3
	BBR2 $1234,$+3
	BBR3 $1234,$+3
	BBR4 $1234,$+3
	BBR5 $1234,$+3
	BBR6 $1234,$+3
	BBR7 $1234,$+3
	BBS0 $1234,$+3
	BBS1 $1234,$+3
	BBS2 $1234,$+3
	BBS3 $1234,$+3
	BBS4 $1234,$+3
	BBS5 $1234,$+3
	BBS6 $1234,$+3
	BBS7 $1234,$+3
	BCC $+2
	BCS $+2
	BEQ $+2
	BIT <$1234
	BIT $12345678
	BIT <$1234,X
	BIT $12345678,X
	BIT #$1234
	BMI $+2
	BNE $+2
	BPL $+2
	BRA $+2
	BRK
	BVC $+2
	BVS $+2
	CLC
	CLD
	CLI
	CLV
	CMP #$1234
	CMP <$1234
	CMP <$1234,X
	CMP $12345678
	CMP $12345678,X
	CMP $12345678,Y
	CMP ($1234,X)
	CMP ($1234),Y
	CMP ($1234)
	CPX #$1234
	CPX <$1234
	CPX $12345678
	CPY #$1234
	CPY <$1234
	CPY $12345678
	DEC <$1234
	DEC <$1234,X
	DEC $12345678
	DEC $12345678,X
	DEC A
	DEX
	DEY
	EOR #$1234
	EOR <$1234
	EOR <$1234,X
	EOR $12345678
	EOR $12345678,X
	EOR $12345678,Y
	EOR ($1234,X)
	EOR ($1234),Y
	EOR ($1234)
	INC <$1234
	INC <$1234,X
	INC $12345678
	INC $12345678,X
	INC A
	INX
	INY
	JMP $12345678
	JMP ($12345678)
	JMP ($12345678,X)
	JSR $12345678
	LDA #$1234
	LDA <$1234
	LDA <$1234,X
	LDA $12345678
	LDA $12345678,X
	LDA $12345678,Y
	LDA ($1234,X)
	LDA ($1234),Y
	LDA ($1234)
	LDX #$1234
	LDX <$1234
	LDX <$1234,Y
	LDX $12345678
	LDX $12345678,Y
	LDY #$1234
	LDY <$1234
	LDY <$1234,X
	LDY $12345678
	LDY $12345678,X
	LSR A
	LSR <$1234
	LSR <$1234,X
	LSR $12345678
	LSR $12345678,X
	NOP
	ORA #$1234
	ORA <$1234
	ORA <$1234,X
	ORA $12345678
	ORA $12345678,X
	ORA $12345678,Y
	ORA ($1234,X)
	ORA ($1234),Y
	ORA ($1234)
	PHA
	PHP
	PHX
	PHY
	PLA
	PLP
	PLX
	PLY
	RMB0 $1234
	RMB1 $1234
	RMB2 $1234
	RMB3 $1234
	RMB4 $1234
	RMB5 $1234
	RMB6 $1234
	RMB7 $1234
	ROL A
	ROL <$1234
	ROL <$1234,X
	ROL $12345678
	ROL $12345678,X
	ROR A
	ROR <$1234
	ROR <$1234,X
	ROR $12345678
	ROR $12345678,X
	RTI
	RTS
	SBC #$1234
	SBC <$1234
	SBC <$1234,X
	SBC $12345678
	SBC $12345678,X
	SBC $12345678,Y
	SBC ($1234,X)
	SBC ($1234),Y
	SBC ($1234)
	SEC
	SED
	SEI
	SMB0 $1234
	SMB1 $1234
	SMB2 $1234
	SMB3 $1234
	SMB4 $1234
	SMB5 $1234
	SMB6 $1234
	SMB7 $1234
	STA <$1234
	STA <$1234,X
	STA $12345678
	STA $12345678,X
	STA $12345678,Y
	STA ($1234,X)
	STA ($1234),Y
	STA ($1234)
	STP
	STX <$1234
	STX <$1234,Y
	STX $12345678
	STY <$1234
	STY <$1234,X
	STY $12345678
	STZ <$1234
	STZ <$1234,X
	STZ $12345678
	STZ $12345678,X
	TAX
	TAY
	TRB $1234
	TRB $12345678
	TSB $1234
	TSB $12345678
	TSX
	TXA
	TXS
	TYA
	WAI
	
	BRK #$7E		; Test BRK extension

;==============================================================================
; 65SC06 Opcodes & Addressing modes
;------------------------------------------------------------------------------
	
	ADC	#$1234
	ADC <$1234
	ADC <$1234,X
	ADC $12345678
	ADC $12345678,X
	ADC $12345678,Y
	ADC ($1234,X)
	ADC ($1234),Y
	ADC ($1234)
	AND	#$1234
	AND <$1234
	AND <$1234,X
	AND $12345678
	AND $12345678,X
	AND $12345678,Y
	AND ($1234,X)
	AND ($1234),Y
	AND ($1234)
	ASL A
	ASL <$1234
	ASL <$1234,X
	ASL $12345678
	ASL $12345678,X
	BCC $+2
	BCS $+2
	BEQ $+2
	BIT <$1234
	BIT $12345678
	BIT <$1234,X
	BIT $12345678,X
	BIT #$1234
	BMI $+2
	BNE $+2
	BPL $+2
	BRA $+2
	BRK
	BVC $+2
	BVS $+2
	CLC
	CLD
	CLI
	CLV
	CMP #$1234
	CMP <$1234
	CMP <$1234,X
	CMP $12345678
	CMP $12345678,X
	CMP $12345678,Y
	CMP ($1234,X)
	CMP ($1234),Y
	CMP ($1234)
	CPX #$1234
	CPX <$1234
	CPX $12345678
	CPY #$1234
	CPY <$1234
	CPY $12345678
	DEC <$1234
	DEC <$1234,X
	DEC $12345678
	DEC $12345678,X
	DEC A
	DEX
	DEY
	EOR #$1234
	EOR <$1234
	EOR <$1234,X
	EOR $12345678
	EOR $12345678,X
	EOR $12345678,Y
	EOR ($1234,X)
	EOR ($1234),Y
	EOR ($1234)
	INC <$1234
	INC <$1234,X
	INC $12345678
	INC $12345678,X
	INC A
	INX
	INY
	JMP $12345678
	JMP ($12345678)
	JMP ($12345678,X)
	JSR $12345678
	LDA #$1234
	LDA <$1234
	LDA <$1234,X
	LDA $12345678
	LDA $12345678,X
	LDA $12345678,Y
	LDA ($1234,X)
	LDA ($1234),Y
	LDA ($1234)
	LDX #$1234
	LDX <$1234
	LDX <$1234,Y
	LDX $12345678
	LDX $12345678,Y
	LDY #$1234
	LDY <$1234
	LDY <$1234,X
	LDY $12345678
	LDY $12345678,X
	LSR A
	LSR <$1234
	LSR <$1234,X
	LSR $12345678
	LSR $12345678,X
	NOP
	ORA #$1234
	ORA <$1234
	ORA <$1234,X
	ORA $12345678
	ORA $12345678,X
	ORA $12345678,Y
	ORA ($1234,X)
	ORA ($1234),Y
	ORA ($1234)
	PHA
	PHP
	PHX
	PHY
	PLA
	PLP
	PLX
	PLY
	ROL A
	ROL <$1234
	ROL <$1234,X
	ROL $12345678
	ROL $12345678,X
	ROR A
	ROR <$1234
	ROR <$1234,X
	ROR $12345678
	ROR $12345678,X
	RTI
	RTS
	SBC #$1234
	SBC <$1234
	SBC <$1234,X
	SBC $12345678
	SBC $12345678,X
	SBC $12345678,Y
	SBC ($1234,X)
	SBC ($1234),Y
	SBC ($1234)
	SEC
	SED
	SEI
	STA <$1234
	STA <$1234,X
	STA $12345678
	STA $12345678,X
	STA $12345678,Y
	STA ($1234,X)
	STA ($1234),Y
	STA ($1234)
	STP
	STX <$1234
	STX <$1234,Y
	STX $12345678
	STY <$1234
	STY <$1234,X
	STY $12345678
	STZ <$1234
	STZ <$1234,X
	STZ $12345678
	STZ $12345678,X
	TAX
	TAY
	TRB $1234
	TRB $12345678
	TSB $1234
	TSB $12345678
	TSX
	TXA
	TXS
	TYA
	WAI

	BRK #$7E		; Test BRK extension
	
;==============================================================================
; Structured Assembly Code
;------------------------------------------------------------------------------

; A mixture of loops and ifs

	LDY #0
	REPEAT
	 TYA
	 LDX #0
	 REPEAT
	  ASL A
	  PHP
	  IF CS
	   INX
	  ENDIF
	  PLP
	 UNTIL EQ
	 INY
	 CPY #128
	 IF EQ
	  BREAK
	 ENDIF
	FOREVER
	
; Nonsense code to show long branches

	.ORG $E000

	WHILE CC
	 .REPEAT 16
	 .BYTE $EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA
	 .ENDR
	 ASL A
	ENDW

;==============================================================================
; Data Related Directives
;------------------------------------------------------------------------------

	.DATA
	
	.BYTE	1,2,3
	.BYTE 	1+2*3+4
	.BYTE 	'A','B','C'
	.BYTE	"Hello World",13,10
	.BYTE 	LO ($+2),HI ($+2)
	.WORD	1,2,3
	.WORD 	$+3
	.WORD 	($+10)-($+2)
	.LONG	1,2,3
	
;	.END