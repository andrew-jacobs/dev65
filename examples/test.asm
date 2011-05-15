
	.EXTERN	ExtLab
	.GLOBAL GblLab

	.6502
	
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

	.6502
	
	JMP GblLab
	JMP ExtLab
	JMP (GblLab)
	JMP (ExtLab)
	JSR GblLab
	JSR ExtLab
	
	.65816
	
	JMP GblLab
	JMP ExtLab
	JMP (GblLab)
	JMP (ExtLab)
	JSR GblLab
	JSL ExtLab

FIXED	.EQU	1234
UNKNWN	.EQU	ExtLab+2*3
COUNT	.SET 	0
COUNT	.SET	COUNT+1
		
;==============================================================================
; 6501 Opcodes & Addressing modes
;------------------------------------------------------------------------------

	.6501
	
	ADC #$11
	ADC <$11
	ADC <$11,X
	ADC $1122
	ADC $1122,X
	ADC $1122,Y
	ADC ($11,X)
	ADC ($11),Y
	AND	#$11
	AND <$11
	AND <$11,X
	AND $1122
	AND $1122,X
	AND $1122,Y
	AND ($11,X)
	AND ($11),Y
	ASL A
	ASL <$11
	ASL <$11,X
	ASL $1122
	ASL $1122,X
	BBR0 $11,$+3
	BBR1 $11,$+3
	BBR2 $11,$+3
	BBR3 $11,$+3
	BBR4 $11,$+3
	BBR5 $11,$+3
	BBR6 $11,$+3
	BBR7 $11,$+3
	BBS0 $11,$+3
	BBS1 $11,$+3
	BBS2 $11,$+3
	BBS3 $11,$+3
	BBS4 $11,$+3
	BBS5 $11,$+3
	BBS6 $11,$+3
	BBS7 $11,$+3
	BCC $+2
	BCS $+2
	BEQ $+2
	BIT <$11
	BIT $1122
	BMI $+2
	BNE $+2
	BPL $+2
	BRK
	BVC $+2
	BVS $+2
	CLC
	CLD
	CLI
	CLV
	CMP #$11
	CMP <$11
	CMP <$11,X
	CMP $1122
	CMP $1122,X
	CMP $1122,Y
	CMP ($11,X)
	CMP ($11),Y
	CPX #$11
	CPX <$11
	CPX $1122
	CPY #$11
	CPY <$11
	CPY $1122
	DEC <$11
	DEC <$11,X
	DEC $1122
	DEC $1122,X
	DEX
	DEY
	EOR #$11
	EOR <$11
	EOR <$11,X
	EOR $1122
	EOR $1122,X
	EOR $1122,Y
	EOR ($11,X)
	EOR ($11),Y
	INC <$11
	INC <$11,X
	INC $1122
	INC $1122,X
	INX
	INY
	JMP $1122
	JMP ($1122)
	JSR $1122
	LDA #$11
	LDA <$11
	LDA <$11,X
	LDA $1122
	LDA $1122,X
	LDA $1122,Y
	LDA ($11,X)
	LDA ($11),Y
	LDX #$11
	LDX <$11
	LDX <$11,Y
	LDX $1122
	LDX $1122,Y
	LDY #$11
	LDY <$11
	LDY <$11,X
	LDY $1122
	LDY $1122,X
	LSR A
	LSR <$11
	LSR <$11,X
	LSR $1122
	LSR $1122,X
	NOP
	ORA #$11
	ORA <$11
	ORA <$11,X
	ORA $1122
	ORA $1122,X
	ORA $1122,Y
	ORA ($11,X)
	ORA ($11),Y
	PHA
	PHP
	PLA
	PLP
	RMB0 $11
	RMB1 $11
	RMB2 $11
	RMB3 $11
	RMB4 $11
	RMB5 $11
	RMB6 $11
	RMB7 $11
	ROL A
	ROL <$11
	ROL <$11,X
	ROL $1122
	ROL $1122,X
	ROR A
	ROR <$11
	ROR <$11,X
	ROR $1122
	ROR $1122,X
	RTI
	RTS
	SBC #$11
	SBC <$11
	SBC <$11,X
	SBC $1122
	SBC $1122,X
	SBC $1122,Y
	SBC ($11,X)
	SBC ($11),Y
	SEC
	SED
	SEI
	SMB0 $11
	SMB1 $11
	SMB2 $11
	SMB3 $11
	SMB4 $11
	SMB5 $11
	SMB6 $11
	SMB7 $11
	STA <$11
	STA <$11,X
	STA $1122
	STA $1122,X
	STA $1122,Y
	STA ($11,X)
	STA ($11),Y
	STX <$11
	STX <$11,Y
	STX $1122
	STY <$11
	STY <$11,X
	STY $1122
	TAX
	TAY
	TSX
	TXA
	TXS
	TYA

	BRK #$7E		; Test BRK extension

