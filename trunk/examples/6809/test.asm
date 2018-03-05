
	.code
	.org	$1000
	
; Opcode Numerically
	abx
	adca	#$12
	adca	<0
	adca	,x
	adca	>0
	adcb	#$12
	adcb	<0
	adcb	,x
	adcb	>0
	adda	#$12
	adda	<0
	adda	,x
	adda	>0
	addb	#$12
	addb	<0
	addb	,x
	addb	>0
	addd	#$1234
	addd	<0
	addd	,x
	addd	>0	
	anda	#$12
	anda	<0
	anda	,x
	anda	>0
	andb	#$12
	andb	<0
	andb	,x
	andb	>0
	andcc	#$12
	asl	<0
	asl	,x
	asl	>0
	asla
	aslb
	asr	<0
	asr	,x
	asr	>0
	asra
	asrb
	bcc	label
	bcs	label
	beq	label
	bge	label
	bgt	label
	bhs	label
	bita	#$12
	bita	<0
	bita	,x
	bita	>0
	bitb	#$12
	bitb	<0
	bitb	,x
	bitb	>0	
	ble	label
	blo	label
	bls	label
	blt	label	
	bmi	label
	bne	label
	bpl	label
	bra	label
	brn	label
	bsr	label
	bvc	label
	bvs	label
label:
	clr	<0
	clr	,x
	clr	>0
	clra
	clrb
	cmpa	#$12
	cmpa	<0
	cmpa	,x
	cmpa	>0
	cmpb	#$12
	cmpb	<0
	cmpb	,x
	cmpb	>0
	cmpd	#$1234
	cmpd	<0
	cmpd	,x
	cmpd	>0
	cmps	#$1234
	cmps	<0
	cmps	,x
	cmps	>0
	cmpu	#$1234
	cmpu	<0
	cmpu	,x
	cmpu	>0
	cmpx	#$1234
	cmpx	<0
	cmpx	,x
	cmpx	>0
	cmpy	#$1234
	cmpy	<0
	cmpy	,x
	cmpy	>0
	com	<0
	com	,x
	com	>0
	coma
	comb
	cwai	#$12
	daa
	dec	<0
	dec	,x
	dec	>0
	deca
	decb
	eora	#$12
	eora	<0
	eora	,x
	eora	>0
	eorb	#$12
	eorb	<0
	eorb	,x
	eorb	>0
; exg
	inc	<0
	inc	,x
	inc	>0
	inca
	incb
	jmp	<0
	jmp	,x
	jmp	>0
	jsr	<0
	jsr	,x
	jsr	>0
	lda	#$12
	lda	<0
	lda	,x
	lda	>0
	ldb	#$12
	ldb	<0
	ldb	,x
	ldb	>0
	ldd	#$1234
	ldd	<0
	ldd	,x
	ldd	>0
	lds	#$1234
	lds	<0
	lds	,x
	lds	>0
	ldu	#$1234
	ldu	<0
	ldu	,x
	ldu	>0
	ldx	#$1234
	ldx	<0
	ldx	,x
	ldx	>0
	ldy	#$1234
	ldy	<0
	ldy	,x
	ldy	>0
	



	lbcc	label
	lbcs	label

	
; Indexed Addressing modes

;	lda	,x
;	lda	,y

	.end