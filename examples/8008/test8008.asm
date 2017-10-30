; As8008 Test
;
; Notes:
;
; Register names are declared as constants in the assembler so by default
; must be in UPPER case.

		.code
		.org	0h
		
		adc	A
		adc	B
		adc	C
		adc	D
		adc	E
		adc	H
		adc	L
		adc	M
		aci	12h
		add	A
		add	B
		add	C
		add	D
		add	E
		add	H
		add	L
		add	M
		adi	12h
		ana	A
		ana	B
		ana	C
		ana	D
		ana	E
		ana	H
		ana	L
		ana	M
		ani	12h
		cmp	A
		cmp	B
		cmp	C
		cmp	D
		cmp	E
		cmp	H
		cmp	L
		cmp	M
		cpi	12h
		ora	A
		ora	B
		ora	C
		ora	D
		ora	E
		ora	H
		ora	L
		ora	M
		ori	12h
		ral
		rar
		rlc
		rrc
		sbb	A
		sbb	B
		sbb	C
		sbb	D
		sbb	E
		sbb	H
		sbb	L
		sbb	M
		sbi	12h
		sub	A
		sub	B
		sub	C
		sub	D
		sub	E
		sub	H
		sub	L
		sub	M
		sui	12h
		xra	A
		xra	B
		xra	C
		xra	D
		xra	E
		xra	H
		xra	L
		xra	M
		xri	12h
		
		
		.byte	1,2,3
		.word	1,2,3		
		
		.end