;==============================================================================
; 6502 Opcodes & Addressing modes
;------------------------------------------------------------------------------

	.6502
	
	ADC #$11
	ADC <$11
	ADC <$11,X
	ADC $1122
	ADC $1122,X
	ADC $1122,Y
	ADC ($11,X)
	ADC ($11),Y
	AND	#$11
	AND <$11
	AND <$11,X
	AND $1122
	AND $1122,X
	AND $1122,Y
	AND ($11,X)
	AND ($11),Y
	ASL A
	ASL <$11
	ASL <$11,X
	ASL $1122
	ASL $1122,X
	BCC $+2
	BCS $+2
	BEQ $+2
	BIT <$11
	BIT $1122
	BMI $+2
	BNE $+2
	BPL $+2
	BRK
	BVC $+2
	BVS $+2
	CLC
	CLD
	CLI
	CLV
	CMP #$11
	CMP <$11
	CMP <$11,X
	CMP $1122
	CMP $1122,X
	CMP $1122,Y
	CMP ($11,X)
	CMP ($11),Y
	CPX #$11
	CPX <$11
	CPX $1122
	CPY #$11
	CPY <$11
	CPY $1122
	DEC <$11
	DEC <$11,X
	DEC $1122
	DEC $1122,X
	DEX
	DEY
	EOR #$11
	EOR <$11
	EOR <$11,X
	EOR $1122
	EOR $1122,X
	EOR $1122,Y
	EOR ($11,X)
	EOR ($11),Y
	INC <$11
	INC <$11,X
	INC $1122
	INC $1122,X
	INX
	INY
	JMP $1122
	JMP ($1122)
	JSR $1122
	LDA #$11
	LDA <$11
	LDA <$11,X
	LDA $1122
	LDA $1122,X
	LDA $1122,Y
	LDA ($11,X)
	LDA ($11),Y
	LDX #$11
	LDX <$11
	LDX <$11,Y
	LDX $1122
	LDX $1122,Y
	LDY #$11
	LDY <$11
	LDY <$11,X
	LDY $1122
	LDY $1122,X
	LSR A
	LSR <$11
	LSR <$11,X
	LSR $1122
	LSR $1122,X
	NOP
	ORA #$11
	ORA <$11
	ORA <$11,X
	ORA $1122
	ORA $1122,X
	ORA $1122,Y
	ORA ($11,X)
	ORA ($11),Y
	PHA
	PHP
	PLA
	PLP
	ROL A
	ROL <$11
	ROL <$11,X
	ROL $1122
	ROL $1122,X
	ROR A
	ROR <$11
	ROR <$11,X
	ROR $1122
	ROR $1122,X
	RTI
	RTS
	SBC #$11
	SBC <$11
	SBC <$11,X
	SBC $1122
	SBC $1122,X
	SBC $1122,Y
	SBC ($11,X)
	SBC ($11),Y
	SEC
	SED
	SEI
	STA <$11
	STA <$11,X
	STA $1122
	STA $1122,X
	STA $1122,Y
	STA ($11,X)
	STA ($11),Y
	STX <$11
	STX <$11,Y
	STX $1122
	STY <$11
	STY <$11,X
	STY $1122
	TAX
	TAY
	TSX
	TXA
	TXS
	TYA
	
	BRK #$7E		; Test BRK extension

