
	.CODE
	.ORG	X'0000

	NOP
	LDI	X'15

	JMP	XXX

	LD	@-127(P1)
	XPAL	P3
	LD	@1(P1)
	XPAH	P3

XXX:	NOP

	.END