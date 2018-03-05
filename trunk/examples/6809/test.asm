
	.code

;===============================================================================	
; Opcode Alphabetically
;-------------------------------------------------------------------------------

	.org	$1000

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
	lbcc	label
	lbcs	label
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
	leas	,x
	leau	,x
	leax	,x
	leay	,x
	lsl	<0
	lsl	,x
	lsl	>0
	lsla
	lslb
	lsr	<0
	lsr	,x
	lsr	>0
	lsra
	lsrb
	mul
	neg	<0
	neg	,x
	neg	>0
	nega
	negb
	nop
	ora	#$12
	ora	<0
	ora	,x
	ora	>0
	orb	#$12
	orb	<0
	orb	,x
	orb	>0
; psh
; pul
	rol	<0
	rol	,x
	rol	>0
	rola
	rolb
	ror	<0
	ror	,x
	ror	>0
	rora
	rorb
	rti
	rts
	sbca	#$12
	sbca	<0
	sbca	,x
	sbca	>0
	sbcb	#$12
	sbcb	<0
	sbcb	,x
	sbcb	>0
	sex
; st
	suba	#$12
	suba	<0
	suba	,x
	suba	>0
	subb	#$12
	subb	<0
	subb	,x
	subb	>0
	swi
	swi2
	swi3
	sync
; tfr
	tst	<0
	tst	,x
	tst	>0
	tsta
	tstb
	

;===============================================================================
;-------------------------------------------------------------------------------

	.org	$2000
	
	neg	<0
;	*
;	*
	com	<0
	lsr	<0
;	*
	ror	<0
	asr	<0
	asl	<0
	lsl	<0			; same as asl
	rol	<0
	dec	<0
;	*
	inc	<0
	tst	<0
	jmp	<0
	clr	<0
	
; Page 2
; Page 3
	nop
	sync
;	*
;	*
	lbra	label
	lbsr	label
;	*
	daa
	orcc	#$12
;*
	andcc	#$12
	sex
; exg
; tfr

	bra	branch
	brn	branch
	bhi	branch
	bls	branch
	bhs	branch			; same as bcc
	bcc	branch
	blo	branch			; same as bcs
	bcs	branch
	bne	branch
	beq	branch
	bvc	branch
	bvs	branch
	bpl	branch
	bmi	branch
	bge	branch
	blt	branch
	bgt	branch
	ble	branch
branch:

; 30-3f

	nega
;	*
;	*	
	coma
	lsra
;	*
	rora
	asra
	asla
	lsla				; same as asla
	rola
	deca
;	*
	inca
	tsta
;	*
	clra
	
	negb
;	*
;	*	
	comb
	lsrb
;	*
	rorb
	asrb
	aslb
	lslb				; same as aslb
	rolb
	decb
;	*
	incb
	tstb
;	*
	clrb
	
	neg	,x
;	*
;	*
	com	,x
	lsr	,x
;	*
	ror	,x
	asr	,x
	asl	,x
	lsl	,x
	rol	,x
	dec	,x
;	*
	inc	,x
	tst	,x
	jmp	,x
	clr	,x
	
	neg	>0
;	*
;	*
	com	>0
	lsr	>0
;	*
	ror	>0
	asr	>0
	asl	>0
	lsl	>0
	rol	>0
	dec	>0
;	*
	inc	>0
	tst	>0
	jmp	>0
	clr	>0

	suba	#$12
	cmpa	#$12
	sbca	#$12
	subd	#$1234
	anda	#$12
	bita	#$12
	lda	#$12
;	*
	eora	#$12
	adca	#$12
	ora	#$12
	adda	#$12
	cmpx	#$1234
	bsr	branch
	ldx	#$1234
;	*

	suba	<0
	cmpa	<0
	sbca	<0
	subd	<0
	anda	<0
	bita	<0
	lda	<0
	sta	<0
	eora	<0
	adca	<0
	ora	<0
	adda	<0
	cmpx	<0
	jsr	<0
	ldx	<0
	stx	<0

	suba	,x
	cmpa	,x
	sbca	,x
	subd	,x
	anda	,x
	bita	,x
	lda	,x
	sta	,x
	eora	,x
	adca	,x
	ora	,x
	adda	,x
	cmpx	,x
	jsr	,x
	ldx	,x
	stx	,x

	suba	>0
	cmpa	>0
	sbca	>0
	subd	>0
	anda	>0
	bita	>0
	lda	>0
	sta	>0
	eora	>0
	adca	>0
	ora	>0
	adda	>0
	cmpx	>0
	jsr	>0
	ldx	>0
	stx	>0

	subb	#$12
	cmpb	#$12
	sbcb	#$12
	addd	#$1234
	andb	#$12
	bitb	#$12
	ldb	#$12
;	*
	eorb	#$12
	adcb	#$12
	orb	#$12
	addb	#$12
	ldd	#$1234
;	*
	ldu	#$1234
;	*

	subb	<0
	cmpb	<0
	sbcb	<0
	addd	<0
	andb	<0
	bitb	<0
	ldb	<0
	stb	<0
	eorb	<0
	adcb	<0
	orb	<0
	addb	<0
	ldd	<0
	std	<0
	ldu	<0
	stu	<0

	subb	,x
	cmpb	,x
	sbcb	,x
	addd	,x
	andb	,x
	bitb	,x
	ldb	,x
	stb	,x
	eorb	,x
	adcb	,x
	orb	,x
	addb	,x
	ldd	,x
	std	,x
	ldu	,x
	stu	,x

	subb	>0
	cmpb	>0
	sbcb	>0
	addd	>0
	andb	>0
	bitb	>0
	ldb	>0
	stb	>0
	eorb	>0
	adcb	>0
	orb	>0
	addb	>0
	ldd	>0
	std	>0
	ldu	>0
	stu	>0

	lbrn	target2
	lbhi	target2
	lbls	target2
	lbhs	target2
	lbcc	target2
	lblo	target2
	lbcs	target2
	lbne	target2
	lbeq	target2
	lbvc	target2
	lbvs	target2
	lbpl	target2
	lbmi	target2
	lbge	target2
	lbgt	target2
	lble	target2
target2:
	swi2
	cmpd	#$1234
	cmpy	#$1234
	ldy	#$1234
	cmpd	<0
	cmpy	<0
	ldy	<0
	cmpd	,x
	cmpy	,x
	ldy	,x
	cmpd	>0
	cmpy	>0
	ldy	>0
	lds	#$1234
	lds	<0
	sts	<0
	lds	,x
	sts	,x
	lds	>0
	sts	>0
	swi3
	cmpu	#$1234
	cmps	#$1234
	cmpu	<0
	cmps	<0
	cmpu	,x
	cmps	,x
	cmpu	>0
	cmps	>0

;===============================================================================
; Indexed Addressing modes
;-------------------------------------------------------------------------------
	
	.org	$3000
	
	lda	,x
	lda	,y



	.end