;==============================================================================
; 65C02 Opcodes & Addressing modes
;------------------------------------------------------------------------------

	.65C02
	
	ADC #$11
	ADC <$11
	ADC <$11,X
	ADC $1122
	ADC $1122,X
	ADC $1122,Y
	ADC ($11,X)
	ADC ($11),Y
	ADC ($11)
	AND #$11
	AND <$11
	AND <$11,X
	AND $1122
	AND $1122,X
	AND $1122,Y
	AND ($11,X)
	AND ($11),Y
	AND ($11)
	ASL A
	ASL <$11
	ASL <$11,X
	ASL $1122
	ASL $1122,X
	BBR0 $11,$+3
	BBR1 $11,$+3
	BBR2 $11,$+3
	BBR3 $11,$+3
	BBR4 $11,$+3
	BBR5 $11,$+3
	BBR6 $11,$+3
	BBR7 $11,$+3
	BBS0 $11,$+3
	BBS1 $11,$+3
	BBS2 $11,$+3
	BBS3 $11,$+3
	BBS4 $11,$+3
	BBS5 $11,$+3
	BBS6 $11,$+3
	BBS7 $11,$+3
	BCC $+2
	BCS $+2
	BEQ $+2
	BIT <$11
	BIT $1122
	BIT <$11,X
	BIT $1122,X
	BIT #$11
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
	CMP #$11
	CMP <$11
	CMP <$11,X
	CMP $1122
	CMP $1122,X
	CMP $1122,Y
	CMP ($11,X)
	CMP ($11),Y
	CMP ($11)
	CPX #$11
	CPX <$11
	CPX $1122
	CPY #$11
	CPY <$11
	CPY $1122
	DEC <$11
	DEC <$11,X
	DEC $1122
	DEC $1122,X
	DEC A
	DEX
	DEY
	EOR #$11
	EOR <$11
	EOR <$11,X
	EOR $1122
	EOR $1122,X
	EOR $1122,Y
	EOR ($11,X)
	EOR ($11),Y
	EOR ($11)
	INC <$11
	INC <$11,X
	INC $1122
	INC $1122,X
	INC A
	INX
	INY
	JMP $1122
	JMP ($1122)
	JMP ($1122,X)
	JSR $1122
	LDA #$11
	LDA <$11
	LDA <$11,X
	LDA $1122
	LDA $1122,X
	LDA $1122,Y
	LDA ($11,X)
	LDA ($11),Y
	LDA ($11)
	LDX #$11
	LDX <$11
	LDX <$11,Y
	LDX $1122
	LDX $1122,Y
	LDY #$11
	LDY <$11
	LDY <$11,X
	LDY $1122
	LDY $1122,X
	LSR A
	LSR <$11
	LSR <$11,X
	LSR $1122
	LSR $1122,X
	NOP
	ORA #$11
	ORA <$11
	ORA <$11,X
	ORA $1122
	ORA $1122,X
	ORA $1122,Y
	ORA ($11,X)
	ORA ($11),Y
	ORA ($11)
	PHA
	PHP
	PHX
	PHY
	PLA
	PLP
	PLX
	PLY
	RMB0 $11
	RMB1 $11
	RMB2 $11
	RMB3 $11
	RMB4 $11
	RMB5 $11
	RMB6 $11
	RMB7 $11
	ROL A
	ROL <$11
	ROL <$11,X
	ROL $1122
	ROL $1122,X
	ROR A
	ROR <$11
	ROR <$11,X
	ROR $1122
	ROR $1122,X
	RTI
	RTS
	SBC #$11
	SBC <$11
	SBC <$11,X
	SBC $1122
	SBC $1122,X
	SBC $1122,Y
	SBC ($11,X)
	SBC ($11),Y
	SBC ($11)
	SEC
	SED
	SEI
	SMB0 $11
	SMB1 $11
	SMB2 $11
	SMB3 $11
	SMB4 $11
	SMB5 $11
	SMB6 $11
	SMB7 $11
	STA <$11
	STA <$11,X
	STA $1122
	STA $1122,X
	STA $1122,Y
	STA ($11,X)
	STA ($11),Y
	STA ($11)
	STP
	STX <$11
	STX <$11,Y
	STX $1122
	STY <$11
	STY <$11,X
	STY $1122
	STZ <$11
	STZ <$11,X
	STZ $1122
	STZ $1122,X
	TAX
	TAY
	TRB $11
	TRB $1122
	TSB $11
	TSB $1122
	TSX
	TXA
	TXS
	TYA
	WAI
	
	BRK #$7E		; Test BRK extension

;==============================================================================
; 65SC06 Opcodes & Addressing modes
;------------------------------------------------------------------------------

	.65SC02
	
	ADC	#$11
	ADC <$11
	ADC <$11,X
	ADC $1122
	ADC $1122,X
	ADC $1122,Y
	ADC ($11,X)
	ADC ($11),Y
	ADC ($11)
	AND	#$11
	AND <$11
	AND <$11,X
	AND $1122
	AND $1122,X
	AND $1122,Y
	AND ($11,X)
	AND ($11),Y
	AND ($11)
	ASL A
	ASL <$11
	ASL <$11,X
	ASL $1122
	ASL $1122,X
	BCC $+2
	BCS $+2
	BEQ $+2
	BIT <$11
	BIT $1122
	BIT <$11,X
	BIT $1122,X
	BIT #$11
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
	CMP #$11
	CMP <$11
	CMP <$11,X
	CMP $1122
	CMP $1122,X
	CMP $1122,Y
	CMP ($11,X)
	CMP ($11),Y
	CMP ($11)
	CPX #$11
	CPX <$11
	CPX $1122
	CPY #$11
	CPY <$11
	CPY $1122
	DEC <$11
	DEC <$11,X
	DEC $1122
	DEC $1122,X
	DEC A
	DEX
	DEY
	EOR #$11
	EOR <$11
	EOR <$11,X
	EOR $1122
	EOR $1122,X
	EOR $1122,Y
	EOR ($11,X)
	EOR ($11),Y
	EOR ($11)
	INC <$11
	INC <$11,X
	INC $1122
	INC $1122,X
	INC A
	INX
	INY
	JMP $1122
	JMP ($1122)
	JMP ($1122,X)
	JSR $1122
	LDA #$11
	LDA <$11
	LDA <$11,X
	LDA $1122
	LDA $1122,X
	LDA $1122,Y
	LDA ($11,X)
	LDA ($11),Y
	LDA ($11)
	LDX #$11
	LDX <$11
	LDX <$11,Y
	LDX $1122
	LDX $1122,Y
	LDY #$11
	LDY <$11
	LDY <$11,X
	LDY $1122
	LDY $1122,X
	LSR A
	LSR <$11
	LSR <$11,X
	LSR $1122
	LSR $1122,X
	NOP
	ORA #$11
	ORA <$11
	ORA <$11,X
	ORA $1122
	ORA $1122,X
	ORA $1122,Y
	ORA ($11,X)
	ORA ($11),Y
	ORA ($11)
	PHA
	PHP
	PHX
	PHY
	PLA
	PLP
	PLX
	PLY
	ROL A
	ROL <$11
	ROL <$11,X
	ROL $1122
	ROL $1122,X
	ROR A
	ROR <$11
	ROR <$11,X
	ROR $1122
	ROR $1122,X
	RTI
	RTS
	SBC #$11
	SBC <$11
	SBC <$11,X
	SBC $1122
	SBC $1122,X
	SBC $1122,Y
	SBC ($11,X)
	SBC ($11),Y
	SBC ($11)
	SEC
	SED
	SEI
	STA <$11
	STA <$11,X
	STA $1122
	STA $1122,X
	STA $1122,Y
	STA ($11,X)
	STA ($11),Y
	STA ($11)
	STP
	STX <$11
	STX <$11,Y
	STX $1122
	STY <$11
	STY <$11,X
	STY $1122
	STZ <$11
	STZ <$11,X
	STZ $1122
	STZ $1122,X
	TAX
	TAY
	TRB $11
	TRB $1122
	TSB $11
	TSB $1122
	TSX
	TXA
	TXS
	TYA
	WAI

	BRK #$7E		; Test BRK extension

;==============================================================================
; 65816 Opcodes & Addressing modes
;------------------------------------------------------------------------------

	.65816

	.DPAGE $11
	.LONGA OFF
	.LONGI ON
		
	ADC #$11
	ADC <$11
	ADC <$11,X
	ADC $1122
	ADC $1122,X
	ADC $1122,Y
	ADC ($11,X)
	ADC ($11),Y
	ADC ($11)
	ADC >$112233
	ADC >$112233,X
	ADC [$11]
	ADC [$11],Y
	ADC 4,S
	ADC (4,S),Y
	AND #$11
	AND <$11
	AND <$11,X
	AND $1122
	AND $1122,X
	AND $1122,Y
	AND ($11,X)
	AND ($11),Y
	AND ($11)
	AND >$112233
	AND >$112233,X
	AND [$11]
	AND [$11],Y
	AND 4,S
	AND (4,S),Y
	ASL A
	ASL <$11
	ASL <$11,X
	ASL $1122
	ASL $1122,X
	BCC $+2
	BCS $+2
	BEQ $+2
	BIT <$11
	BIT $1122
	BIT <$11,X
	BIT $1122,X
	BIT #$11
	BMI $+2
	BNE $+2
	BPL $+2
	BRA $+2
	BRK
	BRL $+3
	BVC $+2
	BVS $+2
	CLC
	CLD
	CLI
	CLV
	CMP #$11
	CMP <$11
	CMP <$11,X
	CMP $1122
	CMP $1122,X
	CMP $1122,Y
	CMP ($11,X)
	CMP ($11),Y
	CMP ($11)
	CMP >$112233
	CMP >$112233,X
	CMP [$11]
	CMP [$11],Y
	CMP 4,S
	CMP (4,S),Y
	COP
	CPX #$11
	CPX <$11
	CPX $1122
	CPY #$11
	CPY <$11
	CPY $1122
	DEC <$11
	DEC <$11,X
	DEC $1122
	DEC $1122,X
	DEC A
	DEX
	DEY
	EOR #$11
	EOR <$11
	EOR <$11,X
	EOR $1122
	EOR $1122,X
	EOR $1122,Y
	EOR ($11,X)
	EOR ($11),Y
	EOR ($11)
	EOR >$112233
	EOR >$112233,X
	EOR [$11]
	EOR [$11],Y
	EOR 4,S
	EOR (4,S),Y
	INC <$11
	INC <$11,X
	INC $1122
	INC $1122,X
	INC A
	INX
	INY
	JML ($1122)
	JMP $1122
	JMP ($1122)
	JMP ($1122,X)
	JSL >$112233
	JSR $1122
	JSR ($1122,X)
	LDA #$11
	LDA <$11
	LDA <$11,X
	LDA $1122
	LDA $1122,X
	LDA $1122,Y
	LDA ($11,X)
	LDA ($11),Y
	LDA ($11)
	LDA >$112233
	LDA >$112233,X
	LDA [$11]
	LDA [$11],Y
	LDA 4,S
	LDA (4,S),Y
	LDX #$11
	LDX <$11
	LDX <$11,Y
	LDX $1122
	LDX $1122,Y
	LDY #$11
	LDY <$11
	LDY <$11,X
	LDY $1122
	LDY $1122,X
	LSR A
	LSR <$11
	LSR <$11,X
	LSR $1122
	LSR $1122,X
	MVP $11,$22
	MVN $11,$22
	NOP
	ORA #$11
	ORA <$11
	ORA <$11,X
	ORA $1122
	ORA $1122,X
	ORA $1122,Y
	ORA ($11,X)
	ORA ($11),Y
	ORA ($11)
	ORA >$112233
	ORA >$112233,X
	ORA [$11]
	ORA [$11],Y
	ORA 4,S
	ORA (4,S),Y
	PEA #$1122
	PEI #$11
	PER #$1122
	PHA
	PHB
	PHD
	PHK
	PHP
	PHX
	PHY
	PLA
	PLB
	PLD
	PLP
	PLX
	PLY
	REP #$30
	ROL A
	ROL <$11
	ROL <$11,X
	ROL $1122
	ROL $1122,X
	ROR A
	ROR <$11
	ROR <$11,X
	ROR $1122
	ROR $1122,X
	RTI
	RTL
	RTS
	SBC #$11
	SBC <$11
	SBC <$11,X
	SBC $1122
	SBC $1122,X
	SBC $1122,Y
	SBC ($11,X)
	SBC ($11),Y
	SBC ($11)
	SBC >$112233
	SBC >$112233,X
	SBC [$11]
	SBC [$11],Y
	SBC 4,S
	SBC (4,S),Y
	SEC
	SED
	SEI
	SEP #$30	
	STA <$11
	STA <$11,X
	STA $1122
	STA $1122,X
	STA $1122,Y
	STA ($11,X)
	STA ($11),Y
	STA ($11)
	STA >$112233
	STA >$112233,X
	STA [$11]
	STA [$11],Y
	STA 4,S
	STA (4,S),Y
	STP
	STX <$11
	STX <$11,Y
	STX $1122
	STY <$11
	STY <$11,X
	STY $1122
	STZ <$11
	STZ <$11,X
	STZ $1122
	STZ $1122,X
	TAX
	TAY
	TCD
	TCS
	TDC
	TRB $11
	TRB $1122
	TSB $11
	TSB $1122
	TSC
	TSX
	TXA
	TXS
	TXY
	TYA
	TYX
	WAI
	WDM
	XBA
	XCE
	
	
	LDA #"AB"
	LDX #'AB'
	LDY #"BC"
	
	
	.DBREG $2
	
	LDA $10000		; Should be Long
	LDA $20000		; Should be absolute
	
	.DPAGE $30
	
	LDA $24			; Should be absolute
	LDA $34			; Should be direct page
	
	BRK #$7E		; Test BRK extension
	COP #$7F		; Test COP extension

;==============================================================================
; 65832 Opcodes & Addressing modes
;------------------------------------------------------------------------------

	.65832

	.DPAGE $11
	.LONGA OFF
	.LONGI ON
			
	ADC #$11
	ADC <$11
	ADC <$11,X
	ADC $1122
	ADC $1122,X
	ADC $1122,Y
	ADC ($11,X)
	ADC ($11),Y
	ADC ($11)
	ADC >$112233
	ADC >$112233,X
	ADC [$11]
	ADC [$11],Y
	ADC 4,S
	ADC (4,S),Y
	AND #$11
	AND <$11
	AND <$11,X
	AND $1122
	AND $1122,X
	AND $1122,Y
	AND ($11,X)
	AND ($11),Y
	AND ($11)
	AND >$112233
	AND >$112233,X
	AND [$11]
	AND [$11],Y
	AND 4,S
	AND (4,S),Y
	ASL A
	ASL <$11
	ASL <$11,X
	ASL $1122
	ASL $1122,X
	BCC $+2
	BCS $+2
	BEQ $+2
	BIT <$11
	BIT $1122
	BIT <$11,X
	BIT $1122,X
	BIT #$11
	BMI $+2
	BNE $+2
	BPL $+2
	BRA $+2
	BRK
	BRL $+3
	BVC $+2
	BVS $+2
	CLC
	CLD
	CLI
	CLV
	CMP #$11
	CMP <$11
	CMP <$11,X
	CMP $1122
	CMP $1122,X
	CMP $1122,Y
	CMP ($11,X)
	CMP ($11),Y
	CMP ($11)
	CMP >$112233
	CMP >$112233,X
	CMP [$11]
	CMP [$11],Y
	CMP 4,S
	CMP (4,S),Y
	COP
	CPX #$11
	CPX <$11
	CPX $1122
	CPY #$11
	CPY <$11
	CPY $1122
	DEC <$11
	DEC <$11,X
	DEC $1122
	DEC $1122,X
	DEC A
	DEX
	DEY
	EOR #$11
	EOR <$11
	EOR <$11,X
	EOR $1122
	EOR $1122,X
	EOR $1122,Y
	EOR ($11,X)
	EOR ($11),Y
	EOR ($11)
	EOR >$112233
	EOR >$112233,X
	EOR [$11]
	EOR [$11],Y
	EOR 4,S
	EOR (4,S),Y
	INC <$11
	INC <$11,X
	INC $1122
	INC $1122,X
	INC A
	INX
	INY
	JML ($1122)
	JMP $1122
	JMP ($1122)
	JMP ($1122,X)
	JSL >$112233
	JSR $1122
	JSR ($1122,X)
	LDA #$11
	LDA <$11
	LDA <$11,X
	LDA $1122
	LDA $1122,X
	LDA $1122,Y
	LDA ($11,X)
	LDA ($11),Y
	LDA ($11)
	LDA >$112233
	LDA >$112233,X
	LDA [$11]
	LDA [$11],Y
	LDA 4,S
	LDA (4,S),Y
	LDX #$11
	LDX <$11
	LDX <$11,Y
	LDX $1122
	LDX $1122,Y
	LDY #$11
	LDY <$11
	LDY <$11,X
	LDY $1122
	LDY $1122,X
	LSR A
	LSR <$11
	LSR <$11,X
	LSR $1122
	LSR $1122,X
	MVP $11,$22
	MVN $11,$22
	NOP
	ORA #$11
	ORA <$11
	ORA <$11,X
	ORA $1122
	ORA $1122,X
	ORA $1122,Y
	ORA ($11,X)
	ORA ($11),Y
	ORA ($11)
	ORA >$112233
	ORA >$112233,X
	ORA [$11]
	ORA [$11],Y
	ORA 4,S
	ORA (4,S),Y
	PEA #$1122
	PEI #$11
	PER #$1122
	PHA
	PHB
	PHD
	PHK
	PHP
	PHX
	PHY
	PLA
	PLB
	PLD
	PLP
	PLX
	PLY
	REP #$30
	ROL A
	ROL <$11
	ROL <$11,X
	ROL $1122
	ROL $1122,X
	ROR A
	ROR <$11
	ROR <$11,X
	ROR $1122
	ROR $1122,X
	RTI
	RTL
	RTS
	SBC #$11
	SBC <$11
	SBC <$11,X
	SBC $1122
	SBC $1122,X
	SBC $1122,Y
	SBC ($11,X)
	SBC ($11),Y
	SBC ($11)
	SBC >$112233
	SBC >$112233,X
	SBC [$11]
	SBC [$11],Y
	SBC 4,S
	SBC (4,S),Y
	SEC
	SED
	SEI
	SEP #$30	
	STA <$11
	STA <$11,X
	STA $1122
	STA $1122,X
	STA $1122,Y
	STA ($11,X)
	STA ($11),Y
	STA ($11)
	STA >$112233
	STA >$112233,X
	STA [$11]
	STA [$11],Y
	STA 4,S
	STA (4,S),Y
	STP
	STX <$11
	STX <$11,Y
	STX $1122
	STY <$11
	STY <$11,X
	STY $1122
	STZ <$11
	STZ <$11,X
	STZ $1122
	STZ $1122,X
	TAX
	TAY
	TCD
	TCS
	TDC
	TRB $11
	TRB $1122
	TSB $11
	TSB $1122
	TSC
	TSX
	TXA
	TXS
	TXY
	TYA
	TYX
	WAI
	WDM
	XBA
	XCE
	
	
	LDA #"AB"
	LDX #'AB'
	LDY #"BC"
	
; 32-Bit

	.WIDEA ON
	.WIDEI OFF

	ADC #$11
	ADC <$11
	ADC <$11,X
	ADC $1122
	ADC $1122,X
	ADC $1122,Y
	ADC ($11,X)
	ADC ($11),Y
	ADC ($11)
	ADC >$112233
	ADC >$112233,X
	ADC [$11]
	ADC [$11],Y
	ADC 4,S
	ADC (4,S),Y
	AND #$11
	AND <$11
	AND <$11,X
	AND $1122
	AND $1122,X
	AND $1122,Y
	AND ($11,X)
	AND ($11),Y
	AND ($11)
	AND >$112233
	AND >$112233,X
	AND [$11]
	AND [$11],Y
	AND 4,S
	AND (4,S),Y
	ASL A
	ASL <$11
	ASL <$11,X
	ASL $1122
	ASL $1122,X
	BCC $+2
	BCS $+2
	BEQ $+2
	BIT <$11
	BIT $1122
	BIT <$11,X
	BIT $1122,X
	BIT #$11
	BMI $+2
	BNE $+2
	BPL $+2
	BRA $+2
	BRK
	BRL $+3
	BVC $+2
	BVS $+2
	CLC
	CLD
	CLI
	CLV
	CMP #$11
	CMP <$11
	CMP <$11,X
	CMP $1122
	CMP $1122,X
	CMP $1122,Y
	CMP ($11,X)
	CMP ($11),Y
	CMP ($11)
	CMP >$112233
	CMP >$112233,X
	CMP [$11]
	CMP [$11],Y
	CMP 4,S
	CMP (4,S),Y
	COP
	CPX #$11
	CPX <$11
	CPX $1122
	CPY #$11
	CPY <$11
	CPY $1122
	DEC <$11
	DEC <$11,X
	DEC $1122
	DEC $1122,X
	DEC A
	DEX
	DEY
	EOR #$11
	EOR <$11
	EOR <$11,X
	EOR $1122
	EOR $1122,X
	EOR $1122,Y
	EOR ($11,X)
	EOR ($11),Y
	EOR ($11)
	EOR >$112233
	EOR >$112233,X
	EOR [$11]
	EOR [$11],Y
	EOR 4,S
	EOR (4,S),Y
	INC <$11
	INC <$11,X
	INC $1122
	INC $1122,X
	INC A
	INX
	INY
	JML ($1122)
	JMP $1122
	JMP ($1122)
	JMP ($1122,X)
	JSL >$112233
	JSR $1122
	JSR ($1122,X)
	LDA #$11
	LDA <$11
	LDA <$11,X
	LDA $1122
	LDA $1122,X
	LDA $1122,Y
	LDA ($11,X)
	LDA ($11),Y
	LDA ($11)
	LDA >$112233
	LDA >$112233,X
	LDA [$11]
	LDA [$11],Y
	LDA 4,S
	LDA (4,S),Y
	LDX #$11
	LDX <$11
	LDX <$11,Y
	LDX $1122
	LDX $1122,Y
	LDY #$11
	LDY <$11
	LDY <$11,X
	LDY $1122
	LDY $1122,X
	LSR A
	LSR <$11
	LSR <$11,X
	LSR $1122
	LSR $1122,X
	MVP $11,$22
	MVN $11,$22
	NOP
	ORA #$11
	ORA <$11
	ORA <$11,X
	ORA $1122
	ORA $1122,X
	ORA $1122,Y
	ORA ($11,X)
	ORA ($11),Y
	ORA ($11)
	ORA >$112233
	ORA >$112233,X
	ORA [$11]
	ORA [$11],Y
	ORA 4,S
	ORA (4,S),Y
	PEA #$1122
	PEI #$11
	PER #$1122
	PHA
	PHB
	PHD
	PHK
	PHP
	PHX
	PHY
	PLA
	PLB
	PLD
	PLP
	PLX
	PLY
	REP #$30
	ROL A
	ROL <$11
	ROL <$11,X
	ROL $1122
	ROL $1122,X
	ROR A
	ROR <$11
	ROR <$11,X
	ROR $1122
	ROR $1122,X
	RTI
	RTL
	RTS
	SBC #$11
	SBC <$11
	SBC <$11,X
	SBC $1122
	SBC $1122,X
	SBC $1122,Y
	SBC ($11,X)
	SBC ($11),Y
	SBC ($11)
	SBC >$112233
	SBC >$112233,X
	SBC [$11]
	SBC [$11],Y
	SBC 4,S
	SBC (4,S),Y
	SEC
	SED
	SEI
	SEP #$30	
	STA <$11
	STA <$11,X
	STA $1122
	STA $1122,X
	STA $1122,Y
	STA ($11,X)
	STA ($11),Y
	STA ($11)
	STA >$112233
	STA >$112233,X
	STA [$11]
	STA [$11],Y
	STA 4,S
	STA (4,S),Y
	STP
	STX <$11
	STX <$11,Y
	STX $1122
	STY <$11
	STY <$11,X
	STY $1122
	STZ <$11
	STZ <$11,X
	STZ $1122
	STZ $1122,X
	TAX
	TAY
	TCD
	TCS
	TDC
	TRB $11
	TRB $1122
	TSB $11
	TSB $1122
	TSC
	TSX
	TXA
	TXS
	TXY
	TYA
	TYX
	WAI
	WDM
	XBA
	XCE
		
	LDA #"ABCD"
	LDX #'ABCD'
	LDY #"BCDE"	
	
	.DBREG $2
	
	LDA $10000		; Should be Long
	LDA $20000		; Should be absolute
	
	.DPAGE $30
	
	LDA $24			; Should be absolute
	LDA $34			; Should be direct page
	
	
	BRK #$7E		; Test BRK extension
	COP #$7F		; Test COP extension
	
;==============================================================================
; Structured Assembly Code
;------------------------------------------------------------------------------

	.65C02

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

	.65816
	
	.ADDR	$,ExtLab,GblLab
	
;	.END